package org.jboss.resteasy.setup;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.resteasy.utils.TestUtil;
import org.wildfly.extras.creaper.commands.security.AddLoginModule;
import org.wildfly.extras.creaper.commands.security.AddSecurityDomain;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * This abstract class implements steps needed to create PicketBox or Elytron security domain.
 *
 */
public abstract class AbstractUsersRolesSecurityDomainSetup implements ServerSetupTask {

   // Creaper fields
   private static OnlineManagementClient managementClient;
   private Operations ops;
   private Administration administration;

   // Properties file path
   private static final String USERS_FILENAME = "users.properties";
   private static final String ROLES_FILENAME = "roles.properties";
   private File USERS_FILE;
   private File ROLES_FILE;

   // This property decides under which security subsystem will be used for the tests
   private String subsystem = "elytron".equals(System.getProperty("security.provider")) ? "elytron" : "picketbox";

   // Security domain name shared by elytron and picketBox configuration
   private String securityDomainName = "jaxrsSecDomain";

   // PicketBox related settings
   private Address PICKETBOX_SECURITY_DOMAIN_ADDRESS
         = Address.subsystem("security").and("security-domain", securityDomainName);
   private Address PICKETBOX_AUTHN_CLASSIC_ADDRESS = PICKETBOX_SECURITY_DOMAIN_ADDRESS
         .and("authentication", "classic");
   private static final String PICKETBOX_LOGIN_MODULE_NAME = "UsersRoles";
   private Address PICKETBOX_LOGIN_MODULE_ADDRESS = PICKETBOX_AUTHN_CLASSIC_ADDRESS
         .and("login-module", PICKETBOX_LOGIN_MODULE_NAME);

   // Elytron related settings
   private static final String ELYTRON_PROPERTIES_REALM_NAME = "propRealm";
   private static final Address ELYTRON_PROPERTIES_REALM_ADDRESS
         = Address.subsystem("elytron").and("properties-realm", ELYTRON_PROPERTIES_REALM_NAME);
   private static final String ELYTRON_SECURITY_DOMAIN_NAME = "propertyElytronSecDomain";
   private static final Address ELYTRON_SECURITY_DOMAIN_ADDRESS
         = Address.subsystem("elytron").and("security-domain", ELYTRON_SECURITY_DOMAIN_NAME);
   private static final String ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_NAME = "prop-http-authentication-factory";
   private static final Address ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_ADDRESS
         = Address.subsystem("elytron").and("http-authentication-factory", ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_NAME);
   private String UNDERTOW_APPLICATION_SECURITY_DOMAIN_NAME = securityDomainName;
   private Address UNDERTOW_APPLICATION_SECURITY_DOMAIN_ADDRESS
         = Address.subsystem("undertow").and("application-security-domain", UNDERTOW_APPLICATION_SECURITY_DOMAIN_NAME);

   /**
    * Set security subsystem
    * @param subsystem
    */
   public void setSubsystem(String subsystem) {
      this.subsystem = subsystem;
   }

   /**
    * Set security domain name related configuration
    * @param securityDomainName
    */
   public void setSecurityDomainName(String securityDomainName) {
      this.securityDomainName = securityDomainName;
      this.PICKETBOX_SECURITY_DOMAIN_ADDRESS=Address.subsystem("security").and("security-domain", securityDomainName);
      this.UNDERTOW_APPLICATION_SECURITY_DOMAIN_NAME=securityDomainName;
      this.UNDERTOW_APPLICATION_SECURITY_DOMAIN_ADDRESS
            = Address.subsystem("undertow").and("application-security-domain", UNDERTOW_APPLICATION_SECURITY_DOMAIN_NAME);
   }

   /**
    * Creates Files pointing to users.properties and roles.properties for the current test.
    * @param folder
    */
   public void createPropertiesFiles(File folder) {
      this.USERS_FILE = new File(folder, USERS_FILENAME);
      this.ROLES_FILE = new File(folder, ROLES_FILENAME);
   }

   @Override
   public void setup(ManagementClient fakemanagementClient, String s) throws Exception {

      // Set path for users.properties and roles.properties
      setConfigurationPath();

      // Create and initialize management client
      managementClient = TestUtil.clientInit();
      administration = new Administration(managementClient);
      ops = new Operations(managementClient);

      if (subsystem.equals("elytron")) {
         configureElytron();
      } else {
         configurePicketBox();
      }
   }

   @Override
   public void tearDown(ManagementClient fakemanagementClient, String s) throws Exception {

      if (subsystem.equals("elytron")) {
         cleanUpElytron();
      } else {
         cleanUpPicketBox();
      }
   }

