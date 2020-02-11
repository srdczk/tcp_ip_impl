package util;

import jpcap.PacketReceiver;
import jpcap.packet.Packet;

/**
 * create by srdczk 20-2-11
 */
// 观察者模式
public interface IPacketProvider {
    void registerPacketReceiver(PacketReceiver receiver);
    void pushToAllReceiver(Packet packet);
}
