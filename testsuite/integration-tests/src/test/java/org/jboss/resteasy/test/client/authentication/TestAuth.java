/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
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

package org.jboss.resteasy.test.client.authentication;

import java.nio.charset.StandardCharsets;

import dev.resteasy.client.util.authentication.UserCredentials;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
class TestAuth {

    static final String USER_1 = "test-user";
    static final String PASSWORD_1 = "test.12345";
    static final String USER_2 = "鹰爪";
    static final String PASSWORD_2 = "鹰爪功夫";
    static final UserCredentials CREDENTIALS_USER_1 = UserCredentials.clear(USER_1, PASSWORD_1.toCharArray());
    static final UserCredentials CREDENTIALS_USER_2 = UserCredentials.of(USER_2, PASSWORD_2.getBytes(StandardCharsets.UTF_8));
    static final String SECURITY_DOMAIN = "test-security-domain";
    static final String REALM_NAME = "test-realm";

    static final String BASIC_AUTH_HEADER_USER_1 = "Basic dGVzdC11c2VyOnRlc3QuMTIzNDU=";
    static final String BASIC_AUTH_HEADER_USER_2 = "Basic 6bmw54iqOum5sOeIquWKn+Wkqw==";

    private static final String WEB_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<web-app version=\"5.0\" xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
            "   xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_5_0.xsd\">\n" +
            "    <security-constraint>\n" +
            "        <web-resource-collection>\n" +
            "            <url-pattern>/user/*</url-pattern>\n" +
            "        </web-resource-collection>\n" +
            "        <auth-constraint>\n" +
            "            <role-name>user</role-name>\n" +
            "        </auth-constraint>\n" +
            "    </security-constraint>\n" +
            "    <security-role>\n" +
            "        <role-name>user</role-name>\n" +
            "    </security-role>\n" +
            "    <context-param>\n" +
            "        <param-name>resteasy.role.based.security</param-name>\n" +
            "        <param-value>true</param-value>\n" +
            "    </context-param>\n" +
            "    <login-config>\n" +
            "        <auth-method>%s</auth-method>\n" +
            "        <realm-name>%s</realm-name>\n" +
            "    </login-config>\n" +
            "</web-app>";
    private static final String JBOSS_WEB_XML = "<?xml version=\"1.0\"?>\n" +
            "<jboss-web>\n" +
            "    <security-domain>%s</security-domain>\n" +
            "</jboss-web>";

    /**
     * Creates a {@code web.xml} with the {@code auth-method} element set the value passed.
     *
     * @param authMethod the authentication method for the {@code web.xml}
     *
     * @return a {@code web.xml}
     */
    static Asset createWebXml(final String authMethod) {
        return new StringAsset(String.format(WEB_XML, authMethod, REALM_NAME));
    }

    /**
     * Creates a {@code jboss-web.xml} with the {@code security-domain} element set to {@link #SECURITY_DOMAIN}.
     *
     * @return a {@code jboss-web.xml}
     */
    static Asset createJBossWebXml() {
        return new StringAsset(String.format(JBOSS_WEB_XML, SECURITY_DOMAIN));
    }
}
