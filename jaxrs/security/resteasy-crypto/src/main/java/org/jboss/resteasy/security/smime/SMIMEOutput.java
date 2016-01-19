package org.jboss.resteasy.security.smime;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SMIMEOutput
{
   protected Object entity;
   protected Type genericType;
   protected Class type;
   protected MediaType mediaType;
   protected X509Certificate certificate;

   public SMIMEOutput(Object obj, String mediaType)
   {
      this.entity = obj;
      this.type = obj.getClass();
      setMediaType(mediaType);
   }

   public SMIMEOutput(Object obj, MediaType mediaType)
   {
      this.entity = obj;
      this.type = obj.getClass();
      this.mediaType = mediaType;
   }

   public Object getEntity()
   {
      return entity;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   public Class getType()
   {
      return type;
   }

   public void setType(Class type)
   {
      this.type = type;
   }

   public void setType(GenericType t)
   {
      type = t.getRawType();
      genericType = t.getType();
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   public void setMediaType(String mediaType)
   {
      this.mediaType = MediaType.valueOf(mediaType);
   }

   public X509Certificate getCertificate()
   {
      return certificate;
   }

   public void setCertificate(X509Certificate certificate)
   {
      this.certificate = certificate;
   }
}
