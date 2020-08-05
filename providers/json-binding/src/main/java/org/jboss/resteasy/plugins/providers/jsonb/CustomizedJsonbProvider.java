package org.jboss.resteasy.plugins.providers.jsonb;

import javax.json.bind.Jsonb;

public interface CustomizedJsonbProvider {
    Jsonb getJsonb();
}
