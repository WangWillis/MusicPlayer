package com.example.williswang.musicplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    //private final File MUSIC_PATH = this.getExternalFilesDir(Environment.DIRECTORY_MUSIC);

    //backend classes
    private SongManager songFiles;
    private MusicPlayer player;
    private android.os.Handler myHandler = new android.os.Handler();

    //front end objects
    private SeekBar progressBar;
    private ListView songList;
    private EditText searchText;
    private Button playPause, next, previous;
    private TextView currTime, totalTime, songName;

    private boolean touching = false; //used to track if progressbar change from seek or not

    private AdapterView.OnItemClickListener songListClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            player.changeSong(position);
            updateUI();
        }
    };

    private Runnable updateSongTime = new Runnable() {
        @Override
        public void run() {
            currTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) player.getPosition()),
                    TimeUnit.MILLISECONDS.toSeconds((long) player.getPosition()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) player.getPosition()))));
            if(!touching)
                progressBar.setProgress((int)(player.getSongProgress()*progressBar.getMax()));

            if(player.isSongDone()){
                songName.setText(player.getCurrSong().getSongName());
                totalTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) player.getTotalTime()),
                        TimeUnit.MILLISECONDS.toSeconds((long) player.getTotalTime())
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) player.getTotalTime()))));
            }

            //recursively call this object again after a time
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //song playing/managing thing
        songFiles = new SongManager(Environment.getExternalStorageDirectory().toString()+"/Music");
        //UI object initialization
        progressBar = (SeekBar)findViewById(R.id.seekBar);
        songList = (ListView)findViewById(R.id.songList);
        songList.setAdapter(getList(songFiles.getCurrFolder().getAllData()));
        songList.setOnItemClickListener(songListClickHandler);
        searchText = (EditText)findViewById(R.id.searchBar);
        playPause = (Button)findViewById(R.id.playPause);
        next = (Button)findViewById(R.id.nextSong);
        previous = (Button)findViewById(R.id.previousSong);
        currTime = (TextView)findViewById(R.id.currTime);
        totalTime = (TextView)findViewById(R.id.totalTime);
        songName = (TextView)findViewById(R.id.songName);
        player = new MusicPlayer(songFiles.getCurrFolder().getAllData());
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(touching)
                    player.seek(((double)progress)/seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                player.pauseSong();
                touching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.resumeSong();
                touching = false;
            }
        });
        
        //start with a default song
        player.changeSong(0);
        player.pauseSong();
        updateUI();

        //start the updating of ui
        myHandler.postDelayed(updateSongTime, 100);
    }

    private ArrayAdapter<String> getList(ArrayList<MusicFile> songList){
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.list_item_wrapper, R.id.listItemView);

        //add all song names to array
        for(int i = 0; i < songList.size(); i++)
            arr.add(songList.get(i).getSongName());

        return arr;
    }

    private void updateUI(){
        songName.setText(player.getCurrSong().getSongName());
        totalTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) player.getTotalTime()),
                TimeUnit.MILLISECONDS.toSeconds((long) player.getTotalTime())
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                        toMinutes((long) player.getTotalTime()))));
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
