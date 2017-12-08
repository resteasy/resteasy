package org.jboss.resteasy.test.validation;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomer;
import org.jboss.resteasy.test.validation.resource.ContextProvidersCustomerForm;
import org.jboss.resteasy.test.validation.resource.ContextProvidersName;
import org.jboss.resteasy.test.validation.resource.ContextProvidersXop;
import org.junit.Assert;
import org.junit.Test;

import javax.enterprise.util.AnnotationLiteral;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1119.
 *                    This is abstract class, ContextProvidersOldClientTest and ContextProvidersOldClientTest use this abstract class.
 * @tpSince RESTEasy 3.0.16
 */
public abstract class ContextProvidersTestBase {
    static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
    static final MediaType MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
    static final MediaType MULTIPART_RELATED = new MediaType("multipart", "related");

    static final javax.ws.rs.core.GenericType<List<ContextProvidersName>> LIST_NAME_TYPE = new javax.ws.rs.core.GenericType<List<ContextProvidersName>>() {
    };

    public abstract static class S1 extends AnnotationLiteral<PartType> implements PartType {
        private static final long serialVersionUID = 1L;
    }

    public static final Annotation PART_TYPE_APPLICATION_XML = new S1() {
        private static final long serialVersionUID = 1L;

        @Override
        public String value() {
            return "application/xml";
        }
    };

    public abstract static class S2 extends AnnotationLiteral<MultipartForm> implements MultipartForm {
        private static final long serialVersionUID = 1L;
    }

    public static final Annotation MULTIPART_FORM = new S2() {
        private static final long serialVersionUID = 1L;
    };

    public abstract static class S3 extends AnnotationLiteral<XopWithMultipartRelated> implements XopWithMultipartRelated {
        private static final long serialVersionUID = 1L;
    }

    public static final Annotation XOP_WITH_MULTIPART_RELATED = new S3() {
        private static final long serialVersionUID = 1L;
    };

