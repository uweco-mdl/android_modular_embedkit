package com.mdlive.sav.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mdlive.sav.R;
import com.mdlive.unifiedmiddleware.commonclasses.application.ApplicationController;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This adapter class is used to display list of images uploaded by user
 *
 * By tapping on image, it will redirect to MDLiveImageGalleryView page.
 */
public class ImageAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, Object>> myPhotosList;
    private int size = 0;
    private LayoutInflater inflater;

    public ImageAdapter(Activity activity, ArrayList<HashMap<String, Object>> myPhotosList) {
        this.myPhotosList = myPhotosList;
        this.activity = activity;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = IntegerConstants.IMAGE_SAMPLE_SIZE;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        size = width / IntegerConstants.IMAGE_DIVIDED_SIZE;
        inflater = activity.getLayoutInflater();
    }

    public void notifyWithDataSet(ArrayList<HashMap<String, Object>> myPhotosList) {
        this.myPhotosList = myPhotosList;
        notifyDataSetChanged();
    }

    public int getCount() {
        if (myPhotosList == null)
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

    // create a new ImageView for each item referenced by the Adaptergridview
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if (convertView == null || convertView.getTag() == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.mdlive_custom_imageadapter_view, null);
            imageView = (ImageView) convertView.findViewById(R.id.thumpImage);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(size, size - 35));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); //.CENTER_CROP
            imageView.setPadding(5, 0, 5, 0);
            convertView.setTag(convertView);
        } else {
            convertView = (View) convertView.getTag();
            imageView = (ImageView) convertView.findViewById(R.id.thumpImage);
        }


        imageView.setImageBitmap(null);
        if (myPhotosList != null && !TextUtils.isEmpty((String) myPhotosList.get(position).get("download_link"))) {
            if (ApplicationController.getInstance().getBitmapLruCache() != null &&
                    ApplicationController.getInstance().getBitmapLruCache().getBitmap(myPhotosList.get(position).get("id") + "") == null) {
                if (ApplicationController.getInstance().getRequestQueue(activity).getCache().get(myPhotosList.get(position).get("id") + "") != null) {
                    ApplicationController.getInstance().getBitmapLruCache().put(myPhotosList.get(position).get("id") + "",
                            decodeSampledBitmapFromResource(ApplicationController.getInstance().getRequestQueue(activity).getCache().get(myPhotosList.get(position).get("id") + "").data));
                    if (ApplicationController.getInstance().getBitmapLruCache().get(myPhotosList.get(position).get("id") + "") == null) {
                        imageView.setImageResource(R.drawable.account);
                    } else {
                        imageView.setImageBitmap(ApplicationController.getInstance().getBitmapLruCache().get(myPhotosList.get(position).get("id") + ""));
                    }
                }
            } else if (ApplicationController.getInstance().getBitmapLruCache().getBitmap(myPhotosList.get(position).get("id") + "") != null) {
                imageView.setImageBitmap(ApplicationController.getInstance().getBitmapLruCache().get(myPhotosList.get(position).get("id") + ""));
            }else{
                imageView.setImageBitmap(null);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryViewIntent = new Intent(activity, MDLiveImageGalleryView.class);
                    galleryViewIntent.putExtra("download_link", (String) myPhotosList.get(position).get("download_link"));
                    galleryViewIntent.putExtra("doc_type", (String) myPhotosList.get(position).get("doc_type"));
                    galleryViewIntent.putExtra("uploaded_by", (String) myPhotosList.get(position).get("uploaded_by"));
                    galleryViewIntent.putExtra("doc_name", (String) myPhotosList.get(position).get("doc_name"));
                    galleryViewIntent.putExtra("id", ((Integer) myPhotosList.get(position).get("id")));
                    galleryViewIntent.putExtra("uploaded_at", (String) myPhotosList.get(position).get("uploaded_at"));
                    activity.startActivityForResult(galleryViewIntent, IntegerConstants.IMAGE_PREVIEW_CODE);
                    MdliveUtils.startActivityAnimation(activity);
                }
            });
        }

        return convertView;
    }
    /**
     * This method is used to decode byte data and returns bitmap.
     * RGB_565 - is used to reduce the quality of image to handle bitmap efficiently
     * Bitmap will be recycled for each time to avoid bitmap issue.
     *
     * @param decodedString - byte data of image received  from webservice.
     */
    static Bitmap decodeSampledBitmapFromResource(byte[] decodedString) {
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = IntegerConstants.IMAGE_SAMPLE_SIZE;
            options.inMutable = true;
            options.inScaled = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap b = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
            if (b != null) {
                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, b.getWidth(), b.getHeight());
                // Decode bitmap with inSampleSize set
                b.recycle();
            }
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Given the bitmap size and View size calculate a subsampling size (powers of 2)
     * Use of this function is to resize the image and show in gridview.
     * This will avoid bitmap issues in showing bigger images.
     * This method returns the resized samplesize value, which will be set to image
     *
     * @param options - bitmap options of image
     * @param reqHeight - height of image
     * @param reqWidth - width of image
     *
     */
    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        int inSampleSize = IntegerConstants.IMAGE_SAMPLE_SIZE;    //Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / IntegerConstants.IMAGE_DIVIDED_SIZE;
            final int halfWidth = options.outWidth / IntegerConstants.IMAGE_DIVIDED_SIZE;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
