package io.github.polysmee.agora;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.TreeMap;

/**
 * ByteBuffer data structure
 * This class is fully provided by agora.io library and is used to generate the tokens
 * see https://docs.agora.io/en/Video/token_server?platform=Android for javadoc
 */
public class ByteBuf {
    private ByteBuffer buffer = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);

    public ByteBuf() {
    }

    public ByteBuf(byte[] bytes) {
        this.buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    }

    public byte[] asBytes() {
        byte[] out = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(out, 0, out.length);
        return out;
    }

    // packUint16
    public ByteBuf put(short v) {
        buffer.putShort(v);
        return this;
    }

    public ByteBuf put(byte[] v) {
        put((short) v.length);
        buffer.put(v);
        return this;
    }

    // packUint32
    public ByteBuf put(int v) {
        buffer.putInt(v);
        return this;
    }


    public ByteBuf putIntMap(TreeMap<Short, Integer> extra) {
        put((short) extra.size());

        for (Map.Entry<Short, Integer> pair : extra.entrySet()) {
            put(pair.getKey());
            put(pair.getValue());
        }

        return this;
    }

    public short readShort() {
        return buffer.getShort();
    }


    public int readInt() {
        return buffer.getInt();
    }

    public byte[] readBytes() {
        short length = readShort();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }


    public TreeMap<Short, Integer> readIntMap() {
        TreeMap<Short, Integer> map = new TreeMap<>();

        short length = readShort();

        for (short i = 0; i < length; ++i) {
            short k = readShort();
            Integer v = readInt();
            map.put(k, v);
        }

        return map;
    }
}

