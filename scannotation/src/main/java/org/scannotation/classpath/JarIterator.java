package org.scannotation.classpath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JarIterator implements Iterator<InputStream> {
    JarInputStream jar;
    JarEntry next;
    Filter filter;

    public JarIterator(File file, Filter filter) throws IOException {
        this(new FileInputStream(file), filter);
    }


    private void setNext() {
        try {
            if (next != null) jar.closeEntry();
            next = null;
            do {
                next = jar.getNextJarEntry();
            } while (next != null && (next.isDirectory() || (filter == null || !filter.accepts(next.getName()))));
            if (next == null) jar.close();
        }
        catch (IOException e) {
            throw new RuntimeException("failed to browse jar", e);
        }
    }

    public JarIterator(InputStream is, Filter filter) throws IOException {
        this.filter = filter;
        jar = new JarInputStream(is);
        setNext();
    }

    public boolean hasNext() {
        return next != null;
    }

    public InputStream next() {
        int size = (int) next.getSize();
        byte[] buf = new byte[size];
        int count = 0;
        int current = 0;
        try {
            while ((
                    (
                            current = jar.read(buf, count,
                                    size - count)
                    ) != -1
            ) && (count < size)) {
                count += current;
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            setNext();
            return bais;
        }
        catch (IOException e) {
            try {
                jar.close();
            }
            catch (IOException ignored) {

            }
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new RuntimeException("Illegal operation on ArchiveBrowser");
    }
}
