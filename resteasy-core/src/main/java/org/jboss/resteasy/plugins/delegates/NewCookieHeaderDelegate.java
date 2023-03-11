package org.jboss.resteasy.plugins.delegates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.ParameterParser;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NewCookieHeaderDelegate implements RuntimeDelegate.HeaderDelegate<NewCookie> {
    public static final NewCookieHeaderDelegate INSTANCE = new NewCookieHeaderDelegate();
    private static final String OLD_COOKIE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss z";

    @Override
    public NewCookie fromString(String newCookie) throws IllegalArgumentException {
        if (newCookie == null)
            throw new IllegalArgumentException(Messages.MESSAGES.newCookieValueNull());
        String cookieName = null;
        String cookieValue = null;
        String comment = null;
        String domain = null;
        int maxAge = NewCookie.DEFAULT_MAX_AGE;
        String path = null;
        boolean secure = false;
        int version = NewCookie.DEFAULT_VERSION;
        boolean httpOnly = false;
        Date expiry = null;

        ParameterParser parser = new ParameterParser();
        Map<String, String> map = parser.parse(newCookie, ';');

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            // Cookie name is always the first attribute (https://datatracker.ietf.org/doc/html/rfc6265#section-4.1.1).
            if (cookieName == null) {
                cookieName = name;
                cookieValue = value;
            } else if (name.equalsIgnoreCase("Comment"))
                comment = value;
            else if (name.equalsIgnoreCase("Domain"))
                domain = value;
            else if (name.equalsIgnoreCase("Max-Age"))
                maxAge = Integer.parseInt(value);
            else if (name.equalsIgnoreCase("Path"))
                path = value;
            else if (name.equalsIgnoreCase("Secure"))
                secure = true;
            else if (name.equalsIgnoreCase("Version"))
                version = Integer.parseInt(value);
            else if (name.equalsIgnoreCase("HttpOnly"))
                httpOnly = true;
            else if (name.equalsIgnoreCase("Expires")) {
                try {
                    expiry = new SimpleDateFormat(OLD_COOKIE_PATTERN, Locale.US).parse(value);
                } catch (ParseException e) {
                }
            }

        }

        if (cookieValue == null) {
            cookieValue = "";
        }

        return new NewCookie.Builder(cookieName)
                .value(cookieValue)
                .path(path)
                .domain(domain)
                .version(version)
                .comment(comment)
                .maxAge(maxAge)
                .expiry(expiry)
                .secure(secure)
                .httpOnly(httpOnly)
                .build();
    }

    protected void quote(StringBuilder b, String value) {

        if (MediaTypeHeaderDelegate.quoted(value)) {
            b.append('"');
            b.append(value);
            b.append('"');
        } else {
            b.append(value);
        }
    }

    public String toString(NewCookie cookie) {
        if (cookie == null)
            throw new IllegalArgumentException(Messages.MESSAGES.paramNull());
        StringBuilder b = new StringBuilder();

        b.append(cookie.getName()).append('=');

        if (cookie.getValue() != null) {
            quote(b, cookie.getValue());
        }

        b.append(";").append("Version=").append(cookie.getVersion());

        if (cookie.getComment() != null) {
            b.append(";Comment=");
            quote(b, cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            b.append(";Domain=");
            quote(b, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            b.append(";Path=");
            b.append(cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            b.append(";Max-Age=");
            b.append(cookie.getMaxAge());
        }
        if (cookie.getExpiry() != null) {
            b.append(";Expires=");
            b.append(DateUtil.formatDate(cookie.getExpiry(), OLD_COOKIE_PATTERN));
        }
        if (cookie.isSecure())
            b.append(";Secure");
        if (cookie.isHttpOnly())
            b.append(";HttpOnly");
        return b.toString();
    }
}
