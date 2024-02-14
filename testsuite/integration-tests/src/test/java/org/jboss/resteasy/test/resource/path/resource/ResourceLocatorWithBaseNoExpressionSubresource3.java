package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import org.junit.jupiter.api.Assertions;

public class ResourceLocatorWithBaseNoExpressionSubresource3 implements ResourceLocatorWithBaseExpressionSubresource3Interface {
    @Override
    public String get(List<Double> params) {
        Assertions.assertNotNull(params, ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(2, params.size(), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        params.get(0);
        params.get(1);
        return "ResourceLocatorWithBaseNoExpressionSubresource3";
    }
}
