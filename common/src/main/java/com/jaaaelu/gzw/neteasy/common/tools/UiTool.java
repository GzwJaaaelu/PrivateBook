package com.jaaaelu.gzw.neteasy.common.tools;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.widget.TextView;

/**
 * @author qiujuer Email:qiujuer@live.cn
 * @version 1.0.0
 */
public class UiTool {
    private static int STATUS_BAR_HEIGHT = -1;

    /**
     * 得到我们的状态栏的高度
     *
     * @param activity Activity
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        if (STATUS_BAR_HEIGHT == -1) {
            try {
                final Resources res = activity.getResources();
                //  尝试获取 status_bar_height 这个属性的Id对应的资源int值
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object object = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height")
                            .get(object).toString());
                }


                //  如果拿到了就直接调用获取值
                if (resourceId > 0)
                    STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);

                //  如果还是未拿到
                if (STATUS_BAR_HEIGHT <= 0) {
                    //  通过 Window 拿取
                    Rect rectangle = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    STATUS_BAR_HEIGHT = rectangle.top;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //  int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //  int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.heightPixels;
    }

    /**
     * 处理数据
     *
     * @param view   显示文本的空间
     * @param text   显示内容
     * @param prefix 内容前缀
     */
    public static void dealEmptyData(TextView view, String text, String prefix) {
        if (TextUtils.isEmpty(text)) {
            view.setText(prefix + "暂无数据");
        } else {
            view.setText(prefix + text);
        }
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float dipToPx(Resources resources, float dp) {
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static LayoutTransition getLayoutTransition() {
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, layoutTransition.getAnimator(LayoutTransition.APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_APPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_APPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING));
        layoutTransition.setAnimator(LayoutTransition.CHANGING, layoutTransition.getAnimator(LayoutTransition.CHANGING));
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, layoutTransition.getAnimator(LayoutTransition.DISAPPEARING));
        return layoutTransition;
    }
}
