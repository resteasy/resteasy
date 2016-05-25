package org.jboss.resteasy.test.finegrain;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Vector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CaseInsensitiveTest
{
   @Test
   public void testEdge()
   {
      MultivaluedMap<String, Object> map;
      Vector<Response> vec;
      final String[] KEYS = { "key0", "key1", "key2" };

      Response r = Response.ok().build();
      map = r.getMetadata();
      vec = new Vector<Response>();
      vec.add(r);
      map.add(KEYS[0], this);
      map.add(null, vec);
      map.add(null, this);
      List<Object> objects = map.get(null);
      Assert.assertTrue(objects.size() == 2);
      Assert.assertTrue(map.size() == 2);
      Assert.assertTrue(map.getFirst(null).getClass() == Vector.class);

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

      Assert.assertTrue(map.equalsIgnoreValueOrder(map2));
      Assert.assertFalse(map.equals(map2));


   }
}
