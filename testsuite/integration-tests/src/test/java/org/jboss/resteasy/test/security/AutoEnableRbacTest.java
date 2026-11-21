/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.security;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.as.arquillian.api.ServerSetup;
import org.jboss.resteasy.setup.AbstractUsersRolesSecurityDomainSetup;
import org.jboss.resteasy.setup.LoggingSetupTask;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.security.resource.AutoEnableRbacResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

import dev.resteasy.client.util.authentication.HttpAuthenticators;
import dev.resteasy.client.util.authentication.UserCredentials;

/**
 * Integration tests for RESTEASY-3789: {@code RoleBasedSecurityFeature} must be registered by
 * default (i.e. when {@code resteasy.role.based.security} is absent), and must be suppressible
 * by explicitly setting the property to {@code false}.
 *
 * <h2>Test matrix</h2>
 * <ul>
 * <li>{@link #rbac_autoEnabled_withRolesAllowed_wrongRole_returns403()}</li>
 * <li>{@link #rbac_autoEnabled_withRolesAllowed_correctRole_returns200()}</li>
 * <li>{@link #rbac_autoEnabled_withDenyAll_returns403()}</li>
 * <li>{@link #rbac_autoEnabled_withPermitAll_returns200()}</li>
 * <li>{@link #rbac_explicitFalse_annotationsIgnored_returns200()}</li>
 * <li>{@link #rbac_explicitTrue_stillWorks_returns403()}</li>
 * <li>{@link #rbac_explicitFalse_warnLogged()}</li>
 * </ul>
 *
 * <h2>Deployments</h2>
 * <ul>
 * <li>{@value #DEPLOY_DEFAULT} — no {@code resteasy.role.based.security} (RBAC on by default)</li>
 * <li>{@value #DEPLOY_FALSE} — {@code resteasy.role.based.security=false} (opt-out)</li>
 * <li>{@value #DEPLOY_TRUE} — {@code resteasy.role.based.security=true} (explicit opt-in, regression guard)</li>
 * </ul>
 *
 * <h2>Users / roles</h2>
 * <ul>
 * <li>{@code userA / password1} — assigned role {@code ROLE_A} only</li>
 * <li>{@code userB / password1} — assigned role {@code ROLE_B} only</li>
 * </ul>
 */
@ServerSetup({ AutoEnableRbacTest.SecurityDomainSetup.class, AutoEnableRbacTest.WarnLoggingSetupTask.class })
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-core", minVersion = "8.0.0.Alpha2")
public class AutoEnableRbacTest {

    /**
     * Baseline count of RESTEASY002430 lines in server.log taken before the deployments are
     * processed. The {@code DEPLOY_FALSE} deployment will add exactly one line; asserts {@code baseline + 1}.
     */
    private static final int WARN_BASELINE = TestUtil.getWarningCount("RESTEASY002430", false);

    /** Deployment with no {@code resteasy.role.based.security} context-param (RBAC auto-enabled). */
    static final String DEPLOY_DEFAULT = "AutoEnableRbacTestDefault";

    /** Deployment with {@code resteasy.role.based.security=false} (RBAC explicitly disabled). */
    static final String DEPLOY_FALSE = "AutoEnableRbacTestFalse";

    /** Deployment with {@code resteasy.role.based.security=true} (RBAC explicitly enabled — regression guard). */
    static final String DEPLOY_TRUE = "AutoEnableRbacTestTrue";

    // ─────────────────────────────────────────────────────────────────────────
    // Clients
    // ─────────────────────────────────────────────────────────────────────────

    /** Authenticated as {@code userA} (role: {@code ROLE_A}) — wrong role for {@code @RolesAllowed("ROLE_B")}. */
    private static Client clientRoleA;

    /** Authenticated as {@code userB} (role: {@code ROLE_B}) — correct role. */
    private static Client clientRoleB;

    @BeforeAll
    public static void initClients() {
        clientRoleA = buildBasicAuthClient("userA", "password1");
        clientRoleB = buildBasicAuthClient("userB", "password1");
    }

    @AfterAll
    public static void closeClients() {
        clientRoleA.close();
        clientRoleB.close();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Deployments
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Deployment with NO {@code resteasy.role.based.security} context-param.
     * After the RESTEASY-3789 fix, {@code RoleBasedSecurityFeature} is registered by default.
     */
    @Deployment(name = DEPLOY_DEFAULT)
    public static Archive<?> deployDefault() {
        WebArchive war = TestUtil.prepareArchive(DEPLOY_DEFAULT);
        war.addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                "rbac-auto-enable-jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                        "rbac-auto-enable-web-default.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, new Hashtable<>(), AutoEnableRbacResource.class);
    }

