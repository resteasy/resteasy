package org.jboss.resteasy.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class IsHttpMethod {
    public static Set<String> getHttpMethods(Method method) {
        HashSet<String> methods = new HashSet<String>();
        for (Annotation annotation : method.getAnnotations()) {
            HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
            if (http != null)
                methods.add(http.value());
        }
        if (methods.size() == 0)
            return null;
        return methods;
    }
}
