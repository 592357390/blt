package bluetooth.jie.com.jie_bluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class ProgressSeekBar extends View {

    private Paint paint;

    public ProgressSeekBar(Context context) {
        this(context, null);
    }

    public ProgressSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ProgressSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.yellow));
        draw(location, canvas);

    }

    private float location = 0;

    public void setLocation(float location) {
        this.location = location;
        invalidate();
    }

    public void draw(float location, Canvas canvas) {

        if (location <= 2 && location >= -2) {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.normal_state_color));
        } else {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.un_normal_state_color));
        }

        if (location < 0) {
            int top = 0;
            if (location <= -10) {
                top = getHeight();
            } else {
                top = (int) ((10 - location) / 20 * getHeight());
            }
            Rect rect = new Rect(0, getHeight() / 2, getWidth(), top);
            canvas.drawRect(rect, paint);
        }


        if (location > 0) {
            int top = 0;
            if (location >= 10) {
                top = 0;
            } else {
                top = (int) ((10 - location) / 10 * getHeight() / 2);
            }
            Rect rect = new Rect(0, top, getWidth(), getHeight() / 2);
            canvas.drawRect(rect, paint);
        }

        if (location == 0) {
            Rect rect = new Rect(0, 0, 0, 0);
            canvas.drawRect(rect, paint);
        }
    }
}
