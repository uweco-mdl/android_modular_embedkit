package com.mdlive.messages.messagecenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;
import com.mdlive.messages.R;

/**
 * Created by dhiman_da on 8/18/2015.
 */
public class EmptyMessageFragment extends MDLiveBaseFragment {
    private static final String DRAWABLE_RESOURCE = "drawable_resource";
    private static final String STRING_RESOURCE_HEADER = "string_resource_header";
    private static final String STRING_RESOURCE_DETAILS = "string_resource_details";

    public static EmptyMessageFragment newInstance(final int drawableResource, final int headerStringResource, final int detailsStringResource) {
        final Bundle args = new Bundle();
        args.putInt(DRAWABLE_RESOURCE, drawableResource);
        args.putInt(STRING_RESOURCE_HEADER, headerStringResource);
        args.putInt(STRING_RESOURCE_DETAILS, detailsStringResource);

        final EmptyMessageFragment fragment = new EmptyMessageFragment();
        return fragment;
    }

    public EmptyMessageFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message_center_empty, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((ImageView) view.findViewById(R.id.fragment_message_center_empty_image_view)).setImageResource(getArguments().getInt(DRAWABLE_RESOURCE));
        ((TextView) view.findViewById(R.id.fragment_message_center_empty_header_text_view)).setText(getArguments().getInt(STRING_RESOURCE_HEADER));
        ((TextView) view.findViewById(R.id.fragment_message_center_empty_details_text_view)).setText(getArguments().getInt(STRING_RESOURCE_DETAILS));
    }
}
