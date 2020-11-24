package org.jboss.resteasy.test.mediatype;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.junit.Test;
import javax.ws.rs.BadRequestException;

public class MediaTypeHeaderTest {

   @Test(expected = BadRequestException.class)
   public void testNewLineInHeaderValueIsRejected() {
      MediaTypeHeaderDelegate delegate = new MediaTypeHeaderDelegate();

      delegate.fromString("foo/bar\n");
   }
}
