package fm.draw.over.recyclerview.app.drawover.animation

import android.animation.AnimatorSet
import androidx.core.animation.addListener
import fm.draw.over.recyclerview.app.drawover.DrawOverRecyclerView

internal class ReverseAnimation(
    onEnd: () -> Unit,
) : DrawOverRecyclerView.Animation {

    override var isAnimationStarted = false
    override val animator = AnimatorSet()
    private val animationsList = mutableListOf<DrawOverRecyclerView.Animation>()

    init {
        animator.addListener(
            onEnd = {
                isAnimationStarted = false
                onEnd()
            }
        )
    }

    fun addAnimation(animation: DrawOverRecyclerView.Animation) {
        animationsList.add(animation)
    }

    override fun start() {
        animator.playTogether(
            animationsList.map { it.animator }
        )
        animator.start()
        isAnimationStarted = true
    }

    override fun restore() {
        animationsList.clear()
        isAnimationStarted = false
    }

    companion object {
        fun createDefaultForDrawOverRecyclerView(
            onEnd: () -> Unit,
            fromBackgroundColor: Int,
            toBackgroundColor: Int,
            translateX: Float,
            translateY: Float,
            scaleFactor: Float,
            onUpdateBackgroundAnimation: (Int) -> Unit,
            onUpdateTranslateYAnimation: (Float) -> Unit,
            onUpdateTranslateXAnimation: (Float) -> Unit,
            onUpdateScaleFactorAnimation: (Float) -> Unit,
        ) = ReverseAnimation(onEnd).apply {
            addAnimation(
                ColorAnimation(
                    fromColor = fromBackgroundColor,
                    toColor = toBackgroundColor,
                    onUpdate = onUpdateBackgroundAnimation
                )
            )
            addAnimation(
                FloatAnimation(
                    fromFloat = translateY,
                    toFloat = 0f,
                    onUpdate = onUpdateTranslateYAnimation
                )
            )
            addAnimation(
                FloatAnimation(
                    fromFloat = translateX,
                    toFloat = 0f,
                    onUpdate = onUpdateTranslateXAnimation
                )
            )
            addAnimation(
                FloatAnimation(
                    fromFloat = scaleFactor,
                    toFloat = 1f,
                    onUpdate = onUpdateScaleFactorAnimation
                )
            )
        }
    }
}