    /**
     * Deployment with {@code resteasy.role.based.security=false}.
     * {@code RoleBasedSecurityFeature} is explicitly disabled; RBAC annotations are ignored.
     */
    @Deployment(name = DEPLOY_FALSE)
    public static Archive<?> deployFalse() {
        WebArchive war = TestUtil.prepareArchive(DEPLOY_FALSE);
        war.addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                "rbac-auto-enable-jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                        "rbac-auto-enable-web-false.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, new Hashtable<>(), AutoEnableRbacResource.class);
    }

    /**
     * Deployment with {@code resteasy.role.based.security=true}.
     * Explicit opt-in; verifies the fix did not regress the existing behaviour.
     */
    @Deployment(name = DEPLOY_TRUE)
    public static Archive<?> deployTrue() {
        WebArchive war = TestUtil.prepareArchive(DEPLOY_TRUE);
        war.addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                "rbac-auto-enable-jboss-web.xml", "jboss-web.xml")
                .addAsWebInfResource(AutoEnableRbacTest.class.getPackage(),
                        "rbac-auto-enable-web-true.xml", "web.xml");
        return TestUtil.finishContainerPrepare(war, new Hashtable<>(), AutoEnableRbacResource.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RBAC auto-enabled, @RolesAllowed("ROLE_B")
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@code resteasy.role.based.security} not set; user carries only {@code ROLE_A}.
     * {@code @RolesAllowed("ROLE_B")} must deny the request with 403.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_DEFAULT)
    public void rbac_autoEnabled_withRolesAllowed_wrongRole_returns403() {
        Response response = clientRoleA.target(url(DEPLOY_DEFAULT, "/rbac-auto/roles-allowed")).request().get();
        try {
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus(),
                    "@RolesAllowed(ROLE_B) must deny userA (ROLE_A) with 403 when no property is set");
        } finally {
            response.close();
        }
    }

    /**
     * {@code resteasy.role.based.security} not set; user carries {@code ROLE_B}.
     * {@code @RolesAllowed("ROLE_B")} must allow the request with 200.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_DEFAULT)
    public void rbac_autoEnabled_withRolesAllowed_correctRole_returns200() {
        Response response = clientRoleB.target(url(DEPLOY_DEFAULT, "/rbac-auto/roles-allowed")).request().get();
        try {
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                    "@RolesAllowed(ROLE_B) must allow userB (ROLE_B) with 200 when no property is set");
        } finally {
            response.close();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RBAC auto-enabled, @DenyAll
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@code resteasy.role.based.security} not set; endpoint has {@code @DenyAll}.
     * Must return 403 even for an authenticated user.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_DEFAULT)
    public void rbac_autoEnabled_withDenyAll_returns403() {
        try (Response response = clientRoleA.target(url(DEPLOY_DEFAULT, "/rbac-auto/deny-all")).request().get()) {
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus(),
                    "@DenyAll must return 403 when no property is set");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RBAC auto-enabled, @PermitAll
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@code resteasy.role.based.security} not set; endpoint has {@code @PermitAll}.
     * Must return 200 regardless of the caller's role.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_DEFAULT)
    public void rbac_autoEnabled_withPermitAll_returns200() {
        try (Response response = clientRoleA.target(url(DEPLOY_DEFAULT, "/rbac-auto/permit-all")).request().get()) {
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                    "@PermitAll must return 200 when no property is set");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // resteasy.role.based.security=false — RBAC opt-out
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@code resteasy.role.based.security=false}; user carries only {@code ROLE_A}.
     * Because RBAC is explicitly disabled, {@code @RolesAllowed("ROLE_B")} is ignored
     * and the request must succeed with 200.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_FALSE)
    public void rbac_explicitFalse_annotationsIgnored_returns200() {
        try (Response response = clientRoleA.target(url(DEPLOY_FALSE, "/rbac-auto/roles-allowed")).request().get()) {
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus(),
                    "@RolesAllowed(ROLE_B) must be ignored (200) when resteasy.role.based.security=false");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // resteasy.role.based.security=true — regression guard
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * {@code resteasy.role.based.security=true}; user carries only {@code ROLE_A}.
     * {@code @RolesAllowed("ROLE_B")} must still deny the request with 403.
     * Verifies the fix did not break the explicit opt-in behaviour.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_TRUE)
    public void rbac_explicitTrue_stillWorks_returns403() {
        try (Response response = clientRoleA.target(url(DEPLOY_TRUE, "/rbac-auto/roles-allowed")).request().get()) {
            Assertions.assertEquals(HttpResponseCodes.SC_FORBIDDEN, response.getStatus(),
                    "@RolesAllowed(ROLE_B) must deny userA (ROLE_A) with 403 when resteasy.role.based.security=true");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // RESTEASY002430 WARN is emitted when resteasy.role.based.security=false
    //        and RBAC annotations are present
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * When {@code resteasy.role.based.security=false} and RBAC annotations are present,
     * {@code RESTEASY002430} must appear exactly once in the server log.
     * The WARN is produced by deploying {@code DEPLOY_FALSE}; this test reads server.log after
     * all deployments have been processed and compares against the pre-deployment baseline.
     *
     */
    @Test
    @OperateOnDeployment(DEPLOY_FALSE)
    public void rbac_explicitFalse_warnLogged() {
        int count = TestUtil.getWarningCount("RESTEASY002430", false);
        Assertions.assertEquals(WARN_BASELINE + 1, count,
                "Expected exactly one RESTEASY002430 WARN in server.log after deploying "
                        + DEPLOY_FALSE + " with resteasy.role.based.security=false and RBAC annotations present");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static Client buildBasicAuthClient(String username, String password) {
        return ClientBuilder.newBuilder()
                .register(HttpAuthenticators.basic(UserCredentials.clear(username, password.toCharArray())))
                .build();
    }

    private static String url(String deploymentName, String path) {
        return PortProviderUtil.generateURL(path, deploymentName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Server setup tasks
    // ─────────────────────────────────────────────────────────────────────────

    /** Ensures WARN-level logging is active for {@code org.jboss.resteasy} during the test run. */
    public static class WarnLoggingSetupTask extends LoggingSetupTask {
        public WarnLoggingSetupTask() {
            super(Map.of("WARN", Set.of("org.jboss.resteasy")));
        }
    }

    static class SecurityDomainSetup extends AbstractUsersRolesSecurityDomainSetup {

        SecurityDomainSetup() {
            super(AutoEnableRbacTest.class.getResource("rbac-auto-enable-users.properties"),
                    AutoEnableRbacTest.class.getResource("rbac-auto-enable-roles.properties"));
        }

        @Override
        public Map<String, String> getSecurityDomainConfig() {
            return Collections.singletonMap("rbacAutoEnableDomain", "rbacAutoEnableRealm");
        }
    }
}
