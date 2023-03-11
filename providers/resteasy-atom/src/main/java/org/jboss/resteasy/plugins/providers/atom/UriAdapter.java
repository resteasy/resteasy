package org.jboss.resteasy.plugins.providers.atom;

import java.net.URI;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriAdapter extends XmlAdapter<String, URI> {
    public URI unmarshal(String s) throws Exception {
        if (s == null)
            return null;
        return new URI(s);
    }

    public String marshal(URI uri) throws Exception {
        if (uri == null)
            return null;
        return uri.toString();
    }
}
