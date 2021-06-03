package io.github.polysmee.agora;

/**
 * Packable interface used for building the token
 * This class is fully provided by agora.io library and is used to generate the tokens
 * see https://docs.agora.io/en/Video/token_server?platform=Android for javadoc
 */
public interface Packable {
    ByteBuf marshal(ByteBuf out);
}
