package org.jboss.resteasy.plugins.delegates;

import org.junit.Test;

public class MediaTypeHeaderDelegateTest {

    @Test(expected = IllegalArgumentException.class)
    public void parsingBrokenMediaTypeShouldThrowIllegalArgumentException_minimized() {
        MediaTypeHeaderDelegate.parse("x; /x");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parsingBrokenMediaTypeShouldThrowIllegalArgumentException_actual() {
        MediaTypeHeaderDelegate.parse("() { ::}; echo \"NS:\" $(/bin/sh -c \"expr 123456 - 123456\")");
    }
}
