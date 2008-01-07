package org.scannotation.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FileIterator implements Iterator<InputStream> {
    private Iterator files;

    public FileIterator(File file, Filter filter) {
        ArrayList list = new ArrayList();
        try {
            create(list, file, filter);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        files = list.iterator();
    }

    protected static void create(List list, File dir, Filter filter) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                create(list, files[i], filter);
            } else {
                if (filter == null || filter.accepts(files[i].getAbsolutePath())) {
                    list.add(files[i]);
                }
            }
        }
    }

    public boolean hasNext() {
        return files.hasNext();
    }

    public InputStream next() {
        File fp = (File) files.next();
        try {
            return new FileInputStream(fp);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove() {
        throw new RuntimeException("Illegal operation call");
    }


}
