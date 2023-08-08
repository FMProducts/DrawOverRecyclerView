package fm.draw.over.recyclerview.app.drawover.utils

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

internal class ScaleDetector(
    context: Context,
    private val onScale: (ScaleGestureDetector) -> Boolean,
) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    private val scaleGestureDetector = ScaleGestureDetector(context, this)

    fun onTouchEvent(e: MotionEvent) {
        scaleGestureDetector.onTouchEvent(e)
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        return onScale.invoke(detector)
    }

}