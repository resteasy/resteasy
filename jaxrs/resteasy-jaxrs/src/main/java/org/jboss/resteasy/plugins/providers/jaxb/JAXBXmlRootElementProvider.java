/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A JAXBXmlRootElementProvider.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@ProduceMime(
{"text/xml", "application/xml"})
@ConsumeMime(
{"text/xml", "application/xml"})
public class JAXBXmlRootElementProvider extends AbstractJAXBProvider<Object>
{

   @Override
   protected boolean isReadWritable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isAnnotationPresent(XmlRootElement.class);
   }

}
