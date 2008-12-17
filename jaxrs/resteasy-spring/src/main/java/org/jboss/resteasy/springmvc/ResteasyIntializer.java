package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
* @version $Revision: 1 $
*/

public class ResteasyIntializer {

    public ResteasyIntializer(ResteasyProviderFactory providerFactory) {
        RegisterBuiltin.register(providerFactory);
    }
}
