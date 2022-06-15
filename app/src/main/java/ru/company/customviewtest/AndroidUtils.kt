package ru.company.customviewtest

import android.content.Context
import androidx.annotation.Px
import kotlin.math.ceil

object AndroidUtils {

    @Px
    fun convertDpToPx(context: Context, dp: Float): Float =
        ceil(context.resources.displayMetrics.density * dp)
}