package com.quick.quickcountthings.model

import android.os.Parcel
import android.os.Parcelable
import com.quick.quickcountthings.util.Constants

/**
 * Created by T0015
 */
class Shape() : Parcelable {
    /*
     Defines centroid of shape : x,y.  Pivot about which shape has to be drawn
     */
    private var xCordinate: Int = 0
    private var yCordinate: Int = 0
    var width: Int = 0
    var index: Int = 0
    var type: Type? = null
    var isVisible = true
        private set
    var actionNumber = 0
    var lastTranformationIndex = Constants.ACTION_CREATE

    constructor(parcel: Parcel) : this() {
        xCordinate = parcel.readInt()
        yCordinate = parcel.readInt()
        width = parcel.readInt()
        index = parcel.readInt()
        actionNumber = parcel.readInt()
        lastTranformationIndex = parcel.readInt()
    }

    constructor(x: Int, y: Int, width: Int, index : Int) : this() {
        this.xCordinate = x
        this.yCordinate = y
        this.width = width
        this.index = index
    }

    /*
    Define all types of shapes
     */
    enum class Type private constructor(val value: Int) {
        CIRCLE(0), SQUARE(1), TRIANGLE(2)
    }

    fun getxCordinate(): Int {
        return xCordinate
    }

    fun setxCordinate(xCordinate: Int) {
        this.xCordinate = xCordinate
    }

    fun getyCordinate(): Int {
        return yCordinate
    }

    fun setyCordinate(yCordinate: Int) {
        this.yCordinate = yCordinate
    }

    fun getRadius(): Int {
        return width
    }

    fun getIndexs(): String {
        return index.toString()
    }

    fun setVisibility(visibility: Boolean) {
        this.isVisible = visibility
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(xCordinate)
        parcel.writeInt(yCordinate)
        parcel.writeInt(width)
        parcel.writeInt(actionNumber)
        parcel.writeInt(lastTranformationIndex)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Shape> {
        override fun createFromParcel(parcel: Parcel): Shape {
            return Shape(parcel)
        }

        override fun newArray(size: Int): Array<Shape?> {
            return arrayOfNulls(size)
        }
    }

}
