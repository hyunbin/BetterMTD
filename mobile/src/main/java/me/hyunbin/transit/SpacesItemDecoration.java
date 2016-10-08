package me.hyunbin.transit;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.hyunbin.transit.helpers.LayoutUtil;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

  private static final int START_AND_END_MARGIN_PX = LayoutUtil.dpToPx(15);

  private int mSpacePx;
  private int mSpanCount;

  public SpacesItemDecoration(int space, int spanCount) {
    mSpacePx = LayoutUtil.dpToPx(space);
    mSpanCount = spanCount;
  }

  @Override
  public void getItemOffsets(
      Rect outRect,
      View view,
      RecyclerView parent,
      RecyclerView.State state) {
    if (parent.getPaddingLeft() != mSpacePx) {
      parent.setPadding(mSpacePx, mSpacePx, mSpacePx, mSpacePx);
      parent.setClipToPadding(false);
    }

    outRect.top = mSpacePx;
    outRect.bottom = mSpacePx;
    outRect.left = mSpacePx;
    outRect.right = mSpacePx;

    int position = parent.getChildLayoutPosition(view);
    if (position / mSpanCount == 0) {
      outRect.left = START_AND_END_MARGIN_PX;
    } else if (position + mSpanCount >= parent.getAdapter().getItemCount()) {
      outRect.right = START_AND_END_MARGIN_PX;
    }
  }
}