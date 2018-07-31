package org.jboss.resteasy.tracing.api.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.tracing.api.RESTEasyTracingInfoFormat;

public class Jackson2JsonFormatRESTEasyTracingInfo extends TextBasedRESTEasyTracingInfo {

    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String[] getMessages() {
        try {
            return new String[]{mapper.writeValueAsString(messageList)};
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(RESTEasyTracingInfoFormat format) {
        if (format.equals(RESTEasyTracingInfoFormat.JSON))
            return true;
        else
            return false;
    }

}
