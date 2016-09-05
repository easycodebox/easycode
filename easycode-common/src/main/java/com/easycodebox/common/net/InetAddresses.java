package com.easycodebox.common.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 获取本机IP
 * @author WangXiaoJin
 *
 */
public class InetAddresses {

	/**
	 * 返回第一个可用的本机local IP
	 * @return
	 * @throws SocketException
	 */
	public static String getLocalIp() throws SocketException {
		InetAddress addr = getLocalAddress();
        return addr == null ? null : addr.getHostAddress();
    }
	
	/**
	 * 返回第一个可用的本机local IP
	 * @return
	 * @throws SocketException
	 */
	public static InetAddress getLocalAddress() throws SocketException {
		List<InetAddress> addrs = getLocalAddresses();
        return addrs.size() > 0 ? addrs.get(0) : null;
    }
	
	
	/**
	 * 返回本机所有local IP
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
	 * @return
	 * @throws SocketException
	 */
	public static List<InetAddress> getLocalAddresses() throws SocketException {
		List<InetAddress> all = new ArrayList<>();
        for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements();) {
            NetworkInterface ni = nis.nextElement();
            if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                continue;
            }
            for (Enumeration<InetAddress> addrs = ni.getInetAddresses(); addrs.hasMoreElements();) {
            	InetAddress addr = addrs.nextElement();
            	if (!addr.isLoopbackAddress() 
            			&& !addr.isLinkLocalAddress() 
            			&& !addr.isMulticastAddress()
            			&& addr.isSiteLocalAddress())
            		all.add(addr);
            }
        }
        return all;
    }
	
}
