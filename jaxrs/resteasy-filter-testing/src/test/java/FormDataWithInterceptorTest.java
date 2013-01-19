import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Multiple testings for RESTEASY-796
 *
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class FormDataWithInterceptorTest extends BaseResourceTest {

    @Before
    public void setUp() throws Exception {
        deployment.getProviderFactory().registerProvider(FormDataInterceptor.class);
        addPerRequestResource(DefaultResource.class);
    }

    @Test
    public void testReaderInterceptorWithFormData() throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(TestPortProvider.generateURL("/default/form1"));

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("param1", "value1"));
        formparams.add(new BasicNameValuePair("param2", "value2"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        post.setEntity(entity);

        HttpResponse response = httpclient.execute(post);
        HttpEntity resp = response.getEntity();
        if (resp != null) {
            InputStream inputStream = resp.getContent();
            try {
                StringWriter writer = new StringWriter();
                copy(inputStream, writer);
                Assert.assertEquals("XY", writer.toString());
            } finally {
                inputStream.close();
            }
        }
    }

    @Test
    public void testBypassedReaderInterceptorWithFormData() throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(TestPortProvider.generateURL("/default/form1"));

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("param1", "value1"));
        formparams.add(new BasicNameValuePair("param2", "value2"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        post.setEntity(entity);
        post.setHeader(FormDataInterceptor.BYPASS_HEADER, "YES");

        HttpResponse response = httpclient.execute(post);
        HttpEntity resp = response.getEntity();
        if (resp != null) {
            InputStream inputStream = resp.getContent();
            try {
                StringWriter writer = new StringWriter();
                copy(inputStream, writer);
                Assert.assertEquals("value1value2", writer.toString());
            } finally {
                inputStream.close();
            }
        }

    }

    @Test
    public void primitiveTypeBackwardCompatiblityTest() throws Exception {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost post = new HttpPost(TestPortProvider.generateURL("/default/form2"));

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("param1", "value1"));
        formparams.add(new BasicNameValuePair("param2", "value2"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
        post.setEntity(entity);
        post.setHeader(FormDataInterceptor.BYPASS_HEADER, "YES");

        HttpResponse response = httpclient.execute(post);
        HttpEntity resp = response.getEntity();
        if (resp != null) {
            InputStream inputStream = resp.getContent();
            try {
                StringWriter writer = new StringWriter();

                copy(inputStream, writer);
                Assert.assertEquals("param1=value1&param2=value2", writer.toString());
            } finally {
                inputStream.close();
            }
        }

    }


    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        char[] buffer = new char[100];
        int n;
        while (-1 != (n = in.read(buffer))) {
            output.write(buffer, 0, n);
        }
    }
}
