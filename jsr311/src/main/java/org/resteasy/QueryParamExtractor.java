package org.resteasy;

import org.resteasy.spi.HttpInput;

import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamExtractor extends StringParameterExtractor {


    public QueryParamExtractor(Method method, String paramName, Class type, String defaultValue) {
        super(type, method, paramName, "@" + QueryParam.class.getSimpleName(), defaultValue);
    }

    public Object extract(HttpInput request) {
        List<String> list = request.getParameters().get(paramName);
        return extractValues(list);
    }

}
