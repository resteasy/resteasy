package org.jboss.resteasy.test.crypto;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.test.crypto.resource.VerifyDecryptResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.lang.reflect.ReflectPermission;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-962
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class VerifyDecryptTest {
    private static final String RESPONSE_ERROR_MSG = "Response contains wrong content";

    protected static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");

    public static X509Certificate cert;
    public static PrivateKey privateKey;
    private static ResteasyClient client;
    static final String certPemPath;
    static final String certPrivatePemPath;

    static {
        certPemPath = TestUtil.getResourcePath(VerifyDecryptTest.class, "VerifyDecryptMycert.pem");
        certPrivatePemPath = TestUtil.getResourcePath(VerifyDecryptTest.class, "VerifyDecryptMycertPrivate.pem");
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void close() {
        client.close();
        client = null;
    }

    @Deployment
    public static Archive<?> deploy() throws Exception {
        cert = PemUtils.decodeCertificate(new FileInputStream(certPemPath));
        privateKey = PemUtils.decodePrivateKey(new FileInputStream(certPrivatePemPath));

        WebArchive war = TestUtil.prepareArchive(VerifyDecryptTest.class.getSimpleName());
        war.addAsResource(VerifyDecryptTest.class.getPackage(), "VerifyDecryptMycert.pem", "mycert.pem");
        war.addAsResource(VerifyDecryptTest.class.getPackage(), "VerifyDecryptMycertPrivate.pem", "mycert-private.pem");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, VerifyDecryptResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, VerifyDecryptTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Encryption output "application/pkcs7-mime"
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncrypt() throws Exception {
        EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        output.setCertificate(cert);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/encrypt"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Signing text/plain output
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSign() throws Exception {
        SignedOutput signed = new SignedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        signed.setPrivateKey(privateKey);
        signed.setCertificate(cert);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/sign"));
        Response res = target.request().post(Entity.entity(signed, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Encryption and signing test, output type is "application/pkcs7-mime"
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncryptSign() throws Exception {
        EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        output.setCertificate(cert);
        SignedOutput signed = new SignedOutput(output, "application/pkcs7-mime");
        signed.setCertificate(cert);
        signed.setPrivateKey(privateKey);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/encryptSign"));
        Response res = target.request().post(Entity.entity(signed, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Encryption and signing test, output type is "multipart/signed"
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSignEncrypt() throws Exception {
        SignedOutput signed = new SignedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        signed.setPrivateKey(privateKey);
        signed.setCertificate(cert);
        EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
        output.setCertificate(cert);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/signEncrypt"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Encrepted input and output
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncryptedEncrypted() {
        MultipartOutput multipart = new MultipartOutput();
        multipart.addPart("xanadu", MediaType.TEXT_PLAIN_TYPE);

        EnvelopedOutput innerPart = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        innerPart.setCertificate(cert);

        EnvelopedOutput output = new EnvelopedOutput(innerPart, "application/pkcs7-mime");
        output.setCertificate(cert);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/encryptedEncrypted"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Encrepted input and output
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncryptSignSign() throws Exception {
        EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
        output.setCertificate(cert);
        SignedOutput signed = new SignedOutput(output, "application/pkcs7-mime");
        signed.setCertificate(cert);
        signed.setPrivateKey(privateKey);
        SignedOutput resigned = new SignedOutput(signed, "multipart/signed");
        resigned.setCertificate(cert);
        resigned.setPrivateKey(privateKey);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/encryptSignSign"));
        Response res = target.request().post(Entity.entity(resigned, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }

    /**
     * @tpTestDetails Encrypted multipart output
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipartEncrypted() {
        MultipartOutput multipart = new MultipartOutput();
        multipart.addPart("xanadu", MediaType.TEXT_PLAIN_TYPE);
        EnvelopedOutput output = new EnvelopedOutput(multipart, MULTIPART_MIXED);
        output.setCertificate(cert);
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/multipartEncrypted"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "xanadu", result);
    }
}
