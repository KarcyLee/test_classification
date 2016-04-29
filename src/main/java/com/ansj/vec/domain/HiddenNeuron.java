package com.ansj.vec.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiddenNeuron extends Neuron{
    private static Logger logger = LoggerFactory.getLogger(HiddenNeuron.class);
    public double[] syn1 ; //hidden->out
    
    public HiddenNeuron(int layerSize){
        try {
            syn1 = new double[layerSize];
        }catch (Exception e){
            logger.error("HiddenNeuron() error ",e);
        }
    }
    
}
