package com.example.nishtha.capstone.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nishtha.capstone.TopSongsFragment;
import com.example.nishtha.capstone.R;
import com.squareup.picasso.Picasso;


public class GridAdapter extends CursorAdapter {

    public GridAdapter(Context context ,Cursor cursor , int flags){
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String url=cursor.getString(TopSongsFragment.COL_IMAGE_URL);
        ImageView image=(ImageView)view.findViewById(R.id.image1);
        Picasso.with(context).load(url).resize(500, 700).onlyScaleDown().into(image);
        TextView songName = (TextView)view.findViewById(R.id.songName);
        songName.setText(cursor.getString(TopSongsFragment.COL_TITLE));
        ((TextView) view.findViewById(R.id.singer)).setText(cursor.getString(TopSongsFragment.COL_ARTIST));
    }
}
