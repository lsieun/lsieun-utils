package lsieun.utils.core.number;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexUtilsTest {

    @Test
    public void testFromInt() {
        assertEquals("80", HexUtils.fromInt(128).toUpperCase());
        assertEquals("FF", HexUtils.fromInt(255).toUpperCase());
        assertEquals("0FFF", HexUtils.fromInt(0xFFF).toUpperCase());
        assertEquals("FFFF", HexUtils.fromInt(0xFFFF).toUpperCase());
        assertEquals("0FFFFF", HexUtils.fromInt(0xFFFFF).toUpperCase());
    }

    @Test
    public void testGetPrettyFormat() {
        String hex_str = "0A0B0C0D0E0F0A0B0C0D0E0F0A0B0C0D0E0F0A0B0C0D0E0F0A0B0C0D0E0F0A0B0C0D0E0F";
        String pretty_str = HexUtils.getPrettyFormat(hex_str);
        System.out.println(pretty_str);
    }

    @Test
    public void testFormat() {
        byte[] bytes = new byte[156];
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) i;
        }

        String result = HexUtils.format(bytes, HexFormat.FORMAT_FF_SPACE_FF_16);
        System.out.println(result);
    }

    @Test
    public void testParse() {
        String str = "01:23:45:67:89:ab:cd:ef";
        byte[] bytes = HexUtils.parse(str, HexFormat.FORMAT_FF_COLON_FF);
        String value = HexUtils.format(bytes, HexFormat.FORMAT_FF_SPACE_FF);
        System.out.println(value);
    }
}