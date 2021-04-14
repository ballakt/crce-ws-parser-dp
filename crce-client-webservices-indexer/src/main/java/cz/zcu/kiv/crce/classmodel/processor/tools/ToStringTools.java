package cz.zcu.kiv.crce.classmodel.processor.tools;

import java.util.Collection;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.zcu.kiv.crce.classmodel.processor.Endpoint;

public class ToStringTools {
    private static ObjectMapper mapper = new ObjectMapper();

    public static String endpointsToJSON(Collection<Endpoint> collection) {

        String notIndentedJSON = "[";
        for (final Endpoint endpoint : collection) {
            notIndentedJSON += endpoint.toString() + ",";
        }
        notIndentedJSON = notIndentedJSON.substring(0, notIndentedJSON.length() - 1) + "]";
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mapper.readValue(notIndentedJSON, Object.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> String setToString(Set<T> set) {
        String stringified = "[";
        for (T item : set) {
            if (item instanceof String || item instanceof Enum || item instanceof Boolean) {
                stringified += objToString(item) + ",";
            } else {
                stringified += item.toString() + ",";
            }
        }
        if (stringified.length() > 1) {
            stringified = stringified.substring(0, stringified.length() - 1);
        }
        stringified += "]";
        return stringified;
    }

    public static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        return "\"" + obj + "\"";
    }

    public static String stringToString(String str) {
        if (str == null) {
            return null;
        }
        return "\"" + str + "\"";
    }
}
