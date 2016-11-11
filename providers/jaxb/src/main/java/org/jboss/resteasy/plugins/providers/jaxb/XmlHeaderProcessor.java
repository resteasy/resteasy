package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.providers.jaxb.XmlHeader;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.jboss.resteasy.util.StringContextReplacement;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@DecorateTypes({"text/*+xml", "application/*+xml"})
public class XmlHeaderProcessor implements DecoratorProcessor<Marshaller, XmlHeader>
{
   public Marshaller decorate(Marshaller target, XmlHeader annotation, Class type, Annotation[] annotations, MediaType mediaType)
   {
      String h = StringContextReplacement.replace(annotation.value());
      try
      {
         target.setProperty("com.sun.xml.bind.xmlHeaders", h);
      }
      catch (PropertyException e)
      {
         throw new RuntimeException(e);
      }
      return target;
   }
}
