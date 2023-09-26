package test;

import com.googlecode.ipv6.IPv6Network;
import utils.IPUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class IPProcess {
    public static void main(String[] args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("/Users/gaopeng/Desktop/ip.txt"), StandardCharsets.UTF_8);
        for (String line : allLines) {
            if (line.contains(":")) {
                IPv6Network network = IPv6Network.fromString(line);
                String startIp = network.getFirst().toLongString();
                String endIp = network.getLast().toLongString();
//                System.out.println(startIp + "," + endIp);
//                Files.write(Paths.get("/Users/gaopeng/Desktop/ip_result.txt"),("QH,XN,QH-XN,1,IDC," + startIp + "," + endIp + "\n").getBytes(),StandardOpenOption.APPEND);
                Files.write(Paths.get("/Users/gaopeng/Desktop/ip_result.txt"),(startIp + "," + endIp + "\n").getBytes(),StandardOpenOption.APPEND);

            } else {
                String[] splits = line.split("/");
                String beginIpStr = IPUtil.getBeginIpStr(splits[0], splits[1]);
                String endIpStr = IPUtil.getEndIpStr(splits[0], splits[1]);
//                System.out.println(beginIpStr + "," + endIpStr);
//                Files.write(Paths.get("/Users/gaopeng/Desktop/ip_result.txt"),("QH,XN,QH-XN,1,IDC," + beginIpStr + "," + endIpStr + "\n").getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get("/Users/gaopeng/Desktop/ip_result.txt"),(beginIpStr + "," + endIpStr + "\n").getBytes(), StandardOpenOption.APPEND);

            }
        }
    }
}