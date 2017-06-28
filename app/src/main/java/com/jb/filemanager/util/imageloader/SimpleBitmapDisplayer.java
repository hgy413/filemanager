package com.jb.filemanager.util.imageloader;

import android.graphics.Bitmap;

import com.jb.filemanager.util.imageloader.imageaware.ImageAware;

/**
 * Just displays {@link Bitmap} in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.5.6
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware) {
		imageAware.setImageBitmap(bitmap);
	}
}