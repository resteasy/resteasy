package org.scannotation.test;

import com.titan.domain.Address;
import org.junit.Assert;
import org.junit.Test;
import org.scannotation.AnnotationDB;
import org.scannotation.classpath.ClasspathUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestSmoke {

    @Test
    public void testFindResourceBase() throws Exception {
        URL url = ClasspathUrlFinder.findResourceBase("com/titan/domain/Address.class");
        Assert.assertNotNull(url);
        verify(url);
    }

    @Test
    public void testFindResourceBases() throws Exception {
        URL[] urls = ClasspathUrlFinder.findResourceBases("com/titan/domain/Address.class");
        Assert.assertNotNull(urls);
        verify(urls);
    }

    @Test
    public void testFindClasspaths() throws Exception {
        Assert.assertNotNull(System.getProperty("java.class.path"));
        if (System.getProperty("java.class.path").indexOf("titan-cruise-1.0.jar") == -1) {
            System.err.println("WARNING!!!!!!!! CANNOT TEST testFindClasspaths():  This is a Maven2 and Surefire problem in that it doesn't set java.class.path correctly.  I run this test within the IDE");
        }

        URL[] urls = ClasspathUrlFinder.findClassPaths("titan-cruise-1.0.jar");
        Assert.assertNotNull(urls);
        verify(urls);
    }


    @Test
    public void testFindClasspaths2() throws Exception {
        Assert.assertNotNull(System.getProperty("java.class.path"));
        if (System.getProperty("java.class.path").indexOf("titan-cruise-1.0.jar") == -1) {
            System.err.println("WARNING!!!!!!! CANNOT TEST testFindClasspaths2():  This is a Maven2 and Surefire problem in that it doesn't set java.class.path correctly.  I run this test within the IDE");
        }

        URL[] urls = ClasspathUrlFinder.findClassPaths();
        Assert.assertNotNull(urls);
        AnnotationDB db = verify(urls);

        Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
        Set<String> tests = annotationIndex.get("org.junit.Test");
        Assert.assertTrue(tests.contains(TestSmoke.class.getName()));

    }


    @Test
    public void testByClass() throws Exception {
        URL url = ClasspathUrlFinder.findClassBase(Address.class);
        Assert.assertNotNull(url);
        verify(url);
    }


    private AnnotationDB verify(URL... urls)
            throws IOException {
        AnnotationDB db = new AnnotationDB();
        db.scanArchives(urls);

        Map<String, Set<String>> annotationIndex = db.getAnnotationIndex();
        {
            Set<String> entities = annotationIndex.get("javax.persistence.Entity");
            Assert.assertNotNull(entities);

            Assert.assertTrue(entities.contains("com.titan.domain.Address"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cabin"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCard"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCompany"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cruise"));
            Assert.assertTrue(entities.contains("com.titan.domain.Customer"));
            Assert.assertTrue(entities.contains("com.titan.domain.Phone"));
            Assert.assertTrue(entities.contains("com.titan.domain.Reservation"));
            Assert.assertTrue(entities.contains("com.titan.domain.Ship"));
        }

        {
            Set<String> entities = annotationIndex.get("javax.persistence.GeneratedValue");
            Assert.assertNotNull(entities);

            Assert.assertTrue(entities.contains("com.titan.domain.Address"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cabin"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCard"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCompany"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cruise"));
            Assert.assertTrue(entities.contains("com.titan.domain.Customer"));
            Assert.assertTrue(entities.contains("com.titan.domain.Phone"));
            Assert.assertTrue(entities.contains("com.titan.domain.Reservation"));
            Assert.assertTrue(entities.contains("com.titan.domain.Ship"));
        }

        {
            Set<String> entities = annotationIndex.get("javax.persistence.Id");
            Assert.assertNotNull(entities);

            Assert.assertTrue(entities.contains("com.titan.domain.Address"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cabin"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCard"));
            Assert.assertTrue(entities.contains("com.titan.domain.CreditCompany"));
            Assert.assertTrue(entities.contains("com.titan.domain.Cruise"));
            Assert.assertTrue(entities.contains("com.titan.domain.Customer"));
            Assert.assertTrue(entities.contains("com.titan.domain.Phone"));
            Assert.assertTrue(entities.contains("com.titan.domain.Reservation"));
            Assert.assertTrue(entities.contains("com.titan.domain.Ship"));
        }
        return db;
    }
}
