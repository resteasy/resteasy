package org.jboss.resteasy.test.util;

import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Vector;

/**
 * @tpSubChapter Util tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for javax.ws.rs.core.MultivaluedMap class
 *                    and javax.ws.rs.core.MultivaluedHashMap.
 * @tpSince RESTEasy 3.0.16
 */
public class MultivaluedMapTest {

    /**
     * @tpTestDetails Check map order, size and compare with other objects.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEdge() {
        String defaultErrMsg = "MultivaluedMap works incorrectly";
        String hashErrMsg = "MultivaluedHashMap works incorrectly";
        MultivaluedMap<String, Object> map;
        Vector<Response> vec;
        final String[] KEYS = {"key0", "key1", "key2"};

        Response r = Response.ok().build();
        map = r.getMetadata();
        vec = new Vector<>();
        vec.add(r);
        map.add(KEYS[0], this);
        map.add(null, vec);
        map.add(null, this);
        List<Object> objects = map.get(null);
        Assert.assertTrue(defaultErrMsg, objects.size() == 2);
        Assert.assertTrue(defaultErrMsg, map.size() == 2);
        Assert.assertTrue(defaultErrMsg, map.getFirst(null).getClass() == Vector.class);

        map = Response.ok().build().getMetadata();
        Object o1 = new StringBuilder().append(KEYS[0]);
        Object o2 = new StringBuffer().append(KEYS[1]);
        Object o3 = new Object() {
            @Override
            public String toString() {
                return KEYS[2];
            }
        };
        map.add(KEYS[0], o1);
        map.add(KEYS[0], o2);
        map.add(KEYS[0], o3);

        MultivaluedHashMap<String, Object> map2 = new MultivaluedHashMap<String, Object>();
        map2.addAll(KEYS[0], o3, o1, o2);

        Assert.assertTrue(hashErrMsg, map.equalsIgnoreValueOrder(map2));
        Assert.assertFalse(hashErrMsg, map.equals(map2));
    }
}
