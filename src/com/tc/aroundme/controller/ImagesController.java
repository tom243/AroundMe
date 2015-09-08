package com.tc.aroundme.controller;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.tc.aroundme.common.AroundMeApp;
import com.tc.aroundme.util.LruBitmapCache;
 
/**
 * @author Tomer and chen
 *
 * controller for saving the images of the users
 */
public class ImagesController {
 
    public static final String TAG = ImagesController.class.getSimpleName();
 
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
 
    private static ImagesController mInstance;
    
    private ImagesController() {
    	mInstance = this;
    }
    
    /**
     * @return instance of ImagesController
     */
    public static synchronized ImagesController getInstance() {
    	if (mInstance == null)
    		mInstance = new ImagesController();
    	return mInstance;
    }
 
    /**
     * @return request requestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(AroundMeApp.getContext());
        }
 
        return mRequestQueue;
    }
 
    /**
     * @return image loader
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
 
    /**
     * @param req request of the image 
     * @param tag tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
 
    /**
     * @param req request of the image
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
 
    /**
     * @param tag tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}