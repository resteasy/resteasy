package org.jboss.resteasy.extension.systemproperties.container;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.resteasy.extension.systemproperties.SystemProperties;

/**
 * SystemPropertiesLoader
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class SystemPropertiesLoader {
    public void setProperties(@Observes BeforeSuite event) {
        Properties props = load(SystemProperties.FILE_NAME);
        if (props != null) {
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                System.setProperty(entry.getKey().toString(), entry.getValue()
                        .toString());
            }
        }
    }

    public void unsetProperties(@Observes AfterSuite event) {
        Properties props = load(SystemProperties.FILE_NAME);
        if (props != null) {
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                System.clearProperty(entry.getKey().toString());
            }
        }
    }

    private Properties load(String resource) {
        InputStream propsStream = Thread.currentThread()
                .getContextClassLoader().getResourceAsStream(resource);
        if (propsStream != null) {
            Properties props = new Properties();
            try {
                props.load(propsStream);
                return props;
            } catch (Exception e) {
                throw new RuntimeException("Could not load properties", e);
            }
        }
        return null;
    }
}
