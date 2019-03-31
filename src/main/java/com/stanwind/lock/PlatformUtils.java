package com.stanwind.lock;

import com.stanwind.lock.anno.Lock;
import com.stanwind.lock.bean.LockPolicy;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import sun.management.VMManagement;

/**
 * PlatformUtils network工具集
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-03-31 15:41
 **/
public class PlatformUtils {

    /**
     * 获取localhost MAC地址
     */
    @Lock(policy = LockPolicy.BlockLock, timetOut = 500L)
    private static final String MACAddress(NetworkInterface networkInterface) {
        try {
            byte[] macBytes = networkInterface.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < macBytes.length; i++) {
                sb.append(String.format("%02X%s", macBytes[i], i < macBytes.length - 1 ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static final String getMACAddress() {
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (java.net.SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    if (ip.getHostAddress().equals("127.0.0.1")) {
                        continue;
                    }
                    return MACAddress(netInterface);
                }
            }
        }

        return "";
    }

    /**
     * 获取IP地址
     */
    public static String getIpAddress() {
        Enumeration allNetInterfaces = null;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (java.net.SocketException e) {
            e.printStackTrace();
        }
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address) {
                    if (ip.getHostAddress().equals("127.0.0.1")) {
                        continue;
                    }
                    return ip.getHostAddress();
                }
            }
        }

        return "";
    }

    /**
     * 获取当前JVM 的进程ID
     */
    public static final int JVMPid() {
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            VMManagement mgmt = (VMManagement) jvm.get(runtime);
            Method pidMethod = mgmt.getClass().getDeclaredMethod("getProcessId");
            pidMethod.setAccessible(true);
            int pid = (Integer) pidMethod.invoke(mgmt);
            return pid;
        } catch (Exception e) {
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getMACAddress());
    }
}
