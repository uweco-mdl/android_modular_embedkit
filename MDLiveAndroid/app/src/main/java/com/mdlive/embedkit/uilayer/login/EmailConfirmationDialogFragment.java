package com.mdlive.embedkit.uilayer.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

import com.mdlive.embedkit.R;

import org.xml.sax.XMLReader;

import java.util.Stack;

/**
 * Created by dhiman_da on 8/20/2015.
 */
public class EmailConfirmationDialogFragment extends DialogFragment {
    private OnEmailConfirmationClicked mOnEmailConfirmationClicked;

    public EmailConfirmationDialogFragment() {
        super();
    }

    public static EmailConfirmationDialogFragment newInstance() {
        final EmailConfirmationDialogFragment fragment = new EmailConfirmationDialogFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mOnEmailConfirmationClicked = (OnEmailConfirmationClicked) activity;
        } catch (ClassCastException cce) {

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Title
                .setTitle(getString(R.string.mdl_email_confirmation))
                .setView(R.layout.email_confirmation_layout)
                        // Set Dialog Message
                        //.setMessage(Html.fromHtml(getString(R.string.email_confirmation_detail_text), null, new HTMLTagHandler()))
                        // Positive button
                .setPositiveButton(getString(R.string.mdl_resend_email_confirmation), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnEmailConfirmationClicked != null) {
                            mOnEmailConfirmationClicked.onEmailConfirmationClicked();
                        }

                        dismiss();
                    }
                })
                        // Negative Button
                .setNegativeButton(getString(R.string.mdl_dismiss), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnEmailConfirmationClicked = null;
    }

    public interface OnEmailConfirmationClicked {
        void onEmailConfirmationClicked();
    }

    public static class HTMLTagHandler implements Html.TagHandler {
        /**
         * List indentation in pixels. Nested lists use multiple of this.
         */
        private static final int indent = 10;
        private static final int listItemIndent = indent * 2;
        private static final BulletSpan bullet = new BulletSpan(indent);
        /**
         * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
         * and on top of Stack is the most nested list
         */
        Stack<String> lists = new Stack<String>();
        /**
         * Tracks indexes of ordered lists so that after a nested list ends
         * we can continue with correct index of outer list
         */
        Stack<Integer> olNextIndex = new Stack<Integer>();

        private static void start(Editable text, Object mark) {
            int len = text.length();
            text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
        }

        private static void end(Editable text, Class<?> kind, Object... replaces) {
            int len = text.length();
            Object obj = getLast(text, kind);
            int where = text.getSpanStart(obj);
            text.removeSpan(obj);
            if (where != len) {
                for (Object replace : replaces) {
                    text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return;
        }

        private static Object getLast(Spanned text, Class<?> kind) {
        /*
		 * This knows that the last returned object from getSpans()
		 * will be the most recently added.
		 */
            Object[] objs = text.getSpans(0, text.length(), kind);
            if (objs.length == 0) {
                return null;
            }
            return objs[objs.length - 1];
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("ul")) {
                if (opening) {
                    lists.push(tag);
                } else {
                    lists.pop();
                }
            } else if (tag.equalsIgnoreCase("ol")) {
                if (opening) {
                    lists.push(tag);
                    olNextIndex.push(1).toString();
                } else {
                    lists.pop();
                    olNextIndex.pop().toString();
                }
            } else if (tag.equalsIgnoreCase("li")) {
                if (opening) {
                    if (output.length() > 0 && output.charAt(output.length() - 1) != '\n') {
                        output.append("\n");
                    }
                    String parentList = lists.peek();
                    if (parentList.equalsIgnoreCase("ol")) {
                        start(output, new Ol());
                        output.append(olNextIndex.peek().toString() + ". ");
                        olNextIndex.push(olNextIndex.pop() + 1);
                    } else if (parentList.equalsIgnoreCase("ul")) {
                        start(output, new Ul());
                    }
                } else {
                    if (lists.peek().equalsIgnoreCase("ul")) {
                        if (output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
                        int bulletMargin = indent;
                        if (lists.size() > 1) {
                            bulletMargin = indent - bullet.getLeadingMargin(true);
                            if (lists.size() > 2) {
                                // This get's more complicated when we add a LeadingMarginSpan into the same line:
                                // we have also counter it's effect to BulletSpan
                                bulletMargin -= (lists.size() - 2) * listItemIndent;
                            }
                        }
                        BulletSpan newBullet = new BulletSpan(bulletMargin);
                        end(output,
                                Ul.class,
                                new LeadingMarginSpan.Standard(listItemIndent * (lists.size() - 1)),
                                newBullet);
                    } else if (lists.peek().equalsIgnoreCase("ol")) {
                        if (output.charAt(output.length() - 1) != '\n') {
                            output.append("\n");
                        }
                        int numberMargin = listItemIndent * (lists.size() - 1);
                        if (lists.size() > 2) {
                            // Same as in ordered lists: counter the effect of nested Spans
                            numberMargin -= (lists.size() - 2) * listItemIndent;
                        }
                        end(output,
                                Ol.class,
                                new LeadingMarginSpan.Standard(numberMargin));
                    }
                }
            } else {
                if (opening) Log.d("TagHandler", "Found an unsupported tag " + tag);
            }
        }

        private static class Ul {
        }

        private static class Ol {
        }
    }
}
