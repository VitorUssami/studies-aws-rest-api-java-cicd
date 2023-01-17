package com.studies.aws.sam.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;

public class Utils {

    public static String decryptKey(String name) {
        System.out.println("Decrypting key");
        byte[] encryptedKey = Base64.decode(System.getenv(name));

        AWSKMS client = AWSKMSClientBuilder.defaultClient();

        DecryptRequest request = new DecryptRequest()
                .withCiphertextBlob(ByteBuffer.wrap(encryptedKey));

        ByteBuffer plainTextKey = client.decrypt(request).getPlaintext();
        return new String(plainTextKey.array(), Charset.forName("UTF-8")).trim();
    }
}
