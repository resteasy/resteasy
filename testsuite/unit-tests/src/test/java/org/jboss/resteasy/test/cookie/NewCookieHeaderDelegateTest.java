package org.jboss.resteasy.test.cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NewCookieHeaderDelegateTest {

    private RuntimeDelegate.HeaderDelegate<NewCookie> delegate;

    @BeforeEach
    public void setUp() throws Exception {
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class);
    }

    @Test
    public void testParseIgnoresUnknownCookieAttributes() {

        String expectedCookieName = "JSESSIONID";
        String expectedCookieValue = "1fn1creezbh0117ej8n463jjwm";
        NewCookie newCookie = delegate
                .fromString(expectedCookieName + "=" + expectedCookieValue + ";Path=/path;UnknownAttribute=AnyValue");

        assertEquals(expectedCookieName, newCookie.getName());
        assertEquals(expectedCookieValue, newCookie.getValue());
    }

    @Test
    public void cookieToString() {
        // Create a formatter for the expiry date
        final SimpleDateFormat sdf = createDateFormatter();
        // Set up the dates
        final ZonedDateTime now = ZonedDateTime.now();
        final int maxAge = Math.toIntExact(Duration.between(now, now.plusDays(1L)).toSeconds());
        final Date expires = new Date(now.plusDays(1L).truncatedTo(ChronoUnit.SECONDS).toInstant().toEpochMilli());

        // Create the cookie
        final NewCookie newCookie = new NewCookie.Builder("test-cookie")
                .comment("Test Comment")
                .domain("local-domain")
                .expiry(expires)
                .httpOnly(true)
                .maxAge(maxAge)
                .path("/test")
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(true)
                .value("Test Value")
                .version(1)
                .build();
        // Create the expected string, note the order here is important as this is what the NewCookieHeaderDelegate generates
        final var expected = "test-cookie=\"Test Value\";Version=1;Comment=\"Test Comment\";Domain=local-domain;Path=/test;Max-Age="
                + maxAge + ";Expires=" + sdf.format(expires)
                + ";Secure;HttpOnly;SameSite=Strict";
        Assertions.assertEquals(expected, delegate.toString(newCookie));
    }

    @Test
    public void cookieFromString() {
        // Create a formatter for the expiry date
        final SimpleDateFormat sdf = createDateFormatter();
        // Set up the dates
        final ZonedDateTime now = ZonedDateTime.now();
        final int maxAge = Math.toIntExact(Duration.between(now, now.plusDays(1L)).toSeconds());
        final Date expires = new Date(now.plusDays(1L).truncatedTo(ChronoUnit.SECONDS).toInstant().toEpochMilli());

        // Create the cookie
        final NewCookie expected = new NewCookie.Builder("test-cookie")
                .comment("Test Comment")
                .domain("local-domain")
                .expiry(expires)
                .httpOnly(true)
                .maxAge(maxAge)
                .path("/test")
                .sameSite(NewCookie.SameSite.STRICT)
                .secure(true)
                .value("Test Value")
                .version(1)
                .build();
        // Create the expected string, note the order here is important as this is what the NewCookieHeaderDelegate generates
        final var value = "test-cookie=\"Test Value\";Version=1;Comment=\"Test Comment\";Domain=local-domain;Path=/test;Max-Age="
                + maxAge + ";Expires=" + sdf.format(expires)
                + ";Secure;HttpOnly;SameSite=Strict";
        Assertions.assertEquals(expected, delegate.fromString(value));
    }

    private static SimpleDateFormat createDateFormatter() {
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    }

}
