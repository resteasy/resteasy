package org.resteasy.util;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MediaTypeHelper {
    public static MediaType getConsumes(Class declaring, Method method) {
        ConsumeMime consume = method.getAnnotation(ConsumeMime.class);
        if (consume == null) {
            consume = (ConsumeMime) declaring.getAnnotation(ConsumeMime.class);
        }
        if (consume == null) return null;
        return MediaType.parse(consume.value()[0]);
    }

    public static MediaType getProduces(Class declaring, Method method) {
        ProduceMime consume = method.getAnnotation(ProduceMime.class);
        if (consume == null) {
            consume = (ProduceMime) declaring.getAnnotation(ProduceMime.class);
        }
        if (consume == null) return null;
        return MediaType.parse(consume.value()[0]);
    }
}
