package com.jeefix.iot.milight.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests of {@link HexUtils}
 * Created by Maciej Iskra (iskramac) on 2017-01-03.
 */
@RunWith(JUnit4.class)
public class HexUtilsTest {
    @Test
    public void shouldConvertHexToString() {
        byte[] hexArray = new byte[]{0x00, 0x11, 0x21, 0x71, 0x1b, 0x2a};
        String hexArrayString = "00 11 21 71 1B 2A";
        String convertedHexArrayString = HexUtils.getHexAsString(hexArray);

        assertEquals(hexArrayString, convertedHexArrayString);
    }

    @Test
    public void shouldConvertStringToHex() {
        String hexArrayString = "00 11 21 71 1B 2A";
        byte[] hexArray = new byte[]{0x00, 0x11, 0x21, 0x71, 0x1b, 0x2a};

        byte[] convertedHexArray = HexUtils.getStringAsHex(hexArrayString);

        assertArrayEquals(hexArray, convertedHexArray);
    }

    @Test
    public void shouldNotMutateData() {
        String hexArrayString = "00 11 21 71 1B 2A";
        String hexArrayStringConverted = hexArrayString;

        for (int i = 0; i < 100; i++) {
            byte[] tempByte = HexUtils.getStringAsHex(hexArrayStringConverted);
            hexArrayStringConverted = HexUtils.getHexAsString(tempByte);
        }
        assertEquals(hexArrayString, hexArrayStringConverted);
    }
}
