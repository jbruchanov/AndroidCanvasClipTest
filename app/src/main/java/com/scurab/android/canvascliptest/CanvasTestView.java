package com.scurab.android.canvascliptest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by jbruchanov on 08/04/2015.
 */
public class CanvasTestView extends View {

    private static final int NO_CLIP = 0;
    private static final int TYPE_CLIP_RECT = 1;
    private static final int TYPE_CLIP_RECT_ROT = 2;
    private static final int TYPE_CLIP_RECT_TRANSLATE_ROT = 3;
    private static final int TYPE_CLIP_PATH_LINE = 4;
    private static final int TYPE_CLIP_PATH_CIRCLE = 5;

    private int mType = NO_CLIP;
    private Paint mPaint;
    private Rect mRect = new Rect();

    public CanvasTestView(Context context) {
        this(context, null);
    }

    public CanvasTestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressLint("NewApi")
    public CanvasTestView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CanvasTestView);
            for (int i = 0, n = array.getIndexCount(); i < n; i++) {
                int index = array.getIndex(i);
                switch (index) {
                    case R.styleable.CanvasTestView_clipType:
                        mType = array.getInt(index, NO_CLIP);
                        break;
                }
            }
            array.recycle();
        }

        setBackgroundColor(0x80FF0000);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        //noinspection UnnecessaryLocalVariable
        int desiredHeight = desiredWidth;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        mRect.set(0, 0, r - l, b - t);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (mType){
            case NO_CLIP:
                onDrawNoClip(canvas);
                break;
            case TYPE_CLIP_RECT:
                onDrawClipRect(canvas);
                break;
            case TYPE_CLIP_RECT_ROT:
                onDrawClipRectRot(canvas);
                break;
            case TYPE_CLIP_RECT_TRANSLATE_ROT:
                onDrawClipRectRotTran(canvas);
                break;
            case TYPE_CLIP_PATH_LINE:
                onDrawClipPath(canvas);
                break;
            case TYPE_CLIP_PATH_CIRCLE:
                onDrawClipCircle(canvas);
            break;
        }
    }

    private void onDrawNoClip(Canvas canvas) {
        canvas.drawRect(mRect, mPaint);
    }

    private void onDrawClipRect(Canvas canvas) {
        canvas.save();
        int dx = mRect.width() >> 2;
        int dy = mRect.height() >> 2;
        mRect.inset(dx, dy);
        canvas.clipRect(mRect, Region.Op.REPLACE);
        canvas.drawRect(mRect, mPaint);
        mRect.inset(-dx, -dy);
        canvas.restore();
    }

    private void onDrawClipRectRot(Canvas canvas) {
        canvas.save();
        int s = Math.min(mRect.width(), mRect.height());
        canvas.rotate(45);
        canvas.clipRect(0, 0, s, s, Region.Op.INTERSECT);//don't draw outside bounds
        canvas.drawRect(mRect, mPaint);
        canvas.restore();
    }

    private void onDrawClipRectRotTran(Canvas canvas) {
        canvas.save();
        int size = (int) (Math.min(mRect.width(), mRect.height()) * (Math.cos(Math.toRadians(45))));
        canvas.translate(mRect.width() >> 1, 0);
        canvas.rotate(45);
        canvas.clipRect(0, 0, size, size, Region.Op.INTERSECT);//don't draw outside bounds
        canvas.drawRect(mRect, mPaint);
        canvas.restore();
    }

    private void onDrawClipCircle(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        int size = (int) ((Math.min(mRect.width(), mRect.height())) * 0.75f);
        path.addCircle(mRect.width() >> 1, mRect.height() >> 1, size >> 1, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawPaint(mPaint);
        canvas.restore();
    }

    private void onDrawClipPath(Canvas canvas) {
        int ds = mRect.width() >> 3;
        canvas.save();
        Path path = new Path();
        path.moveTo(ds, ds);
        path.lineTo(mRect.width() >> 1, 0);
        path.lineTo(mRect.width() - ds, ds);
        path.lineTo((mRect.width() >> 1) + (mRect.width() >> 3), mRect.height() >> 1);
        path.lineTo(mRect.width(), mRect.height());
        path.lineTo(0, mRect.height());
        path.lineTo((mRect.width() >> 1) - (mRect.width() >> 3), mRect.height() >> 1);
        path.close();
        canvas.clipPath(path);
        canvas.drawColor(Color.WHITE);
        canvas.restore();
    }
}
