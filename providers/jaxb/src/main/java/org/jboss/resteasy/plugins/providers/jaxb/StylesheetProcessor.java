package org.jboss.resteasy.plugins.providers.jaxb;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.providers.jaxb.Stylesheet;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.jboss.resteasy.util.StringContextReplacement;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.PropertyException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@DecorateTypes({"text/*+xml", "application/*+xml"})
public class StylesheetProcessor implements DecoratorProcessor<Marshaller, Stylesheet>
{
   public Marshaller decorate(Marshaller target, Stylesheet annotation, Class type, Annotation[] annotations, MediaType mediaType)
   {
      String doctype = StringContextReplacement.replace(annotation.type());
      String href = StringContextReplacement.replace(annotation.href());
      String h = "<?xml-stylesheet type='" + doctype + "' href='" + href + "' ?>";
      try
      {
         target.setProperty("org.glassfish.jaxb.xmlHeaders", h);
      }
      catch (PropertyException e)
      {
         throw new RuntimeException(e);
      }
      return target;
   }
}
