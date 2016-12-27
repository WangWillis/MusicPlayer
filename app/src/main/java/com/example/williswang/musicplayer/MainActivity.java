package com.example.williswang.musicplayer;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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

    private ArrayList<MusicFile> currListViewList;
    private boolean touching = false; //used to track if progressbar change from seek or not
    private boolean changeQueue = false;

    //used to select and play songs from the list view
    private AdapterView.OnItemClickListener songListClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //change the queue if a song is selected from search results
            if(changeQueue){
                player.newQueue(currListViewList);
                changeQueue = false;
            }

            //check if shuffle pressed if not play the songs
            if(position == 0){
                currListViewList = player.shuffle();
                songList.setAdapter(getList(currListViewList));
            }else{
                player.changeSong(position - 1);
            }
            updateUI();
        }
    };

    //for updating the song time and seek bar and auto continue to next song
    private Runnable updateSongTime = new Runnable() {
        @Override
        public void run() {
            //update song time
            currTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) player.getPosition()),
                    TimeUnit.MILLISECONDS.toSeconds((long) player.getPosition()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) player.getPosition()))));

            //update seekbar if not seeking
            if(!touching)
                progressBar.setProgress((int)(player.getSongProgress()*progressBar.getMax()));

            //update the ui if song has been completed
            if(player.isSongDone())
                updateUI();

            //recursively call this object again after a time
            myHandler.postDelayed(this, 100);
        }
    };

    //used to search for songs
    private TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            ArrayList<MusicFile> searchResults = null; //holds the results of search

            //get search results or return back to current list
            if(v.getText().toString().equals(""))
                searchResults = songFiles.getCurrFolder().getAllData();
            else
                searchResults = songFiles.search(v.getText().toString());

            if(searchResults == null)
                return false;

            songList.setAdapter(getList(searchResults));
            //set the next queue to put if search results are pressed
            currListViewList = searchResults;
            changeQueue = true;
            return true;
        }
    };

    //used to seek the song
    private SeekBar.OnSeekBarChangeListener seekSong = new SeekBar.OnSeekBarChangeListener() {
        //move song to correct position
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(touching)
                player.seek(((double)progress)/seekBar.getMax());
        }

        //check to see if seekbar is being changed by user
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            player.pauseSong();
            touching = true;
        }

        //seekbar is not being changed by user
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            player.resumeSong();
            touching = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //song playing/managing thing
        songFiles = new SongManager(Environment.getExternalStorageDirectory().toString()+"/Music");
        player = new MusicPlayer(songFiles.getCurrFolder().getAllData());
        currListViewList = songFiles.getCurrFolder().getAllData();

        //UI object initialization
        songList = (ListView)findViewById(R.id.songList);
        songList.setAdapter(getList(songFiles.getCurrFolder().getAllData()));
        songList.setOnItemClickListener(songListClickHandler);
        progressBar = (SeekBar)findViewById(R.id.seekBar);
        progressBar.setOnSeekBarChangeListener(seekSong);
        searchText = (EditText)findViewById(R.id.searchBar);
        searchText.setOnEditorActionListener(searchListener);
        playPause = (Button)findViewById(R.id.playPause);
        next = (Button)findViewById(R.id.nextSong);
        previous = (Button)findViewById(R.id.previousSong);
        currTime = (TextView)findViewById(R.id.currTime);
        totalTime = (TextView)findViewById(R.id.totalTime);
        songName = (TextView)findViewById(R.id.songName);

        //setting listeners for the buttons
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player.isPlaying())
                    player.pauseSong();
                else if(!touching)
                    player.resumeSong();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.nextSong();
                updateUI();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.previousSong();
                updateUI();
            }
        });

        //start with a default song
        player.changeSong(0);
        player.pauseSong();
        updateUI();

        //start the updating of ui
        myHandler.postDelayed(updateSongTime, 100);
    }

    //updates the total song time and song name
    private void updateUI(){
        songName.setText(player.getCurrSong().getSongName());
        totalTime.setText(String.format(Locale.getDefault(), "%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) player.getTotalTime()),
                TimeUnit.MILLISECONDS.toSeconds((long) player.getTotalTime())
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                        toMinutes((long) player.getTotalTime()))));
    }

    //turns an array list into and array adapter
    private ArrayAdapter<String> getList(ArrayList<MusicFile> songList){
        ArrayAdapter<String> arr = new ArrayAdapter<String>(this, R.layout.list_item_wrapper, R.id.listItemView);

        //add the shuffle option
        arr.add("Shuffle");

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
