package co.garmax.materialflashlight.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import co.garmax.materialflashlight.extensions.asDp

/**
 * Add transparent mask at top and bottom of the view
 */
class MaskedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val gradientSize = 16.asDp.toFloat()

    private val topPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        shader = LinearGradient(0f, 0f, 0f, gradientSize, 0, Color.WHITE, Shader.TileMode.CLAMP)
    }

    private var bottomPaint: Paint? = null

    private var translationY = 0

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setWillNotDraw(false)

        setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            translationY = scrollY
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBottomPaint()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        with(canvas) {
            if (bottomPaint == null && height > 0) {
                createBottomPaint()
            }

            save()
            translate(0f, translationY.toFloat())

            // Bottom mask
            bottomPaint?.let {
                drawRect(
                    0f, (
                            height - gradientSize),
                    width.toFloat(),
                    height.toFloat(),
                    it
                )
            }

            // Top mask
            drawRect(0f, 0f, width.toFloat(), gradientSize, topPaint)
            restore()
        }
    }

    private fun createBottomPaint() {
        bottomPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
            shader = LinearGradient(
                0f,
                (height - gradientSize),
                0f,
                height.toFloat(),
                Color.WHITE,
                0,
                Shader.TileMode.CLAMP
            )
        }
    }
}