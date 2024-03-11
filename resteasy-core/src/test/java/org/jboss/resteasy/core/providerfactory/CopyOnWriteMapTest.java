package org.jboss.resteasy.core.providerfactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class CopyOnWriteMapTest {

    private final String KEY = "key";
    private final String VALUE = "value";
    private final String NEW_KEY = "newKey";
    private final String NEW_VALUE = "newValue";
    private final String NON_EXISTING_KEY = "nonExistingKey";
    private final String INCORRECT_OLD_VALUE = "incorrectOldValue";

    @Test
    public void testInitialState() {
        assertEmpty(new CopyOnWriteMap<>());
    }

    @Test
    public void testNonEmptyCopyOnWriteMap() {
        final Map<String, String> map = nonEmptyCopyOnWriteMap();
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertTrue(map.containsKey(KEY));
        assertTrue(map.containsValue(VALUE));
        assertEquals(VALUE, map.get(KEY));
        assertEquals(3, map.keySet().size());
        assertEquals(3, map.values().size());
        assertEquals(3, map.entrySet().size());
    }

    @Test
    public void testClear() {
        final Map<String, String> map = nonEmptyCopyOnWriteMap();
        assertEquals(3, map.size());
        map.clear();
        assertEmpty(map);
    }

    @Test
    public void testWithCopyOnWriteMapParameter() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertNotEquals(firstMap, secondMap);
        assertEquals(firstMap.entrySet(), secondMap.entrySet());
    }

    @Test
    public void testWithHashMapParameter() {
        final Map<String, String> firstMap = new HashMap<>();
        firstMap.put(KEY, VALUE);

        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertEquals(firstMap.entrySet(), secondMap.entrySet());
        firstMap.put(NEW_KEY, NEW_VALUE);
        assertFalse(secondMap.containsKey(NEW_KEY));
    }

    @Test
    public void testPut() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        secondMap.put(NEW_KEY, NEW_VALUE);
        assertEquals(4, secondMap.size());
        assertEquals(3, firstMap.size());
        assertFalse(firstMap.containsKey(NEW_KEY));
    }

    @Test
    public void testPutIfAbsentWithExisting() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertNotNull(secondMap.putIfAbsent(KEY, NEW_VALUE));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
    }

    @Test
    public void testPutIfAbsentNull() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertNull(secondMap.putIfAbsent(NEW_KEY, NEW_VALUE));
        assertEquals(4, secondMap.size());
        assertEquals(3, firstMap.size());
        assertFalse(firstMap.containsKey(NEW_KEY));
    }

    @Test
    public void testReplaceWithExisting() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertEquals(VALUE, secondMap.replace(KEY, NEW_VALUE));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
        assertEquals(VALUE, firstMap.get(KEY));
        assertEquals(NEW_VALUE, secondMap.get(KEY));
    }

    @Test
    public void testReplaceNull() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertNull(secondMap.replace(NON_EXISTING_KEY, NEW_VALUE));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
        assertEquals(VALUE, firstMap.get(KEY));
        assertEquals(VALUE, secondMap.get(KEY));
    }

    @Test
    public void testConditionalReplaceTrue() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertTrue(secondMap.replace(KEY, VALUE, NEW_VALUE), () -> VALUE);
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
        assertEquals(VALUE, firstMap.get(KEY));
        assertEquals(NEW_VALUE, secondMap.get(KEY));
    }

    @Test
    public void testConditionalReplaceFalse() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertFalse(secondMap.replace(KEY, INCORRECT_OLD_VALUE, NEW_VALUE));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
        assertEquals(VALUE, firstMap.get(KEY));
        assertEquals(VALUE, secondMap.get(KEY));
    }

    @Test
    public void testRemoveWithExisting() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertEquals(VALUE, secondMap.remove(KEY));
        assertEquals(2, secondMap.size());
        assertEquals(3, firstMap.size());
        assertTrue(firstMap.containsKey(KEY));
    }

    @Test
    public void testRemoveWithoutExisting() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertNull(secondMap.remove(NON_EXISTING_KEY));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
    }

    @Test
    public void testConditionalRemoveTrue() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertTrue(secondMap.remove(KEY, VALUE));
        assertEquals(2, secondMap.size());
        assertEquals(3, firstMap.size());
        assertTrue(firstMap.containsKey(KEY));
    }

    @Test
    public void testConditionalRemoveFalse() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new CopyOnWriteMap<>(firstMap);
        assertFalse(secondMap.remove(KEY, INCORRECT_OLD_VALUE));
        assertEquals(3, secondMap.size());
        assertEquals(3, firstMap.size());
    }

    @Test
    public void testPutAll() {
        final Map<String, String> firstMap = nonEmptyCopyOnWriteMap();
        final Map<String, String> secondMap = new HashMap<>();
        secondMap.put(NEW_KEY, NEW_VALUE);
        firstMap.putAll(secondMap);
        assertEquals(4, firstMap.size());
        assertEquals(NEW_VALUE, firstMap.get(NEW_KEY));
    }

    private Map<String, String> nonEmptyCopyOnWriteMap() {
        final Map<String, String> map = new CopyOnWriteMap<>();
        map.put(KEY, VALUE);
        map.put("key1", "value1");
        map.put("key2", "value2");

        return map;
    }

    private void assertEmpty(final Map<String, String> map) {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertFalse(map.containsKey(KEY));
        assertFalse(map.containsValue(VALUE));
        assertNull(map.get(KEY));
        assertTrue(map.keySet().isEmpty());
        assertTrue(map.values().isEmpty());
        assertTrue(map.entrySet().isEmpty());
    }
}
