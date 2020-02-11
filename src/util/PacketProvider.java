package util;

import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * create by srdczk 20-2-11
 */
public class PacketProvider implements IPacketProvider {

    // 存放 PakcetReceiver 的List
    private List<PacketReceiver> receiverList = new ArrayList<>();

    @Override
    public void registerPacketReceiver(PacketReceiver receiver) {
        if (!receiverList.contains(receiver)) receiverList.add(receiver);
    }

    @SuppressWarnings("unused")
    public void pushToAllReceiver(Packet packet) {
        for (PacketReceiver receiver : receiverList) {
            receiver.receivePacket(packet);
        }
    }
}
