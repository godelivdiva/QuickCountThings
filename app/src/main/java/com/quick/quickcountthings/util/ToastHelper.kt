package com.quick.quickcountthings.util

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Created by T0015
 */
object ToastHelper {

    /*
        Show toasts only for debug builds
 */
    fun makeText(context: Context, text: CharSequence) {

        try {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        } catch (ignored: Exception) {
            var errorMSg = ""
            if (ignored != null)
                errorMSg = ignored.localizedMessage
            Log.d(ToastHelper.javaClass.simpleName," Error for toast "+ errorMSg)
        }

    }

}
