package com.easycodebox.common.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 获取本机IP
 *
 * @author WangXiaoJin
 */
public class InetAddresses {

    private InetAddresses() {

    }

    /**
     * 返回第一个可用的本机local IP
     *
     * @return
     * @throws SocketException
     */
    public static String getLocalIp() throws SocketException {
        InetAddress addr = getLocalAddress();
        return addr == null ? null : addr.getHostAddress();
    }

    /**
     * 返回第一个可用的本机local IP
     *
     * @return
     * @throws SocketException
     */
    public static InetAddress getLocalAddress() throws SocketException {
        List<InetAddress> addrs = getLocalAddresses();
        return addrs.isEmpty() ? null : addrs.get(0);
    }


    /**
     * 返回本机所有local IP
     *
     * @return
     * @throws SocketException
     */
    public static List<String> getLocalIps() throws SocketException {
        List<InetAddress> addrs = getLocalAddresses();
        List<String> ips = new ArrayList<>();
        for (InetAddress addr : addrs) {
            ips.add(addr.getHostAddress());
        }
        return ips;
    }

    /**
     * 返回本机所有local IP
     *
     * @return
     * @throws SocketException
     */
    @SuppressWarnings("squid:S3776")
    public static List<InetAddress> getLocalAddresses() throws SocketException {
        Map<Integer, List<InetAddress>> map = new TreeMap<>();
        for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements(); ) {
            NetworkInterface ni = nis.nextElement();
            if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                continue;
            }
            // 当前网卡可用IP
            List<InetAddress> ntAddrs = new ArrayList<>();
            // 非SiteLocalAddress作为候选IP存储此List
            List<InetAddress> candidates = new ArrayList<>();
            for (Enumeration<InetAddress> addrs = ni.getInetAddresses(); addrs.hasMoreElements(); ) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress()
                    && !addr.isLinkLocalAddress()
                    && !addr.isMulticastAddress()) {
                    if (addr.isSiteLocalAddress()) {
                        ntAddrs.add(addr);
                    } else {
                        //当IP地址不是SiteLocalAddress时，则作为备选IP放置在IP列表最后面
                        // IP4判断是否是SiteLocalAddress依据：以 10/8、172.16/12、192.168/16开头的IP
                        candidates.add(addr);
                    }
                }
            }
            if (!candidates.isEmpty()) {
                ntAddrs.addAll(candidates);
            }
            if (!ntAddrs.isEmpty()) {
                // 网卡索引作为排序规则
                map.put(ni.getIndex(), ntAddrs);
            }
        }
        // 根据所有网卡的索引自然排序，返回对应的所有IP
        List<InetAddress> all = new ArrayList<>();
        for (List<InetAddress> val : map.values()) {
            all.addAll(val);
        }
        return all;
    }

}
