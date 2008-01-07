package org.scannotation.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface DirectoryIteratorFactory {
    Iterator<InputStream> create(URL url, Filter filter) throws IOException;
}
