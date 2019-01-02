package com.easycodebox.common.zookeeper.curator;

import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 此类对{@link LeaderSelector}做了封装，使用起来更方便，功能区别如下：<br/>
 * 1. 原有{@link LeaderSelectorListener#takeLeadership(CuratorFramework)}执行完后会自动回收leader权限。
 * 而此类会持续占有leader权限，除非连接状态变更为{@link ConnectionState#SUSPENDED}或{@link ConnectionState#LOST}时，
 * 会释放leader权限，释放leader权限后可通过{@link #autoRequeue()}配置使其自动排队重新获取leader，
 * 否则永远都不会再获取leader了。<br/>
 * 2. {@link LeaderSelector}类只能传递一个{@link LeaderSelectorListener}监听器，而此类可传递多个{@link LeaderLifecycleListener}，
 * 方便多处逻辑共享一个leader
 * @author WangXiaoJin
 */
public class LastingLeaderSelector implements Closeable {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private LeaderSelector leaderSelector;
    private ExecutorService executorService;
    
    private boolean shutdownExecutorOnClose = true;
    
    private static final ThreadFactory defaultThreadFactory = ThreadUtils.newThreadFactory("LeaderLifecycleListener");
    
    public LastingLeaderSelector(CuratorFramework client, String leaderPath, List<LeaderLifecycleListener> listeners) {
        this(client, leaderPath, listeners, false);
    }
    
    public LastingLeaderSelector(CuratorFramework client, String leaderPath, List<LeaderLifecycleListener> listeners, boolean cancelled) {
        this(client, leaderPath, null, Executors.newSingleThreadExecutor(defaultThreadFactory), listeners, cancelled);
    }
    
    public LastingLeaderSelector(CuratorFramework client, String leaderPath, ExecutorService executorService, List<LeaderLifecycleListener> listeners) {
        this(client, leaderPath, null, executorService, listeners, false);
    }
    
    /**
     * @param client
     * @param leaderPath
     * @param leaderId
     * @param executorService   用于执行listeners
     * @param listeners leader状态监听器
     * @param cancelled 当连接状态为{@link ConnectionState#SUSPENDED}或{@link ConnectionState#LOST}时，
     *                  取消当前Leadership，中断当前正在执行的任务
     */
    public LastingLeaderSelector(CuratorFramework client, String leaderPath, String leaderId,
                                 ExecutorService executorService, List<LeaderLifecycleListener> listeners, boolean cancelled) {
        Preconditions.checkNotNull(executorService, "executorService cannot be null");
        this.executorService = executorService;
        leaderSelector = new LeaderSelector(
                client,
                leaderPath,
                new LastingLeaderSelectorListener(executorService, listeners, cancelled)
        );
        if (leaderId != null) {
            leaderSelector.setId(leaderId);
        }
    }
    
    /**
     * 释放leader权限后自动排队再次获取leader权限
     */
    public void autoRequeue() {
        leaderSelector.autoRequeue();
        log.debug("AutoRequeue leaderSelector.");
    }
    
    public void start() {
        leaderSelector.start();
        log.debug("Start leaderSelector.");
    }
    
    @Override
    public void close() {
        leaderSelector.close();
        log.debug("Close leaderSelector.");
        if (shutdownExecutorOnClose) {
            executorService.shutdown();
            log.debug("Shutdown executorService.");
        }
    }
    
    public LeaderSelector getLeaderSelector() {
        return leaderSelector;
    }
    
    public boolean isShutdownExecutorOnClose() {
        return shutdownExecutorOnClose;
    }
    
    public void setShutdownExecutorOnClose(boolean shutdownExecutorOnClose) {
        this.shutdownExecutorOnClose = shutdownExecutorOnClose;
    }
    
    /**
     * 一旦获得leader之后就不会释放leader权限，除非连接状态变成{@link ConnectionState#SUSPENDED}或{@link ConnectionState#LOST}
     */
    class LastingLeaderSelectorListener implements LeaderSelectorListener {
    
        private final Logger log = LoggerFactory.getLogger(getClass());
        
        private volatile boolean running = true;
        
        /**
         * Leader状态（获取/释放）的监听器
         */
        private List<LeaderLifecycleListener> listeners;
        
        private ExecutorService executorService;
        
        /**
         * 当连接状态为{@link ConnectionState#SUSPENDED}或{@link ConnectionState#LOST}时，取消当前Leadership，
         * 中断当前正在执行的任务
         */
        private boolean cancelled;
        
        public LastingLeaderSelectorListener(ExecutorService executorService, List<LeaderLifecycleListener> listeners, boolean cancelled) {
            Preconditions.checkNotNull(executorService, "executorService cannot be null");
            Preconditions.checkNotNull(listeners, "listeners cannot be null");
            this.executorService = executorService;
            this.listeners = listeners;
            this.cancelled = cancelled;
        }
        
        @Override
        public void takeLeadership(CuratorFramework client) throws Exception {
            log.debug("Executing LastingLeaderSelectorListener.takeLeadership().");
            for (final LeaderLifecycleListener listener : listeners) {
                executorService.submit(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        listener.take();
                        return null;
                    }
                });
            }
            synchronized (this) {
                while (running) {
                    try {
                        log.debug("holding leader...");
                        this.wait();
                    } catch (Exception e) {
                        ThreadUtils.checkInterrupted(e);
                    }
                }
            }
            log.debug("Executed LastingLeaderSelectorListener.takeLeadership() end. Release leader.");
        }
        
        @Override
        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                synchronized (this) {
                    log.debug("Reset leader listener state.");
                    running = true;
                    for (LeaderLifecycleListener listener : listeners) {
                        listener.resetState();
                    }
                }
            } else if (newState == ConnectionState.SUSPENDED || newState == ConnectionState.LOST) {
                log.debug("Executing LastingLeaderSelectorListener.stateChanged(). release-leader begin.");
                for (final LeaderLifecycleListener listener : listeners) {
                    executorService.submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            listener.release();
                            return null;
                        }
                    });
                }
                synchronized (this) {
                    running = false;
                    log.debug("Release the holding leader.");
                    this.notifyAll();
                }
                if (cancelled) {
                    log.debug("Cancel the leadership.Interrupt the inner task.");
                    throw new CancelLeadershipException();
                }
            }
        }
        
    }
    
    private enum State {
        LATENT,
        TAKED,
        RELEASED
    }
    
    /**
     * Leader状态（获取/释放）的监听器
     */
    public abstract static class LeaderLifecycleListener {
    
        private final Logger log = LoggerFactory.getLogger(getClass());
        
        private final AtomicReference<State> state = new AtomicReference<>(State.LATENT);
        
        public void resetState() {
            state.set(State.LATENT);
        }
        
        /**
         * 获得leader时执行的逻辑
         */
        public synchronized void take() throws Exception {
            log.debug("Trigger LeaderLifecycleListener take event. Current state : {}", state.get());
            Preconditions.checkState(state.compareAndSet(State.LATENT, State.TAKED),
                    "Cannot take leadership, because of current state is" + state.get());
            log.debug("Executing LeaderLifecycleListener.doTake().");
            doTake();
            log.debug("Executed LeaderLifecycleListener.doTake() end.");
        }
        
        /**
         * 获得leader时执行的逻辑
         * @throws Exception
         */
        protected abstract void doTake() throws Exception;
        
        
        /**
         * 释放leader时执行的逻辑
         */
        public synchronized void release() throws Exception {
            log.debug("Trigger LeaderLifecycleListener release event. Current state : {}", state.get());
            State preState = state.getAndSet(State.RELEASED);
            if (preState == State.TAKED) {
                log.debug("Executing LeaderLifecycleListener.doRelease().");
                doRelease();
                log.debug("Executed LeaderLifecycleListener.doRelease() end.");
            }
        }
        
        /**
         * 释放leader时执行的逻辑
         * @throws Exception
         */
        protected abstract void doRelease() throws Exception;
        
    }
}
