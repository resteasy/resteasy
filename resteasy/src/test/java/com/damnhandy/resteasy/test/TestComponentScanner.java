/**
 * 
 */
package com.damnhandy.resteasy.test;

import java.util.Set;

import org.jboss.seam.deployment.WebResourceScanner;
import org.junit.Test;

/**
 * @author Ryan J. McDonough
 * Jan 15, 2007
 *
 */
public class TestComponentScanner {

	/**
	 * Test method for {@link org.jboss.seam.deployment.WebResourceScanner#handleItem(java.lang.String)}.
	 */
	@Test
	public void testFindResources() {
		WebResourceScanner scanner = new WebResourceScanner("resteasy.properties");
		Set<Class<Object>> classes = scanner.getClasses();
		//Assert.assertTrue(classes.size() != 0);	
		//Iterator<Class<Object>> iterator = classes.iterator();
	}
}
