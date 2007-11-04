/**
 * 
 */
package com.damnhandy.scanner;

import java.lang.annotation.Annotation;
import java.util.EventObject;

/**
 * An event instance that is fired whenever a class is found that is
 * annotated with the target annotation.
 * 
 * @author Ryan J. McDonough
 * May 29, 2007
 *
 */
public class AnnotatedTypeFoundEvent extends EventObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9034130148214080570L;

	/**
	 * The class that is annotated with the target annotation
	 */
	private Class<?> annotatedClass;
	/**
	 * The annotation instance that is being searched for
	 */
	private Class<? extends Annotation> targetAnnotation;
	
	/**
	 * 
	 * @param source
	 * @param annotatedClass
	 * @param targetAnnotation
	 */
	public AnnotatedTypeFoundEvent(Object source,
								   Class<?> annotatedClass,
								   Class<? extends Annotation> targetAnnotation) {
		super(source);
		this.annotatedClass = annotatedClass;
		this.targetAnnotation = targetAnnotation;
	}
	/**
	 * @return the annotatedClass
	 */
	public Class<?> getAnnotatedClass() {
		return annotatedClass;
	}

	/**
	 * @return the annotation
	 */
	public Class<? extends Annotation> getTargetAnnotation() {
		return targetAnnotation;
	}

	



}
