package top.wuhaojie.awesome.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PagerIndicatorView extends View implements ViewPager.OnPageChangeListener {

    public static final int RADIUS = 12;
    public static final int SPACE = 10;

    private int count = 0;

    private int index = 0;

    private float percent = 0;

    private Paint mPaint;

    public PagerIndicatorView(Context context) {
        super(context);
        init();
    }

    public PagerIndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PagerIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        mPaint.setColor(Color.parseColor("#D6D6D6"));

        for (int i = 0; i < count; i++) {
            canvas.drawCircle(centerX(i), centerY(i), RADIUS, mPaint);
        }

        float nextRightPosition = centerX(index) + rightFunction(percent) * (centerX(safePosition(index + 1)) - centerX(index));
        float nextLeftPosition = centerX(index) + leftFunction(percent) * (centerX(safePosition(index + 1)) - centerX(index));

        // 矩形
        mPaint.setColor(Color.parseColor("#FFFFFF"));

        float left = nextLeftPosition;
        float right = nextRightPosition;
        float top = centerY(index) - RADIUS;
        float bottom = top + 2 * RADIUS;

        canvas.drawRect(left, top, right, bottom, mPaint);

        // 弧形

        RectF leftArc = new RectF(left - RADIUS, top, left + RADIUS, bottom);
        canvas.drawArc(leftArc, 90, 180, true, mPaint);


        RectF rightArc = new RectF(right - RADIUS, top, right + RADIUS, bottom);
        canvas.drawArc(rightArc, -90, 180, true, mPaint);

    }


    public void bind(ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("please make sure the adapter is not null");
        }
        count = adapter.getCount();
        index = viewPager.getCurrentItem();
        viewPager.addOnPageChangeListener(this);
        invalidate();
    }


    private float rightFunction(float percent) {
        float result = (float) (percent / 0.3);
        if (result > 1) {
            result = 1;
        }
        return result;
    }


    private float leftFunction(float percent) {
        float factor = 0.3F;
        if (percent < (1 - factor)) {
            return 0;
        }
        return (percent - (1 - factor)) / factor;
    }

    private int safePosition(int position) {
        if (position < 0) {
            return 0;
        }
        if (position >= count) {
            return count - 1;
        }
        return position;
    }

    private float centerY(int position) {
        return RADIUS + getPaddingTop();
    }

    private float centerX(int position) {
        int startX = getMeasuredWidth() / 2 - contentWidth() / 2;
        return RADIUS + position * (2 * RADIUS + SPACE) + getPaddingLeft() + startX;
    }


    private int contentWidth() {
        return count * 2 * RADIUS + (count - 1) * SPACE + getPaddingLeft() + getPaddingRight();
    }

    private int contentHeight() {
        return 2 * RADIUS + getPaddingTop() + getPaddingBottom();
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
            result = contentWidth();
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
            result = contentHeight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        index = position;
        percent = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

}
