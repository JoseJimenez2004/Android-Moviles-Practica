package mx.ipn.escuela.nivelburbuja;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BubbleLevelView extends View {

    private Paint bubblePaint;
    private Paint linePaint;
    private float angle = 0;
    private float bubblePosition = 0;

    public BubbleLevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bubblePaint = new Paint();
        bubblePaint.setColor(Color.CYAN);
        bubblePaint.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);
    }

    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // Draw the level lines
        canvas.drawLine(width / 4, height / 2, 3 * width / 4, height / 2, linePaint);
        canvas.drawLine(width / 4, height / 2 - 20, width / 4, height / 2 + 20, linePaint);
        canvas.drawLine(3 * width / 4, height / 2 - 20, 3 * width / 4, height / 2 + 20, linePaint);
        canvas.drawLine(width / 2, height / 2 - 10, width / 2, height / 2 + 10, linePaint);


        // Calculate bubble position based on angle
        bubblePosition = (width / 2) + (angle / 90) * (width / 4);


        // Draw the bubble
        canvas.drawCircle(bubblePosition, height / 2, 40, bubblePaint);
    }
}
