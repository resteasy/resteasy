package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("deprecation")
public class JettisonMappedContext extends JAXBContext
{
   private JAXBContext context;
   private MappedNamespaceConvention convention;

   public JettisonMappedContext(Class... classes)
   {
      this(new HashMap<String, String>(), new ArrayList<String>(), new ArrayList<String>(), classes);
   }

   public JettisonMappedContext(Mapped mapped, Class... classes)
   {
      List<String> ignoredElements = Arrays.asList(mapped.ignoredElements());
      List<String> attributesAsElements = Arrays.asList(mapped.attributesAsElements());
      HashMap<String, String> xmlnsToJson = new HashMap<String, String>();
      for (XmlNsMap j : mapped.namespaceMap())
      {
         xmlnsToJson.put(j.xmlElement(), j.jsonName());
      }
      Configuration config = new Configuration(xmlnsToJson, attributesAsElements, ignoredElements);
      convention = new MappedNamespaceConvention(config);

      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public JettisonMappedContext(Map<String, String> xmlnsToJson, List<String> attributesAsElements, List<String> ignoredElements, Class... classes)
   {
      Configuration config = new Configuration(xmlnsToJson, attributesAsElements, ignoredElements);
      convention = new MappedNamespaceConvention(config);

      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public JettisonMappedContext(MappedNamespaceConvention convention, Class... classes)
   {
      this.convention = convention;
      try
      {
         context = JAXBContext.newInstance(classes);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public Unmarshaller createUnmarshaller() throws JAXBException
   {
      return new JettisonMappedUnmarshaller(context, convention);
   }

   public Marshaller createMarshaller() throws JAXBException
   {
      return new JettisonMappedMarshaller(context, convention);
   }

   public Validator createValidator() throws JAXBException
   {
      return context.createValidator();
   }


}