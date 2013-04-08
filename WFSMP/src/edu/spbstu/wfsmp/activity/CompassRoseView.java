package edu.spbstu.wfsmp.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * User: Artegz
 * Date: 08.04.13
 * Time: 18:22
 */
public class CompassRoseView extends ImageView {

    private Paint paint;

    private int direction = 0;

    public CompassRoseView(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        this.setImageResource(R.drawable.compassrose_d);
    }

    public CompassRoseView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        this.setImageResource(R.drawable.compassrose_d);
    }

    public CompassRoseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);

        this.setImageResource(R.drawable.compassrose_d);
    }

    @Override
    public void onDraw(Canvas canvas) {
        int height = this.getHeight();
        int width = this.getWidth();

        canvas.rotate(direction, width / 2, height / 2);
        super.onDraw(canvas);
    }

    public void setDirection(int direction) {
        this.direction = direction;
        this.invalidate();
    }

}
