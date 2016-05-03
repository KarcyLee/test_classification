package com.sohu.text.Classification;

/**
 * Created by pengli211286 on 2016/4/22.
 */
public interface Classify {

    /////************TRAIN*********************////
    public void trainSingleClassifierWithLabel(double[][] features,double[] labels,String model);
    public void trainSingleClassifierWithoutLabel(double[][] features,String model);

    public void trainMultiClassifierWithLabel(double[][] features,double[] labels,String model);
    public void trainMultiClassifierWithoutLabel(double[][] features,String model);


    /////**************TEST**********************////
    public int getCategoryID(double [] features);
    public String getCategoryName(double [] features);
}
