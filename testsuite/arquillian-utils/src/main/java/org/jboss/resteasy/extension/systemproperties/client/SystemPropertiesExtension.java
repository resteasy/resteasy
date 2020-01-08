package org.jboss.resteasy.extension.systemproperties.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.container.test.spi.client.deployment.AuxiliaryArchiveAppender;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * SystemPropertiesExtension
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * @version $Revision: $
 */
public class SystemPropertiesExtension implements LoadableExtension {
   @Override
   public void register(ExtensionBuilder builder) {
      builder.service(ApplicationArchiveProcessor.class,
            ArchiveProcessor.class).service(AuxiliaryArchiveAppender.class,
            ArchiveAppender.class);
   }

}
