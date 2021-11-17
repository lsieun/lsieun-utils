package lsieun.utils.number;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerUtils {
    public static byte[] toBytes(final int i) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bao);
        try {
            out.writeInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bao.toByteArray();
    }

    public static int fromBytes(byte[] bytes, int defaultValue) {
        if(bytes == null || bytes.length < 1) return defaultValue;
        if(bytes.length != 4) {
            System.out.println("IntegerUtils#fromBytes(byte[],int): bytes' length is not 4. It's length is " + bytes.length);
            System.out.println("bytes' Hexcode = '" + HexUtils.fromBytes(bytes) + "'");
        }

        ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
        DataInputStream in = new DataInputStream(bai);
        try {
            return in.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
