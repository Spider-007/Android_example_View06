package com.example.android_example_view06;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.IDN;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final int CAMERA_TASK_PHONE_SUCCESS = 1;
    public static final int WRITE_PERMISSION_CODE = 2;
    public static final int CHOOSE_PHOTO_SUCCESS = 3;
    private Button mTakeButton, mChooseButton;
    private ImageView mImageView;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTakeButton = findViewById(R.id.takeBtn);
        mChooseButton = findViewById(R.id.chooseBtn);
        mImageView = findViewById(R.id.imgPhone);
        mTakeButton.setOnClickListener(this);
        mChooseButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takeBtn:
                //handle take phone logic
                File mFile = new File(getExternalCacheDir(), "SpiderLine.png");
                try {
                    if (mFile.exists()) {
                        mFile.delete();
                    }
                    mFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    mUri = FileProvider.getUriForFile(this, "android.example.android_example_view06", mFile);
                } else {
                    mUri = Uri.fromFile(mFile);
                }
                Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                startActivityForResult(mIntent, CAMERA_TASK_PHONE_SUCCESS);
                break;

            case R.id.chooseBtn:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
                } else {
                    openAlbum();
                }

                break;

            default:
                throw new NullPointerException("not Designation id");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_TASK_PHONE_SUCCESS:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                        mImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case CHOOSE_PHOTO_SUCCESS:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        //if sdk >= android 4.4
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                throw new NullPointerException("Not fount the requestCode");
        }
    }

    private void openAlbum() {
        Intent mIntent = new Intent("android.intent.action.GET_CONTENT");
        mIntent.setType("image/*");
        startActivityForResult(mIntent, CHOOSE_PHOTO_SUCCESS);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        //handle sdk >= android 4.4
        Uri mUri = data.getData();
        String mImagePath = null;
        if (DocumentsContract.isDocumentUri(this, mUri)) {
            String mDoucumentId = DocumentsContract.getDocumentId(mUri);
            if ("com.android.providers.media.documents".equals(mUri
                    .getAuthority())) {
                String numberId = mDoucumentId.split(":")[1]; //Parse the id of the number format
                String mSelection = MediaStore.Images.Media._ID + "= " + numberId;
                mImagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mSelection);
            } else if ("com.android.providers.downloads.documents".equals(mUri
                    .getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(mDoucumentId));
                mImagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(mUri.getScheme())) {
            mImagePath = getImagePath(mUri, null);
        } else if ("file".equalsIgnoreCase(mUri.getScheme())) {
            mImagePath = mUri.getPath();
        }
        disPlayImage(mImagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        //handle sdk < android 4.4 Logic
        Uri mUri = data.getData();
        String path = getImagePath(mUri, null);
        disPlayImage(path);
    }

    private String getImagePath(Uri uri, String selection) {
        //by uri and selection Get the real image path
        Cursor mCursor;
        mCursor = getContentResolver().query(uri, null, selection, null, null);
        String mPath = null;
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                mPath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
        }
        return mPath;
    }

    private void disPlayImage(String mString) {
        if (mString != null) {
            Bitmap mBitmap = BitmapFactory.decodeFile(mString);
            mImageView.setImageBitmap(mBitmap);
        } else {
            Toast.makeText(this, "Path Invalid", Toast.LENGTH_LONG).show();
        }
    }
}
