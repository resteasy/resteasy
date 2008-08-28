/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.json;

import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;

import org.codehaus.jettison.mapped.Configuration;
import org.jboss.resteasy.annotations.JSONConvention;

/**
 * A JSONJAXBContextWrapper.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@SuppressWarnings("deprecation")
public class JSONJAXBContextWrapper extends JAXBContext
{
   /**
    * Base name space for JSON properties
    */
   public static final String JSON_NAMESPACE = "org.jboss.resteasy.json.";

   /**
    * 
    */
   public static final String IGNORED_ELEMENTS = JSON_NAMESPACE + "ignoredElements";

   public static final String ATTRIBUTE_MAPPING = JSON_NAMESPACE + "attributeMapping";

   public static final String XML_TO_JSON = JSON_NAMESPACE + "xmlToJson";

   private JAXBContext jaxbContext;

   private Configuration configuration;

   private JSONConvention convention;

   public JSONJAXBContextWrapper(JSONConvention convention, 
                                 Class<?>[] classes, 
                                 Map<String, Object> properties)
         throws JAXBException
   {
      configuration = createMappedConfiguration(properties);
      jaxbContext = JAXBContext.newInstance(classes);
   }

   /**
    * FIXME Comment this
    * 
    * @param xmlToJSON
    * @param attributeMapping
    * @param ignoredElements
    * @return
    */
   private Configuration createMappedConfiguration(Map<String, String> xmlToJSON,
                                                   List<String> attributeMapping,
                                                   List<String> ignoredElements)
   {
      return new Configuration(xmlToJSON, attributeMapping, ignoredElements);
   }

   /**
    * FIXME Comment this
    * 
    * @param jsonProperties
    * @return
    */
   @SuppressWarnings("unchecked")
   private Configuration createMappedConfiguration(Map<String, Object> jsonProperties)
   {
      Map<String, String> xmlToJSON = (Map<String, String>) jsonProperties.remove(XML_TO_JSON);
      List<String> attributeMapping = (List<String>) jsonProperties.remove(ATTRIBUTE_MAPPING);
      List<String> ignoredElements = (List<String>) jsonProperties.remove(IGNORED_ELEMENTS);
      return createMappedConfiguration(xmlToJSON, attributeMapping, ignoredElements);
   }

   /**
    * Get the jaxbContext.
    * 
    * @return the jaxbContext.
    */
   protected JAXBContext getJaxbContext()
   {
      return jaxbContext;
   }

   /**
    * Get the configuration.
    * 
    * @return the configuration.
    */
   protected Configuration getConfiguration()
   {
      return configuration;
   }

   @Override
   public Marshaller createMarshaller() throws JAXBException
   {
      return new JSONMarshaller(this);
   }

   /**
    * Get the convention.
    * 
    * @return the convention.
    */
   protected JSONConvention getConvention()
   {
      return convention;
   }

   @Override
   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      // FIXME createUnmarshaller
      return null;
   }

   @Override
   public Validator createValidator() throws JAXBException
   {
      return jaxbContext.createValidator();
   }

}
