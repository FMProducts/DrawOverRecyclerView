package fm.draw.over.recyclerview.app.drawover

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import fm.draw.over.recyclerview.app.R
import fm.draw.over.recyclerview.app.drawover.animation.ColorAnimation
import fm.draw.over.recyclerview.app.drawover.animation.ReverseAnimation
import fm.draw.over.recyclerview.app.drawover.utils.ScaleDetector
import fm.draw.over.recyclerview.app.drawover.utils.findDrawOverViewAtPosition
import fm.draw.over.recyclerview.app.drawover.utils.getActivity
import fm.draw.over.recyclerview.app.drawover.utils.getGlobalVisibleRect
import fm.draw.over.recyclerview.app.drawover.utils.getColor
import fm.draw.over.recyclerview.app.drawover.utils.getPointF
import fm.draw.over.recyclerview.app.drawover.utils.getStatusBarHeight
import kotlin.math.max
import kotlin.math.min

class DrawOverRecyclerView : RecyclerView {

    // region drawing && scaling
    private var drawOverImage: DrawOverImage? = null
    private var isScalingStarted: Boolean = true
    private var statusBarHeight: Int = 0
    private val scaleGestureDetector by lazy {
        ScaleDetector(context, ::onDetectScale)
    }
    // endregion

    // region animation
    private var backgroundColorValue: Int = 0
    private val backgroundAnimation: Animation by lazy {
        ColorAnimation(
            fromColor = getColor(R.color.transparent),
            toColor = getColor(R.color.transparent_black),
            onUpdate = { value ->
                backgroundColorValue = value
                invalidate()
            }
        )
    }

    private var reverseAnimation: Animation? = null
    private val isReverseAnimationStarted
        get() = reverseAnimation?.isAnimationStarted ?: false
    // endregion

    // region default constructors
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
    // endregion

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (isReverseAnimationStarted) return false
        if (e.pointerCount >= SCALE_POINTER_COUNT) {

            scaleGestureDetector.onTouchEvent(e)
            initTargetView(e)

            drawOverImage?.let {
                backgroundAnimation.start()
                it.targetView.isInvisible = true
                it.initTargetViewTranslate(e)
                isScalingStarted = true
                invalidate()
            }
            return true
        } else {
            restoreValueBeforeAnimation()
            startReverseAnimation()
        }

        return super.onTouchEvent(e)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawOverImage?.apply {

            if (isReverseAnimationStarted) {
                canvas.drawColor(backgroundColorValue)
                canvas.translate(translateX, translateY)
                canvas.scale(scaleFactor, scaleFactor, scalePivotX, scalePivotY)
                canvas.drawBitmap(targetBitmap, null, targetPosition, null)
            }

            if (isScalingStarted) {
                canvas.drawColor(backgroundColorValue)
                canvas.translate(translateX, translateY)
                canvas.scale(scaleFactor, scaleFactor, scalePivotX, scalePivotY)
                canvas.drawBitmap(targetBitmap, null, targetPosition, null)
            }
        }


    }

    private fun initTargetView(e: MotionEvent) {
        if (isScalingStarted) return

        val view = findDrawOverViewAtPosition(rootView, e.rawX, e.rawY)
        if (view != null && ViewCompat.isLaidOut(view)) {

            val offset = calculateOffset()

            drawOverImage = DrawOverImage(
                targetBitmap = view.drawToBitmap(),
                targetPosition = view.getGlobalVisibleRect(offset),
                targetView = view,
            )
        }
    }

    private fun restoreValuesAfterAnimation() {
        drawOverImage?.targetView?.isInvisible = false
        drawOverImage = null
        invalidate()
    }

    private fun restoreValueBeforeAnimation() {
        backgroundAnimation.restore()
        isScalingStarted = false
        invalidate()
    }

    private fun createReverseAnimation() = ReverseAnimation.createDefaultForDrawOverRecyclerView(
        onEnd = ::restoreValuesAfterAnimation,
        fromBackgroundColor = getColor(R.color.transparent_black),
        toBackgroundColor = getColor(R.color.transparent),
        translateX = drawOverImage?.translateX ?: 0f,
        translateY = drawOverImage?.translateY ?: 0f,
        scaleFactor = drawOverImage?.scaleFactor ?: 1f,
        onUpdateBackgroundAnimation = { value ->
            backgroundColorValue = value
            invalidate()
        },
        onUpdateScaleFactorAnimation = { value ->
            drawOverImage?.scaleFactor = value
            invalidate()
        },
        onUpdateTranslateXAnimation = { value ->
            drawOverImage?.translateX = value
            invalidate()
        },
        onUpdateTranslateYAnimation = { value ->
            drawOverImage?.translateY = value
            invalidate()
        }
    )

    private fun startReverseAnimation() {
        if (drawOverImage == null) return
        reverseAnimation = createReverseAnimation()
        reverseAnimation?.start()
    }

    private fun calculateOffset() : Int {
        if (statusBarHeight >= 0) {
            statusBarHeight = getActivity()?.getStatusBarHeight() ?: 0
        }
        return statusBarHeight + this.top
    }

    private fun onDetectScale(detector: ScaleGestureDetector): Boolean {
        drawOverImage?.apply {
            this.scaleFactor *= detector.scaleFactor
            this.scaleFactor = max(SCALE_FACTOR_MIN, min(scaleFactor, SCALE_FACTOR_MAX))


            this.scalePivotX = calculateScalePivotX()
            this.scalePivotY = calculateScalePivotY()
            invalidate()
        }
        return true
    }

    /**
     * If we applied windowTranslucentStatus then our screen will drop in under the StatusBar.
     * In this case, you need to disable statusBarOffset for the correct calculation of coordinates
     */
    fun disableStatusBarOffset() {
        statusBarHeight = -1
    }

    // region DrawImage Extensions
    private fun DrawOverImage.getTranslatePoint(e: MotionEvent): PointF {
        if (originalTranslatePoint == null) {
            originalTranslatePoint = e.getPointF()
        }
        currentTranslatePoint = e.getPointF()
        return PointF(
            currentTranslatePoint!!.x - originalTranslatePoint!!.x,
            currentTranslatePoint!!.y - originalTranslatePoint!!.y,
        )
    }

    private fun DrawOverImage.initTargetViewTranslate(e: MotionEvent) {
        val translatePoint = getTranslatePoint(e)
        this.translateX = translatePoint.x
        this.translateY = translatePoint.y
    }

    private fun DrawOverImage.calculateScalePivotX() =
        targetPosition.left + targetPosition.width() * 0.5f

    private fun DrawOverImage.calculateScalePivotY() =
        targetPosition.top + targetPosition.height() * 0.5f
    // endregion

    /**
     * Animation - Interface to implement animation for DrawOverRecyclerView
     */
    interface Animation {
        fun start()
        fun restore()
        val animator: Animator
        var isAnimationStarted: Boolean
    }

    /**
     * DrawOverImage - stores the value for drawing over the recyclerview
     */
    private class DrawOverImage(
        val targetBitmap: Bitmap,
        val targetPosition: Rect,
        val targetView: DrawOverView,
    ) {

        var translateX: Float = 0f
        var translateY: Float = 0f

        var originalTranslatePoint: PointF? = null
        var currentTranslatePoint: PointF? = null

        var scaleFactor = 1f
        var scalePivotX = 0f
        var scalePivotY = 0f
    }

    companion object {
        const val SCALE_FACTOR_MAX = 3.0f
        const val SCALE_FACTOR_MIN = 1.0f
        const val SCALE_POINTER_COUNT = 2
    }
}