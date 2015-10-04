package com.mdlive.embedkit.uilayer.sav.adapters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;

import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by srinivasan_ka on 9/5/2015.
 */
public class PickImagePlugin {

    public static Uri fileUri;
    public static Activity parentActivity;
    public UploadRecordInterface uploadInterface;

    public PickImagePlugin(Activity parentActivity, UploadRecordInterface uploadInterface){
        this.parentActivity = parentActivity;
        this.uploadInterface = uploadInterface;
    }

    /*
      * Capturing Camera Image will lauch camera app requrest image capture
      */
    public void captureImage() {
        if (!isDeviceSupportCamera()){
            MdliveUtils.alert(null, parentActivity, "Your Device doesn't support have Camera Feature!");
        }else{
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri = getOutputMediaFileUri();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            fetchLastFileInGallery();
            parentActivity.startActivityForResult(intent, IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            MdliveUtils.startActivityAnimation(parentActivity);
        }
    }

    public void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        parentActivity.startActivityForResult(photoPickerIntent, IntegerConstants.PICK_IMAGE_REQUEST_CODE);
        MdliveUtils.startActivityAnimation(parentActivity);
    }

    /**
     * Checking device has camera hardware or not
     */
    public boolean isDeviceSupportCamera() {
        if (parentActivity.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void removePhotoFromGallery(boolean capturedInCamera, String filePath){
        if(capturedInCamera){
            checkOutLastFileInGallery();
        }
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile(parentActivity));
    }

    String lastFileNameId = "";

    public void fetchLastFileInGallery(){
        String[] projections = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};

        final Cursor cursor = parentActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if(cursor != null && cursor.moveToFirst()){
            lastFileNameId = cursor.getString(0);
        }
    }

    public void checkOutLastFileInGallery(){
        String[] projections = {
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE};

        final Cursor cursor = parentActivity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projections, null, null,
                MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if(cursor != null && cursor.moveToFirst()){
            if(lastFileNameId == null){
                if(cursor.getString(0) != null){
                    ContentResolver cr = parentActivity.getContentResolver();
                    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(0), null);
                }
            }else if(lastFileNameId != null && lastFileNameId != cursor.getString(0)){
                ContentResolver cr = parentActivity.getContentResolver();
                cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(0), null);
            }
            lastFileNameId = "";
        }
    }


    /*
    * returning image / video
    */
    public static File getOutputMediaFile(Context context) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    // directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "MDLive Images";


    /**
     *  This function is used to Check size of Picked/Captured Image from device
     *  If it exceeds more than 10 mb then it will make alert to user about size exceeding
     *
     *  @param file :: Image file captured or picked by user
     */
    public String checkSizeOfImageAndType(File file){
        boolean acceptSize = true;
        double bytes = file.length();
        double kilobytes = (bytes / 1024);
        double megabytes = (kilobytes / 1024);
        double hasexceededSize = 10.0000000 - megabytes;
        if(hasexceededSize < 0){
            acceptSize = false;
        }
        if(!acceptSize)
            return "Please add a photo with a maximum size of 10 MB";
        else
            return null;
    }


    public interface UploadRecordInterface {
        public void uploadMedicalRecordService(String filePath, boolean capturedInCamera);
    }



    public void handleCapturedImageRequest(){
        if(fileUri != null){
            File file = new File(fileUri.getPath());
            String hasErrorText = checkSizeOfImageAndType(file);
            if(file.exists() && hasErrorText == null){
                uploadInterface.uploadMedicalRecordService(fileUri.getPath(), true);
            }else{
                MdliveUtils.showDialog(parentActivity,
                        hasErrorText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                captureImage();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
            }
        }
    }

    public void handlePickedImageRequest(Intent data){
        try {
            fileUri = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = parentActivity.getContentResolver().query(fileUri,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File file = new File(picturePath);
            String hasErrorText = checkSizeOfImageAndType(file);
            if(fileUri != null && hasErrorText == null){
                uploadInterface.uploadMedicalRecordService(getPath(fileUri), false);
            }else{
                MdliveUtils.showDialog(parentActivity,
                        hasErrorText,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                pickImage();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                handleCapturedImageRequest();
            }
        }
        if (requestCode == IntegerConstants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            }
        }
    }*/

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = parentActivity.getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }


}
