package org.jboss.resteasy.spi;

import org.junit.Test;

public class ResteasyDeploymentTest {

    @Test(expected = ApplicationException.class)
    public void testWhenApplicationClassIsEmptyExceptionIsTrown() throws Exception {
        String applicationClass = "";
        ResteasyDeployment.createApplication(applicationClass, null);
    }
}
