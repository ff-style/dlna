package com.talent.allshare.widget.switchbutton;

import com.youplayer.player.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CheckBox;


public class SwitchButton extends CheckBox {
	private Paint mPaint;
	private ViewParent mParent;
	private Bitmap bottom;
	private Bitmap btn;
	private Bitmap btn_pressed;
	private Bitmap btn_unpressed;
	private Bitmap frame;
	private Bitmap mask;

	private float downY; 
	private float downX; 
	private float realPos;
	private float btnPos; 
	private float btnOnPos; 
	private float btnOffPos; 
	private final float offsetY = 0; 
	private float maskWidth;
	private float maskHeight;
	private float btnWidth;
	private float btnInitPos;

	private int mClickTimeout;
	private int mTouchSlop;
	private int mAlpha = 255;

	private boolean mChecked = false;
	private boolean mBroadcasting;
	private boolean doTurnOnAni;

	private PerformClick mPerformClick;

	private OnCheckedChangeListener mOnCheckedChangeListener;
	private OnCheckedChangeListener mOnCheckedChangeWidgetListener;

	private SetCheckedHandler setCheckedHandler = new SetCheckedHandler();

	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.checkboxStyle);
	}

	public SwitchButton(Context context) {
		this(context, null);
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		Resources resources = context.getResources();

		// get viewConfiguration
		mClickTimeout = ViewConfiguration.getPressedStateDuration()+ ViewConfiguration.getTapTimeout();
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

		// get Bitmap
		bottom = BitmapFactory.decodeResource(resources, R.drawable.switch_button_switch);
		btn_pressed = BitmapFactory.decodeResource(resources,R.drawable.switch_button);
		btn_unpressed = BitmapFactory.decodeResource(resources,R.drawable.switch_button);
		frame = BitmapFactory.decodeResource(resources, R.drawable.switch_button_frame);
		mask = BitmapFactory.decodeResource(resources, R.drawable.switch_button_mask);
		btn = btn_unpressed;

		btnWidth = btn_pressed.getWidth();
		maskWidth = mask.getWidth();
		maskHeight = mask.getHeight();

		btnOffPos = btnWidth / 2;
		btnOnPos = maskWidth - btnWidth / 2;

		btnPos = mChecked ? btnOnPos : btnOffPos;
		realPos = getRealPos(btnPos);
	}

	@Override
	public void setEnabled(boolean enabled) {
		
		
		mAlpha = enabled ? 255 : 128;
		super.setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	
	public boolean isChecked() {
		return mChecked;
	}

	public void toggle() {
		setChecked(!mChecked);
	}

	
	private void _setChecked(boolean checked) {
		int msg = checked ? 1 : 0;
		setCheckedHandler.sendEmptyMessageDelayed(msg, 10);
	}

	
	public void setChecked(boolean checked) {
		if (mChecked != checked) {
			mChecked = checked;

			btnPos = checked ? btnOnPos : btnOffPos;
			realPos = getRealPos(btnPos);
			invalidate();
			if (mBroadcasting) {
				return;
			}
			mBroadcasting = true;
			if (mOnCheckedChangeListener != null) {
				mOnCheckedChangeListener.onCheckedChanged(SwitchButton.this,mChecked);
			}
			if (mOnCheckedChangeWidgetListener != null) {
				mOnCheckedChangeWidgetListener.onCheckedChanged(SwitchButton.this, mChecked);
			}
			mBroadcasting = false;
		}
	}

	private class SetCheckedHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			boolean checked = msg.what == 1;
			if (mChecked != checked) {
				mChecked = checked;

				if (mBroadcasting) {
					return;
				}
				mBroadcasting = true;
				if (mOnCheckedChangeListener != null) {
					mOnCheckedChangeListener.onCheckedChanged(SwitchButton.this, mChecked);
				}
				if (mOnCheckedChangeWidgetListener != null) {
					mOnCheckedChangeWidgetListener.onCheckedChanged(SwitchButton.this, mChecked);
				}

				mBroadcasting = false;
			}
			super.handleMessage(msg);
		}

	}

	
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeListener = listener;
	}
	
	void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
		mOnCheckedChangeWidgetListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		float deltaX = Math.abs(x - downX);
		float deltaY = Math.abs(y - downY);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			attemptClaimDrag();
			downX = x;
			downY = y;
			btn = btn_pressed;
			btnInitPos = mChecked ? btnOnPos : btnOffPos;
			break;
		case MotionEvent.ACTION_MOVE:
			float time = event.getEventTime() - event.getDownTime();
			btnPos = btnInitPos + event.getX() - downX;
			if (btnPos >= btnOffPos) {
				btnPos = btnOffPos;
			}
			if (btnPos <= btnOnPos) {
				btnPos = btnOnPos;
			}
			doTurnOnAni = btnPos > (btnOffPos - btnOnPos) / 2 + btnOnPos;

			realPos = getRealPos(btnPos);
			break;
		case MotionEvent.ACTION_UP:
			btn = btn_unpressed;
			time = event.getEventTime() - event.getDownTime();
			if (deltaY < mTouchSlop && deltaX < mTouchSlop
					&& time < mClickTimeout) {
				if (mPerformClick == null) {
					mPerformClick = new PerformClick();
				}
				if (!post(mPerformClick)) {
					performClick();
				}
			} else {
				btnAnimation.start(!doTurnOnAni);
			}
			break;
		}

		invalidate();
		return isEnabled();
	}

	private final class PerformClick implements Runnable {
		public void run() {
			performClick();
		}
	}

	@Override
	public boolean performClick() {
		btnAnimation.start(!mChecked);
		return true;
	}

	/**
	 * Tries to claim the user's drag motion, and requests disallowing any
	 * ancestors from stealing events in the drag.
	 */
	private void attemptClaimDrag() {
		mParent = getParent();
		if (mParent != null) {
			mParent.requestDisallowInterceptTouchEvent(true);
		}
	}

	
	private float getRealPos(float btnPos) {
		return btnPos - btnWidth / 2;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.saveLayerAlpha(
				new RectF(0, offsetY, mask.getWidth(), mask.getHeight()
						+ offsetY), mAlpha, Canvas.MATRIX_SAVE_FLAG
						| Canvas.CLIP_SAVE_FLAG
						| Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
						| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
						| Canvas.CLIP_TO_LAYER_SAVE_FLAG);
		canvas.drawBitmap(mask, 0, offsetY, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bottom, realPos, offsetY, mPaint);
		mPaint.setXfermode(null);
		canvas.drawBitmap(frame, 0, offsetY, mPaint);
		canvas.drawBitmap(btn, realPos, offsetY, mPaint);
		canvas.restore();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) maskWidth, (int) (maskHeight + 2 * offsetY));
	}

	// animation
	private BtnAnimation btnAnimation = new BtnAnimation();

	private class BtnAnimation extends IncrementAnimation {
		private final float INIT_VELOCITY = 400;

		public void start(boolean doTurnOn) {
			long now = SystemClock.uptimeMillis();
			mAnimationLastTime = now;
			mAnimatedVelocity = doTurnOn ? -INIT_VELOCITY : INIT_VELOCITY;
			mAnimationPosition = btnPos;
			mCurrentAnimationTime = now + ANIMATION_FRAME_DURATION;
			mAnimating = true;

			mHandler.removeMessages(MSG_ANIMATE);
			mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE),
					mCurrentAnimationTime);
		}

		@Override
		protected void doAnimation() {
			if (mAnimating) {
				incrementAnimation();
				if (mAnimationPosition <= btnOnPos) {
					mAnimating = false;
					mAnimationPosition = btnOnPos;
					_setChecked(true);
				} else if (mAnimationPosition >= btnOffPos) {
					mAnimating = false;
					mAnimationPosition = btnOffPos;
					_setChecked(false);
				} else {
					mCurrentAnimationTime += ANIMATION_FRAME_DURATION;
					mHandler.sendMessageAtTime(mHandler.obtainMessage(MSG_ANIMATE),mCurrentAnimationTime);
				}
				moveView(mAnimationPosition);
			}
		}

		@Override
		protected void moveView(float position) {
			btnPos = position;
			realPos = getRealPos(btnPos);
			invalidate();
		}

	}
}