package com.cheddargetter.client.util;

import java.util.*;

public class MapUtils {
    public static <K,V> HashMap<K,V> hashMap(Map.Entry<K,V>... entries) {
        return populate(new HashMap<K,V>(entries.length), entries);
    }

    public static <K,V> LinkedHashMap<K,V> linkedHashMap(Map.Entry<K,V>... entries) {
        return populate(new LinkedHashMap<K,V>(entries.length), entries);
    }

    public static <K,V> TreeMap<K,V> treeMap(Map.Entry<K,V>... entries) {
        return populate(new TreeMap<K,V>(), entries);
    }

    public static <K,V> Hashtable<K,V> hashtable(Map.Entry<K,V>... entries) {
        return populate(new Hashtable<K,V>(entries.length), entries);
    }

    public static <M extends Map, K, V> M populate(M map, Map.Entry<K,V>[] entries) {
        for (Map.Entry<K,V> entry : entries)
            map.put(entry.getKey(), entry.getValue());
        return map ;
    }

    public static <K,V> Map.Entry<K,V> entry(final K key, final V value) {
        return new Map.Entry<K,V>() {
            public K getKey() {
                return key;
            }

            public V getValue() {
                return value;
            }

            public V setValue(V o) {
                throw new IllegalArgumentException("Trying to set the value of an immutable map entry.");
            }
        };
    }
}
