package com.example.nishtha.capstone;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


public class Song implements Parcelable {
    String title,artist,image_url;
    String song_url;
    public Song(){

    }
    public Song(Cursor cursor){
        this.title=cursor.getString(TopSongsFragment.COL_TITLE);
        this.artist = cursor.getString(TopSongsFragment.COL_ARTIST);
        this.image_url = cursor.getString(TopSongsFragment.COL_IMAGE_URL);
    }
    Song(Parcel in){
        String[] data=new String[3];
        in.readStringArray(data);
        this.title=data[0];
        this.artist=data[1];
        this.image_url=data[2];
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.title,this.artist,this.image_url
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getTitle() {
        return title;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
