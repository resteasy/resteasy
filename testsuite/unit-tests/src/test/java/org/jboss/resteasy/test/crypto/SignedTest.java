package org.jboss.resteasy.test.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESigned;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.jboss.resteasy.security.DerUtils;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for sign by X509Certificate.
 * @tpSince RESTEasy 3.0.16
 */
public class SignedTest {

    protected static final Logger logger = LogManager.getLogger(SignedTest.class.getName());
    private static X509Certificate cert;
    private static PrivateKey privateKey;
    private static PrivateKey badKey;
    static final String certPemPath;
    static final String certPrivatePemPath;
    static final String certPrivateKeyDerPath;
    static final String pythonPath;

    static {
        String base = TestUtil.getResourcePath(SignedTest.class, "");
        certPemPath = new StringBuilder().append(base).append("SignedMycert.pem").toString();
        certPrivatePemPath = new StringBuilder().append(base).append("MycertPrivate.pem").toString();
        certPrivateKeyDerPath = new StringBuilder().append(base).append("SignedPrivateDkimKeyDer.der").toString();
        pythonPath = new StringBuilder().append(base).append("SignedPython.txt").toString();
    }

    @BeforeClass
    public static void setup() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        InputStream certIs = new FileInputStream(certPemPath);
        cert = PemUtils.decodeCertificate(certIs);

        InputStream privateIs = new FileInputStream(certPrivatePemPath);
        privateKey = PemUtils.decodePrivateKey(privateIs);

        InputStream badIs = new FileInputStream(certPrivateKeyDerPath);
        badKey = DerUtils.decodePrivateKey(badIs);
    }

    private MimeBodyPart createMsg() throws MessagingException {
        InternetHeaders ih = new InternetHeaders();
        ih.addHeader("Content-Type", "application/xml");
        return new MimeBodyPart(ih, "<customer name=\"bill\"/>".getBytes());
    }

    private void output(MimeBodyPart mp) throws IOException, MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mp.writeTo(os);
        String s = new String(os.toByteArray());
        logger.info(s);
    }

    private void output(MimeMultipart mp) throws IOException, MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mp.writeTo(os);
        String s = new String(os.toByteArray());
        logger.info(s);
    }

    /**
     * @tpTestDetails Multipart test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultipart() throws Exception {
        MimeMultipart mp = new MimeMultipart();
        InternetHeaders ih = new InternetHeaders();
        ih.addHeader("Content-Type", "text/xml");
        MimeBodyPart bp = new MimeBodyPart(ih, "<customer/>".getBytes());
        mp.addBodyPart(bp);

        bp = new MimeBodyPart(ih, "<product/>".getBytes());
        mp.addBodyPart(bp);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mp.writeTo(os);
        String s = new String(os.toByteArray());

        logger.info(s);
        logger.info("************");

        String contentType = mp.getContentType();
        contentType = contentType.replace("\r\n", "").replace("\t", " ");
        logger.info("Content-Type: " + contentType);

        mp = new MimeMultipart(new ByteArrayDataSource(s.getBytes(), "multipart/signed"));
        logger.info("count: " + mp.getCount());
    }


    /**
     * @tpTestDetails Test for python sign format
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPythonSigned() throws Exception {
        final InputStream pythonIs = new FileInputStream(pythonPath);

        ByteArrayDataSource ds = new ByteArrayDataSource(pythonIs, "multipart/signed");
        MimeMultipart mm = new MimeMultipart(ds);

        logger.info(mm.getContentType());

        logger.info("Multipart.count(): " + mm.getCount());

        MimeBodyPart mbp = (MimeBodyPart) mm.getBodyPart(0);

        output(mbp);

        SMIMESigned signed = new SMIMESigned(mm);

        SignerInformationStore signers = signed.getSignerInfos();
        Assert.assertEquals("Wrong count of signers", 1, signers.size());
        SignerInformation signer = signers.getSigners().iterator().next();
        Assert.assertTrue("Unsuccessful verification of signer", signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert.getPublicKey())));
    }

    /**
     * @tpTestDetails Check output after signing
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOutput() throws Exception {
        {
            SMIMESignedGenerator gen = new SMIMESignedGenerator();
            SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
            gen.addSignerInfoGenerator(signer);

            MimeMultipart mp = gen.generate(createMsg());
            output(mp);
        }

        {
            SMIMESignedGenerator gen = new SMIMESignedGenerator();
            SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
            gen.addSignerInfoGenerator(signer);

            MimeMultipart mp = gen.generate(createMsg());

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            mp.writeTo(os);

            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            String contentType = mp.getContentType();
            contentType = contentType.replace("\r\n", "").replace("\t", " ");

            ByteArrayDataSource ds = new ByteArrayDataSource(is, contentType);
            MimeMultipart mm = new MimeMultipart(ds);
            MimeBodyPart part = (MimeBodyPart) mm.getBodyPart(0);
        }
    }

    /**
     * @tpTestDetails Test python-style verification
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPythonVerified() throws Exception {
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", privateKey, cert);
        gen.addSignerInfoGenerator(signer);

        MimeMultipart mp = gen.generate(createMsg());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mp.writeTo(os);
        String contentType = mp.getContentType();
        contentType = contentType.replace("\r\n", "").replace("\t", " ");
        logger.info(contentType);
        String s = new String(os.toByteArray());
        StringBuilder builder = new StringBuilder();
        builder.append("Content-Type: ").append(contentType).append("\r\n\r\n").append(s);
        String output = builder.toString();

        FileOutputStream fp = new FileOutputStream("target/smime_signed.txt");
        fp.write(output.getBytes());
        fp.close();
    }

    /**
     * @tpTestDetails Test python-style wrong verification
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPythonVerifiedBad() throws Exception {
        SMIMESignedGenerator gen = new SMIMESignedGenerator();
        SignerInfoGenerator signer = new JcaSimpleSignerInfoGeneratorBuilder().setProvider("BC").build("SHA1WITHRSA", badKey, cert);
        gen.addSignerInfoGenerator(signer);

        MimeMultipart mp = gen.generate(createMsg());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mp.writeTo(os);
        String contentType = mp.getContentType();
        contentType = contentType.replace("\r\n", "").replace("\t", " ");
        logger.info(contentType);
        String s = new String(os.toByteArray());
        StringBuilder builder = new StringBuilder();
        builder.append("Content-Type: ").append(contentType).append("\r\n\r\n").append(s);
        String output = builder.toString();

        FileOutputStream fp = new FileOutputStream("target/smime_signed_bad.txt");
        fp.write(output.getBytes());
        fp.close();
    }
}
