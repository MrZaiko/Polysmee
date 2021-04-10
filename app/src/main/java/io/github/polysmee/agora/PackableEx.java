package io.github.polysmee.agora;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
