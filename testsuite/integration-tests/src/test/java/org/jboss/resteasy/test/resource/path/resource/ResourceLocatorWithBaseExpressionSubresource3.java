package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import org.junit.Assert;

public class ResourceLocatorWithBaseExpressionSubresource3 implements ResourceLocatorWithBaseExpressionSubresource3Interface {
    @Override
    public String get(List<Double> params) {
        Assert.assertNotNull(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, params);
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, 2, params.size());
        params.get(0);
        params.get(1);
        return "ResourceLocatorWithBaseExpressionSubresource3";
    }
}
