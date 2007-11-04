
package com.damnhandy.scanner;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.MemberValue;

import org.apache.log4j.Logger;

/**
 * 
 * @author Ryan J. McDonough
 * 
 */

public class AnnotationScanner {
	private static final Logger logger = Logger.getLogger(AnnotationScanner.class);

	
	
	private String resourceName;
	private ClassLoader classLoader;
	private Map<Class<? extends Annotation>,AnnotatedTypeListener> listeners = 
		new ConcurrentHashMap<Class<? extends Annotation>,AnnotatedTypeListener>();
	
	
	/**
	 * 
	 * @param resourceName
	 */
	public AnnotationScanner(String resourceName) {
		this(resourceName, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 
	 * @param resourceName
	 * @param classLoader
	 */
	public AnnotationScanner(String resourceName, ClassLoader classLoader) {
		this.resourceName = resourceName;
		this.classLoader = classLoader;
		ClassFile.class.getPackage(); 
	}

	/**
	 * 
	 * @param annotation
	 * @param listener
	 */
	public void addAnnotationListener(Class<? extends Annotation> annotation, 
									  AnnotatedTypeListener listener) {
		this.listeners.put(annotation, listener);
	}
	
	/**
	 * 
	 * @param annotation
	 * @param listener
	 */
	public void removeAnnotationListener(Class<? extends Annotation> annotation) {
		this.listeners.remove(annotation);
	}
	
	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static String filenameToClassname(String filename) {
		return filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.').replace('\\', '.');
	}

	protected void scan() {
		Enumeration<URL> urls;
		try {
			urls = classLoader.getResources(resourceName);
		} catch (IOException ioe) {
			logger.warn("could not read: " + resourceName, ioe);
			return;
		}

		while (urls.hasMoreElements()) {
			try {
				URL url = urls.nextElement();
				logger.info("URL: "+url);
				String urlPath = url.getFile();
				urlPath = URLDecoder.decode(urlPath, "UTF-8");
				if (urlPath.startsWith("file:")) {
					// On windows urlpath looks like file:/C: on Linux
					// file:/home
					// substring(5) works for both
					urlPath = urlPath.substring(5);
				}
				if (urlPath.indexOf('!') > 0) {
					urlPath = urlPath.substring(0, urlPath.indexOf('!'));
				} else {
					File dirOrArchive = new File(urlPath);
					if (resourceName.lastIndexOf('/') > 0) {
						// for META-INF/components.xml
						dirOrArchive = dirOrArchive.getParentFile();
					}
					urlPath = dirOrArchive.getParent();
				}
				File file = new File(urlPath);
				if (file.isDirectory()) {
					handleDirectory(file, null);
				} else {
					handleArchive(file);
				}
			} catch (IOException ioe) {
				logger.warn("could not read entries", ioe);
			}
		}
	}

	/**
	 * 
	 * @param archiveFileName
	 * @throws ZipException
	 * @throws IOException
	 */
	private void handleArchive(File archiveFileName) throws ZipException, IOException {
		logger.debug("archive: " + archiveFileName);
		ZipFile zip = new ZipFile(archiveFileName);
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			logger.debug("found: " + name);
			handleItem(name);
		}
	}

	/**
	 * 
	 * @param directoryName
	 * @param path
	 */
	private void handleDirectory(File directoryName, String path) {
		logger.debug("directory: " + directoryName);
		for (File child : directoryName.listFiles()) {
			String newPath = path == null ? child.getName() : path + '/'
					+ child.getName();
			if (child.isDirectory()) {
				handleDirectory(child, newPath);
			} else {
				handleItem(newPath);
			}
		}
	}


	/**
	 * 
	 * @param name
	 */
	public void handleItem(String name) {
		if (name.endsWith(".class")) {
			String classname = filenameToClassname(name);
			//String filename = Scanner.componentFilename(name);
			try {
				ClassFile classFile = getClassFile(name);
				for(Map.Entry<Class<? extends Annotation>,AnnotatedTypeListener> entry : 
					listeners.entrySet()) {
					if (hasAnnotation(classFile, entry.getKey()))  {
						Class<?> targetClass = Class.forName(classFile.getName());
						AnnotatedTypeFoundEvent event = 
							new AnnotatedTypeFoundEvent(this,targetClass,entry.getKey());
						entry.getValue().annotatedTypeFound(event);
					}
				}
			} catch (NoClassDefFoundError ncdfe) {
				logger.debug("could not load class (missing dependency): "
						+ classname, ncdfe);

			} catch (IOException ioe) {
				logger.debug("could not load classfile: " + classname, ioe);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private ClassFile getClassFile(String resourceName) throws IOException {
		InputStream stream = classLoader.getResourceAsStream(resourceName);
		DataInputStream dstream = new DataInputStream(stream);
		try {
			return new ClassFile(dstream);
		} finally {
			dstream.close();
			stream.close();
		}
	}

	/**
	 * 
	 * @param classFile
	 * @param annotationType
	 * @return
	 */
	protected boolean hasAnnotation(ClassFile classFile,
									Class<? extends Annotation> annotationType) {
		AnnotationsAttribute visible = 
			(AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
		if (visible != null) {
			return visible.getAnnotation(annotationType.getName()) != null;
		}
		return false;
	}

	/**
	 * 
	 * @param classFile
	 * @param annotationType
	 * @param memberName
	 * @return
	 */
	protected String getAnnotationValue(ClassFile classFile,
										Class<? extends Annotation> annotationType, 
										String memberName) {
		AnnotationsAttribute visible = 
			(AnnotationsAttribute) classFile
				.getAttribute(AnnotationsAttribute.visibleTag);
		if (visible != null) {
			javassist.bytecode.annotation.Annotation annotation = 
				visible.getAnnotation(annotationType.getName());
			if (annotation == null) {
				return null;
			} else {
				MemberValue memberValue = annotation.getMemberValue(memberName);
				return memberValue == null ? null : memberValue.toString();
			}
		} else {
			return null;
		}
	}
}
