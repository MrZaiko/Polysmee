package io.github.polysmee.photo.editing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

import io.github.polysmee.R;

public class PictureEditActivity extends AppCompatActivity {

    public static final String PICTURE_BYTES_KEY = "io.github.polysmee.photo.editing.BITMAP_KEY";
    private static final float MAX_STROKE = 100f;
    private static final float MIN_STROKE = 1f;

    private Bitmap pictureBitmap;
    private DrawableImageView displayedPictureView;
    private SeekBar strokeBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);

        byte[] pictureBytes = getIntent().getByteArrayExtra(PICTURE_BYTES_KEY);

        pictureBitmap = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);

        displayedPictureView = findViewById(R.id.pictureEditPicture);
        displayedPictureView.setImageBitmap(pictureBitmap);
        displayedPictureView.setColor(R.color.red);

        strokeBar = findViewById(R.id.pictureEditStrokeWidthBar);
        strokeBar.setOnSeekBarChangeListener(strokeBarBehavior());

        findViewById(R.id.pictureEditResetButton).setOnClickListener(v -> reset());
        findViewById(R.id.pictureEditDoneButton).setOnClickListener(this::doneBehavior);
    }

    private SeekBar.OnSeekBarChangeListener strokeBarBehavior() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float newStroke = i*(MAX_STROKE-MIN_STROKE)/100 + MIN_STROKE;
                displayedPictureView.setStrokeWidth(newStroke);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private void applyColorFilter(ColorMatrix matrix) {
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        Bitmap newPicture = Bitmap.createBitmap(pictureBitmap.getWidth(), pictureBitmap.getHeight(), pictureBitmap.getConfig());
        Canvas canvas = new Canvas(newPicture);
        canvas.drawBitmap(pictureBitmap, 0, 0, paint);

        displayedPictureView.setImageBitmap(newPicture);
    }

    private void doneBehavior(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        displayedPictureView.getAlteredPicture().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent data = new Intent();
        data.putExtra("data", byteArray);
        setResult(RESULT_OK, data);
        finish();
    }

    private void reset() {
        displayedPictureView.setImageBitmap(pictureBitmap);
        strokeBar.setProgress(0);
        displayedPictureView.setColor(R.color.black);
        ((RadioButton) findViewById(R.id.pictureEditNormal)).setChecked(true);
        ((RadioButton) findViewById(R.id.pictureEditBlack)).setChecked(true);
    }



    @SuppressLint("NonConstantResourceId")
    public void onFilterSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.pictureEditNormal:
                if (checked)
                    displayedPictureView.setImageBitmap(pictureBitmap);
                break;

            case R.id.pictureEditBinary:
                if (checked)
                    applyColorFilter(Filters.binaryFilter());
                break;

            case R.id.pictureEditInvert:
                if (checked)
                    applyColorFilter(Filters.invertFilter());
                break;

            case R.id.pictureEditSepia:
                if (checked)
                    applyColorFilter(Filters.sepiaFilter());
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onColorSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.pictureEditRed:
                if (checked)
                    displayedPictureView.setColor(R.color.red);
                break;

            case R.id.pictureEditBlack:
                if (checked)
                    displayedPictureView.setColor(R.color.black);
                break;

            case R.id.pictureEditBlue:
                if (checked)
                    displayedPictureView.setColor(R.color.blue);
                break;

            case R.id.pictureEditYellow:
                if (checked)
                    displayedPictureView.setColor(R.color.yellow);
                break;

            case R.id.pictureEditGreen:
                if (checked)
                    displayedPictureView.setColor(R.color.green);
                break;
        }
    }
}
