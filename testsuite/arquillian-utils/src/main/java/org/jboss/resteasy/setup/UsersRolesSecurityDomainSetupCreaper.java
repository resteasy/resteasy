package org.jboss.resteasy.setup;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.resteasy.utils.TestUtil;
import org.wildfly.extras.creaper.commands.security.AddLoginModule;
import org.wildfly.extras.creaper.commands.security.AddSecurityDomain;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import static org.junit.Assert.assertTrue;

public class UsersRolesSecurityDomainSetupCreaper implements ServerSetupTask {

    private static final String TEST_SECURITY_DOMAIN_NAME = "jaxrsSecDomain";
    private static final Address TEST_SECURITY_DOMAIN_ADDRESS
            = Address.subsystem("security").and("security-domain", TEST_SECURITY_DOMAIN_NAME);
    private static final Address TEST_AUTHN_CLASSIC_ADDRESS = TEST_SECURITY_DOMAIN_ADDRESS
            .and("authentication", "classic");
    private static final String TEST_LOGIN_MODULE_NAME = "UsersRoles";
    private static final Address TEST_LOGIN_MODULE_ADDRESS = TEST_AUTHN_CLASSIC_ADDRESS
            .and("login-module", TEST_LOGIN_MODULE_NAME);

    private static OnlineManagementClient managementClient;
    private Operations ops;
    private Administration administration;

    @Override
    public void setup(ManagementClient fakemanagementClient, String s) throws Exception {
        // Create and initialize management client
        managementClient = TestUtil.clientInit();
        administration = new Administration(managementClient);
        ops = new Operations(managementClient);

        // Create security domain
        AddSecurityDomain addSecurityDomain = new AddSecurityDomain.Builder(TEST_SECURITY_DOMAIN_NAME).build();
        managementClient.apply(addSecurityDomain);

        // Create login module
        AddLoginModule addLoginModule = new AddLoginModule.Builder("org.jboss.security.auth.spi.UsersRolesLoginModule",
                TEST_LOGIN_MODULE_NAME)
                .securityDomainName(TEST_SECURITY_DOMAIN_NAME)
                .flag("required")
                .module("org.picketbox")
                .addModuleOption("usersProperties", "users.properties")
                .addModuleOption("rolesProperties", "roles.properties")
                .build();

        managementClient.apply(addLoginModule);

        administration.reloadIfRequired();

        assertTrue("The login module should be created", ops.exists(TEST_LOGIN_MODULE_ADDRESS));
    }

    @Override
    public void tearDown(ManagementClient fakemanagementClient, String s) throws Exception {
        try {
            ops.removeIfExists(TEST_SECURITY_DOMAIN_ADDRESS);
            administration.reloadIfRequired();
        } finally {
            managementClient.close();
        }
    }
}
