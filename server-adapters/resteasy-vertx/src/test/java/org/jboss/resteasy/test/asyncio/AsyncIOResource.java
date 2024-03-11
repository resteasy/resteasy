package org.jboss.resteasy.test.asyncio;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.imageio.IIOImage;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import jakarta.activation.DataSource;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetHeaders;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.jboss.resteasy.annotations.jaxrs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.FileRange;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl.BuilderImpl;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncStreamingOutput;
import org.junit.jupiter.api.Assertions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.Context;

@Path("async-io")
public class AsyncIOResource {

    public static class JacksonType {
        public String foo = "bar";
        @JsonIgnore
        public String notHere = "OUCH";
    }

    public static class MyForm {
        @FormParam
        @PartType(MediaType.TEXT_PLAIN)
        public String foo = "bar";
    }

    @XmlRootElement
    public static class XopRelatedForm {
        @XmlMimeType(MediaType.TEXT_PLAIN)
        public byte[] foo = OK_BYTES;
    }

    @XmlRootElement
    public static class JaxbXmlRootElement {
        public String foo = "bar";
    }

    @XmlType
    public static class JaxbXmlType {
        public String foo = "bar";
    }

    @XmlRootElement
    public static class JaxbXmlSeeAlsoDog extends JaxbXmlSeeAlsoAnimal {
        public String foo = "bar";
    }

    @XmlSeeAlso(JaxbXmlSeeAlsoDog.class)
    public static class JaxbXmlSeeAlsoAnimal {

    }

    static final byte[] OK_BYTES = "OK".getBytes(Charset.forName("UTF-8"));
    private static final byte[] XML_BYTES = "<?xml version='1.0'?><foo/>".getBytes(Charset.forName("UTF-8"));

    public class OKDataSource implements DataSource {

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(OK_BYTES);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public String getContentType() {
            return MediaType.TEXT_PLAIN;
        }

        @Override
        public String getName() {
            return null;
        }
    }

    @GET
    @Path("blocking-writer-on-io-thread")
    public BlockingWriterData blockingWriterOnIoThread() {
        Assertions.assertTrue(Context.isOnEventLoopThread());
        return new BlockingWriterData();
    }

    @GET
    @Path("async-writer-on-io-thread")
    public AsyncWriterData asyncWriterOnIoThread() {
        return new AsyncWriterData(true, false);
    }

    @GET
    @Path("slow-async-writer-on-io-thread")
    public AsyncWriterData slowAsyncWriterOnIoThread() {
        return new AsyncWriterData(true, true);
    }

    @GET
    @Path("blocking-writer-on-worker-thread")
    public CompletionStage<BlockingWriterData> blockingWriterOnWorkerThread() {
        return CompletableFuture.supplyAsync(() -> new BlockingWriterData());
    }

    @GET
    @Path("async-writer-on-worker-thread")
    public CompletionStage<AsyncWriterData> asyncWriterOnWorkerThread() {
        return CompletableFuture.supplyAsync(() -> new AsyncWriterData(false, true));
    }

    @GET
    @Path("slow-async-writer-on-worker-thread")
    public CompletionStage<AsyncWriterData> slowAsyncWriterOnWorkerThread() {
        return CompletableFuture.supplyAsync(() -> new AsyncWriterData(false, true));
    }

