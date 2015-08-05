package com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;

import java.io.File;

/**
 * This class is used to manipulate CRUD (Create, Read, Update, Delete) function for Allergies.
 *
 * This class extends with MDLiveCommonConditionsMedicationsActivity
 *  which has all functions that is helped to achieve CRUD functions.
 *
 */

public class MDLiveImageGalleryView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting up type in parent class for Allergy
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdlive_image_galleryview);

        ((TextView) findViewById(R.id.doneText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   finish();
            }
        });
        ((TextView) findViewById(R.id.deleteImageText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getIntent().getStringExtra("imagePath"));
                if(file.exists())
                    file.delete();
                Intent intent = new Intent();
                intent.putExtra("imageId", getIntent().getStringExtra("imageId"));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ((TextView) findViewById(R.id.uploadText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ((TextView) findViewById(R.id.imageNameText)).setText(getIntent().getStringExtra("imageName"));

        ((ImageView) findViewById(R.id.galleryImageView)).setImageBitmap(BitmapFactory.decodeFile(
                getIntent().getStringExtra("imagePath")));


    }
}
