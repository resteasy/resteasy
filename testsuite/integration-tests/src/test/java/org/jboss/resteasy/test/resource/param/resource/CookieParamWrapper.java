package org.jboss.resteasy.test.resource.param.resource;

/***
 *
 * @author Nicolas NESMON
 *
 * @param <E>
 */
public class CookieParamWrapper<E> {

    private final E element;

    public CookieParamWrapper(final E element) {
        this.element = element;
    }

    public E getElement() {
        return element;
    }

}
