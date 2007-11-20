package org.resteasy;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.core.PathSegment;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamExtractor extends StringParameterExtractor {
    public MatrixParamExtractor(Method method, String paramName, Class type, String defaultValue) {
        super(type, method, paramName, "@" + MatrixParam.class.getSimpleName(), defaultValue);
    }

    public Object extract(HttpInputMessage request) {
        for (PathSegment segment : request.getUri().getPathSegments())
        {
            List<String> list = segment.getMatrixParameters().get(paramName);
            if (list != null) return extractValues(list);
        }
        return extractValue(null);
    }
}