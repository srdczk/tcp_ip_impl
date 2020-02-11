package util;

/**
 * create by srdczk 20-2-11
 */
public class Util {

    public static String decodeIp(byte[] ip) {
        StringBuilder res = new StringBuilder();
        int cnt = 0;
        for (byte i : ip) {
            if (cnt++ > 0) res.append(":");
            res.append((i & 0xff));
        }
        return res.toString();
    }

    public static String decodeMac(byte[] mac) {
        StringBuilder res = new StringBuilder();
        int cnt = 0;
        for (byte i : mac) {
            if (cnt++ > 0) res.append(":");
            res.append(Integer.toHexString(i));
        }
        return res.toString();
    }

}
