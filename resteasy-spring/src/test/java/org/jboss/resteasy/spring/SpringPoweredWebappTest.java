package org.jboss.resteasy.spring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;


public class SpringPoweredWebappTest extends Assert {

    private static final String BASE_URL = "http://somehost";
    private static final String CONTEXT_PATH = "/spring-powered";
    private static final String PATH = "/echo";
    private static final String CONFIG_PATH = "/WEB-INF/web.xml";
    private static final String EXPECTED_URI = BASE_URL + CONTEXT_PATH + PATH + "/uri";
    private static final String EXPECTED_HEADERS = BASE_URL + CONTEXT_PATH + PATH + "/headers" + "?:text/plain";
    protected ServletRunner runner;

    @Before
    public void setUpServlet() throws Exception {
        InputStream config = getClass().getResourceAsStream(CONFIG_PATH);
        runner = new ServletRunner(config, CONTEXT_PATH);
        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
    }

    @Test
    public void testGetUri() throws Exception {
        doTestGet(PATH + "/uri", EXPECTED_URI);
    }

    @Test
    public void testGetHeaders() throws Exception {
        doTestGet(PATH + "/headers", EXPECTED_HEADERS);
    }

    @Test
    public void testConcurrent() throws Exception {
        // ensure concurrent invocations see different injected values
        Thread uri = new Thread(new Runnable() {
            public void run() {
                for (int i = 0 ; i < 10 ; i++) {
                    try {
                        doTestGet(PATH + "/uri", EXPECTED_URI);
                    } catch (Exception e) {
                        fail(e.toString());
                    }
                }
            }
        });
        Thread headers = new Thread(new Runnable() {
            public void run() {
                for (int i = 0 ; i < 10 ; i++) {
                    try {
                        doTestGet(PATH + "/headers", EXPECTED_HEADERS);
                    } catch (Exception e) {
                        fail(e.toString());
                    }
                }
            }
        });
        uri.start();
        headers.start();
        uri.join();
        headers.join();
    }


    private void doTestGet(String context, String expectedReponsePattern) throws Exception {
        ServletUnitClient client = runner.newClient();
        WebRequest request = new GetMethodWebRequest(BASE_URL + CONTEXT_PATH + context);
        request.setHeaderField("Accept", "text/plain");

        verify(client.getResponse(request), 200, expectedReponsePattern);
    }


    private void verify(WebResponse response,
                        int expectedStatus,
                        String expectedResponsePattern) throws Exception {
        assertEquals("unexpected response code", expectedStatus, response.getResponseCode());
        if (expectedResponsePattern != null) {
            InputStream is = response.getInputStream();
            String respStr = toString(is);
            assertTrue("unexpected response: " + respStr
                       + ", no match for: " + expectedResponsePattern,
                       respStr.indexOf(expectedResponsePattern) != -1);
        }
    }

    private String toString(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c = is.read();
        while (c != -1) {
            os.write(c);
            c = is.read();
        }
        os.flush();
        return os.toString();
    }
}