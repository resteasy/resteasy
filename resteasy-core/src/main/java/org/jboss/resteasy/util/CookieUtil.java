package org.jboss.resteasy.util;

import jakarta.ws.rs.ext.RuntimeDelegate;

public class CookieUtil {
 /*******
    public static NewCookie valueOf(final Class<NewCookie> clazz, final String value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .fromString(value);
    }

    public static Cookie valueOf(final Class<Cookie> clazz, final String value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .fromString(value);
    }
*********/

    /**
     * Utility to replace deprectated method Cookie.valueOf
     *
     * @param clazz  must be of type NewCookie or Cookie
     * @param value
     * @param <S>
     * @return
     */
    public static <S> S valueOf(final Class<S> clazz, final String value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .fromString(value);
    }

    /**
     *
     * @param clazz
     * @param value
     * @param <S>
     * @return
     */
    public static <S> String toString(final Class<S> clazz, final S value) {
        return RuntimeDelegate.getInstance()
                .createHeaderDelegate(clazz)
                .toString(value);
    }
}
