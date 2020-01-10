package top.wuhaojie.awesome.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public final class TipView {

    private static final String TAG = TipView.class.getSimpleName();

    private String content = "";

    private int gravity = Gravity.VERTICAL;

    private Point parentWindowSize = new Point();

    // 单位 px
    private int paddingHorizontal = 0;

    // 单位 px
    private int paddingVertical = 0;

    // 单位 px
    private int textSize = 0;

    private boolean closeVisible = false;

    private boolean outSideTouchDismiss = false;

    private PopupWindow popupWindow;

    private static final Set<TipView> tipViews = new HashSet<>();

    private TipView() {
    }

    public static class Builder {

        private Context context;

        private String content;

        private Window window;

        private int windowType = WindowType.TYPE_TIP;

        public static class WindowType {
            public static final int TYPE_GUIDE = 1;
            public static final int TYPE_TIP = 2;
        }

        public Builder with(Context context) {
            this.context = context;
            return this;
        }

        /**
         * 设置需要展示的文本内容。
         *
         * @param text 单行文本、多行文本
         */
        public Builder content(String... text) {
            if (text == null) {
                this.content = "";
                return this;
            }
            if (text.length == 1) {
                this.content = text[0];
                return this;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length; i++) {
                builder.append("● ").append(text[i]);
                if (i < text.length - 1) {
                    builder.append("\n");
                }
            }
            this.content = builder.toString();
            return this;
        }

        /**
         * 设置气泡类型。
         * <p>
         * 1、功能引导类：气泡有关闭按钮
         * 2、解释说明类：气泡无关闭按钮
         *
         * @param windowType 取值 TipView.Builder.WindowType
         */
        public Builder type(int windowType) {
            this.windowType = windowType;
            return this;
        }

        /**
         * 设置气泡展示界面的 window。
         * <p>
         * Activity 中可以通过 getWindow() 获取；
         * Dialog 中可以通过 getDialog().getWindow() 获取
         *
         * @param window Window
         * @return
         */
        public Builder on(@Nullable Window window) {
            this.window = window;
            return this;
        }

        public TipView build() {

            TipView tipView = new TipView();

            // 引导型仅用关闭按钮来关闭
            switch (windowType) {
                case WindowType.TYPE_GUIDE:
                    tipView.closeVisible = true;
                    tipView.outSideTouchDismiss = false;
                    break;
                case WindowType.TYPE_TIP:
                    tipView.closeVisible = false;
                    tipView.outSideTouchDismiss = true;
                    break;
            }

            Point screenSize = new Point();

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getSize(screenSize);

            // 父布局的宽高，无法取得则使用屏幕宽高
            int containerWidth = (window != null) ? window.getDecorView().getWidth() : screenSize.x;
            int containerHeight = (window != null) ? window.getDecorView().getHeight() : screenSize.y;

            // 父布局宽度和高度任一不一致，说明 window 是弹窗
            boolean showInDialog = (screenSize.x != containerWidth || screenSize.y != containerHeight);

            if (showInDialog) {
                tipView.paddingHorizontal = dp2px(15);
            } else {
                tipView.paddingHorizontal = dp2px(10);
            }
            tipView.paddingVertical = dp2px(5);

            tipView.parentWindowSize.x = containerWidth;
            tipView.parentWindowSize.y = containerHeight;

            // 一般字体 18px，引导型样式在弹窗内字体 14px
            if (windowType == WindowType.TYPE_GUIDE && showInDialog) {
                tipView.textSize = (7);
            } else {
                tipView.textSize = (9);
            }

            tipView.content = content;

            return tipView;
        }

    }


    /**
     * 展示方位
     */
    public static class Gravity {
        public static final int TOP = 1;
        public static final int BOTTOM = 2;
        public static final int LEFT = 3;
        public static final int RIGHT = 4;
        public static final int HORIZONTAL = 5;
        public static final int VERTICAL = 6;
    }

    public boolean isShowing() {
        return popupWindow != null && popupWindow.isShowing();
    }


    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private PopupWindow.OnDismissListener onDismissListener = new PopupWindow.OnDismissListener() {

        @Override
        public void onDismiss() {
            if (popupWindow != null) {
                popupWindow.setOnDismissListener(null);
                popupWindow = null;
            }
            tipViews.remove(TipView.this);
        }

    };


    private final static class PopupPoint {

        /**
         * 箭头的位置
         */
        final Point arrow = new Point();

        /**
         * 视图的位置
         */
        final Point location = new Point();

    }

    /**
     * 锚点描述
     */
    private final static class AnchorDesc {

        /**
         * 中心点坐标 X
         */
        int x;

        /**
         * 中心点坐标 Y
         */
        int y;

        /**
         * 锚点宽度
         */
        int width;

        /**
         * 锚点高度
         */
        int height;


        AnchorDesc(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

    }

    /**
     * 父窗口描述
     */
    private final static class WindowDesc {

        /**
         * 宽度
         */
        int width;

        /**
         * 高度
         */
        int height;

        WindowDesc(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

    /**
     * 箭头描述
     */
    private final static class ArrowDesc {

        /**
         * 宽度
         */
        int width;

        /**
         * 高度
         */
        int height;

        ArrowDesc(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }

    private final static class ContentViewDesc {

        int width;

        int height;

        int paddingTop;

        int paddingBottom;

        int paddingLeft;

        int paddingRight;

        ContentViewDesc(int width, int height, int paddingTop, int paddingBottom, int paddingLeft, int paddingRight) {
            this.width = width;
            this.height = height;
            this.paddingTop = paddingTop;
            this.paddingBottom = paddingBottom;
            this.paddingLeft = paddingLeft;
            this.paddingRight = paddingRight;
        }

    }


    private abstract static class GravityProcessor {

        abstract PopupPoint doProcess(ContentViewDesc contentView, ArrowDesc arrow, WindowDesc window, AnchorDesc anchor);

    }


    private static abstract class GravityTopBottomProcessor extends GravityProcessor {

        @Override
        protected PopupPoint doProcess(ContentViewDesc contentView, ArrowDesc arrow, WindowDesc window, AnchorDesc anchor) {

            // 返回值构造
            PopupPoint popupPoint = new PopupPoint();
            int arrowX = 0;
            int arrowY = 0;

            // 处理箭头（垂直方向仅处理水平位置即可）
            int leftSpace = anchor.x;
            int rightSpace = window.width - anchor.x;

            int minSpace = Math.min(leftSpace, rightSpace);
            int contentViewHalfWidth = contentView.width / 2;

            if (minSpace >= contentViewHalfWidth) {
                // 空间足够，可以放中间
                arrowX = contentViewHalfWidth - contentView.paddingLeft - arrow.width / 2;
            } else {
                // 空间不够
                if (leftSpace < rightSpace) {
                    // 在左边展示
                    arrowX = anchor.x - contentView.paddingLeft - arrow.width / 2;
                    // 左侧越界处理
                    if (arrowX < 0) {
                        arrowX = 0;
                    }
                } else {
                    // 在右边展示
                    int contentRightPart = rightSpace - contentView.paddingRight;
                    int contentViewRealWidth = contentView.width - contentView.paddingLeft - contentView.paddingRight;
                    arrowX = contentViewRealWidth - contentRightPart - arrow.width / 2;
                    // 右侧越界处理
                    if (arrowX > contentViewRealWidth - arrow.width) {
                        arrowX = contentViewRealWidth - arrow.width;
                    }
                }
            }

            popupPoint.arrow.x = arrowX;
            popupPoint.arrow.y = arrowY;

            // 处理视图位置
            Point location = location(contentView, anchor);

            popupPoint.location.x = location.x;
            popupPoint.location.y = location.y;

            return popupPoint;
        }


        protected abstract Point location(ContentViewDesc contentView, AnchorDesc anchor);

    }

    private static class GravityTopProcessor extends GravityTopBottomProcessor {

        @Override
        protected Point location(ContentViewDesc contentView, AnchorDesc anchor) {
            // 构造返回值
            Point location = new Point();

            // 展示在上方，箭头朝下
            location.x = anchor.x - (contentView.width / 2);
            location.y = anchor.y - (anchor.height / 2) - contentView.height;

            return location;
        }

    }

    private static class GravityBottomProcessor extends GravityTopBottomProcessor {

        @Override
        protected Point location(ContentViewDesc contentView, AnchorDesc anchor) {
            // 构造返回值
            Point location = new Point();

            // 展示在下方，箭头朝上
            location.x = anchor.x - (contentView.width / 2);
            location.y = anchor.y + (anchor.height / 2);

            return location;
        }

    }

    private static class GravityLeftProcessor extends GravityProcessor {

        @Override
        protected PopupPoint doProcess(ContentViewDesc contentView, ArrowDesc arrow, WindowDesc window, AnchorDesc anchor) {

            // 构造返回值
            PopupPoint popupPoint = new PopupPoint();

            // 展示在左边
            popupPoint.location.x = anchor.x - (anchor.width / 2) - contentView.width;
            popupPoint.location.y = anchor.y - (contentView.height / 2);

            int leftSpace = anchor.x - (anchor.width / 2);
            if (leftSpace < contentView.width) {
                Log.d(TAG, "左侧越界");
            }

            return popupPoint;
        }

    }


    private static class GravityRightProcessor extends GravityProcessor {

        @Override
        protected PopupPoint doProcess(ContentViewDesc contentView, ArrowDesc arrow, WindowDesc window, AnchorDesc anchor) {

            // 构造返回值
            PopupPoint popupPoint = new PopupPoint();

            // 展示在右边
            popupPoint.location.x = anchor.x + (anchor.width / 2);
            popupPoint.location.y = anchor.y - (contentView.height / 2);

            int leftPart = anchor.x + anchor.width / 2;
            int rightSpace = window.width - leftPart;
            if (rightSpace < contentView.width) {
                Log.d(TAG, "右侧越界");
            }

            return popupPoint;
        }

    }


    private int prepareGravity(int gravity, WindowDesc window, AnchorDesc anchor) {
        if (gravity == Gravity.VERTICAL) {
            // 上方距离 < 下方距离 => 视图展示在下方
            return anchor.y < window.height - anchor.y ? Gravity.BOTTOM : Gravity.TOP;
        }
        if (gravity == Gravity.HORIZONTAL) {
            // 左方距离 < 右方距离 => 视图展示在右方
            return anchor.x < window.width - anchor.x ? Gravity.RIGHT : Gravity.LEFT;
        }
        return gravity;
    }


    private View hideArrow(View contentView, int gravity) {
        View arrowTop = contentView.findViewById(R.id.view_arrow_top);
        View arrowBottom = contentView.findViewById(R.id.view_arrow_bottom);
        View arrowLeft = contentView.findViewById(R.id.view_arrow_left);
        View arrowRight = contentView.findViewById(R.id.view_arrow_right);

        arrowTop.setVisibility(View.GONE);
        arrowBottom.setVisibility(View.GONE);
        arrowLeft.setVisibility(View.GONE);
        arrowRight.setVisibility(View.GONE);

        switch (gravity) {
            case Gravity.TOP:
                arrowBottom.setVisibility(View.VISIBLE);
                return arrowBottom;
            case Gravity.BOTTOM:
                arrowTop.setVisibility(View.VISIBLE);
                return arrowTop;
            case Gravity.LEFT:
                arrowRight.setVisibility(View.VISIBLE);
                return arrowRight;
            case Gravity.RIGHT:
                arrowLeft.setVisibility(View.VISIBLE);
                return arrowLeft;

        }
        return arrowTop;
    }


    public void show(View anchorView) {
        show(anchorView, Gravity.VERTICAL);
    }

    public void show(View anchorView, int gravity) {

        // 移除
        if (repeatShow()) {
            return;
        }

        Context context = anchorView.getContext();

        View contentView = LayoutInflater.from(context).inflate(R.layout.view_tip_view_content, null, false);

        // 参数: 父窗口数据描述
        WindowDesc windowDesc = new WindowDesc(parentWindowSize.x, parentWindowSize.y);

        // 参数: 锚点描述
        AnchorDesc anchorDesc = createAnchorDesc(anchorView);

        // 1、重新计算 gravity
        gravity = prepareGravity(gravity, windowDesc, anchorDesc);

        // 2、处理视图样式

        // 边距
        contentView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        // 正文文本
        TextView textContent = contentView.findViewById(R.id.tv_content);
        textContent.setTextSize(textSize);
        textContent.setText(content);

        // 关闭按钮
        View closeView = contentView.findViewById(R.id.view_close);
        if (closeVisible) {
            closeView.setVisibility(View.VISIBLE);
            closeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        } else {
            closeView.setVisibility(View.GONE);
        }

        // 隐藏其它箭头
        View arrowView = hideArrow(contentView, gravity);

        // 重新测量下距离
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        // 参数: 箭头描述
        ArrowDesc arrowDesc = new ArrowDesc(arrowView.getMeasuredWidth(), arrowView.getMeasuredHeight());

        // 参数: 气泡视图描述
        ContentViewDesc contentViewDesc = new ContentViewDesc(
                contentView.getMeasuredWidth(),
                contentView.getMeasuredHeight(),
                contentView.getPaddingTop(),
                contentView.getPaddingBottom(),
                contentView.getPaddingLeft(),
                contentView.getPaddingRight()
        );

        // 3、根据展示方位，计算坐标位置
        GravityProcessor processor = findGravityProcessor(gravity);
        PopupPoint popupPoint = processor.doProcess(contentViewDesc, arrowDesc, windowDesc, anchorDesc);

        // 设置箭头位置
        arrowView.setX(popupPoint.arrow.x);
        arrowView.setY(popupPoint.arrow.y);

        popupWindow = new PopupWindow(); // todo BasePopupWindow
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 点击外部是否可取消
        popupWindow.setOutsideTouchable(true);
        // 不允许超出屏幕边界
        popupWindow.setClippingEnabled(true);
        // Android 6.0 以下，解决外部触摸取消
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        // 关闭时释放资源
        popupWindow.setOnDismissListener(onDismissListener);


        int locationX = popupPoint.location.x;
        int locationY = popupPoint.location.y;

        int overX, overY;

        System.out.println(locationX + " : " + locationY);

//        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        if (locationX < 0) {
            // x 越界
            locationX = 0;
        } else if (locationX + contentView.getMeasuredWidth() > windowDesc.width) {
            locationX = windowDesc.width - contentView.getWidth();
        }

        if (locationY < 0) {
            locationY = 0;
        } else if (locationY + contentView.getMeasuredHeight() > windowDesc.height) {
            locationY = windowDesc.height - contentView.getHeight();
        }


        popupWindow.showAtLocation(anchorView, android.view.Gravity.NO_GRAVITY, locationX, locationY);

        popupWindow.setClippingEnabled(true);
        popupWindow.update();

        // 加入到队列
        tipViews.add(this);

    }

    private boolean repeatShow() {
        for (TipView tipView : tipViews) {
            if (tipView.equals(this)) {
                if (tipView.isShowing()) {
                    return true;
                } else {
                    tipViews.remove(tipView);
                }
            }
        }
        return false;
    }

    private AnchorDesc createAnchorDesc(View view) {
        final int[] location = new int[2];
        // 以父 window 为基准
        view.getLocationInWindow(location);
        // 计算中心位置
        final int x = location[0] + view.getWidth() / 2;
        final int y = location[1] + view.getHeight() / 2;
        return new AnchorDesc(x, y, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    private GravityProcessor findGravityProcessor(int gravity) {
        GravityProcessor processor;
        switch (gravity) {
            case Gravity.TOP:
                processor = new GravityTopProcessor();
                break;
            case Gravity.BOTTOM:
                processor = new GravityBottomProcessor();
                break;
            case Gravity.LEFT:
                processor = new GravityLeftProcessor();
                break;
            case Gravity.RIGHT:
                processor = new GravityRightProcessor();
                break;
            default:
                processor = new GravityBottomProcessor();
                break;
        }
        return processor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TipView) {
            TipView tipView = (TipView) o;
            return content != null ? content.equals(tipView.content) : tipView.content == null;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return content != null ? content.hashCode() : 0;
    }

    private static int dp2px(int value) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private static int px2dp(int value) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (value / scale + 0.5f);
    }

}

