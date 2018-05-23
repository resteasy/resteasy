package org.jboss.resteasy.plugins.providers.jaxb;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import javax.xml.bind.annotation.XmlNs;
import java.util.HashMap;
import java.util.Map;

/**
 * A XmlNamespacePrefixMapper.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class XmlNamespacePrefixMapper extends NamespacePrefixMapper
{

   private final Map<String, String> namespaceMap = new HashMap<String, String>();

   /**
    * Create a new XmlNamespecePrefixMapper.
    *
    * @param namespaces xml namespaces
    */
   public XmlNamespacePrefixMapper(final XmlNs... namespaces)
   {
      for (XmlNs namespace : namespaces)
      {
         namespaceMap.put(namespace.namespaceURI(), namespace.prefix());
      }
   }


   /**
    *
    */
   @Override
   public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix)
   {
      String prefix = namespaceMap.get(namespaceUri);
      if (prefix != null)
      {
         return prefix;
      }
      return suggestion;
   }

}
