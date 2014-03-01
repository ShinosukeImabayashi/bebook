package com.bebook;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class asaViewPager extends android.support.v4.view.ViewPager {

	final public int CAN_SWIPE_NONE= 0; // スワイプによるページ切り替えを許可しない
	final public int CAN_SWIPE_ALL = 1; // スワイプによるページ切り替えを許可する
	final public int CAN_SWIPE_LEFT_ONLY = 2; // スワイプによるページ切り替えを許可する（左スワイプのみ）
	final public int CAN_SWIPE_RIGHT_ONLY = 3; // スワイプによるページ切り替えを許可する（右スワイプのみ）
	final public int LEFT_SWIPE = 1; // 左にスワイプしている
	final public int RIGHT_SWIPE = 2; // 右にスワイプしている


	boolean mIsSwipe = true; // スワイプによるページ切り替えを許可する
	private int mCanSwipeMode = CAN_SWIPE_ALL; // デフォルトはページ切り替え許可

	private float mBeforeX;

	public asaViewPager(Context context) {
		super(context);
	}

	public asaViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCanSwipeMode (int mode) {
		mCanSwipeMode = mode;
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//Log.v("asaViewPager - onTouchEvent", " Action=" + event.getAction() + " X=" + event.getRawX() + " bX=" + mBeforeX);

		// 左スワイプされたか右スワイプされたかを、タッチの移動方向から判断
		int mSwipeMode = 0;
		if (event.getAction() == MotionEvent.ACTION_MOVE)  {
			if (mBeforeX < event.getRawX()) {
				mSwipeMode = LEFT_SWIPE;
			} else {
				mSwipeMode = RIGHT_SWIPE;
			}
		}
		mBeforeX = event.getRawX();

		// モードごとに、前後のページヘの遷移を許可するかを設定する
		if (mCanSwipeMode == CAN_SWIPE_NONE) {
			return false;
		} else  if (mCanSwipeMode == CAN_SWIPE_ALL) {
			return super.onTouchEvent(event);
		} else if (mCanSwipeMode == CAN_SWIPE_LEFT_ONLY) {
			if (mSwipeMode == LEFT_SWIPE ) {
				return false;
			} else {
				return super.onTouchEvent(event);
			}
		} else if (mCanSwipeMode == CAN_SWIPE_RIGHT_ONLY) {
			if (mSwipeMode ==  RIGHT_SWIPE) {
				return false;
			} else {
				return super.onTouchEvent(event);
			}
		}

		return super.onTouchEvent(event);
	}



	// タッチイベントの主受け取り対象でなくとも、割り込んでイベントを処理する
	// ViewPager の場合、スワイプ操作をして次の画面に切り替わり始めた途中から、ViewPager 自身が主受け取り対象となり、 onTouchEvent にシームレスに切り替わる。
	// （タッチ開始時は onInterceptTouchEvent で、タッチの指を離した際には onTouchEvent での受け取りとなる）
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		//Log.v("asaViewPager - onInterceptTouchEvent", " Action=" + event.getAction() + " X=" + event.getRawX() + " bX=" + mBeforeX + " " + (mBeforeX - event.getRawX()) );

		// 左スワイプされたか右スワイプされたかを、タッチの移動方向から判断
		int mSwipeMode = 0;
		if (event.getAction() == MotionEvent.ACTION_MOVE)  {
			if (mBeforeX < event.getRawX()) {
				mSwipeMode = LEFT_SWIPE;
				//Log.v("asaViewPage", "→" + mCanSwipeMode);
			} else {
				mSwipeMode = RIGHT_SWIPE;
				//Log.v("asaViewPage", "←" + mCanSwipeMode);
			}
		}
		mBeforeX = event.getRawX();

		// モードごとに、前後のページヘの遷移を許可するかを設定する
		if (mCanSwipeMode == CAN_SWIPE_NONE) {
			return false;
		} else  if (mCanSwipeMode == CAN_SWIPE_ALL) {
			return super.onInterceptTouchEvent(event);
		} else if (mCanSwipeMode == CAN_SWIPE_LEFT_ONLY) {
			if (mSwipeMode == LEFT_SWIPE ) {
				return false;
			} else {
				return super.onInterceptTouchEvent(event);
			}
		} else if (mCanSwipeMode == CAN_SWIPE_RIGHT_ONLY) {
			if (mSwipeMode ==  RIGHT_SWIPE) {
				return false;
			} else {
				return super.onInterceptTouchEvent(event);
			}
		}

		// デフォルトは許可
		return super.onInterceptTouchEvent(event);
	}
}
