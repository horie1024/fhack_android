package com.example.horie.hack;

import android.content.Context;
import android.support.wearable.view.GridViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowInsets;

/**
 * Created by nissiy on 2014/09/07.
 */
public class InsetGridViewPager extends GridViewPager {
    private OnApplyWindowInsetsListener mOnApplyWindowInsetsListener;

    public InsetGridViewPager(Context context) {
        super(context);
    }

    public InsetGridViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsetGridViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setOnApplyWindowInsetsListener(View.OnApplyWindowInsetsListener listener) {
        mOnApplyWindowInsetsListener = listener;
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (mOnApplyWindowInsetsListener != null) {
            mOnApplyWindowInsetsListener.onApplyWindowInsets(this, insets);
        }
        insets = onApplyWindowInsets(insets); // System insets are consumed here!
        return insets;
    }
}
