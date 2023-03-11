package org.jboss.resteasy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use on async streams to push them to the client as they become available
 * over the OutputStream, rather than collected into collections.
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Stream {
    enum MODE {
        RAW,
        GENERAL
    };

    String INCLUDE_STREAMING_PARAMETER = "streaming";

    MODE value() default MODE.GENERAL;

    boolean includeStreaming() default false;
}
