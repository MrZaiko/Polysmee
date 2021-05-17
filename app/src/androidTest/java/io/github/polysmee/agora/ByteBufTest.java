package io.github.polysmee.agora;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class ByteBufTest {


    @Test
    public void readShortTest() {
        byte[] input = new byte[2];
        input[0] = 5;
        input[1] = 2;
        ByteBuf byteBuf = new ByteBuf(input);
        assertEquals((2 << 8) | 5, byteBuf.readShort());
    }

    @Test
    public void readIntTest() {
        byte[] input = new byte[4];
        input[0] = 5;
        input[1] = 2;
        input[2] = 3;
        input[3] = 1;
        ByteBuf byteBuf = new ByteBuf(input);
        assertEquals((1 << 24) | (3 << 16) | (2 << 8) | 5, byteBuf.readInt());
    }

    @Test
    public void readBytesLengthTestSize() {
        byte[] input = new byte[1024];
        for (int i = 0; i < input.length; ++i) {
            input[i] = (byte) (i);
        }
        ByteBuf byteBuf = new ByteBuf(input);
        byte[] result = byteBuf.readBytes();
        assertEquals(256, result.length);
    }

    @Test
    public void readIntMapTestSize(){
        byte[] input = new byte[1024];
        for (int i = 0; i < input.length; ++i) {
            input[i] = (byte) (i);
        }
        ByteBuf byteBuf = new ByteBuf(input);
        Map<Short, Integer> result = byteBuf.readIntMap();
        assertEquals(256,result.size());
    }
}
