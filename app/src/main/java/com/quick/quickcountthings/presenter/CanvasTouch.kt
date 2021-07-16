package com.quick.quickcountthings.presenter

import android.view.MotionEvent

/**
 * Created by T0015
 */
/*
     Interface to detect canvas touch and canvas long press events
 */
interface CanvasTouch {
    fun onClickEvent(event: MotionEvent)
    fun onLongPressEvent(initialTouchX: Float, initialTouchY: Float)
}
