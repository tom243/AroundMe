package com.tc.aroundme.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomNetworkImageView extends NetworkImageView {

    private Bitmap  mLocalBitmap;
    private boolean mShowLocal;
    private Context mContext;

    public void setLocalImageBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            mShowLocal = true;
        }
        bitmap= getCircularBitmap(bitmap);
        this.mLocalBitmap = bitmap;
        requestLayout();
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        mShowLocal = false;
        super.setImageUrl(url, imageLoader);
    }

    public CustomNetworkImageView(Context context) {
        this(context, null);
        mContext = context;
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public CustomNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    super.onLayout(changed, left, top, right, bottom);
    if (mShowLocal) {
            setImageBitmap(mLocalBitmap);
        }
    }
    
	@Override
	public void setImageBitmap(Bitmap bm) {
		if(bm==null) return;
		setImageDrawable(new BitmapDrawable(mContext.getResources(),
				getCircularBitmap(bm)));
	}
    
	/**
	 * Creates a circular bitmap and uses whichever dimension is smaller to determine the width
	 * <br/>Also constrains the circle to the leftmost part of the image
	 * 
	 * @param bitmap
	 * @return bitmap
	 */
	public Bitmap getCircularBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int width = bitmap.getWidth();
		if(bitmap.getWidth()>bitmap.getHeight())
			width = bitmap.getHeight();
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, width, width);
		final RectF rectF = new RectF(rect);
		final float roundPx = width / 2;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}
	

}
