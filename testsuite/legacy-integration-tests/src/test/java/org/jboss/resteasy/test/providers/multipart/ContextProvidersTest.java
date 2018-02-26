package org.jboss.resteasy.test.providers.multipart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Old client test)
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Old client test)
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.test.providers.multipart.resource.ContextProvidersCustomer;
import org.jboss.resteasy.test.providers.multipart.resource.ContextProvidersCustomerForm;
import org.jboss.resteasy.test.providers.multipart.resource.ContextProvidersName;
import org.jboss.resteasy.test.providers.multipart.resource.ContextProvidersResource;
import org.jboss.resteasy.test.providers.multipart.resource.ContextProvidersXop;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1119.
 *      Unable to find contextual data of type: javax.ws.rs.ext.Providers if ClientBuilder.newClient is used.
 * @tpSince RESTEasy 3.0.16
 */
@SuppressWarnings("deprecation")
@RunWith(Arquillian.class)
@RunAsClient
public class ContextProvidersTest {

    protected final Logger logger = LogManager.getLogger(ContextProvidersTest.class.getName());

    protected enum Version {
        TWO,
        THREE
    }

    public static final Annotation PART_TYPE_APPLICATION_XML = new S1() {
        private static final long serialVersionUID = 1L;

        @Override
        public String value() {
            return "application/xml";
        }
    };
    public static final Annotation MULTIPART_FORM = new S2() {
        private static final long serialVersionUID = 1L;
    };
    public static final Annotation XOP_WITH_MULTIPART_RELATED = new S3() {
        private static final long serialVersionUID = 1L;
    };
    static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
    static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");
    static final javax.ws.rs.core.GenericType<List<ContextProvidersName>> LIST_NAME_TYPE = new javax.ws.rs.core.GenericType<List<ContextProvidersName>>() {
    };

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ContextProvidersTest.class.getSimpleName());
        war.addClasses(ContextProvidersCustomer.class, ContextProvidersCustomerForm.class, ContextProvidersName.class, ContextProvidersXop.class, PortProviderUtil.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, ContextProvidersResource.class);
    }

    /**
     * @tpTestDetails Form data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFormData() throws Exception {
        doTestGetFormData(Version.TWO);
        doTestGetFormData(Version.THREE);
    }

    public void doTestGetFormData(Version version) throws Exception {
        try {
            MultipartFormDataInput entity = get(version, "/get/form", MultipartFormDataInput.class);

            // Get parts by name.
            ContextProvidersCustomer c = entity.getFormDataPart("bill", ContextProvidersCustomer.class, null);
            Assert.assertEquals("Wrong response", "Bill", c.getName());
            String s = entity.getFormDataPart("bob", String.class, null);
            Assert.assertEquals("Wrong response", "Bob", s);

            // Iterate over list of parts.
            for (Map.Entry<String, List<InputPart>> formDataEntry : entity.getFormDataMap().entrySet()) {
//                logger.debug("key: " + formDataEntry.getKey());
                for (InputPart inputPart : formDataEntry.getValue()) {
                    if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                        c = inputPart.getBody(ContextProvidersCustomer.class, null);
                        Assert.assertEquals("Wrong response", "Bill", c.getName());
                    } else {
                        s = inputPart.getBody(String.class, null);
                        Assert.assertEquals("Wrong response", "Bob", s);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    /**
     * @tpTestDetails Mixed data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMixed() throws Exception {
        doTestGetMixed(Version.TWO);
        doTestGetMixed(Version.THREE);
    }

    void doTestGetMixed(Version version) throws Exception {
        try {
            MultipartInput entity = get(version, "/get/mixed", MultipartInput.class);

            // Iterate over list of parts.
            List<InputPart> parts = entity.getParts();
            for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); ) {
                InputPart inputPart = it.next();
                if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                    ContextProvidersCustomer c = inputPart.getBody(ContextProvidersCustomer.class, null);
                    Assert.assertEquals("Wrong response", "Bill", c.getName());
                } else {
                    String s = inputPart.getBody(String.class, null);
                    Assert.assertEquals("Wrong response", "Bob", s);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    /**
     * @tpTestDetails List data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetList() throws Exception {
        doTestGetList(Version.TWO);
        doTestGetList(Version.THREE);
    }

    void doTestGetList(Version version) throws Exception {
        try {
            MultipartInput entity = get(version, "/get/list", MultipartInput.class);

            // Iterate over list of parts.
            List<InputPart> parts = entity.getParts();
            Set<String> customers = new HashSet<String>();
            for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); ) {
                InputPart inputPart = it.next();
                customers.add(inputPart.getBody(ContextProvidersCustomer.class, null).getName());
            }
            Assert.assertThat("Wrong count of customers from response", new Integer(customers.size()), is(2));
            Assert.assertThat("Received customers list do not contain all items", customers, hasItems("Bill"));
            Assert.assertThat("Received customers list do not contain all items", customers, hasItems("Bob"));
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    /**
     * @tpTestDetails Map data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMap() throws Exception {
        doTestGetMap(Version.TWO);
        doTestGetMap(Version.THREE);
    }

    public void doTestGetMap(Version version) throws Exception {
        try {
            MultipartFormDataInput entity = get(version, "/get/map", MultipartFormDataInput.class);

            // Get parts by name.
            ContextProvidersCustomer c = entity.getFormDataPart("bill", ContextProvidersCustomer.class, null);
            Assert.assertEquals("Wrong response", "Bill", c.getName());
            c = entity.getFormDataPart("bob", ContextProvidersCustomer.class, null);
            Assert.assertEquals("Wrong response", "Bob", c.getName());

            // Iterate over map of parts.
            Set<String> customers = new HashSet<>();
            for (Map.Entry<String, List<InputPart>> formDataEntry : entity.getFormDataMap().entrySet()) {
                for (InputPart inputPart : formDataEntry.getValue()) {
                    customers.add(inputPart.getBody(ContextProvidersCustomer.class, null).getName());
                }
            }
            Assert.assertThat("Wrong count of customers from response", new Integer(customers.size()), is(2));
            Assert.assertThat("Received customers list do not contain all items", customers, hasItems("Bill"));
            Assert.assertThat("Received customers list do not contain all items", customers, hasItems("Bob"));
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    /**
     * @tpTestDetails Related data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetRelated() throws Exception {
        doTestGetRelated(Version.TWO);
        doTestGetRelated(Version.THREE);
    }

    void doTestGetRelated(Version version) throws Exception {
        try {
            MultipartRelatedInput entity = get(version, "/get/related", MultipartRelatedInput.class);

            // Iterate over map of parts.
            Map<String, InputPart> map = entity.getRelatedMap();
            Set<String> keys = map.keySet();
            Assert.assertEquals(2, keys.size());
            Assert.assertThat("Wrong count of keys from response", new Integer(keys.size()), is(2));
            Assert.assertTrue(keys.contains("bill"));
            Assert.assertTrue(keys.contains("bob"));
            Assert.assertThat("Missing key from response", keys, hasItems("bill"));
            Assert.assertThat("Missing key from response", keys, hasItems("bob"));
            Set<String> parts = new HashSet<>();
            for (InputPart inputPart : map.values()) {
                parts.add(inputPart.getBody(String.class, null));
            }
            Assert.assertThat("Received customers list do not contain all items", parts, hasItems("Bill"));
            Assert.assertThat("Received customers list do not contain all items", parts, hasItems("Bob"));
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    /**
     * @tpTestDetails Multipart form data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMultipartForm() throws Exception {
        doTestGetMultipartForm(Version.TWO);
        doTestGetMultipartForm(Version.THREE);
    }

    void doTestGetMultipartForm(Version version) throws Exception {
        Annotation[] annotations = new Annotation[1];
        annotations[0] = MULTIPART_FORM;
        ContextProvidersCustomerForm form = get(version, "/get/multipartform", ContextProvidersCustomerForm.class, annotations);
        ContextProvidersCustomer customer = form.getCustomer();
        Assert.assertEquals("Wrong response", "Bill", customer.getName());
    }

    /**
     * @tpTestDetails Xop data in get request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetXop() throws Exception {
        doTestGetXop(Version.TWO);
        doTestGetXop(Version.THREE);
    }

    void doTestGetXop(Version version) throws Exception {
        Annotation[] annotations = new Annotation[1];
        annotations[0] = XOP_WITH_MULTIPART_RELATED;
        ContextProvidersXop xop = get(version, "/get/xop", ContextProvidersXop.class, annotations);
        Assert.assertEquals("Wrong response", "goodbye world", new String(xop.getBytes()));
    }

    /**
     * @tpTestDetails Mixed data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostMixed() throws Exception {
        doTestPostMixed(Version.TWO);
        doTestPostMixed(Version.THREE);
    }

    @SuppressWarnings("unchecked")
    void doTestPostMixed(Version version) throws Exception {
        MultipartOutput output = new MultipartOutput();
        output.addPart(new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE);
        output.addPart("Bob", MediaType.TEXT_PLAIN_TYPE);
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post(version, "/post/mixed", output, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(2, names.size());
        Assert.assertTrue(names.contains(new ContextProvidersName("Bill")));
        Assert.assertTrue(names.contains(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Form data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostFormData() throws Exception {
        doTestPostFormData(Version.TWO);
        doTestPostFormData(Version.THREE);
    }

    @SuppressWarnings("unchecked")
    public void doTestPostFormData(Version version) throws Exception {

        MultipartFormDataOutput output = new MultipartFormDataOutput();
        output.addFormData("bill", new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE);
        output.addFormData("bob", "Bob", MediaType.TEXT_PLAIN_TYPE);
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post(version, "/post/form", output, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertThat("Wrong count of customers from response", new Integer(names.size()), is(2));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bill")));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails List data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostList() throws Exception {
        doTestPostList(Version.TWO);
        doTestPostList(Version.THREE);
    }

    @SuppressWarnings("unchecked")
    public void doTestPostList(Version version) throws Exception {
        List<ContextProvidersCustomer> customers = new ArrayList<ContextProvidersCustomer>();
        customers.add(new ContextProvidersCustomer("Bill"));
        customers.add(new ContextProvidersCustomer("Bob"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post(version, "/post/list", customers, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertThat("Wrong count of customers from response", new Integer(names.size()), is(2));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bill")));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Map data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostMap() throws Exception {
        doTestPostMap(Version.TWO);
        doTestPostMap(Version.THREE);
    }

    @SuppressWarnings("unchecked")
    public void doTestPostMap(Version version) throws Exception {
        Map<String, ContextProvidersCustomer> customers = new HashMap<String, ContextProvidersCustomer>();
        customers.put("bill", new ContextProvidersCustomer("Bill"));
        customers.put("bob", new ContextProvidersCustomer("Bob"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post(version, "/post/map", customers, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertThat("Wrong count of customers from response", new Integer(names.size()), is(2));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("bill:Bill")));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("bob:Bob")));
    }

    /**
     * @tpTestDetails Related data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostRelated() throws Exception {
        doTestPostRelated(Version.TWO);
        doTestPostRelated(Version.THREE);
    }

    @SuppressWarnings("unchecked")
    void doTestPostRelated(Version version) throws Exception {
        MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.setStartInfo("text/html");
        output.addPart("Bill", new MediaType("image", "png"), "bill", "binary");
        output.addPart("Bob", new MediaType("image", "png"), "bob", "binary");
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post(version, "/post/related", output, MULTIPART_RELATED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertThat("Wrong count of customers from response", new Integer(names.size()), is(2));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bill")));
        Assert.assertThat("Received customers list do not contain all items", names, hasItems(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Multipart form data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostMultipartForm() throws Exception {
        doTestPostMultipartForm(Version.TWO);
        doTestPostMultipartForm(Version.THREE);
    }

    void doTestPostMultipartForm(Version version) throws Exception {
        ContextProvidersCustomerForm form = new ContextProvidersCustomerForm();
        form.setCustomer(new ContextProvidersCustomer("Bill"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = MULTIPART_FORM;
        String name = post(version, "/post/multipartform", form, MULTIPART_FORM_DATA, String.class, null, annotations);
        Assert.assertEquals("Wrong response", "Bill", name);
    }

    /**
     * @tpTestDetails Xop data in post request is used.
     * @tpPassCrit RE should be able to find contextual data of type: javax.ws.rs.ext.Providers.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostXop() throws Exception {
        doTestPostXop(Version.TWO);
        doTestPostXop(Version.THREE);
    }

    void doTestPostXop(Version version) throws Exception {
        ContextProvidersXop xop = new ContextProvidersXop("hello world".getBytes());
        Annotation[] annotations = new Annotation[1];
        annotations[0] = XOP_WITH_MULTIPART_RELATED;
        String s = post(version, "/post/xop", xop, MULTIPART_RELATED, String.class, null, annotations);
        Assert.assertEquals("Wrong response", "hello world", s);
    }

    <T> T get(Version version, String path, Class<T> clazz) throws Exception {
        return get(version, path, clazz, null);
    }

    <T> T get(Version version, String path, Class<T> clazz, Annotation[] annotations) throws Exception {
        try {
            switch (version) {
                case TWO: {
                    ClientRequest request = new ClientRequest(PortProviderUtil.generateURL(path, ContextProvidersTest.class.getSimpleName()));

                    ClientResponse<T> response = request.get(clazz);
                    Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                    T entity = response.getEntity(clazz, null, annotations);
                    return entity;
                }

                case THREE: {
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(PortProviderUtil.generateURL(path, ContextProvidersTest.class.getSimpleName()));
                    Response response = target.request().get();
                    Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                    T entity = response.readEntity(clazz, annotations);
                    return entity;
                }

                default:
                    throw new Exception("Unknown version of response: " + version);
            }
        } catch (Exception e) {
            throw new RuntimeException(TestUtil.getErrorMessageForKnownIssue("RESTEASY-1119"), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    <S, T> T post(Version version, String path, S payload, MediaType mediaType, Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception {
        switch (version) {
            case TWO: {
                ClientRequest request = new ClientRequest(PortProviderUtil.generateURL(path, ContextProvidersTest.class.getSimpleName()));
                request.body(mediaType, payload, payload.getClass(), null, annotations);
                ClientResponse<T> response = request.post();
                T entity = null;
                if (genericReturnType != null) {
                    entity = response.getEntity(returnType, genericReturnType);
                } else {
                    entity = response.getEntity(returnType);
                }

                return entity;
            }

            case THREE: {
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(PortProviderUtil.generateURL(path, ContextProvidersTest.class.getSimpleName()));
                Entity<S> entity = Entity.entity(payload, mediaType, annotations);
                Response response = target.request().post(entity);
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                T result = null;
                if (genericReturnType != null) {
                    result = response.readEntity(new GenericType<T>(genericReturnType));
                } else {
                    result = response.readEntity(returnType);
                }
                return result;
            }

            default:
                throw new Exception("Unknown version of response: " + version);
        }
    }

    public abstract static class S1 extends AnnotationLiteral<PartType> implements PartType {
        private static final long serialVersionUID = 1L;
    }

    public abstract static class S2 extends AnnotationLiteral<MultipartForm> implements MultipartForm {
        private static final long serialVersionUID = 1L;
    }

    public abstract static class S3 extends AnnotationLiteral<XopWithMultipartRelated> implements XopWithMultipartRelated {
        private static final long serialVersionUID = 1L;
    }
}
