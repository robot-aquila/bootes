package ru.prolib.bootes.lib.config.kvstore;

public interface KVStore {
	
	boolean hasKey(String key);
	String get(String key);

}
