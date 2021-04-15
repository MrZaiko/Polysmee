package io.github.polysmee.agora;

/**
 * Packable interface used for building the token
 */
public interface Packable {
    ByteBuf marshal(ByteBuf out);
}
