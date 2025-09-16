/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.links.test;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.ws.rs.SeBootstrap;
import jakarta.ws.rs.core.SecurityContext;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.plugins.server.embedded.SimplePrincipal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import dev.resteasy.client.util.authentication.UserCredentials;
import dev.resteasy.client.util.authentication.basic.BasicAuthorizationFilter;
import dev.resteasy.embedded.server.UndertowConfigurationOptions;
import dev.resteasy.junit.extension.api.ConfigurationProvider;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.LoginConfig;

abstract class AbstractTestSecureLinks {

    public static class TestConfigurationProvider implements ConfigurationProvider {
        @Override
        public SeBootstrap.Configuration getConfiguration() {
            final DeploymentInfo deploymentInfo = new DeploymentInfo();
            deploymentInfo.setLoginConfig(new LoginConfig("BASIC", "default"));
            deploymentInfo.setIdentityManager(new TestingIdentityManager());
            return SeBootstrap.Configuration.builder()
                    .property(UndertowConfigurationOptions.DEPLOYMENT_INFO, deploymentInfo)
                    .build();
        }
    }

    private static class TestingIdentityManager implements IdentityManager {
        @Override
        public Account verify(final Account account) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Account verify(final String id, final Credential credential) {
            return createAccount(id);
        }

        @Override
        public Account verify(final Credential credential) {
            throw new UnsupportedOperationException();
        }

        private Account createAccount(final String username) {
            return new Account() {
                @Override
                public Principal getPrincipal() {
                    return new SimplePrincipal(username);
                }

                @Override
                public Set<String> getRoles() {
                    switch (username) {
                        case "admin":
                            return Set.of("admin");
                        case "power-user":
                            return Set.of("power-user");
                        default:
                            return Set.of();
                    }
                }
            };
        }
    }

    @Inject
    private ResteasyWebTarget webTarget;

    @ParameterizedTest
    @ValueSource(strings = { "admin", "power-user", "user" })
    public void testSecureLinksAdmin(String type) {
        final SecurityContext securityContext = getSecurityContext(type);
        ResteasyContext.pushContext(SecurityContext.class, securityContext);

        BookStoreService client;
        final Set<String> expectedLinks;
        switch (type) {
            case "admin":
                expectedLinks = Set.of("add", "update", "list", "self", "remove");
                break;
            case "power-user":
                expectedLinks = Set.of("add", "update", "list", "self");
                break;
            case "user":
                expectedLinks = Set.of("list", "self");
                break;
            default:
                Assertions.fail("Invalid type: " + type);
                throw new AssertionError(); // this should never happen
        }
        client = webTarget.register(BasicAuthorizationFilter.create(UserCredentials.clear(type, new char[] { 'a', 's', 'd' })))
                .proxy(BookStoreService.class);
        Book book = client.getBookXML("foo");
        checkBookLinks1(book, expectedLinks);
    }

    private static SecurityContext getSecurityContext(final String type) {
        final Principal principal = new SimplePrincipal(type);
        return new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public boolean isUserInRole(final String role) {
                return SecurityFunctions.hasRole(role);
            }

            @Override
            public boolean isSecure() {
                return true;
            }

            @Override
            public String getAuthenticationScheme() {
                return BASIC_AUTH;
            }
        };
    }

    private void checkBookLinks1(Book book, Collection<String> expectedLinks) {
        Assertions.assertNotNull(book);
        Assertions.assertEquals("foo", book.getTitle());
        Assertions.assertEquals("bar", book.getAuthor());
        RESTServiceDiscovery links = book.getRest();
        Assertions.assertNotNull(links);
        Assertions.assertEquals(expectedLinks.size(), links.size());
        for (String expectedLink : expectedLinks) {
            Assertions.assertNotNull(links.getLinkForRel(expectedLink));
        }
    }
}
