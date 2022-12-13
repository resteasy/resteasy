package org.jboss.resteasy.plugins.providers.jaxb;

import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlNs;

import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;

/**
 * A XmlNamespacePrefixMapper.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 * @deprecated This should no longer be used as it's quite simple to implement and is tied to the implementation
 */
@Deprecated
public class XmlNamespacePrefixMapper extends NamespacePrefixMapper {

    private final Map<String, String> namespaceMap = new HashMap<String, String>();

    /**
     * Create a new XmlNamespecePrefixMapper.
     *
     * @param namespaces xml namespaces
     */
    public XmlNamespacePrefixMapper(final XmlNs... namespaces) {
        for (XmlNs namespace : namespaces) {
            namespaceMap.put(namespace.namespaceURI(), namespace.prefix());
        }
    }

    /**
    *
    */
    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        String prefix = namespaceMap.get(namespaceUri);
        if (prefix != null) {
            return prefix;
        }
        return suggestion;
    }

}
