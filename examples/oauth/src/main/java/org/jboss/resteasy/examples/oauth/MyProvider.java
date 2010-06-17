package org.jboss.resteasy.examples.oauth;

import org.jboss.resteasy.auth.oauth.OAuthMemoryProvider;

public class MyProvider extends OAuthMemoryProvider {

    public MyProvider() {
        super("default");
    }
    
    public MyProvider(String realm) {
        super(realm);
    }

    
}
