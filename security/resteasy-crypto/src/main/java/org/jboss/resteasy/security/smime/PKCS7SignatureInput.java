package org.jboss.resteasy.security.smime;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.jboss.resteasy.security.doseta.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.util.Base64;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;

import java.io.ByteArrayInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PKCS7SignatureInput<T>
{
   private PublicKey publicKey;
   private X509Certificate certificate;
   private Class<?> type;
   private Type genericType;
   private CMSSignedData data;
   private Annotation[] annotations;
   private Providers providers;
   private Object entity;

   public PKCS7SignatureInput()
   {
   }

   public PKCS7SignatureInput(CMSSignedData data)
   {
      this.data = data;
   }

   /**
    * Base64 encoded pks bytes.
    *
    * @param base64 Base64 encoded string
    */
   public PKCS7SignatureInput(String base64)
   {
      try
      {
         byte[] bytes = Base64.decode(base64);
         this.data = new CMSSignedData(bytes);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }

   }

   /**
    * PKS Encoded bytes
    *
    * @param bytes data
    */
   public PKCS7SignatureInput(byte[] bytes)
   {
      try
      {
         this.data = new CMSSignedData(bytes);
      }
      catch (CMSException e)
      {
         throw new RuntimeException(e);
      }
   }







   public PublicKey getPublicKey()
   {
      return publicKey;
   }

   public void setPublicKey(PublicKey publicKey)
   {
      this.publicKey = publicKey;
   }

   public X509Certificate getCertificate()
   {
      return certificate;
   }

   public void setCertificate(X509Certificate certificate)
   {
      this.certificate = certificate;
   }

   public Class<?> getType()
   {
      return type;
   }

   public void setType(Class<?> type)
   {
      this.type = type;
   }

   public void setType(GenericType<?> type)
   {
      this.type = type.getRawType();
      this.genericType = type.getType();
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   public CMSSignedData getData()
   {
      return data;
   }

   public void setData(CMSSignedData data)
   {
      this.data = data;
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public Providers getProviders()
   {
      return providers;
   }

   public void setProviders(Providers providers)
   {
      this.providers = providers;
   }

   @SuppressWarnings("unchecked")
   public T getEntity(MediaType mediaType)
   {
      return (T)getEntity(type, genericType, annotations, mediaType);
   }

   public <T2> T2 getEntity(Class<T2> type, MediaType mediaType)
   {
      return getEntity(type, type, annotations, mediaType);
   }

   @SuppressWarnings("unchecked")
   public <T2> T2  getEntity(GenericType<T2> gt, MediaType mediaType)
   {
      return getEntity((Class<T2>) gt.getRawType(),  gt.getType(), annotations, mediaType);
   }
   @SuppressWarnings("unchecked")
   public <T2> T2   getEntity(GenericType<T2> gt, Annotation[] ann, MediaType mediaType)
   {
      return getEntity((Class<T2>) gt.getRawType(), gt.getType(), ann, mediaType);
   }
   @SuppressWarnings({"rawtypes", "unchecked"})
   public <T2> T2  getEntity(Class<T2> t, Type gt, Annotation[] ann, MediaType mediaType)
   {
      if (entity != null) return (T2)entity;
      byte[] bytes = (byte[])data.getSignedContent().getContent();
      MessageBodyReader reader = providers.getMessageBodyReader(t, gt, ann, mediaType);
      ByteArrayInputStream is = new ByteArrayInputStream(bytes);

      try
      {
         entity = reader.readFrom(t, gt, ann, mediaType, new MultivaluedMapImpl<String, String>(), is);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      return (T2)entity;
   }

   public boolean verify() throws Exception
   {
      if (certificate != null) return verify(certificate);
      else if (publicKey != null) return verify(publicKey);
      else throw new NullPointerException(Messages.MESSAGES.certificateNorPublicKeySet());
   }

   public boolean verify(X509Certificate certificate) throws Exception
   {
      for (Object info : data.getSignerInfos().getSigners())
      {
         SignerInformation signer = (SignerInformation)info;


         if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(certificate)))
         {
            return true;
         }
      }
      return false;
   }
   public boolean verify(PublicKey publicKey) throws Exception
   {
      for (Object info : data.getSignerInfos().getSigners())
      {
         SignerInformation signer = (SignerInformation)info;
         if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(publicKey)))
         {
            return true;
         }
      }
      return false;
   }


}
