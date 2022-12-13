package org.jboss.resteasy.extension.systemproperties.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.resteasy.extension.systemproperties.SystemProperties;
import org.jboss.resteasy.extension.systemproperties.container.SystemPropertiesRemoteExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * ArchiveAppender
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class ArchiveAppender implements AuxiliaryArchiveAppender {
    @Override
    public Archive<?> createAuxiliaryArchive() {
        return ShrinkWrap
                .create(JavaArchive.class, "arquillian-systemproperties.jar")
                .addPackage(SystemPropertiesRemoteExtension.class.getPackage())
                .addClass(SystemProperties.class)
                .addAsServiceProvider(RemoteLoadableExtension.class,
                        SystemPropertiesRemoteExtension.class);
    }

}
