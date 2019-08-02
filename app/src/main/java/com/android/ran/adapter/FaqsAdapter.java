package com.android.ran.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;

import com.android.ran.R;

import java.util.List;
import java.util.Map;

public class FaqsAdapter extends SimpleExpandableListAdapter {

    private Context mContext;

    public FaqsAdapter(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom,
                       int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout,
                       String[] childFrom, int[] childTo) {
        super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
        mContext = context;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.layout_faqs_question, null);
        }

        ImageView image = convertView.findViewById(R.id.faqs_expand);
        View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);

        if (isExpanded)
            image.setImageResource(R.drawable.ic_hide);
        else
            image.setImageResource(R.drawable.ic_show);

        return v;
    }
}