package fm.draw.over.recyclerview.app.drawover.utils

import android.app.Activity
import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
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

internal fun MotionEvent.getPointF() = PointF(this.x, this.y)

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
            parent as DrawOverView
        } else {
            null
        }
    }
}

