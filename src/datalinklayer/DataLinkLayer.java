package datalinklayer;

import jpcap.*;
import jpcap.packet.EthernetPacket;
import jpcap.packet.Packet;
import util.PacketProvider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

/**
 * create by srdczk 20-2-11
 */
// 模拟实现数据链路层
    //接收数据并且发送数据, 上层协议
public class DataLinkLayer extends PacketProvider implements PacketReceiver {
    // 单例模式
    private static DataLinkLayer instance = null;
    private NetworkInterface device = null;
    private Inet4Address ipAddress = null;
    private byte[] macAddress = null;
    private JpcapCaptor captor = null;
    private JpcapSender sender = null;

    private DataLinkLayer() {}

    public static DataLinkLayer getInstance() {
        if (instance == null) instance = new DataLinkLayer();
        return instance;
    }

    public void initWithOpenDevice(NetworkInterface device) throws IOException {
        this.device = device;
        this.ipAddress = getDeviceIpAddress();
        this.macAddress = device.mac_address;
        captor = JpcapCaptor.openDevice(device, 2000, false, 3000);
        this.sender = captor.getJpcapSenderInstance();
    }

    private Inet4Address getDeviceIpAddress() {
        for (NetworkInterfaceAddress address : this.device.addresses) {
            if (!(address.address instanceof Inet4Address)) continue;
            return (Inet4Address) address.address;
        }
        return null;
    }

    public byte[] getMacAddress() {
        return this.macAddress;
    }

    public byte[] getIpAddress() {
        return ipAddress.getAddress();
    }

    public void work() {
        captor.loopPacket(-1, this);
    }

    public void sendData(byte[] data, byte[] dstMacAddr, short frameType) {
        if (data == null) return;
        Packet packet = new Packet();
        packet.data = data;
        /**
         * 0 - 5 dst mac addr
         * 6 - 11 src mac addr
         * 12 - 13 0x8086 arp 0x0800 ip
         */
        EthernetPacket ether = new EthernetPacket();
        ether.frametype = frameType;
        ether.src_mac = macAddress;
        ether.dst_mac = dstMacAddr;
        packet.datalink = ether;

        sender.sendPacket(packet);

        printToFile("debug.txt", dstMacAddr, macAddress, frameType, data);

    }

    private void printToFile(String name, byte[] dstMac, byte[] srcMac, short frameType, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(name)) {
            ByteBuffer buffer = ByteBuffer.wrap(new byte[2]);
            buffer.putShort(frameType);
            byte[] bufferArray = buffer.array();
            for (byte b : dstMac) {
                fos.write((Integer.toHexString(b) + " ").getBytes());
            }
            fos.write('\n');
            for (byte b : srcMac) {
                fos.write((Integer.toHexString(b) + " ").getBytes());
            }
            fos.write('\n');
            for (byte b : bufferArray) fos.write((Integer.toHexString(b) + " ").getBytes());
            fos.write('\n');
            for (byte b : data) fos.write((Integer.toHexString(b) + " ").getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void receivePacket(Packet packet) {
        this.pushToAllReceiver(packet);
    }
}
