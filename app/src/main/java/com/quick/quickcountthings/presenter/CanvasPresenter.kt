package com.quick.quickcountthings.presenter

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import com.quick.quickcountthings.FileUtils
import com.quick.quickcountthings.interactor.ShapesInteractor
import com.quick.quickcountthings.model.Shape
import com.quick.quickcountthings.util.Constants
import com.quick.quickcountthings.view.CanvasDrawActivity
import com.quick.quickcountthings.view.CustomView
import java.io.File
import java.io.Serializable

/**
 * Created by T0015
 */
class CanvasPresenter(private val canvas: CustomView, private val mContext: Context) {

    public lateinit var imageUri: Uri
    var plus : Boolean = false
    var minus : Boolean = false
    /**
     * Respons to click and long press events on canvas
     */
    private val onTouchListener = object : CanvasTouch {
        override fun onClickEvent(event: MotionEvent) {
            Log.d(LOG_TAG, " onClickEvent done ")
            plus = CanvasDrawActivity.getPlus()
            minus = CanvasDrawActivity.getMinus()
            if (plus) {
                Log.e("cek action", "plus")
                ShapesInteractor.getInstance().addShapeOnTouch(event.x, event.y, Constants.ACTION_TRANSFORM)
            } else if (minus) {
                Log.e("cek action", "minus")
                ShapesInteractor.getInstance().changeShapeOnTouch(event.x, event.y, Constants.ACTION_DELETE)
            } else {
                Log.e("cek action", "null")

            }
        }

        override fun onLongPressEvent(initialTouchX: Float, initialTouchY: Float) {
            Log.d(LOG_TAG, " onLongPressEvent done ")
//            ShapesInteractor.getInstance().changeShapeOnTouch(initialTouchX, initialTouchY, Constants.ACTION_DELETE)
        }
    }

    val countByGroup: Serializable
        get() = ShapesInteractor.getInstance().countByGroup

    init {
        canvas.canvasTouch = onTouchListener
        initializeUIComponents(canvas, mContext)
    }

    private fun initializeUIComponents(canvas: CustomView, mContext: Context) {
        ShapesInteractor.getInstance().canvas = canvas
        ShapesInteractor.getInstance().setContext(mContext)
    }


    fun setMaxX(maxX: Int) {
        ShapesInteractor.getInstance().maxX = maxX
    }

    fun setMaxY(maxY: Int) {
        ShapesInteractor.getInstance().maxY = maxY
    }

    fun addShapeRandom(type: Shape.Type) {
        ShapesInteractor.getInstance().addShapeRandom(type)
    }

    //coba
    fun addShapeCircle(type: Shape.Type, x: Int, y:Int, rad : Int) {
        ShapesInteractor.getInstance().setHeightWidth(getDropboxIMGSize(imageUri)[0],getDropboxIMGSize(imageUri)[1] )
        ShapesInteractor.getInstance().addShapeCircle(type, x, y, rad)
//        ShapesInteractor.getInstance().addShapeOnTouch(x, y, Constants.ACTION_CREATE)
    }

    private fun getDropboxIMGSize(uri: Uri): Array<Int> {
        val file = FileUtils.getFile(mContext, uri)

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
//        BitmapFactory.decodeFile(File(uri.path).getAbsolutePath(), options)
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        return arrayOf(imageHeight, imageWidth)
    }


    fun undo() {
        ShapesInteractor.getInstance().undo()
    }

    companion object {
        private val LOG_TAG = CanvasPresenter.javaClass.simpleName
    }

}
