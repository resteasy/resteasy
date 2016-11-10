package org.jboss.resteasy.test.form;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.form.resource.FormResource;
import org.jboss.resteasy.test.form.resource.FormResourceSecond;
import org.jboss.resteasy.test.form.resource.FormResourceClientForm;
import org.jboss.resteasy.test.form.resource.FormResourceClientFormSecond;
import org.jboss.resteasy.test.form.resource.FormResourceClientProxy;
import org.jboss.resteasy.test.form.resource.FormResourceProxy;
import org.jboss.resteasy.test.form.resource.FormResourceValueHolder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Form test with resource
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormResourceTest {
    private static final String SHORT_VALUE_FIELD = "shortValue";

    private static final String INTEGER_VALUE_FIELD = "integerValue";

    private static final String LONG_VALUE_FIELD = "longValue";

    private static final String DOUBLE_VALUE_FIELD = "doubleValue";

    private static final String NAME_FIELD = "name";

    private static final String BOOLEAN_VALUE_FIELD = "booleanValue";

    private static final String TEST_URI = generateURL("/form/42?query=42");


    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(FormResourceTest.class.getSimpleName());
        war.addClasses(FormResourceClientForm.class, FormResourceClientFormSecond.class,
                FormResourceClientProxy.class, FormResourceProxy.class, FormResourceValueHolder.class);
        return TestUtil.finishContainerPrepare(war, null, FormResourceSecond.class, FormResource.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormResourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-261
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMultiValueParam() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL("/myform/server"));
        Response response = target.request().get();
        int status = response.getStatus();
        Assert.assertEquals(200, status);
        boolean sv1 = false;
        boolean sv2 = false;
        MultivaluedMap<String, String> form = response.readEntity(new javax.ws.rs.core.GenericType<MultivaluedMap<String, String>>() {
        });
        Assert.assertEquals(2, form.get("servername").size());
        for (String str : form.get("servername")) {
            if (str.equals("srv1")) {
                sv1 = true;
            } else if (str.equals("srv2")) {
                sv2 = true;
            }
        }
        Assert.assertTrue(sv1);
        Assert.assertTrue(sv2);
        client.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-691
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy691() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL(""));
        FormResourceProxy proxy = target.proxy(FormResourceProxy.class);
        proxy.post(null);
        client.close();
    }

    /**
     * @tpTestDetails Test for different value type of form by proxy.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateURL(""));
        FormResourceClientProxy proxy = target.proxy(FormResourceClientProxy.class);
        FormResourceClientForm form = new FormResourceClientForm();
        form.setBooleanValue(true);
        form.setName("This is My Name");
        form.setDoubleValue(123.45);
        form.setLongValue(566780L);
        form.setIntegerValue(3);
        form.setShortValue((short) 12345);
        form.setHeaderParam(42);
        form.setQueryParam(42);
        form.setId(42);
        MultivaluedMap<String, String> rtn = proxy.post(form);
        Assert.assertEquals(rtn.getFirst(BOOLEAN_VALUE_FIELD), "true");
        Assert.assertEquals(rtn.getFirst(NAME_FIELD), "This is My Name");
        Assert.assertEquals(rtn.getFirst(DOUBLE_VALUE_FIELD), "123.45");
        Assert.assertEquals(rtn.getFirst(LONG_VALUE_FIELD), "566780");
        Assert.assertEquals(rtn.getFirst(INTEGER_VALUE_FIELD), "3");
        Assert.assertEquals(rtn.getFirst(SHORT_VALUE_FIELD), "12345");
        String str = proxy.postString(form);
        String[] params = str.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < params.length; i++) {
            int index = params[i].indexOf('=');
            String key = params[i].substring(0, index).trim();
            String value = params[i].substring(index + 1).trim().replace('+', ' ');
            map.put(key, value);
        }
        Assert.assertEquals(map.get(BOOLEAN_VALUE_FIELD), "true");
        Assert.assertEquals(map.get(NAME_FIELD), "This is My Name");
        Assert.assertEquals(map.get(DOUBLE_VALUE_FIELD), "123.45");
        Assert.assertEquals(map.get(LONG_VALUE_FIELD), "566780");
        Assert.assertEquals(map.get(INTEGER_VALUE_FIELD), "3");
        Assert.assertEquals(map.get(SHORT_VALUE_FIELD), "12345");
        client.close();
    }

    /**
     * @tpTestDetails Test for different value type of form directly
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormResource() throws Exception {
        InputStream in = null;
        ResteasyClient client = new ResteasyClientBuilder().build();
        try {
            ResteasyWebTarget target = client.target(TEST_URI);
            Invocation.Builder request = target.request();
            request.header("custom-header", "42");
            Form form = new Form().param(BOOLEAN_VALUE_FIELD, "true")
                    .param(NAME_FIELD, "This is My Name")
                    .param(DOUBLE_VALUE_FIELD, "123.45")
                    .param(LONG_VALUE_FIELD, "566780")
                    .param(INTEGER_VALUE_FIELD, "3")
                    .param(SHORT_VALUE_FIELD, "12345");
            Response response = request.post(Entity.form(form));
            Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            String contentType = response.getHeaderString("content-type");
            Assert.assertEquals("application/x-www-form-urlencoded", contentType);
            InputStream responseStream = response.readEntity(InputStream.class);
            in = new BufferedInputStream(responseStream);
            String formData = TestUtil.readString(in);
            String[] keys = formData.split("&");
            Map<String, String> values = new HashMap<String, String>();
            for (String pair : keys) {
                int index = pair.indexOf('=');
                if (index < 0) {
                    values.put(URLDecoder.decode(pair, StandardCharsets.UTF_8.name()), null);
                } else if (index > 0) {
                    values.put(URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8.name()), URLDecoder.decode(pair
                            .substring(index + 1), StandardCharsets.UTF_8.name()));
                }
            }
            Assert.assertEquals(values.get(BOOLEAN_VALUE_FIELD), "true");
            Assert.assertEquals(values.get(NAME_FIELD), "This is My Name");
            Assert.assertEquals(values.get(DOUBLE_VALUE_FIELD), "123.45");
            Assert.assertEquals(values.get(LONG_VALUE_FIELD), "566780");
            Assert.assertEquals(values.get(INTEGER_VALUE_FIELD), "3");
        } finally {
            if (in != null) {
                in.close();
            }
            client.close();
        }
    }
}
