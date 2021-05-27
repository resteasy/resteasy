package org.jboss.resteasy.setup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.jboss.as.arquillian.api.ServerSetupTask;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.dmr.ModelNode;
import org.jboss.resteasy.utils.TestUtil;
import org.wildfly.extras.creaper.core.online.ModelNodeResult;
import org.wildfly.extras.creaper.core.online.OnlineManagementClient;
import org.wildfly.extras.creaper.core.online.operations.Address;
import org.wildfly.extras.creaper.core.online.operations.Batch;
import org.wildfly.extras.creaper.core.online.operations.Operations;
import org.wildfly.extras.creaper.core.online.operations.Values;
import org.wildfly.extras.creaper.core.online.operations.admin.Administration;

/**
 * This abstract class implements steps needed to create Elytron security domain.
 */
public abstract class AbstractUsersRolesSecurityDomainSetup implements ServerSetupTask {

   // Properties file path
   private static final String USERS_FILENAME = "users.properties";
   private static final String ROLES_FILENAME = "roles.properties";

   private final URL userFile;
   private final URL rolesFile;
   private final Deque<Address> toRemove;
   private final Queue<Path> filesToRemove;

   protected AbstractUsersRolesSecurityDomainSetup(final URL userFile, final URL rolesFile) {
      this.userFile = userFile;
      this.rolesFile = rolesFile;
      toRemove = new LinkedList<>();
      filesToRemove = new ArrayDeque<>();
   }

   @Override
   public void setup(ManagementClient client, String s) throws Exception {

      // Create and initialize management client
      final Operations ops = new Operations(TestUtil.clientInit());

      // Generate the user and role files
      final ModelNodeResult result = ops.invoke("path-info", Address.of("path", "jboss.server.config.dir"));
      result.assertSuccess("Failed to resolve the jboss.server.config.dir");
      final Path configDir = Paths.get(result.value().get("path", "resolved-path").asString());
      filesToRemove.add(createPropertiesFile(userFile, configDir.resolve(USERS_FILENAME)));
      filesToRemove.add(createPropertiesFile(rolesFile, configDir.resolve(ROLES_FILENAME)));

      // Use a batch to for the config
      final Batch batch = new Batch();

      for (Map.Entry<String, String> entry : getSecurityDomainConfig().entrySet()) {
         final String domainName = entry.getKey();
         final String realmName = entry.getValue();

         // Create Elytron properties-realm
         final Address propertiesRealmAddress
                 = Address.subsystem("elytron").and("properties-realm", realmName);
         batch.add(propertiesRealmAddress, Values.empty()
                 .andObject("users-properties", Values.empty()
                         .and("path", USERS_FILENAME)
                         .and("relative-to", "jboss.server.config.dir")
                         .and("plain-text", true))
                 .andObjectOptional("groups-properties", Values.empty()
                         .and("path", ROLES_FILENAME)
                         .and("relative-to", "jboss.server.config.dir")));
         toRemove.addLast(propertiesRealmAddress);

         // Create Elytron security-domain
         final Address securityDomainAddress = Address.subsystem("elytron")
                 .and("security-domain", domainName);
         final ModelNode realms = new ModelNode().setEmptyObject();
         realms.get("realm").set(realmName);
         realms.get("role-decoder").set("groups-to-roles");
         batch.add(securityDomainAddress, Values.ofList("realms", realms)
                 .and("default-realm", realmName)
                 .and("permission-mapper", "default-permission-mapper")
         );
         toRemove.addFirst(securityDomainAddress);

         // Create Elytron http-authentication-factory with previous security-domain
         final Address httpAuthAddress = Address.subsystem("elytron")
                 .and("http-authentication-factory", "http-auth-" + domainName);

         // Create the value for the mechanism-configurations
         final ModelNode mechanismConfigs = new ModelNode().setEmptyObject();
         mechanismConfigs.get("mechanism-name").set("BASIC");
         final ModelNode mechanisms = mechanismConfigs.get("mechanism-realm-configurations").setEmptyList();
         final ModelNode mechanismsValue = new ModelNode().setEmptyObject();
         mechanismsValue.get("realm-name").set("\"Property Elytron\"");
         mechanisms.add(mechanismsValue);

         // Add the http-authentication-factory
         batch.add(httpAuthAddress, Values.empty()
                 .and("http-server-mechanism-factory", "global")
                 .and("security-domain", domainName)
                 .andList("mechanism-configurations", mechanismConfigs)
         );
         toRemove.addFirst(httpAuthAddress);

         // Set undertow application-security-domain to the custom http-authentication-factory
         final Address undertowAppSecDomainAddress = Address.subsystem("undertow")
                 .and("application-security-domain", domainName);
         batch.add(undertowAppSecDomainAddress, Values.of("http-authentication-factory", httpAuthAddress.getLastPairValue()));
         toRemove.addFirst(undertowAppSecDomainAddress);
      }

      ops.batch(batch).assertSuccess("Failed to configure Elytron");
   }

   @Override
   public void tearDown(ManagementClient client, String s) throws Exception {
      final OnlineManagementClient managementClient = TestUtil.clientInit();
      final Administration administration = new Administration(managementClient);
      final Operations ops = new Operations(managementClient);
      final Batch batch = new Batch();
      Address address;
      while ((address = toRemove.pollFirst()) != null) {
         batch.remove(address);
      }
      ops.batch(batch).assertSuccess("Failed to remove the Elytron config");

      // Clear any files that need to be
      Path file;
      while ((file = filesToRemove.poll()) != null) {
         Files.deleteIfExists(file);
      }

      administration.reloadIfRequired();
   }

   /**
    * A map of the security domain configuration. The key is the security domain name and the value is the realm name.
    *
    * <p>
    * Override this method to configure more than one security domain.
    * </p>
    *
    * @return the security domain configuration
    */
   public Map<String, String> getSecurityDomainConfig() {
      return Collections.singletonMap("jaxrsSecDomain", "propRealm");
   }

   private Path createPropertiesFile(final URL url, final Path file) throws IOException {
      if (url != null) {
         try (InputStream in = url.openStream()) {
            Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
         }
      } else {
         if (Files.notExists(file)) {
            Files.createFile(file);
         }
      }
      return file;
   }
}