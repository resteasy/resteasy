package org.jboss.resteasy.cookies;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;

/**
 * RFC 6265 version of NewCookie.
 *
 * Currently, Jakarta REST follows an extension of RFC 2109, with some fields
 * from RFC 6265 (e.g., HttpOnly). NewCookie6265 differs from NewCookie by
 *
 * 1. Dispensing with Version field
 * 2. Adding ad hoc "extension" fields
 */
public class NewCookie6265 extends NewCookie {

    public static final int NO_VERSION = -2;

    private List<String> extensions = new ArrayList<String>();

    /**
     * Create a new instance.
     *
     * @param name  the name of the cookie.
     * @param value the value of the cookie.
     * @throws IllegalArgumentException if name is {@code null}.
     * @deprecated This constructor will be removed in a future version. Please use {@link NewCookie.Builder} instead.
     */
    @Deprecated
    public NewCookie6265(final String name, final String value) {
        this(name, value, null, null, DEFAULT_MAX_AGE, null, false, false, null);
    }

    /**
     * Create a new instance.
     *
     * @param name     the name of the cookie.
     * @param value    the value of the cookie.
     * @param path     the URI path for which the cookie is valid.
     * @param domain   the host domain for which the cookie is valid.
     * @param maxAge   the maximum age of the cookie in seconds.
     * @param secure   specifies whether the cookie will only be sent over a secure connection.
     * @param httpOnly if {@code true} make the cookie HTTP only, i.e. only visible as part of an HTTP request.
     * @throws IllegalArgumentException if name is {@code null}.
     * @since 2.0
     * @deprecated This constructor will be removed in a future version. Please use {@link NewCookie.Builder} instead.
     */
    @Deprecated
    public NewCookie6265(final String name,
            final String value,
            final String path,
            final String domain,
            final int maxAge,
            final boolean secure,
            final boolean httpOnly) {
        this(name, value, path, domain, maxAge, null, secure, httpOnly, null);
    }

    /**
     * Create a new instance.
     *
     * @param name   the name of the cookie
     * @param value  the value of the cookie
     * @param path   the URI path for which the cookie is valid
     * @param domain the host domain for which the cookie is valid
     * @param maxAge the maximum age of the cookie in seconds
     * @param secure specifies whether the cookie will only be sent over a secure connection
     * @throws IllegalArgumentException if name is {@code null}.
     * @deprecated This constructor will be removed in a future version. Please use {@link NewCookie.Builder} instead.
     */
    @Deprecated
    public NewCookie6265(final String name,
            final String value,
            final String path,
            final String domain,
            final int maxAge,
            final boolean secure) {
        this(name, value, path, domain, maxAge, null, secure, false, null);
    }

    /**
     * Create a new instance.
     *
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param path     the URI path for which the cookie is valid
     * @param domain   the host domain for which the cookie is valid
     * @param maxAge   the maximum age of the cookie in seconds
     * @param expiry   the cookie expiry date.
     * @param secure   specifies whether the cookie will only be sent over a secure connection
     * @param httpOnly if {@code true} make the cookie HTTP only, i.e. only visible as part of an HTTP request.
     * @throws IllegalArgumentException if name is {@code null}.
     * @since 2.0
     * @deprecated This constructor will be removed in a future version. Please use {@link NewCookie.Builder} instead.
     */
    @Deprecated
    public NewCookie6265(final String name,
            final String value,
            final String path,
            final String domain,
            final int maxAge,
            final Date expiry,
            final boolean secure,
            final boolean httpOnly) {
        this(name, value, path, domain, maxAge, expiry, secure, httpOnly, null);
    }

    /**
     * Create a new instance.
     *
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param path     the URI path for which the cookie is valid
     * @param domain   the host domain for which the cookie is valid
     * @param maxAge   the maximum age of the cookie in seconds
     * @param expiry   the cookie expiry date.
     * @param secure   specifies whether the cookie will only be sent over a secure connection
     * @param httpOnly if {@code true} make the cookie HTTP only, i.e. only visible as part of an HTTP request.
     * @param sameSite specifies the value of the {@code SameSite} cookie attribute
     * @throws IllegalArgumentException if name is {@code null}.
     * @since 3.1
     * @deprecated This constructor will be removed in a future version. Please use {@link NewCookie.Builder} instead.
     */
    @Deprecated
    public NewCookie6265(final String name,
            final String value,
            final String path,
            final String domain,
            final int maxAge,
            final Date expiry,
            final boolean secure,
            final boolean httpOnly,
            final SameSite sameSite) {
        super(name, value, path, domain, NewCookie6265.NO_VERSION, null, maxAge, expiry, secure, httpOnly, sameSite);
    }

