package com.mdlive.embedkit.uilayer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mdlive.unifiedmiddleware.commonclasses.application.LocalizationSingleton;
import com.mdlive.unifiedmiddleware.commonclasses.utils.MdliveUtils;

/**
 * Created by dhiman_da on 8/5/2015.
 */
public class MDLiveBaseFragment extends Fragment {
    private ProgressDialog mProgressDialog;

    public MDLiveBaseFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocalizationSingleton.localiseLayout(view.getContext(), (ViewGroup) view);
        mProgressDialog = MdliveUtils.getFullScreenProgressDialog(getActivity());
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        hideProgressDialog();
    }

    public void logD(final String tag, final String message) {
        if (getActivity() != null) {
            Log.d(tag, message);
        }
    }

    public void logV(final String tag, final String message) {
        if (getActivity() != null) {
            Log.v(tag, message);
        }
    }

    public void logE(final String tag, final String message) {
        if (getActivity() != null) {
            Log.e(tag, message);
        }
    }

    public void showToast(final String message) {
        if (getActivity() != null) {
            /*Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();*/
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public void showToast(final int stringResourceId) {
        if (getActivity() != null) {
            /*Toast.makeText(getActivity(), getActivity().getString(stringResourceId), Toast.LENGTH_SHORT).show();*/
            Snackbar.make(getActivity().findViewById(android.R.id.content),
                    getString(stringResourceId),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    public void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
