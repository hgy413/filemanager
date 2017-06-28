package com.jb.filemanager.function.fileexplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 点击子View透明化的布局
 * 
 * @author chenbenbin
 * 
 */
public class ClickTransparentLayout extends RelativeLayout {
	/**
	 * 半透明
	 */
	private static final int HALF_TRANSPARENT = 128;
	/**
	 * 透明值
	 */
	private int mTransparentValue = HALF_TRANSPARENT;
	private Rect mRect = new Rect();
	private int mAction = 0;

	public ClickTransparentLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (isPressDown() || !isEnabled()) {
			// 获取显示范围
			getDrawingRect(mRect);
			// 将界面设置为半透明
			canvas.saveLayerAlpha(0, 0, mRect.right, mRect.bottom,
					mTransparentValue, Canvas.ALL_SAVE_FLAG);
			super.dispatchDraw(canvas);
			canvas.restore();
		} else {
			super.dispatchDraw(canvas);
		}
	}

	/**
	 * 由于是ViewGroup，需要将事件传递给子View，所以这时候即使收到了ACTION_UP事件，也处于按下状态
	 */
	private boolean isPressDown() {
		return isPressed() && mAction != MotionEvent.ACTION_UP;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mAction = event.getAction();
		if (mAction != MotionEvent.ACTION_MOVE) {
			invalidate();
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置透明度
	 */
	public void setTransparentValue(int value) {
		if (value >= 0 && value <= 255) {
			mTransparentValue = value;
		} else {
			throw new IllegalArgumentException(
					"Transparent values should be [0..255]");
		}
	}
}
