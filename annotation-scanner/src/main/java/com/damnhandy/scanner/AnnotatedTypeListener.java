/**
 * 
 */
package com.damnhandy.scanner;

/**
 * A listener interface which listens to the AnnotationScanner for Classes
 * annotated with a given annotation.
 * 
 * @author Ryan J. McDonough
 * May 29, 2007
 *
 */
public interface AnnotatedTypeListener {

	/**
	 * Called when an annotation matching the desired type has been found.
	 * @param event
	 */
	public void annotatedTypeFound(AnnotatedTypeFoundEvent event);
	

}
