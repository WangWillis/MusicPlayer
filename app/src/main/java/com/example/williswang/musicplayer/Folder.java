package com.example.williswang.musicplayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by williswang on 12/23/16.
 */

public class Folder<T> {

    //private data fields
    private String name; //name of the folder
    private String description; //information about the folder
    private ArrayList<T> data;

    public Folder(String name, String description){
        this.name = name;
        this.description = description;
        data = new ArrayList<T>();
    }

    public Folder(String name, String description, ArrayList<T> data){
        this.name = name;
        this.description = description;
        this.data = data;
    }

    public boolean setData(ArrayList<T> data){
        this.data = data;
        return true;
    }

    public int getSize(){
        return data.size();
    }

    public void sort(){
        Collections.sort(data, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
    }

    public void add(T info){
        data.add(info);
    }

    public void shuffle(){
        Collections.shuffle(data);
    }

    public T getData(int index){
        //check if valid index
        if(index < 0 || index >= data.size())
            return null;

        //give back the data
        return data.get(index);
    }

    public void clearData(){
        data.clear();
    }

    public ArrayList<T> getAllData(){
        return data;
    }
}
