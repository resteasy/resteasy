package org.resteasy.util;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FindAnnotation {
    public static <T> T findAnnotation(Annotation[] searchList, Class<T> annotation) {
        for (Annotation ann : searchList) {
            if (ann.annotationType().equals(annotation)) return (T) ann;
        }
        return null;
    }
}
