package com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMedicalHistory;

import java.util.ArrayList;

/**
 * Created by srinivasan_ka on 6/11/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<String> myPhotosList;
    private BitmapFactory.Options options;

    public ImageAdapter(Activity activity, ArrayList<String> myPhotosList) {
        this.myPhotosList = myPhotosList;
        this.activity = activity;
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
    }

    public void notifyWithDataSet(ArrayList<String> myPhotosList){
        this.myPhotosList = myPhotosList;
        notifyDataSetChanged();
    }

    public int getCount() {
        if(myPhotosList == null)
            return 0;
        else
            return myPhotosList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(activity);
            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }
        if(myPhotosList != null && !TextUtils.isEmpty(myPhotosList.get(position))){
            imageView.setImageBitmap(BitmapFactory.decodeFile(myPhotosList.get(position), options));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryViewIntent = new Intent(activity, MDLiveImageGalleryView.class);
                    galleryViewIntent.putExtra("imageName", myPhotosList.get(position).substring(myPhotosList.get(position).lastIndexOf("/")+1,
                            myPhotosList.get(position).length()));
                    galleryViewIntent.putExtra("imagePath", myPhotosList.get(position));
                    galleryViewIntent.putExtra("imageId", "photo"+(position-1));
                    activity.startActivityForResult(galleryViewIntent, MDLiveMedicalHistory.IMAGE_PREVIEW_CODE);
                }
            });
        }

        return imageView;
    }

}