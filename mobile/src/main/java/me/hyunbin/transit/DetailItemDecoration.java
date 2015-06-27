package me.hyunbin.transit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Hyunbin on 5/22/15.
 */
public class DetailItemDecoration extends RecyclerView.ItemDecoration{

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private int mOrientation;
    Paint mPaintOutside;
    Paint mPaintInside;
    float lineStrokeWidth;
    float circleStrokeWidth;
    float circleRadius;

    public DetailItemDecoration(Context context, int color){
        mPaintOutside = new Paint();
        mPaintOutside.setColor(color);

        mPaintInside = new Paint();
        mPaintInside.setColor(Color.WHITE);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        circleRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, dm);
        lineStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, dm);
        circleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm);

        mPaintOutside.setStyle(Paint.Style.STROKE);
        mPaintOutside.setStrokeWidth(lineStrokeWidth);
        mPaintInside.setStyle(Paint.Style.FILL);
        mPaintInside.setStrokeWidth(circleStrokeWidth);
    }

    @Override
    public void onDrawOver (Canvas c, RecyclerView parent, RecyclerView.State state){
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getTop();
            final int bottom = child.getBottom();
            final int center = child.findViewById(R.id.timeView).getRight()
                    - child.findViewById(R.id.timeView).getPaddingRight() / 2
                    + child.findViewById(R.id.stopName).getPaddingLeft() / 2;

            mPaintOutside.setStrokeWidth(lineStrokeWidth);
            int pos = params.getViewAdapterPosition();
            if (pos == 0) {
                c.drawLine(center, (top + bottom) / 2, center, bottom, mPaintOutside);
            } else if (pos == parent.getAdapter().getItemCount() - 1) {
                c.drawLine(center, top, center, (top + bottom) / 2, mPaintOutside);
            } else {
                c.drawLine(center, top, center, bottom, mPaintOutside);
            }
            mPaintOutside.setStrokeWidth(circleStrokeWidth);
            c.drawCircle(center, (top + bottom) / 2, circleRadius + circleStrokeWidth / 2, mPaintInside);
            c.drawCircle(center, (top + bottom) / 2, circleRadius, mPaintOutside);
        }
    }

}
