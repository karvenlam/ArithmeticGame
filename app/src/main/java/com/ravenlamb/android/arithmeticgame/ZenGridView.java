package com.ravenlamb.android.arithmeticgame;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by kl on 12/10/2014.
 */
public class ZenGridView extends BaseGridView {
    public ZenGridView(Context context) {
        super(context);
    }

    public ZenGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZenGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initDriver() {
        baseGameDriver=new ZenGameDriver(gridSize,gridSize);
    }
}
