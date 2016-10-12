package org.jboss.resteasy.test.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSEnvelopedDataStreamGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientId;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnveloped;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMEException;
import org.bouncycastle.mail.smime.SMIMEUtil;
import org.bouncycastle.operator.OutputEncryptor;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

/**
 * @tpSubChapter Crypto
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for sign by content of message signed by X509Certificate.
 * @tpSince RESTEasy 3.0.16
 */
public class EnvelopedTest {

    protected static final Logger logger = LogManager.getLogger(EnvelopedTest.class.getName());
    private static String python_smime = "MIIBagYJKoZIhvcNAQcDoIIBWzCCAVcCAQAxgewwgekCAQAwUjBFMQswCQYDVQQG\n" +
            "EwJBVTETMBEGA1UECBMKU29tZS1TdGF0ZTEhMB8GA1UEChMYSW50ZXJuZXQgV2lk\n" +
            "Z2l0cyBQdHkgTHRkAgkA7oW81OriflAwDQYJKoZIhvcNAQEBBQAEgYA1AWIoRMsb\n" +
            "Gv2DsHLjcvu6URZPqS0atjGW7uqlthmoQ4XB+l0y+iy2rXFuJnz+iLp/EIn92UpR\n" +
            "ZeFHPoQEDklkk5QqRaIBvkZiJgiPs9VuWiXVfHeOei9Oneyfja9Q88eFWHFToWok\n" +
            "LIDie+Wt/mMYY23QSVTY3r+cgTnOyV8gyDBjBgkqhkiG9w0BBwEwFAYIKoZIhvcN\n" +
            "AwcECJYFaD/eHDkHgEDIBLBzczEdLLk7nQzORmVist6gv30Ez9LCzHlnFteU+jVr\n" +
            "zAUGo6VoZZMmyLVeYEZoXqEjY6fN+rpSWoUVtNQM";
    private static X509Certificate cert;
    private static PrivateKey privateKey;
    static final String certPemPath = TestUtil.getResourcePath(EnvelopedTest.class, "SignedMycert.pem");
    static final String certPrivatePemPath = TestUtil.getResourcePath(EnvelopedTest.class, "MycertPrivate.pem");

    @BeforeClass
    public static void setup() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        InputStream certIs = new FileInputStream(certPemPath);
        cert = PemUtils.decodeCertificate(certIs);

