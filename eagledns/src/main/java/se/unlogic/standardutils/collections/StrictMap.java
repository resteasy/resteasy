package se.unlogic.standardutils.collections;

import java.util.Map;


public interface StrictMap<Key, Value> extends Map<Key, Value> {

	Value put(Key key, Value value) throws KeyAlreadyCachedException;

	Value update(Key key, Value value) throws KeyNotCachedException;

	Value remove(Object key) throws KeyNotCachedException;
}
