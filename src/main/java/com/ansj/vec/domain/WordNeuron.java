package com.ansj.vec.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WordNeuron extends Neuron {
    private static Logger logger = LoggerFactory.getLogger(WordEntry.class);
    public String name;
    public double[] syn0 = null; //input->hidden
    public List<Neuron> neurons = null;//路径神经元
    public int[] codeArr = null;

    public List<Neuron> makeNeurons() {
        try {
            if (neurons != null) {
                return neurons;
            }
            Neuron neuron = this;
            neurons = new LinkedList<Neuron>();
            while ((neuron = neuron.parent) != null) {
                neurons.add(neuron);
            }
            Collections.reverse(neurons);
            codeArr = new int[neurons.size()];

            for (int i = 1; i < neurons.size(); i++) {
                codeArr[i - 1] = neurons.get(i).code;
            }
            codeArr[codeArr.length - 1] = this.code;

            return neurons;
        }catch (Exception e){
            logger.error("makeNeurons() error ",e);
            return null;
        }
    }

    public WordNeuron(String name, int freq, int layerSize) {
        try {
            this.name = name;
            this.freq = freq;
            this.syn0 = new double[layerSize];
            Random random = new Random();
            for (int i = 0; i < syn0.length; i++) {
                syn0[i] = (random.nextDouble() - 0.5) / layerSize;
            }
        }catch (Exception e){
            logger.error("WordNeuron() error ",e);
        }
    }

}