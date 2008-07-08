package org.jboss.resteasy.plugins.providers.json.jettison;

import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Validator;
import java.util.ArrayList;
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

   public JettisonMappedContext(Map<String, String> xmlnsToJson, List<String> attributeMapping, List<String> ignoredElements, Class... classes)
   {
      Configuration config = new Configuration(xmlnsToJson, attributeMapping, ignoredElements);
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