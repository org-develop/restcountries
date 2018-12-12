package com.ccmsd.restcountries.Util;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;


public class PojoUtil {

    private static final Logger logger = LoggerFactory.getLogger(PojoUtil.class);;

    /**
     * Method that can be used to serialize any Java value as a String. Doesn't Throw exception as used for logging
     * purpose
     */

    public static String pojoToJson(Object request) {
        String result = null;
        try {
            if (request != null) {
                ObjectMapper mapper = new ObjectMapper();
                result = mapper.writeValueAsString(request);
            }
        } catch (Exception e) {
            logger.debug("Failed convert POJO to Json");
        }
        return result;
    }

    /**
     * Method to deserialize JSON content from given JSON content String.
     *
     * @throws IOException
     *             if I/O problem
     */
    public static <T> Object jsonToPojo(String content, Class<T> class1) throws IOException {
        if (StringUtils.isEmpty(content.trim())) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content, class1);
    }
}
