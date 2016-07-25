package org.jboss.resteasy.test.crypto.resource;

import org.jboss.resteasy.security.BouncyIntegration;
import org.jboss.resteasy.security.KeyTools;
import org.jboss.resteasy.security.smime.SignedOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Path("/test")
public class PKCS7SignatureSmokeResource {
    private X509Certificate cert;
    private PrivateKey privateKey;

    public PKCS7SignatureSmokeResource() {
        try {
            BouncyIntegration.init();
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA", "BC").generateKeyPair();
            privateKey = keyPair.getPrivate();
            cert = KeyTools.generateTestCertificate(keyPair);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("signed/pkcs7-signature")
    @Produces("application/pkcs7-signature")
    public SignedOutput get() {
        SignedOutput output = new SignedOutput("hello world", "text/plain");
        output.setCertificate(cert);
        output.setPrivateKey(privateKey);
        return output;
    }

    @GET
    @Path("signed/text")
    @Produces("text/plain")
    public SignedOutput getText() {
        SignedOutput output = new SignedOutput("hello world", "text/plain");
        output.setCertificate(cert);
        output.setPrivateKey(privateKey);
        return output;
    }

}
