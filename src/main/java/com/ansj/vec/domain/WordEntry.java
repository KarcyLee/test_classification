package com.ansj.vec.domain;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordEntry implements Comparable<WordEntry> {
    private static Logger logger = LoggerFactory.getLogger(WordEntry.class);
    public String name;
    public float score;

    public WordEntry(String name, float score) {
        this.name = name;
        this.score = score;
    }

    //@Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name + "\t" + score;
    }

    //@Override
    public int compareTo(WordEntry o) {
        // TODO Auto-generated method stub
        try {
            if (this.score < o.score) {
                return 1;
            } else {
                return -1;
            }
        }catch (Exception e){
            logger.error("compareTo() error ",e );
            return 0;
        }
    }

}