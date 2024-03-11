package org.jboss.resteasy.test.form;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.form.resource.FormResource;
import org.jboss.resteasy.test.form.resource.FormResourceClientForm;
import org.jboss.resteasy.test.form.resource.FormResourceClientFormSecond;
import org.jboss.resteasy.test.form.resource.FormResourceClientProxy;
import org.jboss.resteasy.test.form.resource.FormResourceProxy;
import org.jboss.resteasy.test.form.resource.FormResourceSecond;
import org.jboss.resteasy.test.form.resource.FormResourceValueHolder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Form test with resource
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateURL("/myform/server"));
        Response response = target.request().get();
        int status = response.getStatus();
        Assertions.assertEquals(200, status);
        boolean sv1 = false;
        boolean sv2 = false;
        MultivaluedMap<String, String> form = response
                .readEntity(new jakarta.ws.rs.core.GenericType<MultivaluedMap<String, String>>() {
                });
        Assertions.assertEquals(2, form.get("servername").size());
        for (String str : form.get("servername")) {
            if (str.equals("srv1")) {
                sv1 = true;
            } else if (str.equals("srv2")) {
                sv2 = true;
            }
        }
        Assertions.assertTrue(sv1);
        Assertions.assertTrue(sv2);
        client.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-691
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy691() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
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
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
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
        Assertions.assertEquals(rtn.getFirst(BOOLEAN_VALUE_FIELD), "true");
        Assertions.assertEquals(rtn.getFirst(NAME_FIELD), "This is My Name");
        Assertions.assertEquals(rtn.getFirst(DOUBLE_VALUE_FIELD), "123.45");
        Assertions.assertEquals(rtn.getFirst(LONG_VALUE_FIELD), "566780");
        Assertions.assertEquals(rtn.getFirst(INTEGER_VALUE_FIELD), "3");
        Assertions.assertEquals(rtn.getFirst(SHORT_VALUE_FIELD), "12345");
        String str = proxy.postString(form);
        String[] params = str.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < params.length; i++) {
            int index = params[i].indexOf('=');
            String key = params[i].substring(0, index).trim();
            String value = params[i].substring(index + 1).trim().replace('+', ' ');
            map.put(key, value);
        }
        Assertions.assertEquals(map.get(BOOLEAN_VALUE_FIELD), "true");
        Assertions.assertEquals(map.get(NAME_FIELD), "This is My Name");
        Assertions.assertEquals(map.get(DOUBLE_VALUE_FIELD), "123.45");
        Assertions.assertEquals(map.get(LONG_VALUE_FIELD), "566780");
        Assertions.assertEquals(map.get(INTEGER_VALUE_FIELD), "3");
        Assertions.assertEquals(map.get(SHORT_VALUE_FIELD), "12345");
        client.close();
    }

    /**
     * @tpTestDetails Test for different value type of form directly
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormResource() throws Exception {
        InputStream in = null;
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
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
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            String contentType = response.getHeaderString("content-type");
            Assertions.assertEquals("application/x-www-form-urlencoded", contentType);
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
                    values.put(URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8.name()),
                            URLDecoder.decode(pair
                                    .substring(index + 1), StandardCharsets.UTF_8.name()));
                }
            }
            Assertions.assertEquals(values.get(BOOLEAN_VALUE_FIELD), "true");
            Assertions.assertEquals(values.get(NAME_FIELD), "This is My Name");
            Assertions.assertEquals(values.get(DOUBLE_VALUE_FIELD), "123.45");
            Assertions.assertEquals(values.get(LONG_VALUE_FIELD), "566780");
            Assertions.assertEquals(values.get(INTEGER_VALUE_FIELD), "3");
        } finally {
            if (in != null) {
                in.close();
            }
            client.close();
        }
    }
}
