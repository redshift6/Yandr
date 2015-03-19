package com.k7m.yandr;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ColourAdapter extends BaseAdapter {

    private Context mContext;

    public ColourAdapter(Context context) {
        mContext = context;
    }
    public ColourAdapter(Context context, String[] colours) {
        mContext = context;
        mDiceColoursArray = colours;
        usingList = false;
    }
    public ColourAdapter(Context context, List<String> colours) {
        mContext = context;
        mDiceColoursList = colours;
        usingList = true;
    }
    public int getCount() {
        if (usingList) {
            return mDiceColoursList.size();
        }
        return mDiceColoursArray.length;
    }

    public Object getItem(int position) {
        if (usingList) {
            return mDiceColoursList.get(position);
        }
        return mDiceColoursArray[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout;
        ImageView imageview;
        TextView textView;
        if (convertView == null) {  // if it's not recycled(ie. null/incomplete), initialise some attributes
            layout = new View(mContext);
        } else {
            layout = convertView;
        }
        layout = inflater.inflate(R.layout.colourlistlayout, null, true);
        textView = (TextView)layout.findViewById(R.id.colour_name);
        imageview = (ImageView)layout.findViewById(R.id.colour_preview);
        if (usingList) {
            textView.setText(mDiceColoursList.get(position).toUpperCase());
            imageview.setImageDrawable(new ColorDrawable(Color.parseColor(mDiceColoursList.get(position))));
        } else {
            textView.setText(mDiceColoursArray[position].toUpperCase());
            imageview.setImageDrawable(new ColorDrawable(Color.parseColor(mDiceColoursArray[position])));
        }

        return layout;

    }

    private String[] mDiceColoursArray;
    private List<String> mDiceColoursList;
    private boolean usingList;
}
