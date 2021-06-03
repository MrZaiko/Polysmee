package io.github.polysmee.photo.editing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.polysmee.R;
import top.defaults.colorpicker.ColorPickerPopup;

/**
 * Activity class corresponding to the edit picture activity
 */
public class PictureEditActivity extends AppCompatActivity {

    public static final String PICTURE_URI = "io.github.polysmee.photo.editing.PICTURE_URI";
    private static final float MAX_STROKE = 100f;
    private static final float MIN_STROKE = 1f;
    private static final int DEFAULT_QUALITY = 100;
    private static final int SEEK_BAR_DIVISION_CONSTANT = 100;

    private Bitmap pictureBitmap;
    private Button colorPickerButton;
    private DrawableImageView displayedPictureView;
    private Uri pictureUri;
    private SeekBar strokeBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_edit);

        pictureUri = (Uri) getIntent().getExtras().get(PICTURE_URI);

        try {
            pictureBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pictureUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set default editing settings
        displayedPictureView = findViewById(R.id.pictureEditPicture);
        displayedPictureView.setImageBitmap(pictureBitmap);
        displayedPictureView.setColor(Color.RED);

        strokeBar = findViewById(R.id.pictureEditStrokeWidthBar);
        strokeBar.setOnSeekBarChangeListener(strokeBarBehavior());

        findViewById(R.id.pictureEditResetButton).setOnClickListener(v -> reset());
        findViewById(R.id.pictureEditDoneButton).setOnClickListener(this::doneBehavior);

        colorPickerButton = findViewById(R.id.pictureEditColorPicker);
        colorPickerButton.setBackgroundColor(Color.RED);
        colorPickerButton.setOnClickListener(this::colorPickerButtonBehavior);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                setResult(RESULT_CANCELED);
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Sets the color picked for editing depending on the color selected by the user
     * @param view
     */
    private void colorPickerButtonBehavior(View view) {
        new ColorPickerPopup.Builder(PictureEditActivity.this)
                .initialColor(Color.RED)
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(false)
                .build()
                .show(
                        view,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                displayedPictureView.setColor(color);
                                view.setBackgroundColor(color);
                            }
                        });
    }

    /**
     *
     * @return a listener on the SeekBar
     */
    private SeekBar.OnSeekBarChangeListener strokeBarBehavior() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float newStroke = i * (MAX_STROKE - MIN_STROKE) / SEEK_BAR_DIVISION_CONSTANT + MIN_STROKE;
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

    /**
     * Applies the filter corresponding to the given color matrix
     * @param matrix
     */
    private void applyColorFilter(ColorMatrix matrix) {
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(matrix));

        Bitmap newPicture = Bitmap.createBitmap(pictureBitmap.getWidth(), pictureBitmap.getHeight(), pictureBitmap.getConfig());
        Canvas canvas = new Canvas(newPicture);
        canvas.drawBitmap(pictureBitmap, 0, 0, paint);

        displayedPictureView.setImageBitmap(newPicture);
    }


    /**
     * Makes the picture go online
     * @param view
     */
    private void doneBehavior(View view) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        displayedPictureView.getAlteredPicture().compress(Bitmap.CompressFormat.PNG, DEFAULT_QUALITY, stream);
        byte[] byteArray = stream.toByteArray();

        File photoFile = null;
        try {
            photoFile = FileHelper.createImageFile(this);
            try (FileOutputStream fileOutputStream = new FileOutputStream(photoFile)) {
                fileOutputStream.write(byteArray);
                fileOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent data = new Intent();
        data.putExtra("data", FileProvider.getUriForFile(this,
                "com.example.android.fileprovider", photoFile));
        setResult(RESULT_OK, data);



        finish();
    }

    /**
     * Sets the picture as it was before edition
     */
    private void reset() {
        displayedPictureView.setImageBitmap(pictureBitmap);
        strokeBar.setProgress(0);
        displayedPictureView.setColor(Color.RED);
        colorPickerButton.setBackgroundColor(Color.RED);
        ((RadioButton) findViewById(R.id.pictureEditNormal)).setChecked(true);
    }


    /**
     * Call the method to apply the filter corresponding to the selected button
     * @param view
     */
    @SuppressLint("NonConstantResourceId")
    public void onFilterSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
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
}