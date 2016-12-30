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
        if(data == null)
             return 0;

        return data.size();
    }

    public boolean sort(){
        if(data == null)
            return false;

        Collections.sort(data, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return true;
    }

    public boolean add(T info){
        if(data == null)
            return false;
        data.add(info);
        return true;
    }

    public boolean shuffle(){
        if(data == null)
            return false;

        Collections.shuffle(data);

        return true;
    }

    public T getData(int index){
        //check if valid index
        if(index < 0 || index >= data.size())
            return null;

        //give back the data
        return data.get(index);
    }

    public void clearData(){
        if(data == null)
            return;
        data.clear();
    }

    public ArrayList<T> getAllData(){
        return data;
    }
}
