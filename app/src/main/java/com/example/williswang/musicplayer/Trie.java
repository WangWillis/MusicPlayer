package com.example.williswang.musicplayer;

import java.lang.reflect.Array;
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
    private final static int NUM_CHARACTERS = 65; //number of characters recognized by structure

    //Node for the trie for Strings
    private class TrieNode <T>{
        public char letter;
        public T data; //holds a piece of data
        public HashMap<Character, TrieNode<T>> children; //holds the children of this node

        public TrieNode(){
            letter = 0;
            data = null;
            children = new HashMap<Character, TrieNode<T>>();
        }

        public TrieNode(char letter){
            this.letter = letter;
            this.data = null;
            children = new HashMap<Character, TrieNode<T>>();
        }
    }

    private TrieNode<T> head; //holds all possible starting TrieNodes
    private int numElements;

    public Trie(){
        head = new TrieNode<T>();
        numElements = 0;
    }

    //adding is not case sensitive
    public boolean add(String key, T data){
        key = key.toUpperCase().trim();
        TrieNode<T> curr = head; //hold the current node in the travel
        boolean newWord = false; //to see if should add node to arraylist

        for(int i = 0; i < key.length(); i++){
            //get index of letter
            char currLetter = key.charAt(i);

            //create a new node since new string
            if(curr.children.get(currLetter) == null) {
                TrieNode<T> nodeAdd = new TrieNode<T>(key.charAt(i)); //holds the node to add to
                                                                      //structure
                //link node
                curr.children.put(currLetter, nodeAdd);
            }

            //index all sub words too
            if(newWord) {
                add(key.substring(i), data);
                newWord = false;
            }else {
                //next word is a new word
                if (key.charAt(i) == ' ')
                    newWord = true;
            }

            curr = curr.children.get(currLetter);
        }
        curr.data = data;

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
            curr = curr.children.get(key.charAt(i));
        }

        //give back the starting location
        return curr;
    }

    private void getTreeData(TrieNode<T> node, ArrayList<T> list, HashSet<T> added){
        Iterator<Character> childs = node.children.keySet().iterator();
        //check if data valid
        if(node.data != null && !added.contains(node.data)) {
            list.add(node.data);
            added.add(node.data);
        }
        while(childs.hasNext()){
            getTreeData(node.children.get(childs.next()), list, added);
        }
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
        return getSearchTreeStart(key).data;
    }
}
