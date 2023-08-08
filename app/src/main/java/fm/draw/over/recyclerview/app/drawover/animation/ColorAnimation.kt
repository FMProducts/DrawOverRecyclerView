package fm.draw.over.recyclerview.app.drawover.animation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import fm.draw.over.recyclerview.app.drawover.DrawOverRecyclerView

internal class ColorAnimation(
    fromColor: Int,
    toColor: Int,
    onUpdate: (Int) -> Unit
) : DrawOverRecyclerView.Animation {

    override var isAnimationStarted: Boolean = false
    override val animator: ValueAnimator = ValueAnimator.ofInt(fromColor, toColor)

    init {
        animator.setEvaluator(ArgbEvaluator())
        animator.addUpdateListener {
            onUpdate(it.animatedValue as Int)
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