package org.jboss.resteasy.extension.systemproperties.container;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;

/**
 * SystemPropertiesRemoteExtension
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class SystemPropertiesRemoteExtension implements RemoteLoadableExtension {
    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(SystemPropertiesLoader.class);
    }

}
