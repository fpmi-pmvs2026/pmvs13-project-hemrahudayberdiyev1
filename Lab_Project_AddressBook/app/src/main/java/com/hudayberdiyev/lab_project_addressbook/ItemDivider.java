package com.hudayberdiyev.lab_project_addressbook;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDivider extends RecyclerView.ItemDecoration {

    private Drawable divider;

    public ItemDivider(RecyclerView recyclerView) {
        divider = ContextCompat.getDrawable(recyclerView.getContext(),
                android.R.drawable.divider_horizontal_bright);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View child = parent.getChildAt(i);
            int top = child.getBottom();
            int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}