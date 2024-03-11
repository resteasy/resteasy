package org.jboss.resteasy.test.mediatype;

import org.jboss.resteasy.plugins.delegates.MediaTypeHeaderDelegate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MediaTypeHeaderTest {

    @Test
    public void testNewLineInHeaderValueIsRejected() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    MediaTypeHeaderDelegate delegate = new MediaTypeHeaderDelegate();

                    delegate.fromString("foo/bar\n");
                });
        Assertions.assertTrue(thrown instanceof IllegalArgumentException);
    }
}
