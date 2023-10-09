package org.jboss.resteasy.client.jaxrs.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.junit.jupiter.api.Test;

public class TrackingMapTest {

    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_CONTROL_HEADER_VAL = "nocache";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_HEADER_VAL = "application/json";

    @Test
    public void testRemoveExistingKey() {
        final CaseInsensitiveMap<String> caseInsensitiveMap = new CaseInsensitiveMap<>();
        caseInsensitiveMap.add(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VAL);
        caseInsensitiveMap.add(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VAL);

        final TrackingMap<String> trackingMap = new TrackingMap<>(caseInsensitiveMap);

        trackingMap.remove(CACHE_CONTROL_HEADER);
        trackingMap.remove(CONTENT_TYPE_HEADER, Collections.singletonList(CONTENT_TYPE_HEADER_VAL));

        assertEquals(2, trackingMap.getRemovedKeys().size(), () -> "Removed keys list should be empty");
        assertArrayEquals(new Object[] { CACHE_CONTROL_HEADER, CONTENT_TYPE_HEADER }, trackingMap.getRemovedKeys().toArray());
    }

    @Test
    public void testRemoveNonExistingKey() {
        final TrackingMap<String> trackingMap = createStringMap();

        trackingMap.remove("Cache-Control");
        trackingMap.remove("Content-Type", "application/json");

        assertTrue(trackingMap.getRemovedKeys().isEmpty(), () -> "Removed keys list should be empty");
    }

    @Test
    public void testRemoveAndAddExistingKeys() {
        final CaseInsensitiveMap<String> caseInsensitiveMap = new CaseInsensitiveMap<>();
        caseInsensitiveMap.add(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VAL);
        caseInsensitiveMap.add(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VAL);

        final TrackingMap<String> trackingMap = new TrackingMap<>(caseInsensitiveMap);

        trackingMap.remove(CACHE_CONTROL_HEADER);
        trackingMap.remove(CONTENT_TYPE_HEADER, Collections.singletonList(CONTENT_TYPE_HEADER_VAL));

        assertArrayEquals(new Object[] { CACHE_CONTROL_HEADER, CONTENT_TYPE_HEADER }, trackingMap.getRemovedKeys().toArray());

        trackingMap.add(CACHE_CONTROL_HEADER, "no-store");
        trackingMap.add(CONTENT_TYPE_HEADER, "text-plain");

        assertEquals(Collections.singletonList("no-store"), trackingMap.get(CACHE_CONTROL_HEADER));
        assertEquals(Collections.singletonList("text-plain"), trackingMap.get(CONTENT_TYPE_HEADER));
        assertTrue(trackingMap.getRemovedKeys().isEmpty());
        assertArrayEquals(new Object[] { CACHE_CONTROL_HEADER, CONTENT_TYPE_HEADER },
                trackingMap.getAddedOrUpdatedKeys().toArray());
        assertTrue(trackingMap.getRemovedKeys().isEmpty());
    }

    @Test
    public void testClone() {
        final TrackingMap<String> trackingMap = createStringMap();

        trackingMap.add(CACHE_CONTROL_HEADER, "no-store");
        trackingMap.add(CONTENT_TYPE_HEADER, "text-plain");

        final TrackingMap<String> clone = trackingMap.clone();

        // Checking if the object instances are different.
        assertNotSame(trackingMap, clone);
        assertNotSame(trackingMap.getAddedOrUpdatedKeys(), clone.getAddedOrUpdatedKeys());
        assertNotSame(trackingMap.getRemovedKeys(), clone.getRemovedKeys());

        // Checking if the values are same.
        assertEquals(trackingMap, clone);
        assertEquals(trackingMap.getAddedOrUpdatedKeys(), clone.getAddedOrUpdatedKeys());
        assertEquals(trackingMap.getRemovedKeys(), clone.getRemovedKeys());

        // Modifications to the clone should not affect the original object.
        clone.add("some-other-header", "header-value");
        assertEquals(2, trackingMap.getAddedOrUpdatedKeys().size());
        assertNull(trackingMap.get("some-other-header"));
        assertEquals(3, clone.getAddedOrUpdatedKeys().size());
        assertNotNull(clone.get("some-other-header"));
    }

    @Test
    public void testClear() {
        final CaseInsensitiveMap<String> caseInsensitiveMap = new CaseInsensitiveMap<>();
        caseInsensitiveMap.add(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VAL);

        final TrackingMap<String> trackingMap = new TrackingMap<>(caseInsensitiveMap);
        trackingMap.add(CONTENT_TYPE_HEADER, CONTENT_TYPE_HEADER_VAL);
        assertArrayEquals(new Object[] { CONTENT_TYPE_HEADER }, trackingMap.getAddedOrUpdatedKeys().toArray());

        trackingMap.clear();
        assertTrue(trackingMap.getAddedOrUpdatedKeys().isEmpty());
        assertArrayEquals(new Object[] { CACHE_CONTROL_HEADER, CONTENT_TYPE_HEADER }, trackingMap.getRemovedKeys().toArray());
    }

    /**
     * This test demonstrates a known limitation in this decorator.
     * Any changes directly to the list value of the map will not be tracked.
     */
    @Test
    public void testChangeToValue_ofMutableCollectionType() {
        final CaseInsensitiveMap<String> caseInsensitiveMap = new CaseInsensitiveMap<>();
        final List<String> value = new ArrayList<>();
        value.add(CONTENT_TYPE_HEADER_VAL);
        caseInsensitiveMap.put(CONTENT_TYPE_HEADER, value);

        final TrackingMap<String> trackingMap = new TrackingMap<>(caseInsensitiveMap);

        trackingMap.get(CONTENT_TYPE_HEADER).add("text-plain");

        assertTrue(trackingMap.getAddedOrUpdatedKeys().isEmpty());
    }

    private TrackingMap<String> createStringMap() {
        return new TrackingMap<>(new CaseInsensitiveMap<>());
    }
}
