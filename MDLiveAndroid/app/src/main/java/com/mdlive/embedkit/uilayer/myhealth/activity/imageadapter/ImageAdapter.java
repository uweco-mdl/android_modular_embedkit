package com.mdlive.embedkit.uilayer.myhealth.activity.imageadapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.myhealth.activity.MDLiveMedicalHistory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by srinivasan_ka on 6/11/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, Object>> myPhotosList;
    private BitmapFactory.Options options;

    public ImageAdapter(Activity activity, ArrayList<HashMap<String, Object>> myPhotosList) {
        this.myPhotosList = myPhotosList;
        this.activity = activity;
        options = new BitmapFactory.Options();
        options.inSampleSize = 8;
    }

    public void notifyWithDataSet(ArrayList<HashMap<String, Object>> myPhotosList){
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
        if(myPhotosList != null && !TextUtils.isEmpty((String)myPhotosList.get(position).get("download_link"))){
            Log.e("From Adapter-->", (String)myPhotosList.get(position).get("download_link"));
            /*imageView.setImageUrl((String) myPhotosList.get(position).get("download_link"),
                    ApplicationController.getInstance().getImageLoader(activity));*/
            imageView.setImageResource(R.drawable.doctor_icon);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryViewIntent = new Intent(activity, MDLiveImageGalleryView.class);
                    galleryViewIntent.putExtra("download_link", (String) myPhotosList.get(position).get("download_link"));
                    galleryViewIntent.putExtra("doc_type", (String) myPhotosList.get(position).get("doc_type"));
                    galleryViewIntent.putExtra("uploaded_by", (String) myPhotosList.get(position).get("uploaded_by"));
                    galleryViewIntent.putExtra("doc_name", (String) myPhotosList.get(position).get("doc_name"));
                    galleryViewIntent.putExtra("id", ((Integer) myPhotosList.get(position).get("id"))+"");
                    galleryViewIntent.putExtra("uploaded_at", (String) myPhotosList.get(position).get("uploaded_at"));
                    activity.startActivityForResult(galleryViewIntent, MDLiveMedicalHistory.IMAGE_PREVIEW_CODE);
                }
            });
        }

        return imageView;
    }

}