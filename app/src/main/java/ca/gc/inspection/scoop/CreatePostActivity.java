package ca.gc.inspection.scoop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import ca.gc.inspection.scoop.util.ActivityUtils;

public class CreatePostActivity extends AppCompatActivity {

    private CreatePostPresenter mPresenter;
    private CreatePostFragment mView;

    public void returnToPrevious (View view) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // set the system status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));

        mView = (CreatePostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_create_post);
        if (mView == null) {
            // Create the fragment
            mView = CreatePostFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mView, R.id.activity_create_post);
        }

        mPresenter = new CreatePostPresenter();
    }

    public void takePicture() {
        MyCamera.takePicture(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MyCamera.MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                takePicture();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == MyCamera.MY_CAMERA_ROLL_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                Intent choosePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePictureIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(choosePictureIntent, "Select Picture"), MyCamera.CHOOSE_PIC_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String path = null;
            Uri uri = null;
            if (requestCode == MyCamera.CHOOSE_PIC_REQUEST_CODE) {
                if (data == null) {
                    Toast.makeText(getApplicationContext(), "Image cannot be null!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        uri = data.getData();
                        path = ImageFilePath.getPath(getBaseContext(), data.getData());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(MyCamera.currentPhotoPath);
                uri = Uri.fromFile(f);
                mediaScanIntent.setData(uri);
                sendBroadcast(mediaScanIntent);
                path = MyCamera.currentPhotoPath;
            }

            if (path != null || uri != null) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, bmOptions);
                bmOptions.inJustDecodeBounds = false;


                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bitmap newBitmap = MyCamera.imageOrientationValidator(bitmap, path);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.BELOW, R.id.activity_create_post_et_post_content);
                mView.setBitmapWithLayout(layoutParams, newBitmap);
            } else {
                makeToast("Something went wrong while uploading, please try again!", Toast.LENGTH_SHORT);
            }

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_LONG).show();
        }
    }

    public void makeToast(String s, int lengthShort) {
        Toast.makeText(this, "Something went wrong while uploading, please try again!", Toast.LENGTH_SHORT).show();
    }
}
