package com.sohu.text.Classification.impl;

import meka.classifiers.multilabel.incremental.IncrementalEvaluation;



import com.sohu.text.Classification.Classify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by pengli211286 on 2016/4/22.
 */
public class ClassifyByMEKA implements Classify {


    private static Logger logger = LoggerFactory.getLogger(ClassifyByMEKA.class);


    ////***************接口部分**********************////
    /////************TRAIN*********************////
    public void trainSingleClassifierWithLabel(float[][] features,float[] labels){

    }
    public void trainSingleClassifierWithoutLabel(float[][] features){

    }

    public void trainMultiClassifierWithLabel(float[][] features,float[] labels){

    }
    public void trainMultiClassifierWithoutLabel(float[][] features){

    }


    /////**************TEST**********************////
    public int getCategoryID(float [] features){

        return -1;
    }
    public String getCategoryName(float [] features){

        return "";
    }
}
