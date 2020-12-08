package org.jboss.resteasy.test.mediatype;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.junit.Test;

public class MediaTypeHeaderTest {

   @Test(expected = IllegalArgumentException.class)
   public void testNewLineInHeaderValueIsRejected() {
      MediaTypeHeaderDelegate delegate = new MediaTypeHeaderDelegate();

      delegate.fromString("foo/bar\n");
   }
}
