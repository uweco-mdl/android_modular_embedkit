package com.mdlive.embedkit.uilayer.messagecenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.messagecenter.adapter.RecordAdapter;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;
import com.mdlive.unifiedmiddleware.parentclasses.bean.request.CustomerDocument;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Message;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Records;
import com.mdlive.unifiedmiddleware.plugins.NetworkErrorListener;
import com.mdlive.unifiedmiddleware.plugins.NetworkSuccessListener;
import com.mdlive.unifiedmiddleware.services.messagecenter.MessageCenter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by dhiman_da on 6/24/2015.
 */
public class MessageMyRecordsFragment extends Fragment {
    private static final int PICK_PHOTO_INTENT = 0;
    private static final int TAKE_PHOTO_INTENT = 1;

    private ProgressDialog pDialog;

    private ListView mListView;
    private RecordAdapter mRecordAdapter;

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
        if (mListView != null) {
            mRecordAdapter = new RecordAdapter(view.getContext(), R.layout.adapter_record, android.R.id.text1);

            mListView.setAdapter(mRecordAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (getActivity() != null && getActivity() instanceof MessageCenterActivity) {
                        ((MessageCenterActivity) getActivity()).onRecordClicked(mRecordAdapter.getItem(i));
                    }
                }
            });
        }

        final Button button = (Button) view.findViewById(R.id.fragment_message_my_records_button_add_photo);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showChosserDialog();
                }
            });
        }
    }

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case TAKE_PHOTO_INTENT:
                if(resultCode == Activity.RESULT_OK){
                    final Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                    uploadDocument("" + System.currentTimeMillis() + ".jpeg", bytes.toByteArray());
                }

                break;
            case PICK_PHOTO_INTENT:
                if(resultCode == Activity.RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                        uploadDocument("" + System.currentTimeMillis() + ".jpeg", bytes.toByteArray());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fetchMyRecords() {
        pDialog.show();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();

                final Gson gson = new Gson();
                final Records records =  gson.fromJson(response.toString(), Records.class);

                if (mRecordAdapter != null) {
                    mRecordAdapter.addAll(records.records);
                    mRecordAdapter.notifyDataSetChanged();
                }
                Toast.makeText(getActivity(), records.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }
            }
        };
        final MessageCenter messageCenter = new MessageCenter(getActivity(), pDialog);
        messageCenter.getMyRecords(successListener, errorListener);
    }

    private void uploadDocument(final String fileName, final byte[] imageData) {
        pDialog.show();

        final NetworkSuccessListener<JSONObject> successListener = new NetworkSuccessListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.dismiss();

                final Gson gson = new Gson();
                final Message message =  gson.fromJson(response.toString(), Message.class);
                Toast.makeText(getActivity(), message.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        final NetworkErrorListener errorListener = new NetworkErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                try {
                    MdliveUtils.handelVolleyErrorResponse(getActivity(), error, null);
                }
                catch (Exception e) {
                    MdliveUtils.connectionTimeoutError(pDialog, getActivity());
                }

                final String errorResponse  = new String(error.networkResponse.data);
                Toast.makeText(getActivity(), "Error : " + errorResponse, Toast.LENGTH_SHORT).show();
                Log.e("MDLIVE ERROR", "Error : " + errorResponse);
            }
        };

        final CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.documentTypeId = 2;
        customerDocument.fileName = fileName;
        customerDocument.document = new String(imageData);

        final Gson gson = new Gson();
        final String params = gson.toJson(customerDocument);

        Log.d("Params", "Params : " + params);

        final MessageCenter messageCenter = new MessageCenter(getActivity(), pDialog);
        messageCenter.uploadDocument(successListener, errorListener, params);
    }

    public void showChosserDialog() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, TAKE_PHOTO_INTENT);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            PICK_PHOTO_INTENT);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
