/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2024 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.util;

import java.util.function.Predicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class HeaderHelperTest {

    public enum TestHeader {
        VALIDATE_TRIMMED(" application/json  ", null, "application/json"::equals),
        VALIDATE_MULTIPLE1("application/json, application/xml", ",", "application/json"::equals),
        VALIDATE_MULTIPLE2("application/json,application/xml", ",", "application/xml"::equals),
        VALIDATE_MULTIPLE_NULL_REGEX("application/json,application/xml", null, "application/json,application/xml"::equals),
        VALIDATE_MULTIPLE_REGEX("application/json|application/xml", "\\|", "application/xml"::equals),
        ;

        private final String value;
        private final String regex;
        private final Predicate<String> predicate;

        TestHeader(final String value, final String regex, final Predicate<String> predicate) {
            this.value = value;
            this.regex = regex;
            this.predicate = predicate;
        }
    }

    @ParameterizedTest
    @EnumSource
    public void containsHeaderString(final TestHeader header) {
        Assertions.assertTrue(HeaderHelper.containsHeaderString(header.value, header.regex, header.predicate),
                () -> String.format("Validation failed on %s", header));
    }
}
