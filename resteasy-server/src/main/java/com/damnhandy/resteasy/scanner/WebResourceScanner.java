package com.damnhandy.resteasy.scanner;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.log4j.Logger;

import com.damnhandy.resteasy.annotations.WebResource;
import com.damnhandy.resteasy.annotations.WebResources;
import com.damnhandy.resteasy.common.Scanner;

/**
 * 
 * @author ryan
 *
 */
public class WebResourceScanner extends Scanner {
	private static final Logger log = Logger.getLogger(WebResourceScanner.class);

	private Set<Class<Object>> classes;

	public WebResourceScanner(String resourceName) {
		super(resourceName);
	}

	public WebResourceScanner(String resourceName, ClassLoader classLoader) {
		super(resourceName, classLoader);
	}

	/**
	 * Returns only Seam components (ie: classes annotated with
	 * 
	 * @Name)
	 */
	public Set<Class<Object>> getClasses() {
		if (classes == null) {
			classes = new HashSet<Class<Object>>();
			scan();
		}
		return classes;
	}

	
	@SuppressWarnings("unchecked")
	public void handleItem(String name) {
		if (name.endsWith(".class")) {
			String classname = filenameToClassname(name);
			String filename = Scanner.componentFilename(name);
			try {
				ClassFile classFile = getClassFile(name);

				if ((hasAnnotation(classFile, WebResource.class) || 
					 hasAnnotation(classFile, WebResources.class) ) || 
					classLoader.getResources(filename).hasMoreElements()) {
					if (log.isDebugEnabled()) {
						log.info("Found WebResource: " + name);
					}
					classes.add((Class<Object>) classLoader.loadClass(classname));
				}
			} catch (ClassNotFoundException cnfe) {
				log.debug("could not load class: " + classname, cnfe);

			} catch (NoClassDefFoundError ncdfe) {
				log.debug("could not load class (missing dependency): "
						+ classname, ncdfe);

			} catch (IOException ioe) {
				log.debug("could not load classfile: " + classname, ioe);
			}
		}
	}
}
