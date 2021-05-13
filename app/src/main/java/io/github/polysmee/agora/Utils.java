package io.github.polysmee.agora;

import org.apache.commons.codec.binary.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.zip.CRC32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Util class for generating tokens
 */
public class Utils {

    public static byte[] hmacSign(String keyString, byte[] msg) throws InvalidKeyException, NoSuchAlgorithmException {
        SecretKeySpec keySpec = new SecretKeySpec(keyString.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(msg);
    }

    public static byte[] pack(PackableEx packableEx) {
        ByteBuf buffer = new ByteBuf();
        packableEx.marshal(buffer);
        return buffer.asBytes();
    }


    public static String base64Encode(byte[] data) {
        byte[] encodedBytes = Base64.encodeBase64(data);
        return new String(encodedBytes);
    }

    public static int crc32(String data) {
        // get bytes from string
        byte[] bytes = data.getBytes();
        return crc32(bytes);
    }

    public static int crc32(byte[] bytes) {
        CRC32 checksum = new CRC32();
        checksum.update(bytes);
        return (int) checksum.getValue();
    }

    public static int getTimestamp() {
        return (int) ((new Date().getTime()) / 1000);
    }

    public static int randomInt() {
        return new SecureRandom().nextInt();
    }

    public static boolean isUUID(String uuid) {
        if (uuid.length() != 32) {
            return false;
        }

        return uuid.matches("\\p{XDigit}+");
    }
}

