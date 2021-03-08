package com.ivi.openlr.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;

public class Debug {
    /**
     * lineLocation post method의 request parameter 들을 디버깅, logger에 출력 및 문자열 반환
     * @param logger
     * @param wps
     * @param st
     * @param dt
     * @param crs
     * @param requestBody
     * @return
     */
    public static String debugPostLineLocation(final Logger logger, String wps, final String st, String dt, String crs, JsonNode requestBody) {
        if (logger.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("===========DEBUG REQUEST - START ================%s", System.lineSeparator()));
            sb.append(String.format(" st = %s%s", st, System.lineSeparator()));
            sb.append(String.format(" dt = %s%s", dt, System.lineSeparator()));
            if (wps != null)
                sb.append(String.format(" wps = %s%s", wps, System.lineSeparator()));
            sb.append(String.format(" crs = %s%s", crs, System.lineSeparator()));
            if (requestBody != null && !requestBody.isNull())
                sb.append(String.format(" body => \n%s%s", requestBody.toPrettyString(), System.lineSeparator()));
            sb.append(String.format("===========DEBUG REQUEST - END ================%s", System.lineSeparator()));

            if (logger != null) {
                logger.debug(sb.toString());
            }
            return sb.toString();
        }
        return "";
    }

    /**
     * Base64로 인코딩 OpenLR Binary Stream
     * @param logger
     * @param base64EncodedBinary
     * @return
     */
    public static String debugBase64BinaryString(final Logger logger, final String base64EncodedBinary) {
        if (!logger.isDebugEnabled()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format("========DEBUG Request - Start========="));
        result.append(String.format("Base64 encoded openlr binary stream : [%s]", base64EncodedBinary));

        logger.debug(result.toString());
        return result.toString();
    }
}
