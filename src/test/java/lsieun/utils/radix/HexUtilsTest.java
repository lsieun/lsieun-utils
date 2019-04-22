package lsieun.utils.radix;

import org.junit.Assert;
import org.junit.Test;

public class HexUtilsTest {

    @Test
    public void testFromInt() {
        Assert.assertEquals("80", HexUtils.fromInt(128).toUpperCase());
        Assert.assertEquals("FF", HexUtils.fromInt(255).toUpperCase());
        Assert.assertEquals("0FFF", HexUtils.fromInt(0xFFF).toUpperCase());
        Assert.assertEquals("FFFF", HexUtils.fromInt(0xFFFF).toUpperCase());
        Assert.assertEquals("0FFFFF", HexUtils.fromInt(0xFFFFF).toUpperCase());
    }
}