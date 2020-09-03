package com.dengzii.adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * author : dengzi
 * e-mail : master@dengzii.com
 * time   : 20-6-15
 * desc   : none
 */
public class DefaultViewHolder extends AbsViewHolder<Object> {

    DefaultViewHolder(@NonNull ViewGroup parent) {
        super(parent);
    }

    @Override
    public void onCreate(@NonNull ViewGroup parent) {
        TextView textView = new TextView(getContext());
        textView.setLayoutParams(
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 100));
        textView.setText(getContext().getString(R.string.no_view_holder_for_item));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16f);
        textView.setTextColor(Color.rgb(255, 255, 255));
        textView.setBackgroundColor(Color.rgb(0xff, 0, 0));
        setContentView(textView);
    }

    @Override
    public void onBindData(@NonNull Object data, int position) {

    }
}
