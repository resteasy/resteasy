package org.jboss.resteasy.test.crypto;

import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for sign by PKCS8EncodedKeySpec and X509EncodedKeySpec.
 * @tpSince RESTEasy 3.0.16
 */
public class ExampleSignTest {
    static final String publicFileS = TestUtil.getResourcePath(ExampleSignTest.class, "ExampleSignPublicDkimKey.pem");
    static final String privateFileS = TestUtil.getResourcePath(ExampleSignTest.class, "ExampleSignPrivateDkimKey.der");

    public static PrivateKey getPrivate(InputStream is) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        byte[] keyBytes = new byte[dis.available()];
        dis.readFully(keyBytes);
        dis.close();

        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public PublicKey getPublic(InputStream is) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        byte[] pemFile = new byte[dis.available()];
        dis.readFully(pemFile);
        String pem = new String(pemFile);
        pem = pem.replace("-----BEGIN PUBLIC KEY-----", "");
        pem = pem.replace("-----END PUBLIC KEY-----", "");
        pem = pem.trim();

        byte[] der = Base64.decode(pem);

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(der);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    /**
     * @tpTestDetails Check pem file
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPemFiles() throws Exception {
        File publicFile = new File(publicFileS);
        File privateFile = new File(privateFileS);
        InputStream publicIs = new FileInputStream(publicFile);
        InputStream privateIs = new FileInputStream(privateFile);

        PublicKey publicKey = getPublic(publicIs);
        PrivateKey privateKey = getPrivate(privateIs);

        Signature instance = Signature.getInstance("SHA256withRSA");
        instance.initSign(privateKey);
        instance.update("from-java".getBytes());
        byte[] signatureBytes = instance.sign();

        Signature verify = Signature.getInstance("SHA256withRSA");
        verify.initVerify(publicKey);
        verify.update("from-java".getBytes());
        Assert.assertTrue("Sign was unsuccessful", verify.verify(signatureBytes));
    }
}