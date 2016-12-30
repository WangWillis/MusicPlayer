package com.example.williswang.musicplayer;

import android.media.MediaPlayer;

import java.util.ArrayList;

/**
 * Created by williswang on 12/24/16.
 */

public class MusicPlayer {
    private MediaPlayer player; //thing that plays the music
    private Folder<MusicFile> queue; //stream of music
    private int currentSong; //the current song loaded
    private boolean songLoaded; //tells if a song can be played or not
    private boolean songDone; //check to see if the song is finished

    public MusicPlayer(){
        player = new MediaPlayer();
        queue = null;
        currentSong = 0;
        songLoaded = false;
        songDone = false;

        player.setLooping(false);
    }

    //constructor for the music player
    public MusicPlayer(ArrayList<MusicFile> songs){
        player = new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextSong();
                songDone = true;
            }
        });
        //holds a queue of the songs
        queue = new Folder<MusicFile>("Queue", "", new ArrayList<MusicFile>(songs));
        currentSong = 0;
        songLoaded = false;
        songDone = false;

        player.setLooping(false);
    }

    private boolean startSong(){
        songLoaded = false;
        if(queue == null || queue.getSize() == 0)
            return false;

        try {
            player.reset();
            player.setDataSource(queue.getData(currentSong).getAbsolutePath());
            player.prepare();
            player.start();
            songDone = false;
        }catch(java.io.IOException e){
            return false;
        }
        songLoaded = true;
        return true;
    }

    public void newQueue(ArrayList<MusicFile> songs){
        ArrayList<MusicFile> newSongs = null;
        player.pause();

        if(songs != null)
            newSongs = new ArrayList<MusicFile>(songs);

        queue.setData(new ArrayList<MusicFile>(newSongs));
        currentSong = 0;
        songLoaded = false;
    }

    public void clearQueue(){
        player.stop();
        currentSong = 0;
        songLoaded = false;
        queue.clearData();
    }

    public void addSong(MusicFile song){
        queue.add(song);
    }

    public ArrayList<MusicFile> shuffle(){
        if(queue == null || queue.getSize() == 0)
            return null;

        player.stop();
        queue.shuffle();
        currentSong = 0;
        startSong();
        return queue.getAllData();
    }

    public void resumeSong(){
        if(songLoaded)
            player.start();
    }

    public void pauseSong(){
        if(songLoaded)
            player.pause();
    }

    public void seek(double percentage){
        if(percentage > 1 || !songLoaded)
            return;
        player.seekTo((int)(player.getDuration()*percentage));
    }

    public boolean changeSong(int index){
        if(queue == null || queue.getSize() == 0)
            return false;

        currentSong = index%queue.getSize();
        return startSong();
    }

    public boolean nextSong(){
        if(queue == null || queue.getSize() == 0)
            return false;

        currentSong = (currentSong+1)%queue.getSize();
        return startSong();
    }

    public boolean previousSong(){
        if(queue == null || queue.getSize() == 0)
            return false;

        //if playing for less than 5 seconds go to previous song
        if(player.getCurrentPosition() <= 5000)
            currentSong = (queue.getSize() + currentSong - 1)%queue.getSize();
        return startSong();
    }

    public MusicFile getCurrSong(){
        return queue.getData(currentSong);
    }

    public double getSongProgress(){
        if(!songLoaded)
            return 0;
        return ((double)player.getCurrentPosition()/player.getDuration());
    }

    public int getPosition(){
        return player.getCurrentPosition();
    }

    public int getTotalTime(){
        return player.getDuration();
    }

    public boolean isSongDone(){
        return songDone;
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }
}
