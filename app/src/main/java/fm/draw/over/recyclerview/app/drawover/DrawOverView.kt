package fm.draw.over.recyclerview.app.drawover

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 *  DrawOverView - The ViewGroup that will be redrawn over the recyclerview.
 *  It should not have many elements and should not be complicated.
 *  All of its children will be redrawn over the recyclerview
 */
class DrawOverView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}