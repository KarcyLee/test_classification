package com.ansj.vec.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Neuron implements Comparable<Neuron> {
    private static Logger logger = LoggerFactory.getLogger(Neuron.class);
    public int freq;
    public Neuron parent;
    public int code;
    
    //@Override
    public int compareTo(Neuron o) {
        // TODO Auto-generated method stub
        try {
            if (this.freq > o.freq) {
                return 1;
            } else {
                return -1;
            }
        }catch (Exception e){
            logger.error("compareTo() error ",e);
            return 0;
        }
    }

}
