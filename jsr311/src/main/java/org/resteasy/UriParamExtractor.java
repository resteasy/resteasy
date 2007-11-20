package org.resteasy;

import javax.ws.rs.UriParam;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriParamExtractor extends StringParameterExtractor {
    public UriParamExtractor(Method method, String paramName, Class type, String defaultValue) {
        super(type, method, paramName, "@" + UriParam.class.getSimpleName(), defaultValue);
    }

    public Object extract(HttpInputMessage request) {
        List<String> list = request.getUri().getTemplateParameters().get(paramName);
        return extractValues(list);
    }
}
