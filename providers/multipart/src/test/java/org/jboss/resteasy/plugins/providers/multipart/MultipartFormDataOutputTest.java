/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.plugins.providers.multipart;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class MultipartFormDataOutputTest {

    @Test
    public void checkOrder() {
        final Map<String, Object> expectedMap = new LinkedHashMap<>();
        expectedMap.put("key3", "value3");
        expectedMap.put("key5", "value5");
        expectedMap.put("key1", "value1");
        expectedMap.put("key4", "value4");
        expectedMap.put("key2", "value2");
        final MultipartFormDataOutput output = new MultipartFormDataOutput();
        for (Map.Entry<String, Object> entry : expectedMap.entrySet()) {
            output.addFormData(entry.getKey(), entry.getValue(), MediaType.TEXT_PLAIN_TYPE);
        }

        // Expect the exact order
        final List<String> expectedKeys = new LinkedList<>(expectedMap.keySet());
        final List<String> outputKeys = new LinkedList<>(output.getFormDataMap().keySet());
        Assert.assertEquals("The size of the keys do not match", expectedKeys.size(), outputKeys.size());
        for (int i = 0; i < expectedMap.size(); i++) {
            Assert.assertEquals(expectedKeys.get(i), outputKeys.get(i));
        }
    }
}
