/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@RequestScoped
public class AlternativeOrderResource extends OrderResource {
    @Override
    public String get() {
        return "alternative";
    }
}
