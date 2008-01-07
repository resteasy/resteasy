package org.scannotation.classpath;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class WarUrlFinder {
    public static URL[] findWebInfLibClasspaths(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        return findWebInfLibClasspaths(servletContext);
    }

    public static URL[] findWebInfLibClasspaths(ServletContext servletContext) {
        ArrayList<URL> list = new ArrayList<URL>();
        Set libJars = servletContext.getResourcePaths("/WEB-INF/lib");
        for (Object jar : libJars) {
            try {
                list.add(servletContext.getResource((String) jar));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return list.toArray(new URL[list.size()]);
    }

    public static URL findWebInfClassesPath(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        return findWebInfClassesPath(servletContext);
    }

    /**
     * Find the URL pointing to "/WEB-INF/classes"  This method may not work in conjunction with IteratorFactory
     * if your servlet container does not extract the /WEB-INF/classes into a real file-based directory
     *
     * @param servletContext
     * @return
     */
    public static URL findWebInfClassesPath(ServletContext servletContext) {
        Set libJars = servletContext.getResourcePaths("/WEB-INF/classes");
        for (Object jar : libJars) {
            try {
                URL url = servletContext.getResource((String) jar);
                String urlString = url.toString();
                int index = urlString.lastIndexOf("/WEB-INF/classes/");
                urlString = urlString.substring(0, index + "/WEB-INF/classes/".length());
                return new URL(urlString);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;

    }
}
