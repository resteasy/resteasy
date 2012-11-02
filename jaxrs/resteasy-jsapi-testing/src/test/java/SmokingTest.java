import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Weinan Li
 * @created_at 08 24 2012
 */
public class SmokingTest {
    Selenium selenium;

    @Before
    public void setUp() throws Exception {
        selenium = new DefaultSelenium("localhost", 4444, "*firefox", "http://127.0.0.1:8080/resteasy-jsapi-testing/");
        selenium.start();
    }

    @Test
    public void smokeTest() throws Exception {
        selenium.open("/resteasy-jsapi-testing/");
        selenium.click("css=input[type=\"button\"]");
        selenium.click("//input[@value='Test Form Param']");
        selenium.click("//input[@value='Test Form Param2']");
        selenium.click("//input[@value='Test Cookie Param']");
        selenium.click("//input[@value='Test Matrix Param']");
        selenium.click("//input[@value='Test Header Param']");
        selenium.click("//input[@value='RESTEASY-731-false']");
        selenium.click("//input[@value='RESTEASY-731-zero']");

        assertTrue(selenium.isTextPresent("0"));
        assertTrue(selenium.isTextPresent("exact:a::b::c::"));
        assertTrue(selenium.isTextPresent("exact:xyz"));
        assertTrue(selenium.isTextPresent("Weinan"));
        assertTrue(selenium.isTextPresent("exact:g::h::i::"));
        assertTrue(selenium.isTextPresent("/resteasy-jsapi-testing/"));
        assertTrue(selenium.isTextPresent("RESTEASY-731-false"));
        assertTrue(selenium.isTextPresent("RESTEASY-731-0"));

        // Put this at bottom because it will cause weird problem in Selenium.
        selenium.click("//input[@value='Test Query Param']");
        assertTrue(selenium.isTextPresent("exact:d::e::f::"));


        // Cache test
        selenium.open("/resteasy-jsapi-testing/cacheTest.jsp");
        selenium.type("id=uuid", "1");
        selenium.click("css=input[type=\"button\"]");
        assertTrue(selenium.isTextPresent("200"));
        selenium.click("css=input[type=\"button\"]");
        assertTrue(selenium.isTextPresent("304"));
        selenium.type("id=uuid", "2");
        selenium.click("css=input[type=\"button\"]");
        assertTrue(selenium.isTextPresent("200"));
        selenium.click("css=input[type=\"button\"]");
        assertTrue(selenium.isTextPresent("304"));

        // RESTEASY-789
        selenium.open("/resteasy-jsapi-testing/resteasy789.jsp");
        selenium.click("//input[@value='Test Add']");
        selenium.click("//input[@value='Test Minus']");
        assertTrue(selenium.isTextPresent("2"));
        assertTrue(selenium.isTextPresent("0"));

    }

    @Test
    public void testJSAPICache() throws Exception {
        HttpClient client = new HttpClient();
        HttpMethod get1 = new GetMethod("http://127.0.0.1:8080/resteasy-jsapi-testing/rest-js");
        int statusCode = client.executeMethod(get1);
        assertEquals(HttpStatus.SC_OK, statusCode);

        HttpMethod get2 = new GetMethod("http://127.0.0.1:8080/resteasy-jsapi-testing/rest-js");
        String etag = get1.getResponseHeader("Etag").getValue();
        get2.addRequestHeader("If-None-Match", etag);
        statusCode = client.executeMethod(get2);
        assertEquals(HttpStatus.SC_NOT_MODIFIED, statusCode);
    }


    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
