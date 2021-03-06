package com.example.williswang.musicplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by williswang on 12/23/16.
 */

//data structure for search
//keys are strings
public class Trie <T>{
    //Node for the trie for Strings
    private class TrieNode <T>{
        public char letter;
        public HashMap<String, T> data; //holds a piece of data
        public HashMap<Character, TrieNode<T>> children; //holds the children of this node

        public TrieNode(){
            letter = 0;
            data = null;
            children = null;
        }

        public TrieNode(char letter){
            this.letter = letter;
            this.data = null;
            children = null;
        }

        public void addChild(char letter, TrieNode<T> node){
            if(children == null)
                children = new HashMap<Character, TrieNode<T>>();

            children.put(letter, node);
        }

        //for adding data to the node
        public void addData(String key, T data){
            if(this.data == null)
                this.data = new HashMap<String, T>();

            this.data.put(key, data);
        }

        public TrieNode<T> getChild(char letter){
            if(children == null)
                return null;
            return children.get(letter);
        }
    }

    private TrieNode<T> head; //holds all possible starting TrieNodes

    public Trie(){
        head = new TrieNode<T>();
    }

    public boolean add(String key, T data){
        key = key.toUpperCase().trim();
        return add(key, data, 0);
    }

    //adding is not case sensitive
    private boolean add(String key, T data, int start){
        TrieNode<T> curr = head; //hold the current node in the travel

        for(int i = start; i < key.length(); i++){
            //get index of letter
            char currLetter = key.charAt(i);

            //create a new node since new string
            if(curr.getChild(currLetter) == null) {
                TrieNode<T> nodeAdd = new TrieNode<T>(key.charAt(i)); //holds the node to add to
                                                                      //structure
                //link node
                curr.addChild(currLetter, nodeAdd);
            }

            curr = curr.getChild(currLetter);
        }

        curr.addData(key, data);

        if(key.indexOf(" ", start) != -1)
            return add(key, data, key.indexOf(" ", start)+1);

        return true;
    }

    //searches for a key and gets the subtree holding relevant data
    private TrieNode<T> getSearchTreeStart(String key){
        TrieNode<T> curr = head; // used to travel the tree

        for(int i = 0; i < key.length(); i++){
            //if the key is not found
            if(curr == null)
                break;

            //keep traveling
            curr = curr.getChild(key.charAt(i));
        }

        //give back the starting location
        return curr;
    }

    private void getTreeData(TrieNode<T> node, ArrayList<T> list, HashSet<T> added){
        Iterator<Character> childs = null;

        if(node.children != null)
            childs = node.children.keySet().iterator();

        //check if data valid
        if(node.data != null) {
            //add all the keys to the list
            Iterator<String> datas = node.data.keySet().iterator(); //go through all the key in set
            while(datas.hasNext()) {
                String key = datas.next(); //the key to the data
                T data = node.data.get(key); //the data to try to add

                //check if data has been added before
                if(!added.contains(data)) {
                    list.add(data);
                    added.add(data);
                }
            }
        }

        if(childs == null)
            return;

        while(childs.hasNext())
            getTreeData(node.getChild(childs.next()), list, added);

    }

    public ArrayList<T> search(String key){
        key = key.toUpperCase().trim();
        if(key.equals(""))
            return null;
        ArrayList<T> dataHolder = null; //holds the data to return
        TrieNode<T> subHead = getSearchTreeStart(key); //gets the start subtree search for
        HashSet<T> added = null;

        if(subHead == null)
            return null;

        dataHolder = new ArrayList<T>();
        added = new HashSet<T>();

        //get all data in subtree
        getTreeData(subHead, dataHolder, added);

        return dataHolder;
    }

    public T getData(String key){
        key = key.toUpperCase().trim();
        return getSearchTreeStart(key).data.get(key);
    }
}
