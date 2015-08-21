package com.aroundme.util;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
 
/**
 * @author Tomer and chen
 * 
 *	cache for images
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements
        ImageCache {
	
    /**
     * @return size of cache
     */
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        return cacheSize;
    }
 
    /**
     *  constructor for LruBitmapCache
     */
    public LruBitmapCache() {
        this(getDefaultLruCacheSize());
    }
 
    /**
     * @param sizeInKiloBytes size of cache
     */
    public LruBitmapCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }
 
    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight() / 1024;
    }
 
    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }
 
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
