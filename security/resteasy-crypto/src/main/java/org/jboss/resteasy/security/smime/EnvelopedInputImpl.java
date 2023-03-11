package org.jboss.resteasy.security.smime;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;

import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.security.doseta.i18n.Messages;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("rawtypes")
public class EnvelopedInputImpl implements EnvelopedInput {
    private PrivateKey privateKey;
    private X509Certificate certificate;
    private Class<?> type;
    private Type genericType;
    private MimeBodyPart body;
    private Annotation[] annotations;
    private Providers providers;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public void setType(GenericType<?> type) {
        this.type = type.getRawType();
        this.genericType = type.getType();
    }

    public Type getGenericType() {
        return genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }

    public MimeBodyPart getBody() {
        return body;
    }

    public void setBody(MimeBodyPart body) {
        this.body = body;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

    public Providers getProviders() {
        return providers;
    }

    public void setProviders(Providers providers) {
        this.providers = providers;
    }

    public Object getEntity() {
        return getEntity(type, genericType, annotations, privateKey, certificate);
    }

    public Object getEntity(PrivateKey pKey, X509Certificate cert) {
        return getEntity(type, genericType, annotations, pKey, cert);
    }

    public Object getEntity(Class type) {
        return getEntity(type, null, annotations, privateKey, certificate);
    }

    public Object getEntity(Class type, PrivateKey key, X509Certificate cert) {
        return getEntity(type, null, annotations, key, cert);
    }

    public Object getEntity(GenericType type) {
        return getEntity(type.getRawType(), type.getType(), annotations, privateKey, certificate);
    }

    public Object getEntity(GenericType type, PrivateKey key, X509Certificate cert) {
        return getEntity(type, annotations, key, cert);
    }

    public Object getEntity(GenericType gt, Annotation[] ann, PrivateKey pKey, X509Certificate cert) {
        return getEntity(gt.getRawType(), gt.getType(), ann, pKey, cert);
    }

    public Object getEntity(Class t, Type gt, Annotation[] ann, PrivateKey pKey, X509Certificate cert) {
        MimeBodyPart decrypted = null;
        try {
            MimeBodyPart encryptedBodyPart = body;
            SMIMEEnveloped m = new SMIMEEnveloped(encryptedBodyPart);
            JceKeyTransRecipientId recId = new JceKeyTransRecipientId(cert);

            RecipientInformationStore recipients = m.getRecipientInfos();
            RecipientInformation recipient = recipients.get(recId);
            JceKeyTransRecipient pKeyRecp = new JceKeyTransEnvelopedRecipient(pKey);

            decrypted = SMIMEUtil.toMimeBodyPart(recipient.getContent(pKeyRecp));
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }

        return extractEntity(t, gt, ann, decrypted, providers);
    }

    @SuppressWarnings("unchecked")
    public static Object extractEntity(Class t, Type gt, Annotation[] ann, MimeBodyPart decrypted, Providers providers) {
        MultivaluedMap<String, String> mimeHeaders = new Headers<String>();
        Enumeration e = null;
        try {
            e = decrypted.getAllHeaders();
        } catch (MessagingException e1) {
            throw new RuntimeException(e1);
        }
        while (e.hasMoreElements()) {
            Header header = (Header) e.nextElement();
            mimeHeaders.add(header.getName(), header.getValue());
        }
        String contentType = "text/plain";
        if (mimeHeaders.containsKey("Content-Type"))
            contentType = mimeHeaders.getFirst("Content-Type");
        MediaType mediaType = MediaType.valueOf(contentType);
        MessageBodyReader reader = providers.getMessageBodyReader(t, gt, ann, mediaType);
        if (reader == null) {
            throw new RuntimeException(Messages.MESSAGES.couldNotFindMessageBodyReader(t.getClass().getName()));
        }
        Providers old = ResteasyContext.getContextData(Providers.class);
        ResteasyContext.pushContext(Providers.class, providers);
        try {
            InputStream inputStream = null;
            if (EnvelopedInput.class.isAssignableFrom(t)) {
                inputStream = decrypted.getRawInputStream();
            } else {
                inputStream = decrypted.getInputStream();
            }
            return reader.readFrom(t, gt, ann, mediaType, mimeHeaders, inputStream);
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        } finally {
            ResteasyContext.popContextData(Providers.class);
            if (old != null)
                ResteasyContext.pushContext(Providers.class, old);
        }
    }

}
