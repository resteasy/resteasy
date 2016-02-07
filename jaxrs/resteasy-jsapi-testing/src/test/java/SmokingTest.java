import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
        selenium.click("//input[@value='Test Form']");
        selenium.click("//input[@value='Test Form2']");
        selenium.click("//input[@value='Test Form3']");
        selenium.click("//input[@value='Test Prefix Form']");

        selenium.click("//input[@value='Test Cookie Param']");
        selenium.click("//input[@value='Test Matrix Param']");
        selenium.click("//input[@value='Test Header Param']");
        selenium.click("//input[@value='RESTEASY-731-false']");
        selenium.click("//input[@value='RESTEASY-731-zero']");
        selenium.click("//input[@value='Test Subresource Locator']");

        assertTrue(selenium.isTextPresent("Chapter 2This is the content of chapter 2."));
        assertTrue(selenium.isTextPresent("0"));
        assertTrue(selenium.isTextPresent("exact:a::b::c::"));
        assertTrue(selenium.isTextPresent("exact:xyz"));
        assertTrue(selenium.isTextPresent(".-_~=&.-_~=&.-_~=&"));
        assertTrue(selenium.isTextPresent("2B=_2A-&114"));
        assertTrue(selenium.isTextPresent("CBA"));
        assertTrue(selenium.isTextPresent("11111111"));


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
        {
            Client client = ClientBuilder.newBuilder().build();
            WebTarget target = client.target("http://127.0.0.1:8080/resteasy-jsapi-testing/rest-js");
            Response response = target.request().get();
            assertEquals(HttpStatus.SC_OK, response.getStatus());
            response.close();
        }

        String etag;
        {
            Client client = ClientBuilder.newBuilder().build();
            WebTarget target = client.target("http://127.0.0.1:8080/resteasy-jsapi-testing/rest-js");
            Response response = target.request().get();
            etag = response.getHeaderString("Etag");
            response.close();

        }

        {
            Client client = ClientBuilder.newBuilder().build();
            WebTarget target = client.target("http://127.0.0.1:8080/resteasy-jsapi-testing/rest-js");
            Response response = target.request().header("If-None-Match", etag).get();
            assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatus());
            response.close();
        }
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
