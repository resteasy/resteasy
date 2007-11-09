package com.damnhandy.resteasy.test.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Mock implementation of the {@link javax.servlet.ServletContext} interface.
 *
 * <p>Used for testing the Spring web framework; only rarely necessary for testing
 * application controllers. As long as application components don't explicitly
 * access the ServletContext, ClassPathXmlApplicationContext or
 * FileSystemXmlApplicationContext can be used to load the context files for testing,
 * even for DispatcherServlet context definitions.
 *
 * <p>For setting up a full WebApplicationContext in a test environment, you can
 * use XmlWebApplicationContext (or GenericWebApplicationContext), passing in an
 * appropriate MockServletContext instance. You might want to configure your
 * MockServletContext with a FileSystemResourceLoader in that case, to make your
 * resource paths interpreted as relative file system locations.
 *
 * <p>A common setup is to point your JVM working directory to the root of your
 * web application directory, in combination with filesystem-based resource loading.
 * This allows to load the context files as used in the web application, with
 * relative paths getting interpreted correctly. Such a setup will work with both
 * FileSystemXmlApplicationContext (which will load straight from the file system)
 * and XmlWebApplicationContext with an underlying MockServletContext (as long as
 * the MockServletContext has been configured with a FileSystemResourceLoader).
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see #MockServletContext(org.springframework.core.io.ResourceLoader)
 * @see org.springframework.web.context.support.XmlWebApplicationContext
 * @see org.springframework.web.context.support.GenericWebApplicationContext
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 * @see org.springframework.context.support.FileSystemXmlApplicationContext
 */
public class MockServletContext implements ServletContext {

	private static final String TEMP_DIR_SYSTEM_PROPERTY = "java.io.tmpdir";


	private final Log logger = LogFactory.getLog(getClass());

	private final Object resourceLoader;

	private final String resourceBasePath;

	private final Properties initParameters = new Properties();

	private final Hashtable attributes = new Hashtable();

	private String servletContextName = "MockServletContext";


	/**
	 * Create a new MockServletContext, using no base path and a
	 * DefaultResourceLoader (i.e. the classpath root as WAR root).
	 * @see org.springframework.core.io.DefaultResourceLoader
	 */
	public MockServletContext() {
		this("", null);
	}

	/**
	 * Create a new MockServletContext, using a DefaultResourceLoader.
	 * @param resourceBasePath the WAR root directory (should not end with a slash)
	 * @see org.springframework.core.io.DefaultResourceLoader
	 */
	public MockServletContext(String resourceBasePath) {
		this(resourceBasePath, null);
	}

	/**
	 * Create a new MockServletContext, using the specified ResourceLoader
	 * and no base path.
	 * @param resourceLoader the ResourceLoader to use (or null for the default)
	 */
	public MockServletContext(Object resourceLoader) {
		this("", resourceLoader);
	}

	/**
	 * Create a new MockServletContext.
	 * @param resourceBasePath the WAR root directory (should not end with a slash)
	 * @param resourceLoader the ResourceLoader to use (or null for the default)
	 */
	public MockServletContext(String resourceBasePath, Object resourceLoader) {
		this.resourceLoader = null;//(resourceLoader != null ? resourceLoader : new ºDefaultResourceLoader());
		this.resourceBasePath = (resourceBasePath != null ? resourceBasePath : "");

		// Use JVM temp dir as ServletContext temp dir.
		String tempDir = System.getProperty(TEMP_DIR_SYSTEM_PROPERTY);
		if (tempDir != null) {
			this.attributes.put("javax.servlet.context.tempdir", new File(tempDir));
		}
	}

