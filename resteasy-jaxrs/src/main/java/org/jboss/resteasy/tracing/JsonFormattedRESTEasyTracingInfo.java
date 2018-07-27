package org.jboss.resteasy.tracing;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public class JsonFormattedRESTEasyTracingInfo extends RESTEasyTracingInfo {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public String[] getMessages() {
        return new String[]{jsonb.toJson(messageList)};
    }

}
