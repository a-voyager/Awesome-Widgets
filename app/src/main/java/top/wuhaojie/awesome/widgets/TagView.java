package top.wuhaojie.awesome.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TagView extends View {

    private static final String TAG = TagView.class.getSimpleName();

    private final List<String> tags = new ArrayList<>();

    private Paint paintBackground;

    private Paint paintText;

    private int textSize = 60;

    // 标签水平边距
    private int tagPaddingHorizontal = 25;

    // 标签垂直边距
    private int tagPaddingVertical = 15;

    // 标签间距
    private int tagSpace = 40;

    // 弧度
    private int tagRadius = 12;


    private RectF rectBackground = new RectF();

    private Rect rectText = new Rect();


    public TagView(Context context) {
        super(context);
        init(context);
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TagView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void refresh(@NonNull List<String> list) {
        tags.clear();
        tags.addAll(list);
        requestLayout();
        invalidate();
    }

    public void addTag(String tag) {
        tags.add(tag);
        requestLayout();
        invalidate();
    }


    private void init(Context context) {
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(getResources().getColor(R.color.colorAccent));

        paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(textSize);
        paintText.setFakeBoldText(false);
        paintText.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 标签起始位置
        float backgroundStart = getPaddingLeft();

        for (int i = 0; i < tags.size(); i++) {

            String text = tags.get(i);

            paintText.getTextBounds(text, 0, text.length(), rectText);

            int widthText = rectText.right - rectText.left;
            int heightText = rectText.bottom - rectText.top;

            // 基准线距离
            Paint.FontMetrics metrics = paintText.getFontMetrics();
            float distance = (metrics.top + metrics.bottom) / 2F;

            rectBackground.top = getPaddingTop();
            rectBackground.bottom = getPaddingTop() + heightText + 2 * tagPaddingVertical;
            rectBackground.left = backgroundStart;
            rectBackground.right = backgroundStart + widthText + 2 * tagPaddingHorizontal;

            // 背景
            canvas.drawRoundRect(rectBackground, tagRadius, tagRadius, paintBackground);

            // 文字
            canvas.drawText(
                    text,
                    (float) (backgroundStart + widthText / 2.0 + tagPaddingHorizontal),
                    (float) (heightText / 2.0 + tagPaddingVertical - distance) + getPaddingTop(),
                    paintText
            );

            // 起始位置
            backgroundStart = rectBackground.right + tagSpace;

        }

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
            result = contentWidth() + getPaddingLeft() + getPaddingRight();
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
            result = contentHeight() + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    private int contentWidth() {
        float width = 0;
        Rect bounds = new Rect();
        for (int i = 0; i < tags.size(); i++) {
            String text = tags.get(i);
            paintText.getTextBounds(text, 0, text.length(), bounds);
            int tagWidth = (bounds.right - bounds.left) + 2 * tagPaddingHorizontal;
            width += tagWidth + tagSpace;
        }
        return (int) width - tagSpace;
    }


    private int contentHeight() {
        if (tags.size() > 0) {
            Rect bounds = new Rect();
            paintText.getTextBounds(tags.get(0), 0, tags.get(0).length(), bounds);
            return (bounds.bottom - bounds.top) + 2 * tagPaddingVertical;
        }
        return 0;
    }

}