    public String getContextPath() {
        return resourceBasePath;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
	 * Build a full resource location for the given path,
	 * prepending the resource base path of this MockServletContext.
	 * @param path the path as specified
	 * @return the full resource path
	 */
	protected String getResourceLocation(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return this.resourceBasePath + path;
	}


	public ServletContext getContext(String name) {
		throw new UnsupportedOperationException("getContext");
	}

	public int getMajorVersion() {
		return 2;
	}

	public int getMinorVersion() {
		return 4;
	}

	public String getMimeType(String filePath) {
		throw new UnsupportedOperationException("getMimeType");
	}

	public Set getResourcePaths(String path) {
        throw new RuntimeException("NOT IMPLEMENTED");
        /*

        String actualPath = (path.endsWith("/") ? path : path + "/");
		Resource resource = this.resourceLoader.getResource(getResourceLocation(actualPath));
		try {
			File file = resource.getFile();
			String[] fileList = file.list();
			if (ObjectUtils.isEmpty(fileList)) {
				return null;
			}
			Set resourcePaths = new LinkedHashSet();
			for (int i = 0; i < fileList.length; i++) {
				String resultPath = actualPath + fileList[i];
				if (resource.createRelative(fileList[i]).getFile().isDirectory()) {
					resultPath += "/";
				}
				resourcePaths.add(resultPath);
			}
			return resourcePaths;
		}
		catch (IOException ex) {
			logger.warn("Couldn't get resource paths for " + resource, ex);
			return null;
		}
		*/
	}

	public URL getResource(String path) throws MalformedURLException {
        throw new RuntimeException("NOT IMPLEMENTED");

        /*
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
		if (!resource.exists()) {
			return null;
		}
		try {
			return resource.getURL();
		}
		catch (MalformedURLException ex) {
			throw ex;
		}
		catch (IOException ex) {
			logger.warn("Couldn't get URL for " + resource, ex);
			return null;
		}
		*/
	}

	public InputStream getResourceAsStream(String path) {
        throw new RuntimeException("NOT IMPLEMENTED");
        /*
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
		if (!resource.exists()) {
			return null;
		}
		try {
			return resource.getInputStream();
		}
		catch (IOException ex) {
			logger.warn("Couldn't open InputStream for " + resource, ex);
			return null;
		}
		*/
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		if (!path.startsWith("/")) {
			throw new IllegalArgumentException("RequestDispatcher path at ServletContext level must start with '/'");
		}
		return new MockRequestDispatcher(path);
	}

	public RequestDispatcher getNamedDispatcher(String path) {
		return null;
	}

	public Servlet getServlet(String name) {
		return null;
	}

	public Enumeration getServlets() {
		return Collections.enumeration(Collections.EMPTY_SET);
	}

	public Enumeration getServletNames() {
		return Collections.enumeration(Collections.EMPTY_SET);
	}

	public void log(String message) {
		logger.info(message);
	}

	public void log(Exception ex, String message) {
		logger.info(message, ex);
	}

	public void log(String message, Throwable ex) {
		logger.info(message, ex);
	}

	public String getRealPath(String path) {
        throw new RuntimeException("NOT IMPLEMENTED");
        /*
        Resource resource = this.resourceLoader.getResource(getResourceLocation(path));
		try {
			return resource.getFile().getAbsolutePath();
		}
		catch (IOException ex) {
			logger.warn("Couldn't determine real path of resource " + resource, ex);
			return null;
		}
		*/
	}

	public String getServerInfo() {
		return "MockServletContext";
	}

	public String getInitParameter(String name) {
		Assert.notNull(name, "Parameter name must not be null");
		return this.initParameters.getProperty(name);
	}

	public void addInitParameter(String name, String value) {
		Assert.notNull(name, "Parameter name must not be null");
		this.initParameters.setProperty(name, value);
	}

	public Enumeration getInitParameterNames() {
		return this.initParameters.keys();
	}

	public Object getAttribute(String name) {
		Assert.notNull(name, "Attribute name must not be null");
		return this.attributes.get(name);
	}

	public Enumeration getAttributeNames() {
		return this.attributes.keys();
	}

	public void setAttribute(String name, Object value) {
		Assert.notNull(name, "Attribute name must not be null");
		if (value != null) {
			this.attributes.put(name, value);
		}
		else {
			this.attributes.remove(name);
		}
	}

	public void removeAttribute(String name) {
		Assert.notNull(name, "Attribute name must not be null");
		this.attributes.remove(name);
	}

	public void setServletContextName(String servletContextName) {
		this.servletContextName = servletContextName;
	}

	public String getServletContextName() {
		return this.servletContextName;
	}

}
