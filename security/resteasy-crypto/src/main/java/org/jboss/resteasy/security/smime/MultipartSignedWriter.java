package org.jboss.resteasy.security.smime;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.jboss.resteasy.core.messagebody.AsyncBufferedMessageBodyWriter;
import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.spi.WriterException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/signed")
public class MultipartSignedWriter implements AsyncBufferedMessageBodyWriter<SignedOutput> {
    static {
        BouncyIntegration.init();
    }

    @Context
    protected Providers providers;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return SignedOutput.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(SignedOutput smimeOutput, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(SignedOutput out, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> headers, OutputStream os) throws IOException, WebApplicationException {
        try {
            SMIMESignedGenerator gen = new SMIMESignedGenerator();
            SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA",
                    out.getPrivateKey(), out.getCertificate());
            gen.addSignerInfoGenerator(signer);

            MimeMultipart mp = gen.generate(EnvelopedWriter.createBodyPart(providers, out));
            String contentType = mp.getContentType();
            contentType = contentType.replace("\r\n", "").replace("\t", " ");
            headers.putSingle("Content-Type", contentType);
            mp.writeTo(os);
        } catch (Exception e) {
            throw new WriterException(e);
        }
    }
}
