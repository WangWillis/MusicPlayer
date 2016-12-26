package com.example.williswang.musicplayer;

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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String MUSIC_PATH = "/storage/sdcard0/Music";

    //backend classes
    private SongManager songFiles;
    private MusicPlayer player;
    //front end objects
    private SeekBar progressBar;
    private ListView songList;
    private EditText searchText;
    private Button playPause, next, previous;
    private TextView currTime, totalTime, songName;

    private AdapterView.OnItemClickListener songListClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            player.changeSong(position);
            songName.setText(player.getCurrSong().getSongName());
            totalTime.setText(String.format(Locale.getDefault(), "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) player.getTotalTime()),
                    TimeUnit.MILLISECONDS.toSeconds((long) player.getTotalTime())
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) player.getTotalTime()))));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //song playing/managing thing
        songFiles = new SongManager(MUSIC_PATH);
        player = new MusicPlayer(songFiles.getCurrFolder().getAllData());
        //UI object initialization
        progressBar = (SeekBar)findViewById(R.id.seekBar);
        //set what happens when seek bar changed
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                player.seek(((double)progress)/seekBar.getMax());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //set the list of songs to all songs
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

    }

    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            currTime.setText(String.format(Locale.getDefault(), "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) player.getPosition()),
                    TimeUnit.MILLISECONDS.toSeconds((long) player.getPosition()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) player.getPosition()))));
            if(player.continueQueue())
                songName.setText(player.getCurrSong().getSongName());
            progressBar.setProgress((int)(player.getSongProgress()*progressBar.getMax()));
        }
    };

    private ArrayAdapter<String> getList(ArrayList<MusicFile> songList){
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.activity_main);

        //add all song names to array
        for(int i = 0; i < songList.size(); i++)
            arr.add(songList.get(i).getSongName());

        return arr;
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
