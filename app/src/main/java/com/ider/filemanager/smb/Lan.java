package com.ider.filemanager.smb;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 * Created by ider-eric on 2016/10/21.
 * 局域网操作类
 */

public class Lan {



    /**
     * 获取此设备所在网段下所有的IP地址
     * Javadoc:可以使用 getNetworkInterfaces()+getInetAddresses() 来获取此节点的所有 IP 地址。
     */
    public static Vector<Vector<InetAddress>> getSubnetAddress() {
        int[] hostParts = new int[4];
        int[] maskParts = new int[4];
        int[] hostStart = new int[4];
        int[] hostEnd = new int[4];

        Vector<Vector<InetAddress>> vectorList = new Vector<>();
        ArrayList<String> ipAndMaskList = getIpAndMask();
        for (int i = 0; i < ipAndMaskList.size(); i++) {
            Log.i("tag", "ipAndMask = " + ipAndMaskList.get(i));
            String[] maskAndIpSplit = ipAndMaskList.get(i).split(";");
            String host = maskAndIpSplit[0];
            String subNetmask = maskAndIpSplit[1];

            String[] split = host.split("\\.");
            if(split.length != 4) {
                continue;
            }
            hostParts[0] = Integer.parseInt(split[0]);
            hostParts[1] = Integer.parseInt(split[1]);
            hostParts[2] = Integer.parseInt(split[2]);
            hostParts[3] = Integer.parseInt(split[3]);

            split = subNetmask.split("\\.");
            maskParts[0] = Integer.parseInt(split[0]);
            maskParts[1] = Integer.parseInt(split[1]);
            maskParts[2] = Integer.parseInt(split[2]);
            maskParts[3] = Integer.parseInt(split[3]);

            hostStart[0] = maskParts[0] & hostParts[0];         //255&[0] = [0]    192
            hostStart[1] = maskParts[1] & hostParts[1];         //255&[1] = [1]    168
            hostStart[2] = maskParts[2] & hostParts[2];         //255&[2] = [2]    1
            hostStart[3] = maskParts[3] & hostParts[3];         //0&[3] = 0      0

            // 0xFF = 255
            // 异或^:相同输出0，不同输出1
            hostEnd[0] = hostParts[0] | (maskParts[0] ^ 0XFF);    //[0]|0 = [0]    192
            hostEnd[1] = hostParts[1] | (maskParts[1] ^ 0XFF);    //[1]|0 = [1]    168
            hostEnd[2] = hostParts[2] | (maskParts[2] ^ 0XFF);    //[2]|0 = [2]    1
            hostEnd[3] = hostParts[3] | (maskParts[3] ^ 0XFF);    //[3]|255 = 255  255

            Log.i("tag", "maskPart = " + maskParts[0] + maskParts[1] + maskParts[2] + maskParts[3]);
            Log.i("tag", "host.start = " + hostStart[0] + hostStart[1] + hostStart[2] + hostStart[3]);
            Log.i("tag", "host.end = " + hostEnd[0] + hostEnd[1] + hostEnd[2] + hostEnd[3]);

            Vector<InetAddress> vector = new Vector<>();
            for(int a = hostStart[0]; a <= hostEnd[0]; a++) {
                for (int b = hostStart[1]; b <= hostEnd[1]; b++) {
                    for (int c = hostStart[2]; c <= hostEnd[2]; c++) {
                        for (int d = hostStart[3]; d <= hostEnd[3]; d++) {
                            byte[] iNetAddrhost = new byte[4];
                            iNetAddrhost[0] = (byte) a;
                            iNetAddrhost[1] = (byte) b;
                            iNetAddrhost[2] = (byte) c;
                            iNetAddrhost[3] = (byte) d;
                            try {
                                InetAddress inetAddress = InetAddress.getByAddress(iNetAddrhost);
                                vector.add(inetAddress);

                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            vectorList.add(vector);
        }
        return vectorList;
    }


    /**
     * 获取ip和子网掩码
     * 如192.168.1.118;255.255.255.255.0；
     *   fe80::215:18ff:fe01:8131%eth0;255.255.255.255.255
     */
    private static ArrayList<String> getIpAndMask() {
        ArrayList<String> ipAndMaskList = new ArrayList<>();
        try {
            /*
             返回此机器所有的网络接口
             例如 1.lo:127.0.0.1
                  2.eth0
                  3.tunl0
                  4.sit0
                  5.ip6tnl0
             */
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while(en.hasMoreElements()) {
                NetworkInterface networkInterface = en.nextElement();
                List<InterfaceAddress> listIAddr = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress IAddr : listIAddr) {
                    InetAddress inetAddress = IAddr.getAddress();
                    // 非127.0.0.1等，过滤掉没用的接口信息，只保留eth0的IP地址
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = inetAddress.getHostAddress();
                        String subnetmask = calcMaskByPrefixLength(IAddr.getNetworkPrefixLength());
                        String ipAndMask = ip + ";" + subnetmask;
                        Log.i("tag", ipAndMask);
                        ipAndMaskList.add(ipAndMask);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAndMaskList;
    }


    // 根据InterfaceAddress的长度获取到mask子网掩码
    private static String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;

    }


    public static boolean ping(String ip) {
        try {
            Socket server = new Socket();
            InetSocketAddress address = new InetSocketAddress(ip, 445);
            server.connect(address, 4000);
            server.close();
        } catch (IOException e) {
            Socket server = new Socket();
            InetSocketAddress address = new InetSocketAddress(ip, 139);
            try {
                server.connect(address, 4000);
                server.close();
                return true;
            } catch (IOException e1) {
                return false;
            }
        }
        return true;
    }


}
