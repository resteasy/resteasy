/**
 * 
 */
package com.damnhandy.scanner;

import java.net.URL;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ryan J. McDonough
 *
 */
public class TestAnnotationScanner {
	
	private static final Logger logger = Logger.getLogger(TestAnnotationScanner.class);
	
	static {
		URL log4j = Thread.currentThread().getContextClassLoader().getResource("log4j.xml");
        DOMConfigurator.configure(log4j);
	}
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		logger.info("Starting up...");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.damnhandy.scanner.AnnotationScanner#scan()}.
	 */
	@Test
	public void testScan() {
		AnnotationScanner scanner = new AnnotationScanner("scanner.properties");
		DummyListener listener = new DummyListener();
		scanner.addAnnotationListener(DummyAnnotation.class, listener);
		scanner.scan();
		while(!listener.isStatus()) {
			if(listener.isStatus()) {
				Assert.assertTrue(listener.isStatus());
				break;
			}
		}
	}

}
