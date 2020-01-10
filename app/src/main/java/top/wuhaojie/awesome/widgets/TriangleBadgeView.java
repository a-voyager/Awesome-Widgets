package top.wuhaojie.awesome.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class TriangleBadgeView extends View {

    public static final String TAG = TriangleBadgeView.class.getSimpleName();

    private final Rect bounds = new Rect();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int width = 300;

    private int offset = 150;

    private String text = "斜角标签";

    private int textSize = 60;

    private int textColor = Color.parseColor("#000000");


    public TriangleBadgeView(Context context) {
        super(context);
    }

    public TriangleBadgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TriangleBadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        paint.setColor(Color.parseColor("#FFBD00"));
        paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.lineTo(width - offset, 0);
        path.lineTo(width, offset);
        path.lineTo(width, width);
        path.lineTo(0, 0);
        canvas.drawPath(path, paint);
        canvas.save();


        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(textSize);
        paint.setFakeBoldText(true);
        paint.getTextBounds(text, 0, text.length(), bounds);

        int textWidth = bounds.right - bounds.left;
        int textHeight = bounds.bottom - bounds.top;

        float widthHeight = calculateEqualSideLen(width);
        float offsetHeight = calculateEqualSideLen(offset);

        // 颜色彩带高度
        float contentHeight = widthHeight - offsetHeight;
        // 文本上下边距
        float textPadding = (float) ((contentHeight - textHeight) / 2.0);

        Log.d(TAG, "textPadding: " + textPadding + "; contentHeight: " + contentHeight);

        canvas.rotate(45, width / 2, width / 2);

        float x = (float) ((width - textWidth) / 2.0);
        float y = (float) (width / 2.0 - contentHeight / 2.0);
        float baselineY = y + Math.abs(paint.ascent() + paint.descent()) / 2;

        canvas.drawText(text, x, baselineY, paint);
        canvas.restore();

    }

    private float calculateEqualSideLen(int longLen) {
        return (float) (Math.sqrt(2) * longLen / 2.0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = width + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int measureHeight(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = width + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


}
