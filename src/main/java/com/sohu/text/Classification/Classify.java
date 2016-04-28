package com.sohu.text.Classification;

/**
 * Created by pengli211286 on 2016/4/22.
 */
public interface Classify {

    /////************TRAIN*********************////
    public void trainSingleClassifierWithLabel(float[][] features,float[] labels);
    public void trainSingleClassifierWithoutLabel(float[][] features);

    public void trainMultiClassifierWithLabel(float[][] features,float[] labels);
    public void trainMultiClassifierWithoutLabel(float[][] features);


    /////**************TEST**********************////
    public int getCategoryID(float [] features);
    public String getCategoryName(float [] features);
}
