/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A JAXBXmlRootElementProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces(
        {"text/*+xml", "application/*+xml"})
@Consumes(
        {"text/*+xml", "application/*+xml"})
public class JAXBXmlRootElementProvider extends AbstractJAXBProvider<Object>
{

   @Override
   protected boolean isReadWritable(Class<?> type,
                                    Type genericType,
                                    Annotation[] annotations,
                                    MediaType mediaType)
   {
      return type.isAnnotationPresent(XmlRootElement.class);
   }

}
