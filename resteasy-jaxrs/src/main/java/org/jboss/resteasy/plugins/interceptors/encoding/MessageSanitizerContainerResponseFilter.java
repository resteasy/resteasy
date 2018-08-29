package org.jboss.resteasy.plugins.interceptors.encoding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.util.HttpResponseCodes;

/**
 * (RESTEASY-1485) Thwart select XSS attack by escaping special chars in
 * Exception message.
 *
 * User: rsearls
 * Date: 9/16/16
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
public class MessageSanitizerContainerResponseFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        if (HttpResponseCodes.SC_BAD_REQUEST == responseContext.getStatus()) {
            Object entity = responseContext.getEntity();
            if (entity != null && entity instanceof String) {
                ArrayList contentTypes = (ArrayList)responseContext.getHeaders().get("Content-Type");
                if (contentTypes != null  && containsHtmlText(contentTypes)) {
                    String escapedMsg = escapeXml((String) entity);
                    responseContext.setEntity(escapedMsg);
                }
            }
        }
    }


    // set of replacement chars to hex encoding
    private static final HashMap<String, String> replacementMap;
    static {
        replacementMap = new HashMap<String, String>();
        replacementMap.put("/", "&#x2F;");
        replacementMap.put("<", "&lt;");
        replacementMap.put(">", "&gt;");
        replacementMap.put("&", "&amp;");
        replacementMap.put("\"", "&quot;");
        replacementMap.put("'", "&#x27;");
    }

    /**
     * Replace char with the hex encoding
     * @param str
     * @return
     */
    private String escapeXml(String str) {
        StringBuilder sb = new StringBuilder();
        if (!str.isEmpty()) {
            // the vertical bar, |, is a special regular expression char that
            // causes splitting on individual characters.
            for (String key : str.split("|")) {
                String value = replacementMap.get(key);
                if (value == null) {
                    sb.append(key);
                } else {
                    sb.append(value);
                }

            }
        }
        return sb.toString();
    }

    private boolean containsHtmlText(ArrayList<Object> list) {
        for (Object o : list) {
           if (o instanceof String) {
              String mediaType = (String) o;
              String[] partsType = mediaType.split("/");
              if (partsType.length >= 2) {
                 String[] partsSubtype = partsType[1].split(";");
                 if (partsType[0].trim().equalsIgnoreCase("text") && 
                       partsSubtype[0].trim().toLowerCase().equals("html")) {
                    return true;
                 }
              }
           }
        }
        return false;
     }
}

