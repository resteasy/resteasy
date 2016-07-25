package org.jboss.resteasy.test.crypto.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.utils.TestApplication;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Path("/")
public class VerifyDecryptResource {
    private static Logger logger = Logger.getLogger(VerifyDecryptResource.class);

    @POST
    @Path("encrypt")
    public String encrypt(EnvelopedInput<String> input) throws Exception {
        String secret = input.getEntity(privateKey, cert);
        logger.info("secret: " + secret);
        return secret;
    }

    @POST
    @Path("sign")
    public String sign(SignedInput<String> input) throws Exception {
        if (!input.verify(cert)) {
            throw new WebApplicationException(500);
        }
        String secret = input.getEntity();
        logger.info("secret: " + secret);
        return secret;
    }

    @POST
    @Path("encryptSign")
    public String encryptSign(SignedInput<EnvelopedInput<String>> input) throws Exception {
        if (!input.verify(cert)) {
            throw new WebApplicationException(500);
        }
        final EnvelopedInput<String> envelop = input.getEntity();
        String secret = envelop.getEntity(privateKey, cert);
        logger.info("secret: " + secret);
        return secret;
    }

    @POST
    @Path("signEncrypt")
    public String signEncrypt(EnvelopedInput<SignedInput<String>> input) throws Exception {
        SignedInput<String> signedInput = input.getEntity(privateKey, cert);

        if (!signedInput.verify(cert)) {
            throw new WebApplicationException(500);
        }
        String secret = signedInput.getEntity();
        logger.info("secret: " + secret);
        return secret;
    }

    @Path("encryptedEncrypted")
    @POST
    public String encryptedEncrypted(EnvelopedInput<EnvelopedInput<String>> input) throws Exception {
        EnvelopedInput<String> envelope = input.getEntity(privateKey, cert);
        String secret = envelope.getEntity(privateKey, cert);
        logger.info("secret: " + secret);
        return secret;
    }

    @Path("encryptSignSign")
    @POST
    public String encryptSignSign(SignedInput<SignedInput<EnvelopedInput<String>>> input) throws Exception {
        if (!input.verify(cert)) {
            throw new WebApplicationException(500);
        }
        SignedInput<EnvelopedInput<String>> inner = input.getEntity();
        if (!inner.verify(cert)) {
            throw new WebApplicationException(500);
        }
        final EnvelopedInput<String> envelop = inner.getEntity();
        String secret = envelop.getEntity(privateKey, cert);
        logger.info("secret: " + secret);
        return secret;
    }

    @Path("multipartEncrypted")
    @POST
    public String post(EnvelopedInput<MultipartInput> input) throws Exception {
        MultipartInput multipart = input.getEntity(privateKey, cert);
        InputPart inputPart = multipart.getParts().iterator().next();
        String secret = inputPart.getBody(String.class, null);
        logger.info("secret: " + secret);
        return secret;
    }


    public static X509Certificate cert;
    public static PrivateKey privateKey;

    static {
        try {
            cert = PemUtils.decodeCertificate(loadString("mycert.pem"));
            privateKey = PemUtils.decodePrivateKey(loadString("mycert-private.pem"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream loadString(String name) throws IOException {
        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(name);
        }
        if (stream == null) {
            stream = TestApplication.class.getResourceAsStream(name);
        }
        if (stream == null) {
            throw new RuntimeException();
        }
        return stream;
    }
}