   /**
    * Set necessary test related paths
    */
   public abstract void setConfigurationPath() throws URISyntaxException, MalformedURLException;

   /**
    * Creates Elytron security domain
    * @throws Exception
    */
   private void configureElytron() throws Exception {

      // Note: This complicated setting may be simplified once WFLY-7949 is resolved

      // Create Elytron properties-realm
      ops.add(ELYTRON_PROPERTIES_REALM_ADDRESS, Values.empty()
            .andObject("users-properties", Values.empty()
                  .and("path", USERS_FILE.getAbsolutePath())
                  .andOptional("plain-text", true))
            .andObjectOptional("groups-properties", Values.empty()
               .and("path", ROLES_FILE.getAbsolutePath())));

      administration.reloadIfRequired();

      // Create Elytron security-domain
      managementClient.executeCli("/subsystem=elytron/security-domain="
            + ELYTRON_SECURITY_DOMAIN_NAME
            + ":add(realms=[{realm="
            + ELYTRON_PROPERTIES_REALM_NAME + ",role-decoder=groups-to-roles}],default-realm=propRealm,permission-mapper=default-permission-mapper)");

      // Create Elytron http-authentication-factory with previous security-domain
      managementClient.executeCli("/subsystem=elytron/http-authentication-factory="
            + ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_NAME + ":add(http-server-mechanism-factory=global,security-domain="
            + ELYTRON_SECURITY_DOMAIN_NAME
            + ",mechanism-configurations=[{mechanism-name=BASIC,mechanism-realm-configurations=[{realm-name=\"Property Elytron\"}]}])");

      // Set undertow application-security-domain to the custom http-authentication-factory
      managementClient.executeCli("/subsystem=undertow/application-security-domain="
            + securityDomainName + ":add(http-authentication-factory="
            +  ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_NAME + ")");

      administration.reloadIfRequired();

      assertTrue("The elytron/properties-realm should be created", ops.exists(ELYTRON_PROPERTIES_REALM_ADDRESS));
      assertTrue("The elytron/security-domain should be created", ops.exists(ELYTRON_SECURITY_DOMAIN_ADDRESS));
      assertTrue("The elytron/http-authentication-factory should be created", ops.exists(ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_ADDRESS));
      assertTrue("The undertow/application-security-domain should be created", ops.exists(UNDERTOW_APPLICATION_SECURITY_DOMAIN_ADDRESS));
   }

   /**
    * Creates PicketBox security domain
    * @throws Exception
    */
   private void configurePicketBox() throws Exception {

      // Create security domain
      AddSecurityDomain addSecurityDomain = new AddSecurityDomain.Builder(securityDomainName).build();
      managementClient.apply(addSecurityDomain);

      // Create login module
      AddLoginModule addLoginModule = new AddLoginModule.Builder("org.jboss.security.auth.spi.UsersRolesLoginModule",
            PICKETBOX_LOGIN_MODULE_NAME)
            .securityDomainName(securityDomainName)
            .flag("required")
            .module("org.picketbox")
            .addModuleOption("usersProperties", USERS_FILE.getAbsolutePath())
            .addModuleOption("rolesProperties", ROLES_FILE.getAbsolutePath())
            .build();

      managementClient.apply(addLoginModule);

      administration.reloadIfRequired();

      assertTrue("The login module should be created", ops.exists(PICKETBOX_LOGIN_MODULE_ADDRESS));
   }

   /**
    * Reverts all configuration done for Elytron
    * @throws Exception
    */
   private void cleanUpElytron() throws Exception {
      try {
         ops.removeIfExists(UNDERTOW_APPLICATION_SECURITY_DOMAIN_ADDRESS);
         ops.removeIfExists(ELYTRON_PROP_HTTP_AUTHENTICATION_FACTORY_ADDRESS);
         ops.removeIfExists(ELYTRON_SECURITY_DOMAIN_ADDRESS);
         ops.removeIfExists(ELYTRON_PROPERTIES_REALM_ADDRESS);
         administration.reloadIfRequired();
      } finally {
         managementClient.close();
      }
   }

   /**
    * Reverts all configuration done for PicketBox
    * @throws Exception
    */
   private void cleanUpPicketBox() throws Exception {
      try {
         ops.removeIfExists(PICKETBOX_SECURITY_DOMAIN_ADDRESS);
         administration.reloadIfRequired();
      } finally {
         managementClient.close();
      }
   }
}
