package org.resteasy;

import org.resteasy.spi.HttpInputMessage;

import javax.ws.rs.HeaderParam;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamExtractor extends StringParameterExtractor implements ParameterExtractor {

    public HeaderParamExtractor(Method method, String header, Class type, String defaultValue) {
        super(type, method, header, "@" + HeaderParam.class.getSimpleName(), defaultValue);
    }

    public Object extract(HttpInputMessage request) {
        List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
        return extractValues(list);
    }
}
