package com.example.williswang.musicplayer;

import java.io.File;

/**
 * Created by williswang on 12/24/16.
 */

public class MusicFile extends File{
    private String songName;

    public MusicFile(String path){
        super(path);
        songName = super.getName();
    }

    public MusicFile(File path){
        super(path.getAbsolutePath());
        songName = super.getName();
    }

    public String getSongName(){
        return songName;
    }

    public String toString(){
        return songName;
    }
}
