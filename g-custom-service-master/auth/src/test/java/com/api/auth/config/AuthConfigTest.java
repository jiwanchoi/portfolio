package com.api.auth.config;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class AuthConfigTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(AuthConfigTest.class);

    public void testBcrypt() {
        final String[] src = {
                "4a182199-9315-4896-8db9-ce330ca83adf",
                "cfb931d4-bfa4-4301-99f5-593b71757857",
                "62872157-1408-405a-94fa-b83947f6663c",
                "540d3efd-afa7-4973-a470-07a937a704b5",
                "gcustom",
                "seesun0601"
        };
        // {bcrypt}$2a$10$kYNjRhfqCHhYmyS1qmsDGuAnzoRl97NQirr08N3XACFrG17Egi8rq
        final String[] hashedString = {
                "$2a$10$kYNjRhfqCHhYmyS1qmsDGuAnzoRl97NQirr08N3XACFrG17Egi8rq",
                "$2a$10$6w45PHkVgH3ckfwrTzlQ9.nwW4UwQ./zyEbRudBDxx9wiLUkGs1ni"
        };

        logger.debug("Source String : " + src);
        logger.debug("hashed String : " + hashedString);
        for (int i = 0; i < src.length; i++) {
            String newhash = BCrypt.hashpw(src[i], BCrypt.gensalt());
            logger.debug("new Hashed string : " + newhash);
            for (int j = 0; j < hashedString.length; j++) {
                boolean result = BCrypt.checkpw(src[i], hashedString[j]);
                if (result) {
                    logger.debug(src[i] + " is same as " + hashedString[j]);
                }

            }

        }
    }
}