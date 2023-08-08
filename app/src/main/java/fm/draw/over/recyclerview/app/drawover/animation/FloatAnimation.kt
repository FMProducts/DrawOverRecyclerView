package fm.draw.over.recyclerview.app.drawover.animation

import android.animation.ValueAnimator
import fm.draw.over.recyclerview.app.drawover.DrawOverRecyclerView

internal class FloatAnimation(
    fromFloat: Float,
    toFloat: Float,
    onUpdate: (Float) -> Unit,
) : DrawOverRecyclerView.Animation {

    override var isAnimationStarted: Boolean = false
    override val animator: ValueAnimator = ValueAnimator.ofFloat(fromFloat, toFloat)

    init {
        animator.addUpdateListener {
            onUpdate(it.animatedValue as Float)
        }
    }

    override fun start() {
        if (isAnimationStarted) return
        animator.start()
        isAnimationStarted = true
    }

    override fun restore() {
        isAnimationStarted = false
    }
}