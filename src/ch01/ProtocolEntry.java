package ch01;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;

public class ProtocolEntry implements PacketReceiver {

    @Override
    public void receivePacket(Packet packet) {
        System.out.println(packet + "\nReceive a packet!");
    }
    // 获取网卡列表, 并且打印详细信息
    // 访问硬件权限, Lib 中 加上 编译好的 .so 库文件
    public static void main(String[] args) throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
        for (int i = 0; i < devices.length; i++) {
            System.out.println(i + " " + devices[i].name + " description:" + devices[i].description);
            // 打印 mac 地址
            System.out.print("Mac Address: ");
            int cnt = 0;
            for (byte b : devices[i].mac_address) {
                if (cnt++ > 0) System.out.print(":");
                System.out.print(Integer.toHexString(b));
            }
            System.out.println();
            for (NetworkInterfaceAddress a : devices[i].addresses) {
                System.out.println(" address:" + a.address + " " + a.subnet + " " + a.broadcast);
            }
            // 操作需要管理员权限, ---> 将ide 下面的命令行复制到 shell 里加 sudo
            JpcapCaptor captor = JpcapCaptor.openDevice(devices[i], 65536, false, 20);
            if (captor != null) {
                System.out.println("Open captor on device" + i);
                break;
            }
        }
    }


}
