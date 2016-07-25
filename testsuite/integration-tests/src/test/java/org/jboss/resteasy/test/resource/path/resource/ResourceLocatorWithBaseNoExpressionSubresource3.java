package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import java.util.List;

public class ResourceLocatorWithBaseNoExpressionSubresource3 implements ResourceLocatorWithBaseExpressionSubresource3Interface {
    @Override
    public String get(List<Double> params) {
        Assert.assertNotNull(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, params);
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 2, params.size());
        params.get(0);
        params.get(1);
        return "ResourceLocatorWithBaseNoExpressionSubresource3";
    }
}
