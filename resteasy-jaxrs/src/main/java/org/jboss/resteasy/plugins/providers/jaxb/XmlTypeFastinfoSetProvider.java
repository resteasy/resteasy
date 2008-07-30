/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;

/**
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@ProduceMime("application/fastinfoset")
@ConsumeMime("application/fastinfoset")
public class XmlTypeFastinfoSetProvider extends JAXBXmlTypeProvider
{

   protected static final String OBJECT_FACTORY_NAME = ".ObjectFactory";
   /**
    * 
    */
   @Override
   public void writeTo(Object t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      JAXBElement<?> result = wrapInJAXBElement(t, type);
      
      super.writeTo(result, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }


   
   
}
