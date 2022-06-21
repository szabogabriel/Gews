package gews.service;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import gews.config.Config;

public class MimeTypeCache {
	public Map<File, String> MIME_TYPE_CACHE = new HashMap<>();

	public void store(File file, String mimeType) {
		cacheMimeType(file, mimeType);
	}
	
	public String get(File file) {
		return MIME_TYPE_CACHE.get(file);
	}
	
	public void remove(File file) {
		MIME_TYPE_CACHE.remove(file);
	}
	
	private void cacheMimeType(File key, String value) {
		checkCacheSize();
		MIME_TYPE_CACHE.put(key, value);
	}
	
	private void checkCacheSize() {
		if (MIME_TYPE_CACHE.keySet().size() > Config.MIME_CACHE_MAX_SIZE.getIntValue()) {
			int elementOrder = (int)(Math.random() * (double)Config.MIME_CACHE_MAX_SIZE.getIntValue());
			Iterator<File> iter = MIME_TYPE_CACHE.keySet().iterator();
			File remove = null;
			for (int i = 0; i < elementOrder; i++) {
				if (iter.hasNext()) {
					remove = iter.next();
				}
			}
			MIME_TYPE_CACHE.remove(remove);
		}
	}
}