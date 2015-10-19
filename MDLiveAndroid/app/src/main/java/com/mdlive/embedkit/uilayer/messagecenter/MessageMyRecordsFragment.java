package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.RecordAdapter;
import com.mdlive.embedkit.uilayer.sav.adapters.PickImagePlugin;
import com.mdlive.unifiedmiddleware.commonclasses.constants.IntegerConstants;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.commonclasses.utils.TimeZoneUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Records;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;
import com.mdlive.unifiedmiddleware.services.myhealth.DownloadMedicalImageService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageMyRecordsFragment extends MDLiveBaseFragment implements PickImagePlugin.UploadRecordInterface {
    public static PickImagePlugin cameraPlugIn;
    public static ProgressDialog pDialog;
    private ListView mListView;
    private View mEmptyView;
    public static RecordAdapter mRecordAdapter;

    public static MessageMyRecordsFragment newInstance() {
        final MessageMyRecordsFragment messageMyRecordsFragment = new MessageMyRecordsFragment();
        return messageMyRecordsFragment;
    }

    public MessageMyRecordsFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_my_records, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.fragment_message_my_records_list_view);
        mEmptyView = view.findViewById(R.id.emptyView);
        mEmptyView.setVisibility(View.GONE);

        if (mListView != null) {
            mRecordAdapter = new RecordAdapter(view.getContext(), R.layout.adapter_record, android.R.id.text1);

            mListView.setAdapter(mRecordAdapter);
            File dataDir = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                dataDir = new File(Environment.getExternalStorageDirectory(), "Download");
                if (!dataDir.isDirectory()) {
                    dataDir.mkdirs();
                    dataDir = getActivity().getFilesDir();
                }
            }

            final File finalDataDir = dataDir;
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                    final File tempFile = new File(finalDataDir, mRecordAdapter.getItem(i).docName);
//                    new DownloadFileFromURL().execute(mRecordAdapter.getItem(i).downloadLink,mRecordAdapter.getItem(i).docName);
                    final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject responObj) {
                            hideProgressDialog();
                            try {
                                String base64String = null;
                                JsonParser parser = new JsonParser();
                                if (responObj.optString("file_stream").length() != 0 && responObj.optString("file_type").length() != 0) {
                                    base64String = responObj.optString("file_stream");
                                    FileOutputStream fos = null;
                                    try {
                                        if (base64String != null) {
                                            fos = new FileOutputStream(tempFile);
                                            byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
                                            fos.write(decodedString);
                                            fos.flush();
                                            fos.close();
                                        }
                                        Uri path = Uri.fromFile(tempFile);
                                        Intent openIntent = new Intent(Intent.ACTION_VIEW);
                                        String docType = getDocumentType(mRecordAdapter.getItem(i).docName);
                                        openIntent.setDataAndType(path, docType);
                                        getActivity().startActivity(openIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (fos != null) {
                                            fos = null;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    showProgressDialog();
                    DownloadMedicalImageService service = new DownloadMedicalImageService(getActivity(), getProgressDialog());
                    service.doDownloadImagesRequest(mRecordAdapter.getItem(i).id, successListener, errorListener);
                }
            });
        }


    }


    String getDocumentType(String urlName) {
        String type = "";
        if ((MdliveUtils.getExtention(urlName).equalsIgnoreCase("gif") || MdliveUtils.getExtention(urlName).equalsIgnoreCase("png")
                || MdliveUtils.getExtention(urlName).equalsIgnoreCase("jpg") || MdliveUtils.getExtention(urlName).equalsIgnoreCase("jpeg"))) {
            type = "image/*";
        } else if (MdliveUtils.getExtention(urlName).equalsIgnoreCase("pdf")) {
            type = "application/pdf";
        } else if (MdliveUtils.getExtention(urlName).equalsIgnoreCase("doc") || (MdliveUtils.getExtention(urlName).equalsIgnoreCase("docx"))) {
            type = "application/msword";
        } else if (MdliveUtils.getExtention(urlName).equalsIgnoreCase("xls") || (MdliveUtils.getExtention(urlName).equalsIgnoreCase("xlsx"))) {
            type = "application/vnd.ms-excel";
        } else if (MdliveUtils.getExtention(urlName).equalsIgnoreCase("pptx") || (MdliveUtils.getExtention(urlName).equalsIgnoreCase("ppt"))) {
            type = "application/vnd.ms-powerpoint";
        } else {
            MdliveUtils.alert(getProgressDialog(), getActivity(), getString(R.string.mdl_no_compitable_app));
        }

        return type;
    }

    final NetworkErrorListener errorListener = new NetworkErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            hideProgressDialog();
            try {
                MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                final String errorResponse = new String(error.networkResponse.data);
                Log.e("MDLIVE ERROR", "Error : " + errorResponse);
            } catch (Exception e) {
                MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cameraPlugIn = new PickImagePlugin(getActivity(), this);
        pDialog = MdliveUtils.getProgressDialog("Please wait...", getActivity());

        fetchMyRecords();
        //uploadDocument();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }




 /*   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 1:
                if(resultCode == Activity.RESULT_OK) {

                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        convertToBase64(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 2:
                if(resultCode == Activity.RESULT_OK) {
                    final Bitmap  bitmap = (Bitmap) data.getExtras().get("data");
                    convertToBase64(bitmap);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/

    @Override
    public void uploadMedicalRecordService(String filePath, boolean capturedInCamera) {

        Log.e("File Path From Local", filePath);
        Log.e("capturedInCamera", capturedInCamera + "");

        final File file = new File(filePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String currentTimeStamp = dateFormat.format(TimeZoneUtils.getCalendarWithOffset(getActivity()).getTime());
        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_name", currentTimeStamp + ".jpg");
            jsonObject.put("document_type_id", 2);
            jsonObject.put("document", MdliveUtils.encodeFileToBase64Binary(file, MdliveUtils.getFileExtention(file)));
            parent.put("customer_document", jsonObject);
            uploadDocument(parent.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

  /*  public void convertToBase64(Bitmap selectedImage){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte b[] = baos.toByteArray();
        String base64String = Base64.encodeToString(b, Base64.DEFAULT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());

        try {
            JSONObject parent = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("file_name", currentTimeStamp+".jpg");
            jsonObject.put("document_type_id", 2);
            jsonObject.put("document", base64String);
            parent.put("customer_document", jsonObject);
            uploadDocument(parent.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void fetchMyRecords() {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                logE("", response.toString());

                try {
                    final JSONArray array = response.getJSONArray("records");

                    final JSONObject firstObject = array.getJSONObject(0);
                } catch (JSONException e) {
                    mListView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    return;
                }

                final Gson gson = new Gson();
                final Records records = gson.fromJson(response.toString(), Records.class);


                if(records.records != null && records.records.size() > 0){
                    mListView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                if (mRecordAdapter != null) {
                    mRecordAdapter.clear();
                    mRecordAdapter.addAll(records.records);
                    mRecordAdapter.notifyDataSetChanged();
                }
            }
        };
        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };
        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.getMyRecords(successListener, errorListener);
    }

    private void uploadDocument(final String params) {
        showProgressDialog();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                hideProgressDialog();
                fetchMyRecords();
//                final Gson gson = new Gson();
//                final Message message = gson.fromJson(response.toString(), Message.class);
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                } catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(getProgressDialog(), getActivity());
                }
            }
        };

//        final CustomerDocument customerDocument = new CustomerDocument();
//        customerDocument.documentTypeId = 2;
//        customerDocument.fileName = fileName;
//        customerDocument.document = new String("Hello");
//
//        final Gson gson = new Gson();
//        final String params = gson.toJson(customerDocument);
//
//        Log.d("Params", "Params : " + params);

        final MessageCenter messageCenter = new MessageCenter(getActivity(), getProgressDialog());
        messageCenter.uploadDocument(successListener, errorListener, params);
    }

    public void showChosserDialog() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Photo");
        builder.setCancelable(false);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    //cameraPlugIn.captureImage();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    PickImagePlugin.fileUri = cameraPlugIn.getOutputMediaFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, PickImagePlugin.fileUri);
                    startActivityForResult(intent, IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals("Choose from Library")) {
                    //cameraPlugIn.pickImage();
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            IntegerConstants.PICK_IMAGE_REQUEST_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == IntegerConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // successfully captured the image
                cameraPlugIn.handleCapturedImageRequest();
            }
        }
        if (requestCode == IntegerConstants.PICK_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cameraPlugIn.handlePickedImageRequest(data);
            }
        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }


        @Override
        protected String[] doInBackground(String... f_url) {
            int count;
            try {

                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                OutputStream output = new FileOutputStream("/sdcard/" + f_url[1]);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();

                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

//            return new String[] {f_url[0],f_url[1]};
            return f_url;
        }


        @Override
        protected void onPostExecute(String... file_url) {

            hideProgressDialog();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            if (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("jpeg") || MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("jpg") || MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("png") || MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("gif")) {
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/" + file_url[1]), "image/*");
                startActivity(intent);
            } else if (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("pdf")) {
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/" + file_url[1]), "application/pdf");
                startActivity(intent);
            } else if (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("doc") || (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("docx"))) {
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/" + file_url[1]), "application/msword");
                startActivity(intent);
            } else if (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("xls") || (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("xlsx"))) {
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/" + file_url[1]), "application/vnd.ms-excel");
                startActivity(intent);
            } else if (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("pptx") || (MdliveUtils.getExtention(file_url[0]).equalsIgnoreCase("ppt"))) {
                intent.setDataAndType(Uri.parse("file://" + "/sdcard/" + file_url[1]), "application/vnd.ms-powerpoint");
                startActivity(intent);
            } else {
                MdliveUtils.alert(getProgressDialog(), getActivity(), getString(R.string.mdl_no_compitable_app));
            }
        }
    }
}
