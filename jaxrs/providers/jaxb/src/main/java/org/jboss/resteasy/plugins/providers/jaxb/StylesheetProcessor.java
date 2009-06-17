package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.providers.jaxb.Stylesheet;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
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
public class StylesheetProcessor implements DecoratorProcessor<Marshaller, Stylesheet>
{
   public Marshaller decorate(Marshaller target, Stylesheet annotation, Class type, Annotation[] annotations, MediaType mediaType)
   {
      String doctype = StringContextReplacement.replace(annotation.type());
      String href = StringContextReplacement.replace(annotation.href());
      String h = "<?xml-stylesheet type='" + doctype + "' href='" + href + "' ?>";
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