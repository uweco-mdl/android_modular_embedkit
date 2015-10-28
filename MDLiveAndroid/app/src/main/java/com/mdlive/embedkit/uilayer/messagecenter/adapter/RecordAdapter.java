package com.mdlive.embedkit.uilayer.messagecenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mdlive.embedkit.R;
import com.mdlive.unifiedmiddleware.parentclasses.bean.response.Record;

/**
 * Created by dhiman_da on 6/28/2015.
 */
public class RecordAdapter extends ArrayAdapter<Record> {
    public RecordAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.adapter_record, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.adapter_record_image_view);
            viewHolder.mTextViewTop = (TextView) convertView.findViewById(R.id.adapter_record_top_text_view);
            viewHolder.mTextViewBottom = (TextView) convertView.findViewById(R.id.adapter_record_bottom_text_view);

//        String extensionRemoved = MdliveUtils.getExtention(getItem(position).downloadLink);


            /*if(extensionRemoved.equalsIgnoreCase("jpeg") || extensionRemoved.equalsIgnoreCase("jpg"))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_jpg_format);
            }
            else if(extensionRemoved.equalsIgnoreCase("pdf"))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_pdf_format);
            }
            else if(extensionRemoved.equalsIgnoreCase("png"))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_png_format);
            }
            else if(extensionRemoved.equalsIgnoreCase("doc")||(extensionRemoved.equalsIgnoreCase("docx") ))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_word_format);
            }
            else if(extensionRemoved.equalsIgnoreCase("xls") ||(extensionRemoved.equalsIgnoreCase("xlsx") ))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_xl_format);
            }
            else if(extensionRemoved.equalsIgnoreCase("pptx") ||(extensionRemoved.equalsIgnoreCase("ppt") ))
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_pp_format);
            }
            else
            {
                viewHolder.mImageView.setImageResource(R.drawable.ic_empty_format);
            }
*/

        viewHolder.mTextViewTop.setText(getItem(position).docName);
        viewHolder.mTextViewBottom.setText(" by " + getItem(position).uploadedBy + " on " +getItem(position).uploadedAt);

        return convertView;
    }

    private static class ViewHolder {
        ImageView mImageView;
        TextView mTextViewTop;
        TextView mTextViewBottom;
    }

//    public void setMaxWidthForLeftText(final View parentView, final TextView leftTextView,
//                                       final TextView rightTextView) {
//        parentView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                int rWidth, aWidth, bWidth;
//                rWidth = parentView.getWidth();
//                leftTextView.measure(0, 0);
//                aWidth = leftTextView.getMeasuredWidth();
//                rightTextView.measure(0, 0);
//                bWidth = rightTextView.getMeasuredWidth();
//                int aMarginEnd = 0, bMarginStart = 0;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    aMarginEnd = (int) (((LinearLayout.LayoutParams)leftTextView.getLayoutParams()).getMarginEnd() * context.getResources().getDisplayMetrics().density);
//                    bMarginStart = (int) (((LinearLayout.LayoutParams)rightTextView.getLayoutParams()).getMarginStart() * context.getResources().getDisplayMetrics().density);
//                } else {
//                    aMarginEnd = 10;
//                    bMarginStart = 10;
//                }
//                leftTextView.setMaxWidth(rWidth - (bWidth + aMarginEnd + bMarginStart));
//                leftTextView.invalidate();
//                rightTextView.invalidate();
//                parentView.invalidate();
//            }
//        }, 50);
//    }
}
