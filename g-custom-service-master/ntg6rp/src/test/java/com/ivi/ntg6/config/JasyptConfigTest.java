package com.ivi.ntg6.config;

import junit.framework.TestCase;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptConfigTest extends TestCase {
    public void testJasyptDecoding() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword("gcustom");
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setPoolSize(1);
        encryptor.setConfig(config);

        String str = encryptor.decrypt("885KhBdXXcgqqbN0cWKJ7KoECCVrT+o6");

        System.out.println("decripted : " + str);
    }
}