    private <T> CompletionStage<T> async(T value) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return value;
        });
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/reject-blocking-interceptor")
    @GET
    public String getTextRejectBlockingInterceptor() {
        return "OK";
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/text")
    @GET
    public CompletionStage<String> getTextBlocking() {
        return async("OK");
    }

    @WithAsyncWriterInterceptor
    @Path("async/text")
    @GET
    public String getTextAsync() {
        return "OK";
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/bytes")
    @GET
    public CompletionStage<byte[]> getBytesBlocking() {
        return async(OK_BYTES);
    }

    @WithAsyncWriterInterceptor
    @Path("async/bytes")
    @GET
    public byte[] getBytesAsync() {
        return OK_BYTES;
    }

    @Produces(MediaType.TEXT_PLAIN)
    @WithBlockingWriterInterceptor
    @Path("blocking/default-text")
    @GET
    public CompletionStage<Character> getDefaultTextBlocking() {
        return async('K');
    }

    @Produces(MediaType.TEXT_PLAIN)
    @WithAsyncWriterInterceptor
    @Path("async/default-text")
    @GET
    public char getDefaultTextAsync() {
        return 'K';
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/number")
    @GET
    public CompletionStage<Integer> getNumberBlocking() {
        return async(42);
    }

    @WithAsyncWriterInterceptor
    @Path("async/number")
    @GET
    public int getNumberAsync() {
        return 42;
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/boolean")
    @GET
    public CompletionStage<Boolean> getBooleanBlocking() {
        return async(true);
    }

    @WithAsyncWriterInterceptor
    @Path("async/boolean")
    @GET
    public boolean getBooleanAsync() {
        return true;
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/input-stream")
    @GET
    public CompletionStage<InputStream> getInputStreamBlocking() {
        return async(new ByteArrayInputStream(OK_BYTES));
    }

    @WithAsyncWriterInterceptor
    @Path("async/input-stream")
    @GET
    public InputStream getInputStreamAsync() {
        return new ByteArrayInputStream(OK_BYTES);
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/reader")
    @GET
    public CompletionStage<Reader> getReaderBlocking() {
        return async(new StringReader("OK"));
    }

    @WithAsyncWriterInterceptor
    @Path("async/reader")
    @GET
    public Reader getReaderAsync() {
        return new StringReader("OK");
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/data-source")
    @GET
    public CompletionStage<DataSource> getDataSourceBlocking() {
        return async(new OKDataSource());
    }

    @WithAsyncWriterInterceptor
    @Path("async/data-source")
    @GET
    public DataSource getDataSourceAsync() {
        return new OKDataSource();
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/source")
    @GET
    public CompletionStage<Source> getSourceBlocking() {
        return async(new StreamSource(new ByteArrayInputStream(XML_BYTES)));
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/source")
    @GET
    public Source getSourceAsync() {
        return new StreamSource(new ByteArrayInputStream(XML_BYTES));
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/document")
    @GET
    public CompletionStage<Document> getDocumentBlocking() throws ParserConfigurationException {
        return async(makeDocument());
    }

    private Document makeDocument() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("foo");
        doc.appendChild(rootElement);
        return doc;
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/document")
    @GET
    public Document getDocumentAsync() throws ParserConfigurationException {
        return makeDocument();
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/file")
    @GET
    public CompletionStage<File> getFileBlocking() {
        return async(new File("src/test/resources/file.txt"));
    }

    @WithAsyncWriterInterceptor
    @Path("async/file")
    @GET
    public File getFileAsync() {
        return new File("src/test/resources/file.txt");
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/file-range")
    @GET
    public CompletionStage<FileRange> getFileRangeBlocking() {
        return async(new FileRange(new File("src/test/resources/file.txt"), 0, 2));
    }

    @WithAsyncWriterInterceptor
    @Path("async/file-range")
    @GET
    public FileRange getFileRangeAsync() {
        return new FileRange(new File("src/test/resources/file.txt"), 0, 2);
    }

    @WithBlockingWriterInterceptor
    @Path("blocking/streaming-output")
    @GET
    public CompletionStage<StreamingOutput> getStreamingOutputBlocking() {
        return async(new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                output.write(OK_BYTES);
            }
        });
    }

    @WithAsyncWriterInterceptor
    @Path("async/streaming-output")
    @GET
    public AsyncStreamingOutput getStreamingOutputAsync() {
        return new AsyncStreamingOutput() {
            @Override
            public CompletionStage<Void> asyncWrite(AsyncOutputStream output) {
                return output.asyncWrite(OK_BYTES);
            }
        };
    }

    @Produces("image/jpeg")
    @WithBlockingWriterInterceptor
    @Path("blocking/iioimage")
    @GET
    public CompletionStage<IIOImage> getIIOImageBlocking() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        return async(new IIOImage(image, Collections.emptyList(), null));
    }

    @Produces("image/jpeg")
    @WithAsyncWriterInterceptor
    @Path("async/iioimage")
    @GET
    public IIOImage getIIOImageAsync() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        return new IIOImage(image, Collections.emptyList(), null);
    }

    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @WithBlockingWriterInterceptor
    @Path("blocking/form-url-encoded")
    @GET
    public CompletionStage<MultivaluedMap<?, ?>> getFormUrlEncodedBlocking() {
        MultivaluedMapImpl<Object, Object> map = new MultivaluedMapImpl<>();
        map.put("foo", Arrays.asList("bar"));
        return async(map);
    }

    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @WithAsyncWriterInterceptor
    @Path("async/form-url-encoded")
    @GET
    public MultivaluedMap<?, ?> getFormUrlEncodedAsync() {
        MultivaluedMapImpl<Object, Object> map = new MultivaluedMapImpl<>();
        map.put("foo", Arrays.asList("bar"));
        return map;
    }

    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @WithBlockingWriterInterceptor
    @Path("blocking/jax-rs-form")
    @GET
    public CompletionStage<Form> getJaxrsFormBlocking() {
        return async(new Form("foo", "bar"));
    }

    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @WithAsyncWriterInterceptor
    @Path("async/jax-rs-form")
    @GET
    public Form getJaxrsFormAsync() {
        return new Form("foo", "bar");
    }

    // CUT HERE ASYNC PROVIDERS

    @Produces(MediaType.APPLICATION_ATOM_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/atom-feed")
    @GET
    public CompletionStage<Feed> getAtomFeedBlocking() {
        Feed feed = new Feed();
        feed.setLanguage("fubar");
        return async(feed);
    }

    @Produces(MediaType.APPLICATION_ATOM_XML)
    @WithAsyncWriterInterceptor
    @Path("async/atom-feed")
    @GET
    public Feed getAtomFeedAsync() {
        Feed feed = new Feed();
        feed.setLanguage("fubar");
        return feed;
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-xml-see-also")
    @GET
    public CompletionStage<JaxbXmlSeeAlsoAnimal> getJaxbXmlSeeAlsoBlocking() {
        return async(new JaxbXmlSeeAlsoDog());
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-xml-see-also")
    @GET
    public JaxbXmlSeeAlsoAnimal getJaxbXmlSeeAlsoAsync() {
        return new JaxbXmlSeeAlsoDog();
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-xml-root-element")
    @GET
    public CompletionStage<JaxbXmlRootElement> getJaxbXmlRootElement() {
        return async(new JaxbXmlRootElement());
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-xml-root-element")
    @GET
    public JaxbXmlRootElement getJaxbXmlRootElementAsync() {
        return new JaxbXmlRootElement();
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-element")
    @GET
    public CompletionStage<JAXBElement<JaxbXmlRootElement>> getJaxbElement() {
        return async(
                new JAXBElement<>(QName.valueOf("jaxbXmlRootElement"), JaxbXmlRootElement.class, new JaxbXmlRootElement()));
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-element")
    @GET
    public JAXBElement<JaxbXmlRootElement> getJaxbElementAsync() {
        return new JAXBElement<>(QName.valueOf("jaxbXmlRootElement"), JaxbXmlRootElement.class, new JaxbXmlRootElement());
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-xml-type")
    @GET
    public CompletionStage<JaxbXmlType> getJaxbXmlType() {
        return async(new JaxbXmlType());
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-xml-type")
    @GET
    public JaxbXmlType getJaxbXmlTypeAsync() {
        return new JaxbXmlType();
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-collection")
    @GET
    public CompletionStage<Collection<JaxbXmlRootElement>> getJaxbCollection() {
        return async(Arrays.asList(new JaxbXmlRootElement()));
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-collection")
    @GET
    public Collection<JaxbXmlRootElement> getJaxbCollectionAsync() {
        return Arrays.asList(new JaxbXmlRootElement());
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithBlockingWriterInterceptor
    @Path("blocking/jaxb-map")
    @GET
    public CompletionStage<Map<String, JaxbXmlRootElement>> getJaxbMap() {
        return async(Collections.singletonMap("foo", new JaxbXmlRootElement()));
    }

    @Produces(MediaType.APPLICATION_XML)
    @WithAsyncWriterInterceptor
    @Path("async/jaxb-map")
    @GET
    public Map<String, JaxbXmlRootElement> getJaxbMapAsync() {
        return Collections.singletonMap("foo", new JaxbXmlRootElement());
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-output")
    @GET
    public CompletionStage<MultipartOutput> getMultipartOutput() {
        MultipartOutput ret = new MultipartOutput();
        ret.addPart("foo", MediaType.TEXT_PLAIN_TYPE);
        return async(ret);
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithAsyncWriterInterceptor
    @Path("async/multipart-output")
    @GET
    public MultipartOutput getMultipartOutputAsync() {
        MultipartOutput ret = new MultipartOutput();
        ret.addPart("foo", MediaType.TEXT_PLAIN_TYPE);
        return ret;
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-form-data-output")
    @GET
    public CompletionStage<MultipartFormDataOutput> getMultipartFormDataOutput() {
        MultipartFormDataOutput ret = new MultipartFormDataOutput();
        ret.addFormData("foo", "bar", MediaType.TEXT_PLAIN_TYPE);
        return async(ret);
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithAsyncWriterInterceptor
    @Path("async/multipart-form-data-output")
    @GET
    public MultipartFormDataOutput getMultipartFormDataOutputAsync() {
        MultipartFormDataOutput ret = new MultipartFormDataOutput();
        ret.addFormData("foo", "bar", MediaType.TEXT_PLAIN_TYPE);
        return ret;
    }

    @Produces("multipart/related")
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-related-output")
    @GET
    public CompletionStage<MultipartRelatedOutput> getMultipartRelatedOutput() {
        MultipartRelatedOutput ret = new MultipartRelatedOutput();
        ret.addPart("foo", MediaType.TEXT_PLAIN_TYPE);
        return async(ret);
    }

    @Produces("multipart/related")
    @WithAsyncWriterInterceptor
    @Path("async/multipart-related-output")
    @GET
    public MultipartRelatedOutput getMultipartRelatedAsync() {
        MultipartRelatedOutput ret = new MultipartRelatedOutput();
        ret.addPart("foo", MediaType.TEXT_PLAIN_TYPE);
        return ret;
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-list")
    @PartType(MediaType.TEXT_PLAIN)
    @GET
    public CompletionStage<List<String>> getMultipartList() {
        return async(Arrays.asList("bar"));
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithAsyncWriterInterceptor
    @Path("async/multipart-list")
    @PartType(MediaType.TEXT_PLAIN)
    @GET
    public List<String> getMultipartListAsync() {
        return Arrays.asList("bar");
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-map")
    @PartType(MediaType.TEXT_PLAIN)
    @GET
    public CompletionStage<Map<String, String>> getMultipartMap() {
        return async(Collections.singletonMap("foo", "bar"));
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithAsyncWriterInterceptor
    @Path("async/multipart-map")
    @PartType(MediaType.TEXT_PLAIN)
    @GET
    public Map<String, String> getMultipartMapAsync() {
        return Collections.singletonMap("foo", "bar");
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-form-annotation")
    @MultipartForm
    @GET
    public CompletionStage<MyForm> getMultipartFormAnnotation() {
        return async(new MyForm());
    }

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @WithAsyncWriterInterceptor
    @Path("async/multipart-form-annotation")
    @MultipartForm
    @GET
    public MyForm getMultipartFormAnnotationAsync() {
        return new MyForm();
    }

    @Produces("multipart/mixed")
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-mime")
    @GET
    public CompletionStage<MimeMultipart> getMultipartMime() throws MessagingException {
        MimeMultipart ret = new MimeMultipart(new MimeBodyPart(new InternetHeaders(), OK_BYTES));
        return async(ret);
    }

    @Produces("multipart/mixed")
    @WithAsyncWriterInterceptor
    @Path("async/multipart-mime")
    @GET
    public MimeMultipart getMultipartMimeAsync() throws MessagingException {
        return new MimeMultipart(new MimeBodyPart(new InternetHeaders(), OK_BYTES));
    }

    @Produces("multipart/related")
    @WithBlockingWriterInterceptor
    @Path("blocking/multipart-xop-related")
    @XopWithMultipartRelated
    @GET
    public CompletionStage<XopRelatedForm> getMultipartXopRelated() {
        return async(new XopRelatedForm());
    }

    @Produces("multipart/related")
    @WithAsyncWriterInterceptor
    @Path("async/multipart-xop-related")
    @XopWithMultipartRelated
    @GET
    public XopRelatedForm getMultipartXopRelatedAsync() {
        return new XopRelatedForm();
    }

    // jsonp provider

    @Produces(MediaType.APPLICATION_JSON)
    @WithBlockingWriterInterceptor
    @Path("blocking/jsonp-array")
    @GET
    public CompletionStage<JsonArray> getJsonpArray() {
        JsonArray ret = Json.createArrayBuilder().add("foo").build();
        return async(ret);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithAsyncWriterInterceptor
    @Path("async/jsonp-array")
    @GET
    public JsonArray getJsonpArrayAsync() {
        return Json.createArrayBuilder().add("foo").build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithBlockingWriterInterceptor
    @Path("blocking/jsonp-structure")
    @GET
    public CompletionStage<JsonStructure> getJsonpStructure() {
        JsonArray ret = Json.createArrayBuilder().add("foo").build();
        return async(ret);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithAsyncWriterInterceptor
    @Path("async/jsonp-structure")
    @GET
    public JsonStructure getJsonpStructureAsync() {
        return Json.createArrayBuilder().add("foo").build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithBlockingWriterInterceptor
    @Path("blocking/jsonp-object")
    @GET
    public CompletionStage<JsonObject> getJsonpObject() {
        JsonObject ret = Json.createObjectBuilder().add("foo", "bar").build();
        return async(ret);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithAsyncWriterInterceptor
    @Path("async/jsonp-object")
    @GET
    public JsonObject getJsonpObjectAsync() {
        return Json.createObjectBuilder().add("foo", "bar").build();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithBlockingWriterInterceptor
    @Path("blocking/jsonp-value")
    @GET
    public CompletionStage<JsonString> getJsonpValue() {
        JsonString ret = Json.createValue("foo");
        return async(ret);
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithAsyncWriterInterceptor
    @Path("async/jsonp-value")
    @GET
    public JsonString getJsonpValueAsync() {
        return Json.createValue("foo");
    }

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @WithBlockingWriterInterceptor
    @Path("blocking/sse")
    @GET
    public CompletionStage<OutboundSseEvent> getSse() {
        BuilderImpl builder = new OutboundSseEventImpl.BuilderImpl();
        builder.name("foo");
        builder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        builder.data("bar\ngee");
        return async(builder.build());
    }

    @Produces(MediaType.SERVER_SENT_EVENTS)
    @WithAsyncWriterInterceptor
    @Path("async/sse")
    @GET
    public OutboundSseEvent getSseAsync() {
        BuilderImpl builder = new OutboundSseEventImpl.BuilderImpl();
        builder.name("foo");
        builder.mediaType(MediaType.TEXT_PLAIN_TYPE);
        builder.data("bar\ngee");
        return builder.build();
    }

    // Jackson

    @Produces(MediaType.APPLICATION_JSON)
    @WithBlockingWriterInterceptor
    @Path("blocking/jackson")
    @GET
    public CompletionStage<JacksonType> getJacksonJson() {
        return async(new JacksonType());
    }

    @Produces(MediaType.APPLICATION_JSON)
    @WithAsyncWriterInterceptor
    @Path("async/jackson")
    @GET
    public JacksonType getJacksonAsync() {
        return new JacksonType();
    }

    // Throwing

    @Path("throwing/blocking-writer")
    @GET
    public BlockingThrowingWriterData getThrowingBlockingWriter() {
        return new BlockingThrowingWriterData();
    }

    @Path("throwing/blocking-interceptor")
    @GET
    @WithBlockingThrowingWriterInterceptor
    public CompletionStage<String> getThrowingBlockingInterceptor() {
        return CompletableFuture.supplyAsync(() -> "KO");
    }

    @Path("throwing/async-writer-1")
    @GET
    public AsyncThrowingWriterData getThrowingAsyncWriter1() {
        return new AsyncThrowingWriterData(true);
    }

    @Path("throwing/async-writer-2")
    @GET
    public AsyncThrowingWriterData getThrowingAsyncWriter2() {
        return new AsyncThrowingWriterData(false);
    }

    @Path("throwing/async-interceptor-1")
    @GET
    @WithAsyncThrowingWriterInterceptor(throwNow = true)
    public String getThrowingAsyncInterceptor1() {
        return "KO";
    }

    @Path("throwing/async-interceptor-2")
    @GET
    @WithAsyncThrowingWriterInterceptor(throwNow = false)
    public String getThrowingAsyncInterceptor2() {
        return "KO";
    }
}