        InputStream privateIs = new FileInputStream(certPrivatePemPath);
        privateKey = PemUtils.decodePrivateKey(privateIs);
    }

    /**
     * @tpTestDetails Check body of message content
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBody() throws Exception {
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC)
                .setProvider("BC")
                .build();
        JceKeyTransRecipientInfoGenerator infoGenerator = new JceKeyTransRecipientInfoGenerator(cert);
        CMSEnvelopedDataStreamGenerator generator = new CMSEnvelopedDataStreamGenerator();
        generator.addRecipientInfoGenerator(infoGenerator);


        InternetHeaders internetHeaders = new InternetHeaders();
        internetHeaders.addHeader("Content-Type", "application/xml");

        MimeBodyPart mimeBodyPart = new MimeBodyPart(internetHeaders, "<customer name=\"bill\"/>".getBytes());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStream encrypted = generator.open(os, encryptor);

        mimeBodyPart.writeTo(encrypted);
        encrypted.close();

        String str = Base64.encodeBytes(os.toByteArray(), Base64.DO_BREAK_LINES);

        internetHeaders = new InternetHeaders();
        internetHeaders.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
        internetHeaders.addHeader("Content-Type", "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"");
        internetHeaders.addHeader("Content-Transfer-Encoding", "base64");
        MimeBodyPart newMimeBodyPart = new MimeBodyPart(internetHeaders, str.getBytes());

        newMimeBodyPart = decode2Mime(newMimeBodyPart);

        Assert.assertEquals("Wrong type of mimeBodyPart content", "application/xml", newMimeBodyPart.getContentType());

        String body = toString(newMimeBodyPart.getInputStream());
        Assert.assertEquals("Wrong decoded content", "<customer name=\"bill\"/>", body.trim());
    }

    /**
     * @tpTestDetails Check headers
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHeaders()
            throws Exception {
        InternetHeaders internetHeaders = new InternetHeaders();
        internetHeaders.addHeader("Content-Type", "application/xml");

        MimeBodyPart _msg = new MimeBodyPart(internetHeaders, "<customer name=\"bill\"/>".getBytes());


        SMIMEEnvelopedGenerator gen = new SMIMEEnvelopedGenerator();
        OutputEncryptor encryptor = new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC)
                .setProvider("BC")
                .build();

        gen.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(cert).setProvider("BC"));

        // generate a MimeBodyPart object which encapsulates the content
        // we want encrypted.
        MimeBodyPart mp = gen.generate(_msg, encryptor);

        output(mp);
    }

    private void output(MimeBodyPart mimeBodyPart) throws IOException, MessagingException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        mimeBodyPart.writeTo(os);
        String s = new String(os.toByteArray());
        logger.info(s);
    }

    /**
     * @tpTestDetails Check bython generated content
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFromPythonGenerated() throws Exception {
        {
            InternetHeaders internetHeaders = new InternetHeaders();
            internetHeaders.addHeader("Content-Disposition", "attachment; filename=\"smime.p7m\"");
            internetHeaders.addHeader("Content-Type", "application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"");
            internetHeaders.addHeader("Content-Transfer-Encoding", "base64");
            MimeBodyPart mp = new MimeBodyPart(internetHeaders, python_smime.getBytes());

            output(mp);
            logger.info("------------");
            mp = decode2Mime(mp);

            Assert.assertEquals("Wrong media type of content", "application/xml", mp.getContentType());

            String body = toString(mp.getInputStream());
            Assert.assertEquals("Wrong decoded content", "<customer name=\"bill\"/>", body.trim());
        }
        {
            ByteArrayInputStream is = new ByteArrayInputStream(python_smime.getBytes(StandardCharsets.UTF_8));
            MimeBodyPart mp = decode2Mime(is);

            Assert.assertEquals("Wrong media type of content", "application/xml", mp.getContentType());

            String body = toString(mp.getInputStream());
            Assert.assertEquals("Wrong decoded content", "<customer name=\"bill\"/>", body.trim());
        }
    }

    private static String toString(InputStream is) throws Exception {
        DataInputStream dis = new DataInputStream(is);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        dis.close();
        return new String(bytes);
    }

    private MimeBodyPart decode2Mime(InputStream body) throws MessagingException, CMSException, SMIMEException, NoSuchProviderException, IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("Content-Disposition: attachment; filename=\"smime.p7m\"\r\n");
        builder.append("Content-Type: application/pkcs7-mime; smime-type=enveloped-data; name=\"smime.p7m\"\r\n");
        builder.append("Content-Transfer-Encoding: base64\r\n\r\n");
        ByteArrayInputStream is = new ByteArrayInputStream(builder.toString().getBytes(StandardCharsets.UTF_8));
        MimeBodyPart mp = new MimeBodyPart(new SequenceInputStream(is, body));
        return decode2Mime(mp);
    }

    private MimeBodyPart decode2Mime(MimeBodyPart mp) throws MessagingException, CMSException, SMIMEException, NoSuchProviderException, IOException {
        SMIMEEnveloped m = new SMIMEEnveloped(mp);
        RecipientId recId = new JceKeyTransRecipientId(cert);

        RecipientInformationStore recipients = m.getRecipientInfos();
        RecipientInformation recipient = recipients.get(recId);
        JceKeyTransRecipient pKeyRecp = new JceKeyTransEnvelopedRecipient(privateKey);

        return SMIMEUtil.toMimeBodyPart(recipient.getContent(pKeyRecp));
    }
}
