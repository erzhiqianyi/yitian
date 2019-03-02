package io.github.buniaowanfeng.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import io.github.buniaowanfeng.R;

/**
 * Created by caofeng on 16-7-30.
 */
public class Circle extends View{

    private int width;
    private int height;
    private int radius;

    private float starSweep;
    private float progress;

    private int mBackgroundColor;
    private int mForegroundColor;

    private Paint paint = new Paint();
    public Circle(Context context) {
        this(context,null);
    }

    public Circle(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Circle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Circle);
        mBackgroundColor = array.getColor(R.styleable.Circle_backgroundColor,Color.GRAY);
        mForegroundColor = array.getColor(R.styleable.Circle_foregroundColor,Color.BLUE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        radius = Math.min(width,height);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        RectF rectF = new RectF(0,0,radius,radius);
        canvas.drawArc(rectF,-90,360,true,paint);
        paint.setColor(mForegroundColor);
//        canvas.drawArc(rectF,starSweep,progress,true,paint);
        canvas.drawArc(rectF,-90,progress,true,paint);
    }

    public void setData(float starSweap,float progress){
        this.starSweep = starSweap;
        this.progress = progress;
        invalidate();
    }
}
