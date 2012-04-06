package org.jboss.resteasy.security.smime;

import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SignedOutput extends SMIMEOutput
{
   protected PrivateKey privateKey;

   public SignedOutput(Object obj, String mediaType)
   {
      super(obj, mediaType);
   }

   public SignedOutput(Object obj, MediaType mediaType)
   {
      super(obj, mediaType);
   }

   public PrivateKey getPrivateKey()
   {
      return privateKey;
   }

   public void setPrivateKey(PrivateKey privateKey)
   {
      this.privateKey = privateKey;
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
      type = t.getType();
      genericType = t.getGenericType();
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
