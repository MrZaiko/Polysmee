package io.github.polysmee.agora;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccessTokenTest {
    private final String appId = "970CA35de60c44645bbae8a215061b33";
    private final String appCertificate = "5CFd2fd1755d40ecb72977518be15d3b";
    private final String channelName = "7d72365eb983485397e3e3f9d460bdda";
    private final String uid = "2882341273";
    private final int ts = 1111111;
    private final int salt = 1;
    private final int expireTimestamp = 1446455471;

    @Test
    public void testGenerateDynamicKey() throws Exception {
        String expected = "006970CA35de60c44645bbae8a215061b33IACV0fZUBw+72cVoL9eyGGh3Q6Poi8bgjwVLnyKSJyOXR7dIfRBXoFHlEAABAAAAR/QQAAEAAQCvKDdW";
        AccessToken token = new AccessToken(appId, appCertificate, channelName, uid);
        token.message.ts = ts;
        token.message.salt = salt;
        token.addPrivilege(AccessToken.Privileges.kJoinChannel, expireTimestamp);
        String result = token.build();
        assertEquals(expected, result);
    }

    @Test
    public void testAccessTokenWithIntUid() throws Exception {
        String expected =
                "006970CA35de60c44645bbae8a215061b33IACV0fZUBw+72cVoL9eyGGh3Q6Poi8bgjwVLnyKSJyOXR7dIfRBXoFHlEAABAAAAR/QQAAEAAQCvKDdW";
        AccessToken key = new AccessToken(appId, appCertificate, channelName, uid);
        key.message.salt = salt;
        key.message.ts = ts;
        key.message.messages.put((short) AccessToken.Privileges.kJoinChannel.intValue, expireTimestamp);
        String result = key.build();
        assertEquals(expected, result);
    }
}
