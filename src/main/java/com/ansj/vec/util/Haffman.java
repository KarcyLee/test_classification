package com.ansj.vec.util;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import com.ansj.vec.domain.HiddenNeuron;
import com.ansj.vec.domain.Neuron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 构建Haffman编码树
 * @author ansj
 *
 */
public class Haffman {
    private static Logger logger = LoggerFactory.getLogger(Haffman.class);
    private int layerSize;

    public Haffman(int layerSize) {
        this.layerSize = layerSize;
    }

    private TreeSet<Neuron> set = new TreeSet<Neuron>();

    public void make(Collection<Neuron> neurons) {
        try {
            set.addAll(neurons);
            while (set.size() > 1) {
                merger();
            }
        }catch (Exception e){
            logger.error("make() error ",e);
        }
    }


    private void merger() {
        // TODO Auto-generated method stub
        try {
            HiddenNeuron hn = new HiddenNeuron(layerSize);
            Neuron min1 = set.pollFirst();
            Neuron min2 = set.pollFirst();
            hn.freq = min1.freq + min2.freq;
            min1.parent = hn;
            min2.parent = hn;
            min1.code = 0;
            min2.code = 1;
            set.add(hn);
        }catch (Exception e){
            logger.error("merger() error ",e);
        }
    }
    
}