    /**
     * @tpTestDetails Test get request and form data in response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetFormData() throws Exception {
        MultipartFormDataInput entity = get("/get/form", MultipartFormDataInput.class);

        // Get parts by name.
        ContextProvidersCustomer c = entity.getFormDataPart("bill", ContextProvidersCustomer.class, null);
        Assert.assertTrue(RESPONSE_ERROR_MSG, c.getName().startsWith("Bill"));
        String s = entity.getFormDataPart("bob", String.class, null);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Bob", s);

        Assert.assertTrue(RESPONSE_ERROR_MSG, 2 == entity.getFormDataMap().get("bill").size());

        // Iterate over list of parts.
        Map<String, List<InputPart>> map = entity.getFormDataMap();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            List<InputPart> list = map.get(key);
            for (Iterator<InputPart> it2 = list.iterator(); it2.hasNext(); ) {
                InputPart inputPart = it2.next();
                if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                    c = inputPart.getBody(ContextProvidersCustomer.class, null);
                    Assert.assertTrue(RESPONSE_ERROR_MSG, c.getName().startsWith("Bill"));
                } else {
                    s = inputPart.getBody(String.class, null);
                    Assert.assertEquals(RESPONSE_ERROR_MSG, "Bob", s);
                }
            }
        }
    }

    /**
     * @tpTestDetails Test get request. Response should contain xml and text-plain data.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMixed() throws Exception {
        MultipartInput entity = get("/get/mixed", MultipartInput.class);

        // Iterate over list of parts.
        List<InputPart> parts = entity.getParts();
        for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); ) {
            InputPart inputPart = it.next();
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                ContextProvidersCustomer c = inputPart.getBody(ContextProvidersCustomer.class, null);
                Assert.assertEquals(RESPONSE_ERROR_MSG, "Bill", c.getName());
            } else {
                String s = inputPart.getBody(String.class, null);
                Assert.assertEquals(RESPONSE_ERROR_MSG, "Bob", s);
            }
        }
    }

    /**
     * @tpTestDetails Test get request. Response should contain list.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetList() throws Exception {
        MultipartInput entity = get("/get/list", MultipartInput.class);

        // Iterate over list of parts.
        List<InputPart> parts = entity.getParts();
        Set<String> customers = new HashSet<String>();
        for (Iterator<InputPart> it = parts.iterator(); it.hasNext(); ) {
            InputPart inputPart = it.next();
            customers.add(inputPart.getBody(ContextProvidersCustomer.class, null).getName());
        }
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, customers.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, customers.contains("Bill"));
        Assert.assertTrue(RESPONSE_ERROR_MSG, customers.contains("Bob"));
    }

    /**
     * @tpTestDetails Test get request. Response should contain map.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMap() throws Exception {
        MultipartFormDataInput entity = get("/get/map", MultipartFormDataInput.class);

        // Get parts by name.
        ContextProvidersCustomer c = entity.getFormDataPart("bill", ContextProvidersCustomer.class, null);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Bill", c.getName());
        c = entity.getFormDataPart("bob", ContextProvidersCustomer.class, null);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Bob", c.getName());

        // Iterate over map of parts.
        Map<String, List<InputPart>> map = entity.getFormDataMap();
        Set<String> customers = new HashSet<String>();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            List<InputPart> list = map.get(key);
            for (Iterator<InputPart> it2 = list.iterator(); it2.hasNext(); ) {
                InputPart inputPart = it2.next();
                customers.add(inputPart.getBody(ContextProvidersCustomer.class, null).getName());
            }
        }
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, customers.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, customers.contains("Bill"));
        Assert.assertTrue(RESPONSE_ERROR_MSG, customers.contains("Bob"));
    }

    /**
     * @tpTestDetails Test get request. Response should contain multipart/related.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetRelated() throws Exception {
        MultipartRelatedInput entity = get("/get/related", MultipartRelatedInput.class);

        // Iterate over map of parts.
        Map<String, InputPart> map = entity.getRelatedMap();
        Set<String> keys = map.keySet();
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, keys.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, keys.contains("bill"));
        Assert.assertTrue(RESPONSE_ERROR_MSG, keys.contains("bob"));
        Set<String> parts = new HashSet<String>();
        for (Iterator<InputPart> it = map.values().iterator(); it.hasNext(); ) {
            parts.add(it.next().getBody(String.class, null));
        }
        Assert.assertTrue(RESPONSE_ERROR_MSG, parts.contains("Bill"));
        Assert.assertTrue(RESPONSE_ERROR_MSG, parts.contains("Bob"));
    }

    /**
     * @tpTestDetails Test get request. Response should contain multipart/form.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetMultipartForm() throws Exception {
        Annotation[] annotations = new Annotation[1];
        annotations[0] = MULTIPART_FORM;
        ContextProvidersCustomerForm form = get("/get/multipartform", ContextProvidersCustomerForm.class, annotations);
        ContextProvidersCustomer customer = form.getCustomer();
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Bill", customer.getName());
    }

    /**
     * @tpTestDetails Test get request. Response should contain xop (XML-binary Optimized Packaging).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetXop() throws Exception {
        Annotation[] annotations = new Annotation[1];
        annotations[0] = XOP_WITH_MULTIPART_RELATED;
        ContextProvidersXop xop = get("/get/xop", ContextProvidersXop.class, annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "goodbye world", new String(xop.getBytes()));
    }

    /**
     * @tpTestDetails Test post request with mixed params.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPostMixed() throws Exception {
        MultipartOutput output = new MultipartOutput();
        output.addPart(new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE);
        output.addPart("Bob", MediaType.TEXT_PLAIN_TYPE);
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post("/post/mixed", output, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, names.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bill")));
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Test post request with form params.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPostFormData() throws Exception {
        MultipartFormDataOutput output = new MultipartFormDataOutput();
        output.addFormData("bill", new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE);
        output.addFormData("bob", "Bob", MediaType.TEXT_PLAIN_TYPE);
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post("/post/form", output, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, names.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bill")));
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Test post request with list param.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPostList() throws Exception {
        List<ContextProvidersCustomer> customers = new ArrayList<ContextProvidersCustomer>();
        customers.add(new ContextProvidersCustomer("Bill"));
        customers.add(new ContextProvidersCustomer("Bob"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post("/post/list", customers, MULTIPART_MIXED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, names.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bill")));
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Test post request with map param.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPostMap() throws Exception {
        Map<String, ContextProvidersCustomer> customers = new HashMap<String, ContextProvidersCustomer>();
        customers.put("bill", new ContextProvidersCustomer("Bill"));
        customers.put("bob", new ContextProvidersCustomer("Bob"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post("/post/map", customers, MULTIPART_FORM_DATA, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, names.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("bill:Bill")));
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("bob:Bob")));
    }

    /**
     * @tpTestDetails Test post request with multipart/related param.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testPostRelated() throws Exception {
        MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.setStartInfo("text/html");
        output.addPart("Bill", new MediaType("image", "png"), "bill", "binary");
        output.addPart("Bob", new MediaType("image", "png"), "bob", "binary");
        Annotation[] annotations = new Annotation[1];
        annotations[0] = PART_TYPE_APPLICATION_XML;
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        names = post("/post/related", output, MULTIPART_RELATED, names.getClass(), LIST_NAME_TYPE.getType(), annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 2, names.size());
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bill")));
        Assert.assertTrue(RESPONSE_ERROR_MSG, names.contains(new ContextProvidersName("Bob")));
    }

    /**
     * @tpTestDetails Test post request with multipart/form param.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostMultipartForm() throws Exception {
        ContextProvidersCustomerForm form = new ContextProvidersCustomerForm();
        form.setCustomer(new ContextProvidersCustomer("Bill"));
        Annotation[] annotations = new Annotation[1];
        annotations[0] = MULTIPART_FORM;
        String name = post("/post/multipartform", form, MULTIPART_FORM_DATA, String.class, null, annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "Bill", name);
    }

    /**
     * @tpTestDetails Test post request with xop param (XML-binary Optimized Packaging).
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPostXop() throws Exception {
        ContextProvidersXop xop = new ContextProvidersXop("hello world".getBytes());
        Annotation[] annotations = new Annotation[1];
        annotations[0] = XOP_WITH_MULTIPART_RELATED;
        String s = post("/post/xop", xop, MULTIPART_RELATED, String.class, null, annotations);
        Assert.assertEquals(RESPONSE_ERROR_MSG, "hello world", s);
    }

    <T> T get(String path, Class<T> clazz) throws Exception {
        return get(path, clazz, null);
    }

    abstract <T> T get(String path, Class<T> clazz, Annotation[] annotations) throws Exception;

    abstract <S, T> T post(String path, S payload, MediaType mediaType, Class<T> returnType, Type genericReturnType, Annotation[] annotations) throws Exception;

}
