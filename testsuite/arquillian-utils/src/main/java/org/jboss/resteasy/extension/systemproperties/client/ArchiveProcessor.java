package org.jboss.resteasy.extension.systemproperties.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.Set;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.resteasy.extension.systemproperties.SystemProperties;
import org.jboss.resteasy.utils.TestConfiguration;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * ArchiveProcessor
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class ArchiveProcessor implements ApplicationArchiveProcessor {
    @Inject
    private Instance<ArquillianDescriptor> descriptor;

    @Override
    public void process(Archive<?> applicationArchive, TestClass testClass) {
        String prefix = getPrefix();
        if (prefix != null) {
            if (applicationArchive instanceof ResourceContainer) {
                ResourceContainer<?> container = (ResourceContainer<?>) applicationArchive;
                container.addAsResource(new StringAsset(
                        toString(filterSystemProperties(prefix))),
                        SystemProperties.FILE_NAME);
            }
        }
        // Add a permissions.xml file if the security manager is enabled in the server
        if (TestConfiguration.isSecurityManagerEnabled()) {
            final String jbossHome = resolveJBossHome();
            final Set<Permission> requirePermissions = Set.of(
                    new FilePermission(jbossHome + "*", "read"),
                    new FilePermission(jbossHome + "modules" + File.separatorChar + "-", "read"),
                    new ReflectPermission("suppressAccessChecks"),
                    new RuntimePermission("accessDeclaredMembers"),
                    new RuntimePermission("accessClassInPackage.sun.reflect.annotation"),
                    new PropertyPermission("arquillian.*", "read"),
                    new PropertyPermission("module.path", "read"),
                    new PropertyPermission("jboss.home.*", "read"),
                    new PropertyPermission("junit.platform.reflection.search.useLegacySemantics", "read"));
            final Node node = applicationArchive.delete("/META-INF/permissions.xml");
            final Asset permissionsXml;
            if (node != null) {
                final Asset currentPermissions = node.getAsset();
                permissionsXml = DeploymentDescriptors.appendPermissions(currentPermissions, requirePermissions);
            } else {
                permissionsXml = DeploymentDescriptors.createPermissionsXmlAsset(requirePermissions);
            }
            applicationArchive.add(permissionsXml, "/META-INF/permissions.xml");
        }
    }

    private String getPrefix() {
        return getConfiguration().get(SystemProperties.CONFIG_PREFIX);
    }

    private Map<String, String> getConfiguration() {
        for (ExtensionDef def : descriptor.get().getExtensions()) {
            if (SystemProperties.EXTENSION_NAME.equalsIgnoreCase(def
                    .getExtensionName())) {
                return def.getExtensionProperties();
            }
        }
        return new HashMap<String, String>();
    }

    private Properties filterSystemProperties(String prefix) {
        Properties filteredProps = new Properties();
        Properties sysProps = System.getProperties();
        for (Map.Entry<Object, Object> entry : sysProps.entrySet()) {
            if (entry.getKey().toString().startsWith(prefix)) {
                filteredProps.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredProps;
    }

    private String toString(Properties props) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            props.store(out, "Arquillian SystemProperties Extension");
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Could not store properties", e);
        }
    }

    private static String resolveJBossHome() {
        final String value = System.getProperty("jboss.home");
        String jbossHome;
        if (value == null) {
            // Default provisioning directory
            jbossHome = "target/server";
        } else {
            jbossHome = value;
        }
        if (jbossHome.endsWith("/") || jbossHome.endsWith("\\")) {
            return jbossHome;
        }
        return jbossHome + File.separatorChar;
    }
}
