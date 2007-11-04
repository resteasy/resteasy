/**
 * 
 */
package com.damnhandy.scanner;

/**
 * @author ryan
 *
 */
public class DummyListener implements AnnotatedTypeListener {

	private boolean status = false;
	
	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see com.damnhandy.scanner.AnnotationListener#handleAnnotation(com.damnhandy.scanner.AnnotationFoundEvent)
	 */
	public void annotatedTypeFound(AnnotatedTypeFoundEvent event) {
		setStatus(true);
		System.out.println("Found Annotated Class: "+event.getAnnotatedClass().getSimpleName());
	}
	
	

}
