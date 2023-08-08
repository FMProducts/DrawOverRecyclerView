package fm.draw.over.recyclerview.app.drawover.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import fm.draw.over.recyclerview.app.drawover.DrawOverView

internal fun View.getGlobalVisibleRect(offset: Int = 0): Rect {
    val rect = Rect()
    this.getGlobalVisibleRect(rect)
    rect.top -= offset
    rect.bottom -= offset
    return rect
}

internal fun View.getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

internal fun MotionEvent.getAvgPointF(): PointF {
    val firstPoint = getPointFByIndex(0)
    val secondPoint = getPointFByIndex(1)

    return PointF(
        (secondPoint.x + firstPoint.x) / 2,
        (secondPoint.y + firstPoint.y) / 2,
    )
}

internal fun MotionEvent.getPointFByIndex(index: Int): PointF {
    val pointerId = getPointerId(index)
    val pointerIndex = findPointerIndex(pointerId)

    return PointF(
        getX(pointerIndex),
        getY(pointerIndex)
    )
}

internal fun View.getActivity() = context as? Activity

internal fun Activity.getStatusBarHeight(): Int {
    val rect = Rect()
    window.decorView.getWindowVisibleDisplayFrame(rect)
    return rect.top
}

internal fun findDrawOverViewAtPosition(parent: View, x: Float, y: Float): DrawOverView? {
    if (parent is ViewGroup && (parent is DrawOverView).not()) {
        parent.children.forEach { child ->
            val viewAtPosition = findDrawOverViewAtPosition(child, x, y)
            if (viewAtPosition != null) {
                return viewAtPosition
            }
        }
        return null
    } else {
        val rect = parent.getGlobalVisibleRect()
        return if (rect.contains(x.toInt(), y.toInt())) {
            parent as? DrawOverView
        } else {
            null
        }
    }
}

// https://stackoverflow.com/questions/58314397/java-lang-illegalstateexception-software-rendering-doesnt-support-hardware-bit
internal fun View.createBitmap(bitmapCallback: (Bitmap) -> Unit) {
    val window = getActivity()?.window ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Above Android O, use PixelCopy
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val dest = getGlobalVisibleRect()
        val onPixelCopyFinished: (Int) -> Unit = {
            if (it == PixelCopy.SUCCESS) {
                bitmapCallback.invoke(bitmap)
            }
        }
        val threadListener = Handler(Looper.getMainLooper())
        PixelCopy.request(window, dest, bitmap, onPixelCopyFinished, threadListener)
    } else {
        val tBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(tBitmap)
        draw(canvas)
        canvas.setBitmap(null)
        bitmapCallback.invoke(tBitmap)
    }
}