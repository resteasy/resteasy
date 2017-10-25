package org.jboss.resteasy.test.resource.param.resource;

/***
 * 
 * @author Nicolas NESMON
 *
 * @param <E>
 */
public class PathParamWrapper<E> {

	private final E element;

	public PathParamWrapper(E element) {
		this.element = element;
	}
	
	public E getElement() {
		return element;
	}

}
