package com.quick.quickcountthings.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.quick.quickcountthings.R;
import com.quick.quickcountthings.model.Shape;
import com.quick.quickcountthings.presenter.CanvasTouch;
import com.quick.quickcountthings.util.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by T0015
 */
public class CustomView extends AppCompatImageView {
    Context mContext;
    private static final String LOG_TAG = CustomView.class.getSimpleName();
    private final String TAG = CustomView.class.getSimpleName();
    public final int RADIUS = Constants.INSTANCE.getRADIUS();
    private Canvas canvas;
    List<Shape> historyList = new ArrayList<>();
    CanvasTouch canvasTouch;
    private boolean longPressDone;
    Boolean plus, minus;

    //edit
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        setFocusable(true);
        mContext = context;
        setFocusableInTouchMode(true);
        setupPaint();
        Log.d(TAG, "  constructor called");

        //edit
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    // defines paint and canvas
    private Paint drawPaint;
    private Paint textPaint;

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
//        drawPaint.setColor(Color.RED);
//        drawPaint.setAntiAlias(true);
//        drawPaint.setStrokeWidth(8f);
//        drawPaint.setStyle(Paint.Style.STROKE);
//        drawPaint.setStrokeJoin(Paint.Join.ROUND);
//        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        drawPaint.setColor(getResources().getColor(R.color.grey));
//        drawPaint.setAlpha(70);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setTextAlign(Paint.Align.CENTER);
//        drawPaint.setStrokeWidth(5f);

        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.black));
//        textPaint.setTextSize(64f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;


        canvas.save();
//        canvas.scale(mScaleFactor, mScaleFactor);
//        setScaleX (mScaleFactor);
//        setScaleY (mScaleFactor);
        animate().scaleX(mScaleFactor).scaleY(mScaleFactor).setDuration(200)
                .setInterpolator(INTERPOLATOR)
                .start();

//        canvas.scale(0,0);
        Log.d(TAG, "  onDraw called");
        for (Shape shape : getHistoryList()) {
            if (shape.isVisible()) {
                switch (shape.getType()) {
                    case CIRCLE:
                        Log.e("get radius", String.valueOf(shape.getRadius()));
                        canvas.drawCircle(shape.getxCordinate(), shape.getyCordinate(), 20, drawPaint);
//                        textPaint.setTextSize(Float.parseFloat(String.valueOf(shape.getxCordinate()/3))+'f');
                        canvas.drawText(shape.getIndexs(), shape.getxCordinate(), shape.getyCordinate(), textPaint);
                        break;
                }
            }
        }

        canvas.restore();
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        public boolean onScale(ScaleGestureDetector detector)
//        {
//            mScaleFactor *= detector.getScaleFactor();  // class variable of type float
////            if (mScaleFactor > 5) mScaleFactor = 5;      // some limitations
////            if (mScaleFactor < 1) mScaleFactor = 1;
//            return true;
//        }
//        public boolean onScaleBegin(ScaleGestureDetector detector)
//        { return true;}
//
//        public void onScaleEnd(ScaleGestureDetector detector)
//        {
//            setScaleX(mScaleFactor); setScaleY(mScaleFactor);
//            invalidate();     // it seems to me - no effect
//        }
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

    private boolean longClickActive = false;
    float initialTouchX = 0;
    float initialTouchY = 0;
    private static final int MIN_CLICK_DURATION = 1000;
    private long startClickTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //edit
        mScaleDetector.onTouchEvent(event);

//        CanvasDrawActivity global=(CanvasDrawActivity)mContext.getApplicationContext();
//        if(global.plus){
//            Log.e("cek action", "plus");
//        } else if(global.minus){
//            Log.e("cek action", "minus");
//        } else {
//            Log.e("cek action", "null");
//
//        }
        plus = CanvasDrawActivity.getPlus();
        minus = CanvasDrawActivity.getMinus();
        if(plus){
            Log.e("cek action", "plus");
        } else if(minus){
            Log.e("cek action", "minus");
        } else {
            Log.e("cek action", "null");

        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, " ACTION_DOWN");

                initialTouchX = event.getX();
                initialTouchY = event.getY();
                longPressDone = false;
                if (!longClickActive) {
                    longClickActive = true;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(LOG_TAG, " ACTION_UP");
                long currentTime = Calendar.getInstance().getTimeInMillis();
                long clickDuration = currentTime - startClickTime;
                if (clickDuration <= MIN_CLICK_DURATION && !longPressDone) {
                    //normal click
                    if (canvasTouch != null) {
                        canvasTouch.onClickEvent(event);
                    }
                    longClickActive = false;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, " ACTION_MOVE");
                currentTime = Calendar.getInstance().getTimeInMillis();
                clickDuration = currentTime - startClickTime;
                if (clickDuration >= MIN_CLICK_DURATION) {
                    if (canvasTouch != null) {
                        canvasTouch.onLongPressEvent(initialTouchX, initialTouchY);
                    }
                    longClickActive = false;
                    longPressDone = true;
                    startClickTime = Calendar.getInstance().getTimeInMillis();
                    return false;
                }
                break;
        }
        return true;
    }

     double squareSideHalf = 1 / Math.sqrt(2);
    //Consider pivot x,y as centroid.

    public void drawRectangle(int x, int y) {
//        drawPaint.setColor(Color.RED);
        Rect rectangle = new Rect((int) (x - (squareSideHalf * RADIUS)), (int) (y - (squareSideHalf * RADIUS)), (int) (x + (squareSideHalf * RADIUS)), (int) (y + ((squareSideHalf * RADIUS))));
        canvas.drawRect(rectangle, drawPaint);
    }

    /*
    select three vertices of triangle. Draw 3 lines between them to form a traingle
     */
    public void drawTriangle(int x, int y, int width) {
//        drawPaint.setColor(Color.GREEN);
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x, y - halfWidth); // Top
        path.lineTo(x - halfWidth, y + halfWidth); // Bottom left
        path.lineTo(x + halfWidth, y + halfWidth); // Bottom right
        path.lineTo(x, y - halfWidth); // Back to Top
        path.close();
        canvas.drawPath(path, drawPaint);
    }

    public List<Shape> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<Shape> historyList) {
        this.historyList = historyList;
    }

    public CanvasTouch getCanvasTouch() {
        return canvasTouch;
    }

    public void setCanvasTouch(CanvasTouch canvasTouch) {
        this.canvasTouch = canvasTouch;
    }

}
