package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pulsar.common.util.ObjectMapperFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Helpers {

    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static Map<String, String> configureFromJsonBASE64String(String authParamsString) throws IOException {
        String authDecoded = new String(Base64.getDecoder().decode(authParamsString));
        ObjectMapper jsonMapper = ObjectMapperFactory.create();
        return jsonMapper.readValue(authDecoded, new TypeReference<HashMap<String, String>>() {
        });
    }

    public static String toBase64(String data){
        return Base64.getEncoder().encodeToString(data.getBytes());
    }
    public static String toBase64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static String fromBase64(String data){
        return new String(Base64.getDecoder().decode(data));
    }

    public static byte[] fromBase64ToBytes(String data){
        return Base64.getDecoder().decode(data);
    }

}
