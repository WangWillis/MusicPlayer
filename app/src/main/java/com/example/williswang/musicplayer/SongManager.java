package com.example.williswang.musicplayer;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by williswang on 12/25/16.
 */

public class SongManager {
    public final static int ALL_SONG_FOLDER = 0;

    private ArrayList<Folder<MusicFile>> allPlaylists; //holds all available folders with music
    private Folder<MusicFile> currSongFolder; //current folder being viewed
    private Trie<MusicFile> allSongSearch; //holds songs for easy search

    public SongManager(String musicPath){
        //set up all available songs
        //starts off holding all the songs
        currSongFolder = new Folder<MusicFile>("All Songs", "All songs in Music Folder");
        allSongSearch = new Trie<MusicFile>();
        parseFolder(new File(musicPath));
        currSongFolder.sort();
        allPlaylists = new ArrayList<Folder<MusicFile>>();

        //add all available playlists
        allPlaylists.add(ALL_SONG_FOLDER, currSongFolder);
    }

    //get all the songs from an inputted song folder
    private void parseFolder(File currFolder){
        File[] children = currFolder.listFiles();

        //loop through all the files
        for(int i = 0; i < children.length; i++) {
            if(children[i].isDirectory()){
                parseFolder(children[i]);
            }else{
                //not a directory so add file
                MusicFile song = new MusicFile(children[i]);
                currSongFolder.add(song);
                allSongSearch.add(song.getSongName(), song);
            }
        }
    }

    //used to change playlists
    public boolean setCurrSongFolder(int index){
        //check for invalid indexs
        if(index < 0 || index >= allPlaylists.size())
            return false;

        currSongFolder = allPlaylists.get(index);
        return true;
    }

    //used to get the current folder
    public Folder<MusicFile> getCurrFolder(){
        return currSongFolder;
    }

    public MusicFile getSong(int index){
        return currSongFolder.getData(index);
    }

    public ArrayList<MusicFile> search(String key){
        return allSongSearch.search(key);
    }
}
