package entry;

import datalinklayer.DataLinkLayer;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import protocol.ARPProtocol;

import java.io.IOException;
import java.net.Inet4Address;

/**
 * create by srdczk 20-2-11
 */
public class ProtocolEntry {
    public static void main(String[] args) throws IOException {
        ARPProtocol protocol = new ARPProtocol();
        DataLinkLayer.getInstance().registerPacketReceiver(protocol);
        NetworkInterface[] deviceList = JpcapCaptor.getDeviceList();
        NetworkInterface workDevice = null;
        for (NetworkInterface device : deviceList) {
            boolean x = false;
            for (NetworkInterfaceAddress address : device.addresses) {
                if (!(address.address instanceof Inet4Address)) {
                    continue;
                }
                x = true;
                workDevice = device;
            }
            if (x) break;
        }
        DataLinkLayer.getInstance().initWithOpenDevice(workDevice);
        assert workDevice != null;
        protocol.sendData(new byte[] {(byte) 192, (byte) 168, 0, 1});
        DataLinkLayer.getInstance().work();
    }
}
