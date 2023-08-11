/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2023 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for tests that simply indicates a follow-up is required.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@Inherited
@Target({
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.MODULE,
        ElementType.PARAMETER,
        ElementType.RECORD_COMPONENT,
        ElementType.TYPE
})
@Documented
@Retention(RetentionPolicy.CLASS)
public @interface FollowUpRequired {

    /**
     * A simple message indicating information about the follow-up.
     *
     * @return the follow-up information.
     */
    String value();

    /**
     * Defines the version the follow-up should be done by.
     *
     * @return the version the follow-up should be done by or an empty string if no version was specified
     */
    String version() default "";
}
