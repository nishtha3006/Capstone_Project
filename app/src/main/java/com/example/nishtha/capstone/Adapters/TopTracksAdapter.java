package com.example.nishtha.capstone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nishtha.capstone.R;
import com.example.nishtha.capstone.Song;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TopTracksAdapter extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;

    private List<Song> mObjects;

    public TopTracksAdapter(Context context, List<Song> objects) {
        mContext = context;
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mObjects = objects;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Song getItem(int position) {
        return mObjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null && mInflater!=null) {
            view = mInflater.inflate(R.layout.item_trailer, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Song song = getItem(position);
        viewHolder = (ViewHolder) view.getTag();
        String yt_thumbnail_url = song.getImage_url();
        Picasso.with(getContext()).load(yt_thumbnail_url).into(viewHolder.imageView);
        viewHolder.songName.setText(song.getTitle());
        return view;
    }

    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView songName;
        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
            songName = (TextView) view.findViewById(R.id.songName);
        }
    }
}
