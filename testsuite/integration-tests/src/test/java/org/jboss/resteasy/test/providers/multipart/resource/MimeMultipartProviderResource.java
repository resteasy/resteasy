package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.junit.Assert;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Assert.assertEquals(ERR_MULTIPART_FORM, "Bill", form.name.trim());
        Assert.assertEquals(ERR_MULTIPART_FORM, "hello world", new String(form.file).trim());
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
        Assert.assertEquals(ERR_NUMBER, 2, multipart.getParts().size());

        Assert.assertTrue(ERR_MULTIPART_FORM, multipart.getFormDataMap().containsKey("bill"));
        Assert.assertTrue(ERR_MULTIPART_FORM, multipart.getFormDataMap().containsKey("monica"));

        logger.info(multipart.getFormDataMap().get("bill").get(0).getBodyAsString());
        MimeMultipartProviderCustomer cust = multipart.getFormDataPart("bill", MimeMultipartProviderCustomer.class, null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_MULTIPART_FORM, "bill", cust.getName());

        cust = multipart.getFormDataPart("monica", MimeMultipartProviderCustomer.class, null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "monica", cust.getName());

    }

    @PUT
    @Path("related")
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    public void putMultipartRelated(MultipartRelatedInput multipart)
            throws IOException {
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, MultipartConstants.APPLICATION_XOP_XML, multipart.getType());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "<mymessage.xml@example.org>", multipart.getStart());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "text/xml", multipart.getStartInfo());
        Assert.assertEquals(ERR_NUMBER, 3, multipart.getParts().size());
        Iterator<InputPart> inputParts = multipart.getParts().iterator();
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, inputParts.next(), multipart.getRootPart());
        InputPart rootPart = multipart.getRootPart();

        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "application", rootPart.getMediaType().getType());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "xop+xml", rootPart.getMediaType().getSubtype());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, StandardCharsets.UTF_8.name(), rootPart.getMediaType().getParameters().get("charset"));
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "text/xml", rootPart.getMediaType().getParameters().get("type"));
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "<mymessage.xml@example.org>", rootPart.getHeaders().getFirst("Content-ID"));
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "8bit", rootPart.getHeaders().getFirst("Content-Transfer-Encoding"));
        Assert.assertEquals(ERR_VALUE,
                "<m:data xmlns:m='http://example.org/stuff'>"
                        + "<m:photo><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/></m:photo>"
                        + "<m:sig><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/my.hsh'/></m:sig>"
                        + "</m:data>", rootPart.getBodyAsString());

        InputPart relatedPart1 = inputParts.next();
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "image", relatedPart1.getMediaType().getType());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "png", relatedPart1.getMediaType().getSubtype());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "<http://example.org/me.png>", relatedPart1
                .getHeaders().getFirst("Content-ID"));
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "binary", relatedPart1.getHeaders().getFirst("Content-Transfer-Encoding"));
        Assert.assertEquals(ERR_VALUE, "// binary octets for png", relatedPart1.getBodyAsString());

        InputPart relatedPart2 = inputParts.next();
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "application", relatedPart2.getMediaType().getType());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "pkcs7-signature", relatedPart2.getMediaType().getSubtype());
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "<http://example.org/me.hsh>", relatedPart2
                .getHeaders().getFirst("Content-ID"));
        Assert.assertEquals(ERR_MULTIPART_PROPERTY, "binary", relatedPart2.getHeaders().getFirst("Content-Transfer-Encoding"));
        Assert.assertEquals(ERR_VALUE, "// binary octets for signature", relatedPart2.getBodyAsString());
    }

    @PUT
    @Path("form/map")
    @Consumes("multipart/form-data")
    public void putMultipartMap(Map<String, MimeMultipartProviderCustomer> multipart)
            throws IOException {
        Assert.assertEquals(ERR_NUMBER, 2, multipart.size());

        Assert.assertTrue(ERR_VALUE, multipart.containsKey("bill"));
        Assert.assertTrue(ERR_VALUE, multipart.containsKey("monica"));

        MimeMultipartProviderCustomer cust = multipart.get("bill");
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "bill", cust.getName());

        cust = multipart.get("monica");
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "monica", cust.getName());

    }

    @PUT
    @Path("multi")
    @Consumes("multipart/form-data")
    public void putMultipartData(MultipartInput multipart) throws IOException {
        Assert.assertEquals(ERR_NUMBER, 2, multipart.getParts().size());

        MimeMultipartProviderCustomer cust = multipart.getParts().get(0).getBody(MimeMultipartProviderCustomer.class,
                null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "bill", cust.getName());

        cust = multipart.getParts().get(1).getBody(MimeMultipartProviderCustomer.class, null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "monica", cust.getName());

    }

    @PUT
    @Path("mixed")
    @Consumes("multipart/mixed")
    public void putMultipartMixed(MultipartInput multipart) throws IOException {
        Assert.assertEquals(ERR_NUMBER, 2, multipart.getParts().size());

        MimeMultipartProviderCustomer cust = multipart.getParts().get(0).getBody(MimeMultipartProviderCustomer.class,
                null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "bill", cust.getName());

        cust = multipart.getParts().get(1).getBody(MimeMultipartProviderCustomer.class, null);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "monica", cust.getName());

    }

    @PUT
    @Path("multi/list")
    @Consumes("multipart/form-data")
    public void putMultipartList(List<MimeMultipartProviderCustomer> multipart) throws IOException {
        Assert.assertEquals(ERR_NUMBER, 2, multipart.size());

        MimeMultipartProviderCustomer cust = multipart.get(0);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "bill", cust.getName());

        cust = multipart.get(1);
        Assert.assertNotNull(ERR_CUST_NULL, cust);
        Assert.assertEquals(ERR_VALUE, "monica", cust.getName());

    }

    @PUT
    @Path("form/class")
    @Consumes("multipart/form-data")
    public void putMultipartForm(@MultipartForm Form form) throws IOException {
        Assert.assertNotNull(ERR_CUST_NULL, form.getBill());
        Assert.assertEquals(ERR_VALUE, "bill", form.getBill().getName());

        Assert.assertNotNull(ERR_CUST_NULL, form.getMonica());
        Assert.assertEquals(ERR_VALUE, "monica", form.getMonica().getName());
    }

    @PUT
    @Path("xop")
    @Consumes(MultipartConstants.MULTIPART_RELATED)
    public void putXopWithMultipartRelated(@XopWithMultipartRelated Xop xop)
            throws IOException {
        Assert.assertNotNull(ERR_CUST_NULL, xop.getBill());
        Assert.assertEquals(ERR_VALUE, "bill\u00E9", xop.getBill().getName());

        Assert.assertNotNull(ERR_CUST_NULL, xop.getMonica());
        Assert.assertEquals(ERR_VALUE, "monica", xop.getMonica().getName());
        Assert.assertNotNull(ERR_CUST_NULL, xop.getMyBinary());
        Assert.assertNotNull(ERR_CUST_NULL, xop.getMyDataHandler());
        Assert.assertEquals(ERR_VALUE, "Hello Xop World!", new String(xop.getMyBinary(),
        		StandardCharsets.UTF_8));
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
                Assert.assertEquals(ERR_VALUE, "Hello Xop World!", writer.toString());
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
