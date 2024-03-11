package org.jboss.resteasy.test.providers.multipart.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.junit.jupiter.api.Assertions;

@Path("/mime")
public class MimeMultipartProviderResource {

    public static class Form {
        @FormParam("bill")
        @PartType("application/xml")
        private MimeMultipartProviderCustomer bill;

        @FormParam("monica")
        @PartType("application/xml")
        private MimeMultipartProviderCustomer monica;

        public Form() {
        }

        public Form(final MimeMultipartProviderCustomer bill, final MimeMultipartProviderCustomer monica) {
            this.bill = bill;
            this.monica = monica;
        }

        public MimeMultipartProviderCustomer getBill() {
            return bill;
        }

        public MimeMultipartProviderCustomer getMonica() {
            return monica;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Xop {
        private MimeMultipartProviderCustomer bill;

        private MimeMultipartProviderCustomer monica;

        @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
        private byte[] myBinary;

        @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
        private DataHandler myDataHandler;

        public Xop() {
        }

        public Xop(final MimeMultipartProviderCustomer bill, final MimeMultipartProviderCustomer monica, final byte[] myBinary,
                final DataHandler myDataHandler) {
            this.bill = bill;
            this.monica = monica;
            this.myBinary = myBinary;
            this.myDataHandler = myDataHandler;
        }

        public MimeMultipartProviderCustomer getBill() {
            return bill;
        }

        public MimeMultipartProviderCustomer getMonica() {
            return monica;
        }

        public byte[] getMyBinary() {
            return myBinary;
        }

        public DataHandler getMyDataHandler() {
            return myDataHandler;
        }
    }

    public static class Form2 {
        @FormParam("submit-name")
        public String name;

        @FormParam("files")
        public byte[] file;
    }

    private static Logger logger = Logger.getLogger(MimeMultipartProviderResource.class);
    private static final String ERR_MULTIPART_PROPERTY = "Property from multipart is not correct";
    private static final String ERR_MULTIPART_FORM = "Multipart form value is incorrect";
    private static final String ERR_NUMBER = "The number of enclosed bodypart objects doesn't match to the expectation";
    private static final String ERR_CUST_NULL = "The customer entity is not expected to be null";
    private static final String ERR_VALUE = "Unexpected value";

    @POST
    @Path("file/test")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("text/html")
    public String post(@MultipartForm Form2 form) {
        Assertions.assertEquals("Bill", form.name.trim(), ERR_MULTIPART_FORM);
        Assertions.assertEquals("hello world", new String(form.file).trim(), ERR_MULTIPART_FORM);
        return "hello world";
    }

    @PUT
    @Consumes("multipart/form-data")
    @Produces("text/plain")
    public String putData(MimeMultipart multipart) {
        StringBuilder b = new StringBuilder("Count: ");
        try {
            b.append(multipart.getCount());
            for (int i = 0; i < multipart.getCount(); i++) {
                try {
                    logger.debug(multipart.getBodyPart(i).getContent().toString());
                    logger.debug("bytes available {0}" + multipart.getBodyPart(i).getInputStream().available());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getCause());
                }
            }
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            logger.info(e.getCause());
        }
        return b.toString();
    }

