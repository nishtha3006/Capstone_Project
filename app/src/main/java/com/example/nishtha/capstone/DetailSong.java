package com.example.nishtha.capstone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class DetailSong extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_song);
        if(savedInstanceState==null){
            DetailSongFragment fragment=new DetailSongFragment();
            Bundle temp=new Bundle();
            temp.putParcelable("song",getIntent().getParcelableExtra("song"));
            fragment.setArguments(temp);
            getSupportFragmentManager().beginTransaction().
                    add(R.id.detail_layout,fragment).commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

}
