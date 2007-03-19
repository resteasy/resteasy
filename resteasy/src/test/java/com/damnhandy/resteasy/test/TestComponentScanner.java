/**
 * 
 */
package com.damnhandy.resteasy.test;

import java.util.Set;

import org.junit.Test;

import com.damnhandy.resteasy.scanner.WebResourceScanner;

/**
 * @author Ryan J. McDonough
 * Jan 15, 2007
 *
 */
public class TestComponentScanner {

	/**
	 * Test method for {@link com.damnhandy.resteasy.scanner.WebResourceScanner#handleItem(java.lang.String)}.
	 */
	@Test
	public void testFindResources() {
		WebResourceScanner scanner = new WebResourceScanner("resteasy.properties");
		Set<Class<Object>> classes = scanner.getClasses();
		//Assert.assertTrue(classes.size() != 0);	
		//Iterator<Class<Object>> iterator = classes.iterator();
	}
}
