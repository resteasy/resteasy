package org.jboss.resteasy.extension.systemproperties.client;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.resteasy.extension.systemproperties.SystemProperties;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

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
}
