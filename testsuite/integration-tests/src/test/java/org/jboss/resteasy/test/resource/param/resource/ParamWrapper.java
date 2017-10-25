package org.jboss.resteasy.test.resource.param.resource;

/***
 * 
 * @author Nicolas NESMON
 *
 * @param <E>
 */
public class ParamWrapper<E> {

	private final E element;

	public ParamWrapper(E element) {
		this.element = element;
	}
	
	public E getElement() {
		return element;
	}

}
