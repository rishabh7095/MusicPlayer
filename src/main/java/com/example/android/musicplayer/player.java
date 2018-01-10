package com.example.android.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.util.ArrayList;

public class player extends AppCompatActivity implements View.OnClickListener{

    static MediaPlayer mp;
    ArrayList<File> mySongs;
    int position;
    SeekBar sb;
    Thread updateseekbar;
    Button prev,fb,play,ff,next;
    Uri u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        play=(Button)findViewById(R.id.play);
        prev=(Button)findViewById(R.id.prev);
        fb=(Button)findViewById(R.id.fb);
        ff=(Button)findViewById(R.id.ff);
        next=(Button)findViewById(R.id.next);

        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        ff.setOnClickListener(this);
        fb.setOnClickListener(this);
        next.setOnClickListener(this);

        sb=(SeekBar)findViewById(R.id.seekBar);
        updateseekbar = new Thread(){
            @Override
            public void run(){
                int totalDuration=mp.getDuration();
                int currentPosition=0;
                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition=mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                //super.run();
            }
        };
        if(mp!=null){
            mp.stop();
            mp.release();
        }

        Intent i=getIntent();
        Bundle b=i.getExtras();
        mySongs=(ArrayList)b.getParcelableArrayList("songlist");
        position=b.getInt("pos", 0);

        Uri u=Uri.parse(mySongs.get(position).toString());
        mp=MediaPlayer.create(getApplicationContext(),u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateseekbar.start();

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){
                mp.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id= view.getId();
        switch (id){
            case R.id.play:
                if(mp.isPlaying()){
                    play.setText(">");
                    mp.pause();
                }
                else {
                    play.setText("||");
                    mp.start();
                }
                break;
            case R.id.ff:
                mp.seekTo(mp.getCurrentPosition()+5000);
                break;
            case R.id.fb:
                mp.seekTo(mp.getCurrentPosition()-5000);
                break;
            case R.id.next:
                mp.stop();
                mp.release();
                position=(position+1)%mySongs.size();
                u=Uri.parse(mySongs.get(position).toString());
                mp=MediaPlayer.create(getApplicationContext(), u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
            case R.id.prev:
                mp.stop();
                mp.release();
                position=(position-1<0)?mySongs.size()-1:position-1;
                /*if(position-1<0){
                    position=mySongs.size()-1;
                }
                else{
                    position-=1;
                }*/
                u=Uri.parse(mySongs.get(position).toString());
                mp=MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                sb.setMax(mp.getDuration());
                break;
        }
    }
}
