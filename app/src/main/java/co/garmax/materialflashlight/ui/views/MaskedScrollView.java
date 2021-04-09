package co.garmax.materialflashlight.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

import co.garmax.materialflashlight.R;

/**
 * Add transparent mask at top and bottom of the view
 */
public class MaskedScrollView extends NestedScrollView {

    private final int gradientSize;
    private final Paint topPaint = new Paint();
    private Paint bottomPaint;
    private int translationY;

    public MaskedScrollView(Context context) {
        this(context, null);
    }

    public MaskedScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(LAYER_TYPE_HARDWARE, null);
        setWillNotDraw(false);
        gradientSize = getResources().getDimensionPixelSize(R.dimen.margin_large);

        LinearGradient gradient = new LinearGradient(0, 0, 0, gradientSize, 0, Color.WHITE, Shader.TileMode.CLAMP);
        topPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        topPaint.setShader(gradient);

        setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY)
                        -> translationY = scrollY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createBottomPaint();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (bottomPaint == null && getHeight() > 0) {
            createBottomPaint();
        }

        canvas.save();
        canvas.translate(0, translationY);

        // Bottom mask
        canvas.drawRect(0,
                getHeight() - gradientSize,
                getWidth(),
                getHeight(),
                bottomPaint);

        // Top mask
        canvas.drawRect(0, 0, getWidth(), gradientSize, topPaint);

        canvas.restore();
    }

    private void createBottomPaint() {
        bottomPaint = new Paint();
        LinearGradient bottomGradient = new LinearGradient(0, getHeight() - gradientSize, 0,
                getHeight(), Color.WHITE, 0, Shader.TileMode.CLAMP);
        bottomPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        bottomPaint.setShader(bottomGradient);
    }
}
