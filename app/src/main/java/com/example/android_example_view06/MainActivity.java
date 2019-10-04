package com.example.android_example_view06;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final int CAMERA_TASK_PHONE_SUCCESS = 1;
    private Button mButton;
    private ImageView mImageView;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mButton = findViewById(R.id.takeBtn);
        mImageView = findViewById(R.id.imgPhone);
        mButton.setOnClickListener(this);
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
            default:
                throw new NullPointerException("Not fount the requestCode");
        }
    }
}