    /**
     * Create a new instance from the supplied {@link AbstractNewCookieBuilder} instance.
     *
     * @param builder the builder.
     * @throws IllegalArgumentException if {@code builder.name} is {@code null}.
     * @since 3.1
     */
    protected NewCookie6265(AbstractNewCookie6265Builder<?> builder) {
        super(builder);
        extensions = builder.extensions;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * JAX-RS {@link NewCookie6265} builder class.
     * <p>
     * {@code NewCookie6265} builder provides methods that let you conveniently configure and subsequently build a new
     * {@code NewCookie6265} instance.
     * </p>
     * For example:
     *
     * <pre>
     * NewCookie6265 cookie = new NewCookie6265.Builder("name")
     *         .path("/")
     *         .domain("domain.com")
     *         .sameSite(SameSite.LAX)
     *         .build();
     * </pre>
     *
     * @since 3.1
     */
    public static class Builder extends AbstractNewCookie6265Builder<Builder> {

        /**
         * Create a new instance.
         *
         * @param name the name of the cookie.
         */
        public Builder(String name) {
            super(name);
        }

        /**
         * Create a new instance supplementing the information in the supplied cookie.
         *
         * @param cookie the cookie to clone.
         */
        public Builder(Cookie cookie) {
            super(cookie);
        }

        @Override
        public NewCookie6265 build() {
            return new NewCookie6265(this);
        }
    }

    /**
     * JAX-RS abstract {@link NewCookie} builder class.
     *
     * @param <T> the current AbstractNewCookieBuilder type.
     *
     * @since 3.1
     */
    public abstract static class AbstractNewCookie6265Builder<T extends AbstractNewCookie6265Builder<T>>
            extends AbstractNewCookieBuilder<T> {

        private List<String> extensions = new ArrayList<String>();

        /**
         * Create a new instance.
         *
         * @param name the name of the cookie.
         */
        public AbstractNewCookie6265Builder(String name) {
            super(name);
            version(NewCookie.DEFAULT_VERSION);
        }

        /**
         * Create a new instance supplementing the information in the supplied cookie.
         *
         * @param cookie the cookie to clone.
         */
        public AbstractNewCookie6265Builder(Cookie cookie) {
            super(cookie);
        }

        public T value(String value) {
            super.value(value);
            return self();
        }

        /**
         * Set the version of the cookie. A {@code NewCookie6265} has no version field,
         * so this method always sets the version field to NewCookie6265.NO_VERSION.
         *
         * @param version the version of the specification to which the cookie complies.
         * @return the updated builder instance.
         */
        public T version(int version) {
            super.version(NewCookie6265.NO_VERSION);
            return self();
        }

        /**
         * Set the path of the cookie.
         *
         * @param path the URI path for which the cookie is valid.
         * @return the updated builder instance.
         */
        public T path(String path) {
            super.path(path);
            return self();
        }

        /**
         * Set the domain of the cookie.
         *
         * @param domain the host domain for which the cookie is valid.
         * @return the updated builder instance.
         */
        public T domain(String domain) {
            super.domain(domain);
            return self();
        }

        /**
         * Set the comment associated with the cookie. A {@code NewCookie6265} has no comment field,
         * so this method always sets the comment field to null.
         *
         * @param comment the comment.
         * @return the updated builder instance.
         */
        public T comment(String comment) {
            super.comment(null);
            return self();
        }

        /**
         * Set the maximum age of the the cookie in seconds. Cookies older than the maximum age are discarded. A cookie can be
         * unset by sending a new cookie with maximum age of 0 since it will overwrite any existing cookie and then be
         * immediately discarded. The default value of {@code -1} indicates that the cookie will be discarded at the end of the
         * browser/application session.
         *
         * @param maxAge the maximum age in seconds.
         * @return the updated builder instance.
         * @see #expiry(Date)
         */
        public T maxAge(int maxAge) {
            super.maxAge(maxAge);
            return self();
        }

        /**
         * Set the cookie expiry date. Cookies whose expiry date has passed are discarded. A cookie can be unset by setting a
         * new cookie with an expiry date in the past, typically the lowest possible date that can be set.
         * <p>
         * Note that it is recommended to use {@link #maxAge(int) Max-Age} to control cookie expiration, however some browsers
         * do not understand {@code Max-Age}, in which case setting {@code Expires} parameter may be necessary.
         * </p>
         *
         * @param expiry the cookie expiry date
         * @return the updated builder instance.
         * @see #maxAge(int)
         */
        public T expiry(Date expiry) {
            super.expiry(expiry);
            return self();
        }

        /**
         * Whether the cookie will only be sent over a secure connection. Defaults to {@code false}.
         *
         * @param secure specifies whether the cookie will only be sent over a secure connection.
         * @return the updated builder instance.
         */
        public T secure(boolean secure) {
            super.secure(secure);
            return self();
        }

        /**
         * Whether the cookie will only be visible as part of an HTTP request. Defaults to {@code false}.
         *
         * @param httpOnly if {@code true} make the cookie HTTP only, i.e. only visible as part of an HTTP request.
         * @return the updated builder instance.
         */
        public T httpOnly(boolean httpOnly) {
            super.httpOnly(httpOnly);
            return self();
        }

        /**
         * Set the attribute that controls whether the cookie is sent with cross-origin requests, providing protection against
         * cross-site request forgery.
         *
         * @param sameSite specifies the value of the {@code SameSite} cookie attribute.
         * @return the updated builder instance.
         */
        public T sameSite(SameSite sameSite) {
            super.sameSite(sameSite);
            return self();
        }

        /**
         * Adds an extension field
         * 
         * @param ex
         * @return
         */
        public T extension(String ex) {
            extensions.add(ex);
            return self();
        }

        /**
         * Adds a list of extension fields
         * 
         * @param exs
         * @return
         */
        public T extensions(List<String> exs) {
            extensions = exs;
            return self();
        }

        @SuppressWarnings("unchecked")
        private T self() {
            return (T) this;
        }

        /**
         * Build a new {@link NewCookie6265} instance using all the configuration previously specified in this builder.
         *
         * @return a new {@link NewCookie6265} instance.
         * @throws IllegalArgumentException if name is {@code null}.
         */
        @Override
        public abstract NewCookie6265 build();
    }
}
