package com.mdlive.embedkit.uilayer.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.embedkit.uilayer.MDLiveBaseFragment;

/**
 * Created by venkataraman_r on 8/28/2015.
 */
public class MDLiveWaitingRoomFragment extends MDLiveBaseFragment {
    private static final String HEADER = "header";
    private static final String COLOR = "color";
    private static final String BODY = "body";

    public static MDLiveWaitingRoomFragment newInstance(final String header, final int headerColor, final String bodyText) {
        final MDLiveWaitingRoomFragment fragment = new MDLiveWaitingRoomFragment();

        final Bundle args = new Bundle();
        args.putString(HEADER, header);
        args.putInt(COLOR, headerColor);
        args.putString(BODY, bodyText);

        fragment.setArguments(args);

        return  fragment;
    }

    public MDLiveWaitingRoomFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_room_text, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.header_text_view)).setText(getArguments().getString(HEADER));
        ((TextView) view.findViewById(R.id.header_text_view)).setBackgroundColor(getArguments().getInt(COLOR));
        ((TextView) view.findViewById(R.id.body_text_view)).setBackgroundColor(getArguments().getInt(BODY));

        ((TextView) view.findViewById(R.id.body_text_view)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        ((TextView) view.findViewById(R.id.body_text_view)).setMovementMethod(new ScrollingMovementMethod());
    }
}
