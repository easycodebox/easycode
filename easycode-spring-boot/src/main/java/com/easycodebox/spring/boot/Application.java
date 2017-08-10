package com.easycodebox.spring.boot;

import com.easycodebox.common.log.slf4j.Logger;
import com.easycodebox.common.log.slf4j.LoggerFactory;
import com.easycodebox.common.validate.Assert;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 1. 默认禁用SpringBoot的Banner，可以通过<b>-Dspring.banner.disable=false</b>来启用SpringBoot的Banner <br/>
 * 2. 当你启动的应用是非web应用，且没有线程在运行时，启动成功后会自动关闭应用。可以通过<b>spring.app.wait=true</b>参数
 * 让应用一直处于运行状态。
 * @author WangXiaoJin
 */
public class Application {
    
    private static final Logger log = LoggerFactory.getLogger(Application.class);
    
    public static final String BOOT_BANNER_DISABLE_KEY = "spring.banner.disable";
    
    public static final String SPRING_APP_WAIT_KEY = "spring.app.wait";
    
    private static volatile boolean running = true;
    
    public static ConfigurableApplicationContext run(Object source, String... args) {
        return run(new Object[] { source }, args);
    }
    
    public static ConfigurableApplicationContext run(Object[] sources, String[] args) {
        return run(new SpringApplication(sources), args);
    }
    
    public static ConfigurableApplicationContext run(SpringApplication application, String[] args) {
        ConfigurableApplicationContext context = null;
        try {
            Assert.notNull(application);
            //禁用SpringBoot Banner
            String disableBanner = System.getProperty(BOOT_BANNER_DISABLE_KEY);
            if (disableBanner == null || disableBanner.equals("true")) {
                application.setBannerMode(Mode.OFF);
            }
            context = application.run(args);
            //注册关闭服务钩子
            if (!application.isWebEnvironment() &&
                    "true".equals(context.getEnvironment().getProperty(SPRING_APP_WAIT_KEY))) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        synchronized (Application.class) {
                            running = false;
                            Application.class.notify();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Application startup error.", e);
            System.exit(1);
        }
        if (!application.isWebEnvironment() &&
                "true".equals(context.getEnvironment().getProperty(SPRING_APP_WAIT_KEY))) {
            synchronized (Application.class) {
                while (running) {
                    try {
                        Application.class.wait();
                    } catch (Throwable ignored) {
                
                    }
                }
            }
        }
        return context;
    }
    
}
