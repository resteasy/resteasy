package org.scannotation.classpath;

import java.io.InputStream;

/**
 * Simpler iterator than java.util.iterator.  Things like JarInputStream does not allow you to implement hasNext()
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface StreamIterator
{
   InputStream next();
}
