package top.wuhaojie.awesome.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

public class RadiusButton extends AppCompatTextView {

    private static final int STYLE_STROKE = 0x01;

    private static final int STYLE_FILL = 0x01 << 1;

    private static final int RADIUS_LEFT = 0x01;

    private static final int RADIUS_RIGHT = 0x01 << 1;

    private final RectF rect = new RectF();

    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    private int strokeStyle = STYLE_FILL;

    private int radiusStyle = RADIUS_LEFT | RADIUS_RIGHT;

    private int themeColor = Color.RED;

    private int disableColor = Color.RED;

    private int innerPadding = 0;


    public RadiusButton(Context context) {
        super(context);
        init(null);
    }

    public RadiusButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RadiusButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initAttrs(attrs);
        initPaint(isEnabled());
        initText();

        setFocusable(true);
        setClickable(true);

        innerPadding = dp2px(1);
        setHeight(dp2px(50));
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.RadiusButton);
        strokeStyle = attributes.getInteger(R.styleable.RadiusButton_nw_stroke_style, STYLE_FILL);
        radiusStyle = attributes.getInteger(R.styleable.RadiusButton_nw_radius_style, RADIUS_LEFT | RADIUS_RIGHT);
        themeColor = attributes.getColor(R.styleable.RadiusButton_nw_theme_color, getResources().getColor(R.color.colorAccent));
        disableColor = attributes.getColor(R.styleable.RadiusButton_nw_disable_color, getResources().getColor(R.color.colorAccent));
        attributes.recycle();
    }

    private void initText() {
        if ((strokeStyle & STYLE_STROKE) != 0) {
            setTextColor((themeColor));
        } else {
            setTextColor(Color.WHITE);
        }
        setTextSize(16);
        setGravity(Gravity.CENTER);
    }

    private void initPaint(boolean enable) {
        fillPaint.setColor(enable ? themeColor : disableColor);
        fillPaint.setStyle(Paint.Style.FILL);

        strokePaint.setColor(enable ? themeColor : disableColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(dp2px(1));

        linePaint.setColor(enable ? themeColor : disableColor);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(dp2px(1));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        initPaint(isEnabled());

        int radius = getMeasuredHeight() / 2;

        if ((radiusStyle & RADIUS_LEFT) != 0) {
            drawLeftArc(canvas, radius);
        }

        if ((radiusStyle & RADIUS_RIGHT) != 0) {
            drawRightArc(canvas, radius);
        }

        if ((strokeStyle & STYLE_STROKE) != 0) {

            int startX = (radiusStyle & RADIUS_LEFT) != 0 ? radius : innerPadding;
            int stopX = (radiusStyle & RADIUS_RIGHT) != 0 ? getMeasuredWidth() - radius : getMeasuredWidth() - innerPadding;

            canvas.drawLine(startX, innerPadding, stopX, innerPadding, linePaint);
            canvas.drawLine(startX, getMeasuredHeight() - innerPadding, stopX, getMeasuredHeight() - innerPadding, linePaint);

            if ((radiusStyle & RADIUS_LEFT) == 0) {
                canvas.drawLine(innerPadding, innerPadding, innerPadding, getMeasuredHeight() - innerPadding, linePaint);
            }

            if ((radiusStyle & RADIUS_RIGHT) == 0) {
                canvas.drawLine(getMeasuredWidth() - innerPadding, innerPadding, getMeasuredWidth() - innerPadding, getMeasuredHeight() - innerPadding, linePaint);
            }

        }

        if ((strokeStyle & STYLE_FILL) != 0) {

            rect.left = (radiusStyle & RADIUS_LEFT) != 0 ? radius : 0;
            rect.top = 0;
            rect.right = (radiusStyle & RADIUS_RIGHT) != 0 ? getMeasuredWidth() - radius : getMeasuredWidth();
            rect.bottom = getMeasuredHeight();

            canvas.drawRect(rect, fillPaint);
        }

        super.onDraw(canvas);
    }


    private void drawLeftArc(Canvas canvas, int radius) {
        rect.left = 0;
        rect.top = 0;
        rect.right = 2 * radius;
        rect.bottom = getMeasuredHeight();

        if ((strokeStyle & STYLE_FILL) != 0) {
            canvas.drawArc(rect, 90, 180, false, fillPaint);
        }
        if ((strokeStyle & STYLE_STROKE) != 0) {
            rect.left = rect.left + innerPadding;
            rect.top = rect.top + innerPadding;
            rect.bottom = rect.bottom - innerPadding;
            canvas.drawArc(rect, 90, 180, false, strokePaint);
        }
    }

    private void drawRightArc(Canvas canvas, int radius) {
        rect.left = getMeasuredWidth() - 2 * radius;
        rect.top = 0;
        rect.right = getMeasuredWidth();
        rect.bottom = getMeasuredHeight();

        if ((strokeStyle & STYLE_FILL) != 0) {
            canvas.drawArc(rect, -90, 180, false, fillPaint);
        }
        if ((strokeStyle & STYLE_STROKE) != 0) {
            rect.right = rect.right - innerPadding;
            rect.top = rect.top + innerPadding;
            rect.bottom = rect.bottom - innerPadding;
            canvas.drawArc(rect, -90, 180, false, strokePaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isEnabled() && (MotionEvent.ACTION_DOWN == event.getAction() || MotionEvent.ACTION_MOVE == event.getAction())) {
            setAlpha(0.6F);
        } else {
            setAlpha(1F);
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        invalidate();
    }

    public int dp2px(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

}
