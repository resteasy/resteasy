package org.jboss.resteasy.test.crypto;

import java.io.FileInputStream;
import java.lang.reflect.ReflectPermission;
import java.security.PrivateKey;
import java.security.SecurityPermission;
import java.security.cert.X509Certificate;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.test.crypto.resource.VerifyDecryptResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Crypto
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-962
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                // Can be removed when WFLY-17061 is resolved
                DeploymentDescriptors.addModuleFilePermission("org.eclipse.angus.activation", "org.eclipse.angus.mail"),
                // Can be removed when WFLY-17061 is resolved
                new RuntimePermission("getClassLoader"),
                new RuntimePermission("getenv.org.apache.james.mime4j.defaultStorageProvider"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                // These two can be removed once RESTEASY-3344 is resolved and the WildFly upgrade (WFLY-18231) is done
                new SecurityPermission("removeProviderProperty.BC"),
                new SecurityPermission("putProviderProperty.BC")), "permissions.xml");
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
        ResteasyWebTarget target = client.target(generateURL("/encrypt"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/sign"));
        Response res = target.request().post(Entity.entity(signed, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/encryptSign"));
        Response res = target.request().post(Entity.entity(signed, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/signEncrypt"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/encryptedEncrypted"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/encryptSignSign"));
        Response res = target.request().post(Entity.entity(resigned, "multipart/signed"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
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
        ResteasyWebTarget target = client.target(generateURL("/multipartEncrypted"));
        Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
        String result = res.readEntity(String.class);
        Assertions.assertEquals("xanadu", result, RESPONSE_ERROR_MSG);
    }
}
