package io.github.polysmee.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.Message;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.databaselisteners.StringValueListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.photo.editing.FileHelper;
import io.github.polysmee.photo.editing.PictureEditActivity;
import io.github.polysmee.profile.fragments.ProfileActivityInfosFragment;
import io.github.polysmee.settings.fragments.SettingsMainFragment;

public class ProfileActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private CircleImageView profilePicture;
    private ImageView pickGallery;
    private ImageView takePhoto  ;
    private Uri currentPictureUri;
    private StringValueListener pictureListener;
    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PICTURE = 200;
    private static final int EDIT_PICTURE = 300;
    private String currentPictureId;
    public final static String PROFILE_VISITING_MODE = "io.github.polysmee.profile.visiting_mode";
    public final static String PROFILE_OWNER_MODE = "io.github.polysmee.profile.owner_mode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileActivityInfoContainer, new ProfileActivityInfosFragment())
                    .commit();
        }
        attributeSetters();
    }

    protected void attributeSetters(){
        pickGallery = (ImageView)findViewById(R.id.profileActivitySendPictureButton);
        takePhoto   = (ImageView)findViewById(R.id.profileActivityTakePictureButton);
        profilePicture = ((CircleImageView)((ConstraintLayout)findViewById(R.id.profileActivityProfilePictureContainer))
                .findViewById(R.id.profileActivityProfilePicture));

        pickGallery.setOnClickListener(this::chooseFromGallery);
        takePhoto.setOnClickListener(this::takePicture);
        pictureListener = setPictureListener();
        MainUser.getMainUser().getProfilePicture_Once_And_Then(pictureListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainUser.getMainUser().removeProfilePictureListener(pictureListener);
    }

    private void chooseFromGallery(View v){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void takePicture(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileHelper.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                this.currentPictureUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPictureUri);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    private StringValueListener setPictureListener(){
        return new StringValueListener() {
            @Override
            public void onDone(String pictureId) {
                if(!pictureId.equals("")){
                    currentPictureId = pictureId;
                    downloadPicture(pictureId);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_IMAGE:
                    currentPictureUri = data.getData();
                case TAKE_PICTURE:
                    CropImage.activity(currentPictureUri)
                            //.setMinCropResultSize(200,200)
                            //.setMaxCropResultSize(200,200)
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    currentPictureUri = result.getUri();
                    Intent photoEditIntent = new Intent(this,PictureEditActivity.class);
                    photoEditIntent.putExtra(PictureEditActivity.PICTURE_URI, currentPictureUri);
                    startActivityForResult(photoEditIntent, EDIT_PICTURE);
                    break;
                case EDIT_PICTURE:
                    currentPictureUri = (Uri) data.getExtras().get("data");

                    byte[] picturesToByte = new byte[0];
                    try{
                        picturesToByte = getBytes(this.getContentResolver().openInputStream(currentPictureUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(currentPictureId != null){
                        MainUser.getMainUser().removeProfilePicture();
                        UploadServiceFactory.getAdaptedInstance().deleteImage(currentPictureId,(id)->{
                            MainUser.getMainUser().removeProfilePicture();
                        },s-> showToast(getString(R.string.genericErrorText)));
                    }
                    UploadServiceFactory.getAdaptedInstance().uploadImage(picturesToByte,
                            MainUser.getMainUser().getId(), pictureId->{
                        currentPictureId = pictureId;
                        MainUser.getMainUser().setProfilePicture(currentPictureId);
                        MainUser.getMainUser().getProfilePicture_Once_And_Then(pictureListener);
                            }, s -> showToast(getString(R.string.genericErrorText)));
                    break;

                default:
                    break;

            }
        }
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.profileActivityInfoContainer, fragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void showToast(String message) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, message, duration);
        toast.show();
    }

    private void downloadPicture(String pictureId){
        UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profilePicture.setImageBitmap(Bitmap.createBitmap(bmp));
        },s-> showToast(getString(R.string.genericErrorText)));
    }

}