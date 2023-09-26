package utils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IPUtil {

    /**
     * 功能：判断一个IP是不是在一个网段下的
     * 格式：isInRange("192.168.8.3", "192.168.9.10/22");
     */
    public static boolean isInRange(String ip, String cidr) {
        String[] ips = ip.split("\\.");
        int ipAddr = (Integer.parseInt(ips[0]) << 24)
                | (Integer.parseInt(ips[1]) << 16)
                | (Integer.parseInt(ips[2]) << 8) | Integer.parseInt(ips[3]);
        int type = Integer.parseInt(cidr.replaceAll(".*/", ""));
        int mask = 0xFFFFFFFF << (32 - type);
        String cidrIp = cidr.replaceAll("/.*", "");
        String[] cidrIps = cidrIp.split("\\.");
        int cidrIpAddr = (Integer.parseInt(cidrIps[0]) << 24)
                | (Integer.parseInt(cidrIps[1]) << 16)
                | (Integer.parseInt(cidrIps[2]) << 8)
                | Integer.parseInt(cidrIps[3]);

        return (ipAddr & mask) == (cidrIpAddr & mask);
    }

    /**
     * 功能：根据IP和位数返回该IP网段的所有IP
     * 格式：parseIpMaskRange("192.192.192.1.", "23")
     */
    public static List<String> parseIpMaskRange(String ip, String mask) {
        List<String> list = new ArrayList<>();
        if ("32".equals(mask)) {
            list.add(ip);
        } else {
            String startIp = getBeginIpStr(ip, mask);
            String endIp = getEndIpStr(ip, mask);
            list = parseIpRange(startIp, endIp);
        }
        return list;
    }

    /**
     * 功能：根据位数返回IP总数
     * 格式：parseIpMaskRange("192.192.192.1", "23")
     */
    public static int getIpCount(String mask) {
        return BigDecimal.valueOf(Math.pow(2, 32 - Integer.parseInt(mask))).setScale(0, BigDecimal.ROUND_DOWN).intValue();//IP总数，去小数点
    }

    /**
     * 功能：校验ip格式
     */
    public static boolean isIP(String str) {
        String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(str).matches();
    }

    public static List<String> parseIpRange(String ipfrom, String ipto) {
        List<String> ips = new ArrayList<>();
        String[] ipfromd = ipfrom.split("\\.");
        String[] iptod = ipto.split("\\.");
        int[] int_ipf = new int[4];
        int[] int_ipt = new int[4];
        for (int i = 0; i < 4; i++) {
            int_ipf[i] = Integer.parseInt(ipfromd[i]);
            int_ipt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
            for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1]
                    : 255); B++) {
                for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2]
                        : 255); C++) {
                    for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3]
                            : 255); D++) {
                        ips.add(A + "." + B + "." + C + "." + D);
                    }
                }
            }
        }
        return ips;
    }

    /**
     * 把long类型的Ip转为一般Ip类型：xx.xx.xx.xx
     *
     * @param ip
     * @return
     */
    public static String long2Str(Long ip) {
        String s1 = String.valueOf((ip & 4278190080L) / 16777216L);
        String s2 = String.valueOf((ip & 16711680L) / 65536L);
        String s3 = String.valueOf((ip & 65280L) / 256L);
        String s4 = String.valueOf(ip & 255L);
        return s1 + "." + s2 + "." + s3 + "." + s4;
    }

    /**
     * 把xx.xx.xx.xx类型的转为long类型的
     *
     * @param ip
     * @return
     */
    public static Long str2Long(String ip) {
        Long ipLong = 0L;
        String ipTemp = ip;
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf('.')));
        ipTemp = ipTemp.substring(ipTemp.indexOf('.') + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp);
        return ipLong;
    }

    /**
     * 根据掩码位获取掩码
     *
     * @param maskBit 掩码位数，如"28"、"30"
     * @return
     */
    public static String getMaskByMaskBit(String maskBit) {
        return "".equals(maskBit) ? "error, maskBit is null !" : getMaskMap(maskBit);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String getBeginIpStr(String ip, String maskBit) {
        long ipLong = getBeginIpLong(ip, maskBit);
        if (!"32".equals(maskBit) && !"31".equals(maskBit)) {
            ipLong = ipLong + 1L;
        }
        return long2Str(ipLong);
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 起始IP的长整型表示
     */
    private static Long getBeginIpLong(String ip, String maskBit) {
        return str2Long(ip) & str2Long(getMaskByMaskBit(maskBit));
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, String maskBit) {
        long ipLong = getEndIpLong(ip, maskBit);
        if (!"32".equals(maskBit) && !"31".equals(maskBit)) {
            ipLong = ipLong - 1L;
        }
        return long2Str(ipLong);
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip      给定的IP，如218.240.38.69
     * @param maskBit 给定的掩码位，如30
     * @return 终止IP的长整型表示
     */
    private static Long getEndIpLong(String ip, String maskBit) {
        return getBeginIpLong(ip, maskBit)
                + ~str2Long(getMaskByMaskBit(maskBit));
    }


    /**
     * 根据子网掩码转换为掩码位 如 255.255.255.252转换为掩码位 为 30
     *
     * @param netmarks
     * @return
     */
    public static int getNetMask(String netmarks) {
        StringBuilder sbf;
        String str;
        int inetmask = 0;
        int count = 0;
        String[] ipList = netmarks.split("\\.");
        for (int n = 0; n < ipList.length; n++) {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i);
                if (i == -1) {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    /**
     * 计算子网大小
     *
     * @param maskBit 掩码位
     * @return
     */
    public static int getPoolMax(int maskBit) {
        if (maskBit <= 0 || maskBit >= 32) {
            return 0;
        }
        return (int) Math.pow(2, 32 - maskBit) - 2;
    }

    private static StringBuilder toBin(int x) {
        StringBuilder result = new StringBuilder();
        result.append(x % 2);
        x /= 2;
        while (x > 0) {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

    public static String getMaskMap(String maskBit) {
        if ("0".equals(maskBit)) return "0.0.0.0";
        if ("1".equals(maskBit)) return "128.0.0.0";
        if ("2".equals(maskBit)) return "192.0.0.0";
        if ("3".equals(maskBit)) return "224.0.0.0";
        if ("4".equals(maskBit)) return "240.0.0.0";
        if ("5".equals(maskBit)) return "248.0.0.0";
        if ("6".equals(maskBit)) return "252.0.0.0";
        if ("7".equals(maskBit)) return "254.0.0.0";
        if ("8".equals(maskBit)) return "255.0.0.0";
        if ("9".equals(maskBit)) return "255.128.0.0";
        if ("10".equals(maskBit)) return "255.192.0.0";
        if ("11".equals(maskBit)) return "255.224.0.0";
        if ("12".equals(maskBit)) return "255.240.0.0";
        if ("13".equals(maskBit)) return "255.248.0.0";
        if ("14".equals(maskBit)) return "255.252.0.0";
        if ("15".equals(maskBit)) return "255.254.0.0";
        if ("16".equals(maskBit)) return "255.255.0.0";
        if ("17".equals(maskBit)) return "255.255.128.0";
        if ("18".equals(maskBit)) return "255.255.192.0";
        if ("19".equals(maskBit)) return "255.255.224.0";
        if ("20".equals(maskBit)) return "255.255.240.0";
        if ("21".equals(maskBit)) return "255.255.248.0";
        if ("22".equals(maskBit)) return "255.255.252.0";
        if ("23".equals(maskBit)) return "255.255.254.0";
        if ("24".equals(maskBit)) return "255.255.255.0";
        if ("25".equals(maskBit)) return "255.255.255.128";
        if ("26".equals(maskBit)) return "255.255.255.192";
        if ("27".equals(maskBit)) return "255.255.255.224";
        if ("28".equals(maskBit)) return "255.255.255.240";
        if ("29".equals(maskBit)) return "255.255.255.248";
        if ("30".equals(maskBit)) return "255.255.255.252";
        if ("31".equals(maskBit)) return "255.255.255.254";
        if ("32".equals(maskBit)) return "255.255.255.255";
        return "-1";
    }

    public static double ipToDouble(String ip) {
        String[] arr = ip.split("\\.");
        double d1 = Double.parseDouble(arr[0]);
        double d2 = Double.parseDouble(arr[1]);
        double d3 = Double.parseDouble(arr[2]);
        double d4 = Double.parseDouble(arr[3]);
        return d1 * Math.pow(256, 3) + d2 * Math.pow(256, 2) + d3 * 256 + d4;
    }

    public static boolean matches(String ip, String ipAddress) {
        try {
            int nMaskBits = 0;
            if (ipAddress.indexOf('/') > 0) {
                String[] addressAndMask = ipAddress.split("/");
                ipAddress = addressAndMask[0];
                nMaskBits = Integer.parseInt(addressAndMask[1]);
            } else {
                nMaskBits = -1;
            }
            InetAddress requiredAddress = parseAddress(ipAddress);
            InetAddress remoteAddress = parseAddress(ip);

            if (!requiredAddress.getClass().equals(remoteAddress.getClass())) {
                return false;
            }

            if (nMaskBits < 0) {
                return remoteAddress.equals(requiredAddress);
            }

            byte[] remAddr = remoteAddress.getAddress();
            byte[] reqAddr = requiredAddress.getAddress();

            int nMaskFullBytes = nMaskBits / 8;
            byte finalByte = (byte) (0xFF00 >> (nMaskBits & 0x07));

            // System.out.println("Mask is " + new sun.misc.HexDumpEncoder().encode(mask));

            for (int i = 0; i < nMaskFullBytes; i++) {
                if (remAddr[i] != reqAddr[i]) {
                    return false;
                }
            }

            if (finalByte != 0) {
                return (remAddr[nMaskFullBytes] & finalByte) == (reqAddr[nMaskFullBytes] & finalByte);
            }
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static InetAddress parseAddress(String address) {
        try {
            return InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Failed to parse address" + address, e);
        }
    }

    /**
     * 获取全IPv6地址
     *
     * @param ipv6
     * @return
     */
    public static String getFullIPv6(String ipv6) {
        //入参为::时，此时全为0
        if (ipv6.equals("::")) {
            return "0000:0000:0000:0000:0000:0000:0000:0000";
        }
        //入参已::结尾时，直接在后缀加0
        if (ipv6.endsWith("::")) {
            ipv6 += "0";
        }
        String[] arrs = ipv6.split(":");
        String symbol = "::";
        int arrleng = arrs.length;
        while (arrleng < 8) {
            symbol += ":";
            arrleng++;
        }
        ipv6 = ipv6.replace("::", symbol);
        String fullip = "";
        for (String ip : ipv6.split(":")) {
            while (ip.length() < 4) {
                ip = "0" + ip;
            }
            fullip += ip + ':';
        }
        return fullip.substring(0, fullip.length() - 1).toLowerCase();
    }

    /**
     * 获取简写IPv6地址
     * @param ipv6
     * @return
     */
//    public static String getShortIPv6(String ipv6){
//        String shortIP="";
//        ipv6=getFullIPv6(ipv6);
//        String[] arr = ipv6.split(":");
//        //去掉每组数据前的0
//        for (int i = 0; i < arr.length; i++){
//            arr[i] = arr[i].replaceAll("^0{1,3}", "");
//        }
//        //最长的连续0
//        String[] arr2 = arr.clone();
//        for (int i = 0; i < arr2.length; i++){
//            if (!"0".equals(arr2[i])){
//                arr2[i] = "-";
//            }
//        }
//        Pattern pattern = Pattern.compile("0{2,}");
//        Matcher matcher = pattern.matcher(StringUtils.join(Arrays.asList(arr2), ""));
//        String maxStr= "";
//        int start = -1;
//        int end = -1;
//        while (matcher.find()) {
//            if(maxStr.length()<matcher.group().length()) {
//                maxStr=matcher.group();
//                start = matcher.start();
//                end = matcher.end();
//            }
//        }
//        // 组合IPv6简写地址
//        if(maxStr.length()>0) {
//            for (int i = start; i < end; i++){
//                arr[i] = ":";
//            }
//        }
//        shortIP = StringUtils.join(Arrays.asList(arr), ":");
//        shortIP= shortIP.replaceAll(":{2,}", "::");
//        return shortIP;
//    }
}
