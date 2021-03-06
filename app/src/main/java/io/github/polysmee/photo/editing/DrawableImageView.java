package io.github.polysmee.photo.editing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.github.polysmee.R;


/**
 * Class to implement the custom image view to be used when sharing pictures
 */
public class DrawableImageView extends androidx.appcompat.widget.AppCompatImageView {
    private final Paint paint;
    private final Path path;

    private Canvas alteredCanvas;
    private Bitmap alteredBitmap;

    private int currentColorId;
    private float currentStrokeWidth;

    private float previousX, previousY;

    private final static int DEFAULT_STROKE_WIDTH = 5;

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.primaryColor));
        currentColorId = R.color.primaryColor;
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        currentStrokeWidth = DEFAULT_STROKE_WIDTH;

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (alteredBitmap.getWidth() >= alteredBitmap.getHeight()) {
            float aspectRatio = alteredBitmap.getWidth() / (float) alteredBitmap.getHeight();
            int height = Math.round(w / aspectRatio);
            alteredBitmap = Bitmap.createScaledBitmap(
                    alteredBitmap, w, height, false);
        } else {
            float aspectRatio = alteredBitmap.getHeight() / (float) alteredBitmap.getWidth();
            int width = Math.round(h / aspectRatio);
            alteredBitmap = Bitmap.createScaledBitmap(
                    alteredBitmap, width, h, false);
        }


        alteredCanvas = new Canvas(alteredBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //keep the previous size
        if (alteredBitmap != null) {
            width = alteredBitmap.getWidth();
            height = alteredBitmap.getHeight();
        }

        alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        alteredCanvas = new Canvas(alteredBitmap);
        alteredCanvas.drawBitmap(bitmap, 0, 0, null);

        onSizeChanged(width, height, bitmap.getWidth(), bitmap.getHeight());
    }

    public void setStrokeWidth(float strokeWidth) {
        currentStrokeWidth = strokeWidth;
        paint.setStrokeWidth(strokeWidth);
    }

    /**
     *
     * @return the current stroke width
     */
    public float getCurrentStrokeWidth() {
        return currentStrokeWidth;
    }

    public void setColor(int colorId) {
        currentColorId = colorId;
        paint.setColor(colorId);
    }

    /**
     *
     * @return the current color id
     */
    public int getCurrentColorId() {
        return currentColorId;
    }

    private void touchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        previousX = x;
        previousY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - previousX);
        float dy = Math.abs(y - previousX);
        if (dx >= DEFAULT_STROKE_WIDTH || dy >= DEFAULT_STROKE_WIDTH) {
            path.quadTo(previousX, previousY, (x + previousX) / 2, (y + previousY) / 2);
            previousX = x;
            previousY = y;
        }
    }

    private void touchUp() {
        path.lineTo(previousX, previousY);
        // commit the path to our offscreen
        alteredCanvas.drawPath(path, paint);
        // kill this so we don't double draw
        path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(alteredBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    public Bitmap getAlteredPicture() {
        return alteredBitmap;
    }
}