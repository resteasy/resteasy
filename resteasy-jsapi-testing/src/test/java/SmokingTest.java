import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
        selenium.click("//input[@value='Test Query Param']");
        selenium.click("//input[@value='Test Cookie Param']");
        selenium.click("//input[@value='Test Matrix Param']");
        selenium.click("//input[@value='Test Header Param']");

        assertTrue(selenium.isTextPresent("0"));
        assertTrue(selenium.isTextPresent("exact:a::b::c::"));
        assertTrue(selenium.isTextPresent("exact:xyz"));
        assertTrue(selenium.isTextPresent("exact:d::e::f::"));
        assertTrue(selenium.isTextPresent("Weinan"));
        assertTrue(selenium.isTextPresent("exact:g::h::i::"));
        assertTrue(selenium.isTextPresent("/resteasy-jsapi-testing/"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
