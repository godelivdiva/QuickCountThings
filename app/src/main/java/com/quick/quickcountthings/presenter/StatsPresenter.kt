package com.quick.quickcountthings.presenter

import com.quick.quickcountthings.interactor.ShapesInteractor
import com.quick.quickcountthings.model.Shape
import java.io.Serializable

/**
 * Created by T0015
 */
/*
    Bind Stats activity with interactor
 */
class StatsPresenter {
    val countByGroup: Serializable
        get() = ShapesInteractor.getInstance().countByGroup

    fun deleteAllByShape(shapeType: Shape.Type) {
        ShapesInteractor.getInstance().deleteAllByShape(shapeType)
    }
}
