package org.jboss.resteasy.test.cookie;

import java.util.Date;

import jakarta.ws.rs.core.NewCookie.SameSite;

import org.jboss.resteasy.cookies.NewCookie6265;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

/**
 * Test Cookie6265, NewCookie6265 constructors
 */
public class NewCookie6265Test {

    @Test
    public void testNewCookie6265() {
        NewCookie6265 c1a = new NewCookie6265("name", "value");
        NewCookie6265 c2a = new NewCookie6265("name", "value");
        Assert.assertTrue(c1a.equals(c2a));

        NewCookie6265 c1b = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, true);
        NewCookie6265 c2b = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, true);
        Assert.assertTrue(c1b.equals(c2b));

        NewCookie6265 c1c = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, false, true);
        NewCookie6265 c2c = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, false, true);
        Assert.assertTrue(c1c.equals(c2c));

        NewCookie6265 c1d = new NewCookie6265("name", "value", "/path/", "domain", 17, "comment", 3, false);
        NewCookie6265 c2d = new NewCookie6265("name", "value", "/path/", "domain", 19, "comment", 3, false);
        Assert.assertTrue(c1d.equals(c2d));

        NewCookie6265 c1e = new NewCookie6265("name", "value", "/path/", "domain", 17, "comment", 3, new Date(1, 2, 3), true,
                false);
        NewCookie6265 c2e = new NewCookie6265("name", "value", "/path/", "domain", 19, "comment", 3, new Date(1, 2, 3), true,
                false);
        Assert.assertTrue(c1e.equals(c2e));

        NewCookie6265 c1f = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, new Date(1, 2, 3), true, false,
                SameSite.LAX);
        NewCookie6265 c2f = new NewCookie6265("name", "value", "/path/", "domain", "comment", 3, new Date(1, 2, 3), true, false,
                SameSite.LAX);
        Assert.assertTrue(c1f.equals(c2f));

        NewCookie6265.Builder builder1 = new NewCookie6265.Builder("name");
        NewCookie6265 c1g = builder1.value("value").version(33).path("/path/").domain("domain").comment("comment").maxAge(11)
                .expiry(new Date(4, 5, 6)).secure(false).httpOnly(true).sameSite(SameSite.STRICT).build();
        NewCookie6265.Builder builder2 = new NewCookie6265.Builder("name");
        NewCookie6265 c2g = builder2.value("value").version(44).path("/path/").domain("domain").comment("comment").maxAge(11)
                .expiry(new Date(4, 5, 6)).secure(false).httpOnly(true).sameSite(SameSite.STRICT).build();
        Assert.assertTrue(c1g.equals(c2g));
    }
}
