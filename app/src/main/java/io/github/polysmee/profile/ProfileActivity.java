package io.github.polysmee.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.polysmee.R;
import io.github.polysmee.database.DatabaseUser;
import io.github.polysmee.database.UploadServiceFactory;
import io.github.polysmee.database.databaselisteners.valuelisteners.StringValueListener;
import io.github.polysmee.login.MainUser;
import io.github.polysmee.photo.editing.FileHelper;
import io.github.polysmee.photo.editing.PictureEditActivity;
import io.github.polysmee.profile.fragments.ProfileActivityInfosFragment;
import io.github.polysmee.room.fragments.HelperImages;

public class ProfileActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private CircleImageView profilePicture;
    private Uri currentPictureUri;
    private StringValueListener pictureListener;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    public static final int VISIT_MODE_REQUEST_CODE = 400;

    private static final int PICK_IMAGE = 100;
    private static final int TAKE_PICTURE = 200;
    private static final int EDIT_PICTURE = 300;
    private String currentPictureId;

    public final static String PROFILE_VISITING_MODE = "io.github.polysmee.profile.visiting_mode";
    public final static String PROFILE_OWNER_MODE = "io.github.polysmee.profile.owner_mode";
    public final static String PROFILE_VISIT_CODE = "io.github.polysmee.profile.visit_type";

    public final static String PROFILE_ID_USER = "io.github.polysmee.profile.visited_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String visitingMode = getIntent().getStringExtra(PROFILE_VISIT_CODE);
        if (savedInstanceState == null) {
            ProfileActivityInfosFragment profileActivityInfosFragment = new ProfileActivityInfosFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PROFILE_VISIT_CODE, visitingMode);
            if (visitingMode.equals(PROFILE_VISITING_MODE)) {
                bundle.putString(PROFILE_ID_USER, getIntent().getStringExtra(PROFILE_ID_USER));
            }
            profileActivityInfosFragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.profileActivityInfoContainer, profileActivityInfosFragment)
                    .commit();
        }

        initializePermissionRequester();

        if (visitingMode.equals(PROFILE_OWNER_MODE))
            attributeSettersOwner();
        else {
            attributeSettersVisitor();
        }
    }

    /**
     * The layout to be shown and behavior to be set in case we're visiting another user's profile
     */
    protected void attributeSettersVisitor() {
        profilePicture = findViewById(R.id.profileActivityProfilePictureContainer)
                .findViewById(R.id.profileActivityProfilePicture);
        findViewById(R.id.profileActivitySendPictureButton).setVisibility(View.GONE);
        findViewById(R.id.profileActivityTakePictureButton).setVisibility(View.GONE);
        pictureListener = setPictureListener();
        (new DatabaseUser(getIntent().getStringExtra(PROFILE_ID_USER))).getProfilePicture_Once_And_Then(pictureListener);
    }

    /**
     * The layout to be shown and behavior to be set in case we're visiting our own profile
     */
    protected void attributeSettersOwner() {
        profilePicture = findViewById(R.id.profileActivityProfilePictureContainer)
                .findViewById(R.id.profileActivityProfilePicture);

        findViewById(R.id.profileActivitySendPictureButton).setOnClickListener(this::chooseFromGallery);
        findViewById(R.id.profileActivityTakePictureButton).setOnClickListener(this::takePicture);
        pictureListener = setPictureListener();
        MainUser.getMainUser().getProfilePicture_Once_And_Then(pictureListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainUser.getMainUser().removeProfilePictureListener(pictureListener);
    }

    private void chooseFromGallery(View v) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void takePicture(View v) {
        if(!(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            return;
        }

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

    private StringValueListener setPictureListener() {
        return pictureId -> {
            if (!pictureId.equals("")) {
                currentPictureId = pictureId;
                downloadPicture(pictureId);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE: //In case we choose a picture from the gallery
                    currentPictureUri = data.getData();
                case TAKE_PICTURE: //launches the crop activity, in case we choose or took a picture
                    CropImage.activity(currentPictureUri)
                            .start(this);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: //When we're done with cropping: send it to the edit activity
                    launchEditActivity(data);
                    break;
                case EDIT_PICTURE: //When done editing: set it as profile picture
                    currentPictureUri = (Uri) data.getExtras().get("data");

                    byte[] picturesToByte = new byte[0];
                    try {
                        picturesToByte = HelperImages.getBytes(this.getContentResolver().openInputStream(currentPictureUri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (currentPictureId != null) {
                        MainUser.getMainUser().removeProfilePicture();
                        UploadServiceFactory.getAdaptedInstance().deleteImage(currentPictureId, (id) -> MainUser.getMainUser().removeProfilePicture(), s -> HelperImages.showToast(getString(R.string.genericErrorText), this), this);
                    }
                    UploadServiceFactory.getAdaptedInstance().uploadImage(picturesToByte,
                            MainUser.getMainUser().getId(), pictureId -> {
                                currentPictureId = pictureId;
                                MainUser.getMainUser().setProfilePicture(currentPictureId);
                                MainUser.getMainUser().getProfilePicture_Once_And_Then(pictureListener);
                            }, s -> HelperImages.showToast(getString(R.string.genericErrorText), this), this);
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

    /*
     * Initializes the request permission requester
     */
    private void initializePermissionRequester() {
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    //joins the channel if granted and do nothing otherwise
                    if (isGranted) {
                        takePicture(findViewById(R.id.profileActivityTakePictureButton));

                    } else {
                        System.out.println("not granted");
                    }
                });
    }

    /*
     * Download picture with given id
     */
    private void downloadPicture(String pictureId) {
        UploadServiceFactory.getAdaptedInstance().downloadImage(pictureId, imageBytes -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profilePicture.setImageBitmap(Bitmap.createBitmap(bmp));
        }, ss -> HelperImages.showToast(getString(R.string.genericErrorText), this), this);
    }

    private void launchEditActivity(Intent data){
        currentPictureUri = CropImage.getActivityResult(data).getUri();
        Intent photoEditIntent = new Intent(this, PictureEditActivity.class);
        photoEditIntent.putExtra(PictureEditActivity.PICTURE_URI, currentPictureUri);
        startActivityForResult(photoEditIntent, EDIT_PICTURE);
    }
}