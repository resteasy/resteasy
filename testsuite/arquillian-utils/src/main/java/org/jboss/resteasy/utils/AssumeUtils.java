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

package org.jboss.resteasy.utils;

import org.junit.jupiter.api.Assumptions;

/**
 * Utilities for test assumptions.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AssumeUtils {

    /**
     * Checks to see if the {@link javax.xml.XMLConstants#FEATURE_SECURE_PROCESSING} can be set to {@code false}. This
     * is not allowed when the security manager is enabled.
     * From the <a href=
     * "https://docs.oracle.com/en/java/javase/11/security/java-api-xml-processing-jaxp-security-guide.html#GUID-88B04BE2-35EF-4F61-B4FA-57A0E9102342">docs</a>:
     * <p>
     * <i>While FSP can be turned on and off through factories, it is always on when a Java Security Manager is present and
     * cannot be turned off.</i>
     * </p>
     */
    public static void canDisableFeatureSecureProcessing() {
        assumeSecurityManagerDisabled(
                "Cannot disable the XMLConstants.FEATURE_SECURE_PROCESSING when the security manager is enabled");
    }

    /**
     * Checks if the {@code security.manager} system property has been set.
     *
     * @param msg the message for the assumption
     */
    public static void assumeSecurityManagerDisabled(final String msg) {
        Assumptions.assumeTrue(System.getProperty("security.manager") == null, msg);
    }
}
