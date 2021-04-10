package io.github.polysmee.agora;

public interface Packable {
    ByteBuf marshal(ByteBuf out);
}
