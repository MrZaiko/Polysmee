package io.github.polysmee.photo.editing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import io.github.polysmee.R;

public class DrawableImageView extends androidx.appcompat.widget.AppCompatImageView {
    private Paint paint;
    private Path path;

    private Canvas alteredCanvas;
    private Bitmap alteredBitmap;

    private float previousX, x;
    private float previousY, y;

    public DrawableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.primaryColor));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        path = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float aspectRatio = alteredBitmap.getWidth() / (float) alteredBitmap.getHeight();
        int height = Math.round(w / aspectRatio);

        alteredBitmap = Bitmap.createScaledBitmap(
                alteredBitmap, w, height, false);

        alteredCanvas = new Canvas(alteredBitmap);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        super.setImageBitmap(bitmap);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (alteredBitmap != null) {
            width = alteredBitmap.getWidth();
            height = alteredBitmap.getHeight();
        }

        alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        alteredCanvas = new Canvas(alteredBitmap);
        alteredCanvas.drawBitmap(bitmap,0,0,null);

        onSizeChanged(width, height, bitmap.getWidth(), bitmap.getHeight());
    }

    public void setStrokeWidth(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
    }

    public void setColor(int colorId) {
        paint.setColor(getResources().getColor(colorId));
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
        if (dx >= 5 || dy >= 5) {
            path.quadTo(previousX, previousY, (x + previousX)/2, (y + previousY)/2);
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
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchStart(x,y);
                invalidate();
                break ;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break ;
            case MotionEvent.ACTION_MOVE:
                touchMove(x,y);
                invalidate();
                break ;
        }
        return true ;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(alteredBitmap, 0, 0, null);
        canvas.drawPath(path, paint);
    }

    public Bitmap getAlteredPicture() {
        return alteredBitmap;
    }
}