    @PUT
    @Path("form")
    @Consumes("multipart/form-data")
    public void putMultipartFormData(MultipartFormDataInput multipart)
            throws IOException {
        Assertions.assertEquals(2, multipart.getParts().size(), ERR_NUMBER);

        Assertions.assertTrue(multipart.getFormDataMap().containsKey("bill"), ERR_MULTIPART_FORM);
        Assertions.assertTrue(multipart.getFormDataMap().containsKey("monica"), ERR_MULTIPART_FORM);

        logger.info(multipart.getFormDataMap().get("bill").get(0).getBodyAsString());
        MimeMultipartProviderCustomer cust = multipart.getFormDataPart("bill", MimeMultipartProviderCustomer.class, null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("bill", cust.getName(), ERR_MULTIPART_FORM);

        cust = multipart.getFormDataPart("monica", MimeMultipartProviderCustomer.class, null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("monica", cust.getName(), ERR_VALUE);

    }

    @PUT
    @Path("related")
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    public void putMultipartRelated(MultipartRelatedInput multipart)
            throws IOException {
        Assertions.assertEquals(MultipartConstants.APPLICATION_XOP_XML, multipart.getType(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("<mymessage.xml@example.org>", multipart.getStart(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("text/xml", multipart.getStartInfo(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals(3, multipart.getParts().size(), ERR_NUMBER);
        Iterator<InputPart> inputParts = multipart.getParts().iterator();
        Assertions.assertEquals(inputParts.next(), multipart.getRootPart(), ERR_MULTIPART_PROPERTY);
        InputPart rootPart = multipart.getRootPart();

        Assertions.assertEquals("application", rootPart.getMediaType().getType(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("xop+xml", rootPart.getMediaType().getSubtype(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals(StandardCharsets.UTF_8.name(),
                rootPart.getMediaType().getParameters().get("charset"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("text/xml", rootPart.getMediaType().getParameters().get("type"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("<mymessage.xml@example.org>",
                rootPart.getHeaders().getFirst("Content-ID"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("8bit", rootPart.getHeaders().getFirst("Content-Transfer-Encoding"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals(
                "<m:data xmlns:m='http://example.org/stuff'>"
                        + "<m:photo><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/></m:photo>"
                        + "<m:sig><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/my.hsh'/></m:sig>"
                        + "</m:data>",
                rootPart.getBodyAsString(), ERR_VALUE);

        InputPart relatedPart1 = inputParts.next();
        Assertions.assertEquals("image", relatedPart1.getMediaType().getType(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("png", relatedPart1.getMediaType().getSubtype(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("<http://example.org/me.png>", relatedPart1
                .getHeaders().getFirst("Content-ID"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("binary",
                relatedPart1.getHeaders().getFirst("Content-Transfer-Encoding"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("// binary octets for png", relatedPart1.getBodyAsString(), ERR_VALUE);

        InputPart relatedPart2 = inputParts.next();
        Assertions.assertEquals("application", relatedPart2.getMediaType().getType(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("pkcs7-signature", relatedPart2.getMediaType().getSubtype(), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("<http://example.org/me.hsh>", relatedPart2
                .getHeaders().getFirst("Content-ID"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("binary",
                relatedPart2.getHeaders().getFirst("Content-Transfer-Encoding"), ERR_MULTIPART_PROPERTY);
        Assertions.assertEquals("// binary octets for signature", relatedPart2.getBodyAsString(), ERR_VALUE);
    }

    @PUT
    @Path("form/map")
    @Consumes("multipart/form-data")
    public void putMultipartMap(Map<String, MimeMultipartProviderCustomer> multipart)
            throws IOException {
        Assertions.assertEquals(2, multipart.size(), ERR_NUMBER);

        Assertions.assertTrue(multipart.containsKey("bill"), ERR_VALUE);
        Assertions.assertTrue(multipart.containsKey("monica"), ERR_VALUE);

        MimeMultipartProviderCustomer cust = multipart.get("bill");
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("bill", cust.getName(), ERR_VALUE);

        cust = multipart.get("monica");
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("monica", cust.getName(), ERR_VALUE);

    }

    @PUT
    @Path("multi")
    @Consumes("multipart/form-data")
    public void putMultipartData(MultipartInput multipart) throws IOException {
        Assertions.assertEquals(2, multipart.getParts().size(), ERR_NUMBER);

        MimeMultipartProviderCustomer cust = multipart.getParts().get(0).getBody(MimeMultipartProviderCustomer.class,
                null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("bill", cust.getName(), ERR_VALUE);

        cust = multipart.getParts().get(1).getBody(MimeMultipartProviderCustomer.class, null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("monica", cust.getName(), ERR_VALUE);

    }

    @PUT
    @Path("mixed")
    @Consumes("multipart/mixed")
    public void putMultipartMixed(MultipartInput multipart) throws IOException {
        Assertions.assertEquals(2, multipart.getParts().size(), ERR_NUMBER);

        MimeMultipartProviderCustomer cust = multipart.getParts().get(0).getBody(MimeMultipartProviderCustomer.class,
                null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("bill", cust.getName(), ERR_VALUE);

        cust = multipart.getParts().get(1).getBody(MimeMultipartProviderCustomer.class, null);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("monica", cust.getName(), ERR_VALUE);

    }

    @PUT
    @Path("multi/list")
    @Consumes("multipart/form-data")
    public void putMultipartList(List<MimeMultipartProviderCustomer> multipart) throws IOException {
        Assertions.assertEquals(2, multipart.size(), ERR_NUMBER);

        MimeMultipartProviderCustomer cust = multipart.get(0);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("bill", cust.getName(), ERR_VALUE);

        cust = multipart.get(1);
        Assertions.assertNotNull(cust, ERR_CUST_NULL);
        Assertions.assertEquals("monica", cust.getName(), ERR_VALUE);

    }

    @PUT
    @Path("form/class")
    @Consumes("multipart/form-data")
    public void putMultipartForm(@MultipartForm Form form) throws IOException {
        Assertions.assertNotNull(form.getBill(), ERR_CUST_NULL);
        Assertions.assertEquals("bill", form.getBill().getName(), ERR_VALUE);

        Assertions.assertNotNull(form.getMonica(), ERR_CUST_NULL);
        Assertions.assertEquals("monica", form.getMonica().getName(), ERR_VALUE);
    }

    @PUT
    @Path("xop")
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    public void putXopWithMultipartRelated(@XopWithMultipartRelated Xop xop)
            throws IOException {
        Assertions.assertNotNull(xop.getBill(), ERR_CUST_NULL);
        Assertions.assertEquals("bill\u00E9", xop.getBill().getName(), ERR_VALUE);

        Assertions.assertNotNull(xop.getMonica(), ERR_CUST_NULL);
        Assertions.assertEquals("monica", xop.getMonica().getName(), ERR_VALUE);
        Assertions.assertNotNull(xop.getMyBinary(), ERR_CUST_NULL);
        Assertions.assertNotNull(xop.getMyDataHandler(), ERR_CUST_NULL);
        Assertions.assertEquals("Hello Xop World!", new String(xop.getMyBinary(),
                StandardCharsets.UTF_8), ERR_VALUE);
        // lets do it twice to test that we get different InputStream-s each
        // time.
        for (int fi = 0; fi < 2; fi++) {
            InputStream inputStream = xop.getMyDataHandler().getInputStream();
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                StringWriter writer = new StringWriter();
                char[] buffer = new char[4048];
                int n = 0;
                while ((n = inputStreamReader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                Assertions.assertEquals("Hello Xop World!", writer.toString(), ERR_VALUE);
            } finally {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                inputStream.close();
            }
        }
    }

    @PUT
    @Path("text")
    @Consumes("multipart/form-data")
    @Produces("text/plain")
    public void putData(String multipart) {
        logger.info(multipart);
    }

    @GET
    @Produces("multipart/mixed")
    public MimeMultipart getMimeMultipart() throws MessagingException {
        MimeMultipart multipart = new MimeMultipart("mixed");
        multipart.addBodyPart(createPart("Body of part 1", "text/plain",
                "This is a description"));
        multipart.addBodyPart(createPart("Body of part 2", "text/plain",
                "This is another description"));
        return multipart;
    }

    private MimeBodyPart createPart(String value, String type,
            String description) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        part.setDescription(description);
        part.setContent(value, type);
        return part;
    }
}
