package protocol;

import datalinklayer.DataLinkLayer;
import jpcap.PacketReceiver;
import jpcap.packet.ARPPacket;
import jpcap.packet.Packet;
import util.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * create by srdczk 20-2-11
 */
public class ARPProtocol implements PacketReceiver {

    private static final int ARP_OPCODE_START = 20,
            ARP_SENDER_MAC_START = 22,
            ARP_SENDER_IP_START = 28,
            ARP_TARGET_IP_START = 38;

    @Override
    public void receivePacket(Packet packet) {
        // 解析返回的数据包, 输出 mac 地址信息
        // 分析返回 的 mac 地址
        byte[] header = packet.header;
        // 确定 操作码 opCode == reply
        byte[] opCode = new byte[2];
        System.arraycopy(header, ARP_OPCODE_START, opCode, 0, opCode.length);
        short op = ByteBuffer.wrap(opCode).getShort();
//        if (op != ARPPacket.ARP_REPLY) {
//            System.out.println("Not a reply");
//            return;
//        }
//        byte[] ip = DataLinkLayer.getInstance().getIpAddress();
//        for (int i = 0; i < 4; i++) {
//            if (ip[i] != header[ARP_TARGET_IP_START + i]) {
//                System.out.println("Not your IP");
//                return;
//            }
//        }
        byte[] senderIP = new byte[4];
        System.arraycopy(header, ARP_SENDER_IP_START, senderIP, 0, senderIP.length);
        byte[] senderMac = new byte[4];
        System.arraycopy(header, ARP_SENDER_MAC_START, senderMac, 0, senderMac.length);
        System.out.println("Receive:   IP: " + Util.decodeIp(senderIP) + "   MAC: " + Util.decodeMac(senderMac));
    }

    public void sendData(byte[] ip) {
        DataLinkLayer instance = DataLinkLayer.getInstance();
        byte[] data = new byte[28];
        // 广播 请求 ip 地址 mac 地址 的 数据包
        byte[] broadcast = new byte[] {(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};
        // 设置 hardware type 字段
        ByteBuffer buffer = ByteBuffer.allocate(2);
        // 转换为大端 字节序
        buffer.order(ByteOrder.BIG_ENDIAN);
        // 写死
        buffer.putShort(ARPPacket.HARDTYPE_ETHER);
        int cnt = 0;
        for (int i = 0; i < buffer.array().length; i++) data[cnt++] = buffer.array()[i];
        buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        // 写死
        buffer.putShort(ARPPacket.PROTOTYPE_IP);
        for (int i = 0; i < buffer.array().length; i++) data[cnt++] = buffer.array()[i];
        data[cnt++] = 6;
        data[cnt++] = 4;
        buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putShort(ARPPacket.ARP_REQUEST);
        for (int i = 0; i < buffer.array().length; i++) data[cnt++] = buffer.array()[i];
        byte[] macAddr = instance.getMacAddress();
        for (byte aMacAddr : macAddr) data[cnt++] = aMacAddr;
        byte[] deviceIp = instance.getIpAddress();
        for (byte aDeviceIp : deviceIp) data[cnt++] = aDeviceIp;
        for (byte aBroadcast : broadcast) data[cnt++] = aBroadcast;
        for (byte anIp : ip) data[cnt++] = anIp;
        instance.sendData(data, broadcast, (short) 0x8086);
    }

}
