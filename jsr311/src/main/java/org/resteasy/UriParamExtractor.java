package org.resteasy;

import org.resteasy.spi.HttpInput;

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

    public Object extract(HttpInput request) {
        List<String> list = request.getUri().getTemplateParameters().get(paramName);
        return extractValues(list);
    }
}
