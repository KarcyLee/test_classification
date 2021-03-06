package com.sohu.text.Classification.impl;

import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.classifiers.multilabel.PS;
import meka.classifiers.multilabel.incremental.IncrementalEvaluation;



import com.sohu.text.Classification.Classify;
import mst.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Classifier;
import weka.core.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pengli211286 on 2016/4/22.
 */
public class ClassifyByMEKA implements Classify {

    /////**********成员变量部分***************************
    private static Logger logger = LoggerFactory.getLogger(ClassifyByMEKA.class);
    private Classifier m_cls = null; //分类器

    ///////******************方法部分******************************
    public void setClassifier(Classifier cls){
        m_cls = cls;
    }
    //默认最后一列为label
    public static Instances doubleToInstances(double[][] features,double[] labels){
        //features:每行为一个样本
        ///输入校验
        if (features == null || labels == null){
            logger.error("输入数据为空！");
            return null;
        }
        int rowNum = features.length;
        if (labels.length != rowNum){
            logger.error("标签和样本个数不一致！");
            return null;
        }
        int colNum = features[0].length;
        if (colNum <= 0){
            logger.error("输入样本集为空！");
            return null;
        }
        for (int i = 1; i < rowNum;++i){
            if (colNum != features[i].length){
                logger.error("样本集特征个数不一致！");
                logger.error("第一个样本为"+colNum+"个特征，而第"+(i+1)+"个为"+features[i].length);
                return  null;
            }
        }

        try {
            //arff声明
            ArrayList<Attribute> atts = new ArrayList<Attribute>();
            for (int i = 0; i < colNum; ++i) {
                atts.add(new Attribute("feature_" + (i + 1)));
            }

            ArrayList<String> label_list = new  ArrayList<String>() ;
            Set<String> set = new HashSet<String>();
            for(int i = 0; i < labels.length;++i){
                set.add(Double.toString(labels[i]));
            }
            for(String s :set){
                label_list.add(s);
            }
            atts.add(new Attribute("label",label_list));
            // 生成Instance数据
            Instances data = new Instances("dataSet", atts, 0);
            for (int i = 0; i < rowNum; ++i) {
                double[] vals = new double[colNum + 1];
                for (int j = 0; j < colNum; ++j) {
                    vals[j] = features[i][j];
                }
                vals[colNum] = label_list.indexOf(Double.toString(labels[i]));
                data.add(new DenseInstance(1.0, vals));
            }
            data.setClassIndex(data.numAttributes() -1);
            return data;
        }catch(Exception e){
            logger.error("生成Instances 失败！",e);
            return null;
        }


    }
    public static Instances doubleToInstances(double[][] features){
        //features:每行为一个样本
        ///输入校验
        if (features == null ){
            logger.error("输入数据为空！");
            return null;
        }
        int rowNum = features.length;
        int colNum = features[0].length;
        if (colNum <= 0){
            logger.error("输入样本集为空！");
            return null;
        }
        for (int i = 1; i < rowNum;++i){
            if (colNum != features[i].length){
                logger.error("样本集特征个数不一致！");
                logger.error("第一个样本为"+colNum+"个特征，而第"+(i+1)+"个为"+features[i].length);
                return  null;
            }
        }

        try {
            //arff声明
            ArrayList<Attribute> atts = new ArrayList<Attribute>();
            for (int i = 0; i < colNum; ++i) {
                atts.add(new Attribute("feature_" + (i + 1)));
            }

            // 生成Instance数据
            Instances data = new Instances("dataSet", atts, 0);
            for (int i = 0; i < rowNum; ++i) {
                double[] vals = new double[colNum];
                for (int j = 0; j < colNum; ++j) {
                    vals[j] = features[i][j];
                }
                data.add(new DenseInstance(1.0, vals));
            }
            return data;
        }catch(Exception e){
            logger.error("生成Instances 失败！",e);
            return null;
        }
    }
    public static boolean doubleToARFF(double[][] features,double[] labels,String arff_file){
        //features:每行为一个样本
        try {
            // 生成Instance数据
            Instances data = doubleToInstances(features,labels);
            if (data == null){
                logger.error("未生成Instances!");
                return false;
            }
            //保存arrf
            PrintWriter writer = new PrintWriter(arff_file, "UTF-8");
            writer.println(data);
            writer.close();
            return true;
        }catch (Exception e){
            logger.error("保存arff文件失败！",e);
            return false;
        }
    }
    public static boolean doubleToARFF(double[][] features,String arff_file){
        //features:每行为一个样本
        try {
            // 生成Instance数据
            Instances data = doubleToInstances(features);
            if (data == null){
                logger.error("未生成Instances!");
                return false;
            }

            //保存arrf
            PrintWriter writer = new PrintWriter(arff_file, "UTF-8");
            writer.println(data);
            writer.close();
            return true;
        }catch (Exception e){
            logger.error("保存arff文件失败！",e);
            return false;
        }
    }

    private  void trainAndSaveClassifier(Instances data,Classifier cls,String model){
        if (data == null){
            logger.error("未生成Instances!");
            return;
        }
        try {
            logger.info("开始训练分类器");
            m_cls.buildClassifier(data);
            //m_hasLoadModel = true;
            logger.info("训练分类器完毕！");

        }catch(Exception e){
            logger.error("训练分类器失败！",e);
            return;
        }
        try{
            logger.info("保存分类器");
            SerializationHelper.write(model, m_cls);
            logger.info("保存分类器完毕！");
        }catch(Exception e){
            logger.error("保存分类器失败！");
            return;
        }

    }
    public Attribute loadAttribute(){
        Attribute res = null;
        try{
            res = (Attribute) weka.core.SerializationHelper.read("Attribute");
            return res;
        } catch(Exception e){
            logger.error("读取Attribute 失败！",e);
            return res;
        }
    }
    public Classifier loadClassifier(String modelPath){
        try {
            return (Classifier) weka.core.SerializationHelper.read(modelPath);
        }catch (Exception e){
            logger.error("加载分类器失败！",e);
            return null;
        }
    }

    public static  void main(String[] args){

        //仅供例子
        double features[][] = null;
        double labels[] = null;

        MultiLabelClassifier ps = new PS();
        //String[] options = {"-P","1", "-N", "1", "-t", "test.arff", "-W","weka.classifiers.functions.SMO"};
        String[] options = {"-P","0", "-N", "1","-W","weka.classifiers.functions.SMO"};
        try {
            ps.setOptions(options);
        }catch (Exception e){
            logger.error("设置分类器错误！",e);
        }

        ClassifyByMEKA csy = new ClassifyByMEKA();
        csy.setClassifier(ps);
        csy.trainMultiClassifierWithLabel(features,labels,"model");

        //加载分类器和Attribute
        csy.loadClassifier("model");

        //预测分类
        int id = csy.getCategoryID(features[0]);

    }

    ////***************接口部分**********************////

    /////************TRAIN*********************////
    public void trainSingleClassifierWithLabel(double[][] features,double[] labels,String model){
        if (m_cls == null){
            logger.error("分类器未指定！");
            return;
        }
        Instances data = doubleToInstances(features,labels);
        if (data == null){
            logger.error("未生成Instances!");
            return;
        }
       trainAndSaveClassifier(data,m_cls,model);
    }
    public void trainSingleClassifierWithoutLabel(double[][] features,String model){
        if (m_cls == null){
            logger.error("分类器未指定！");
            return;
        }
        Instances data = doubleToInstances(features);
        if (data == null){
            logger.error("未生成Instances!");
            return;
        }
        trainAndSaveClassifier(data,m_cls,model);
    }
    public void trainMultiClassifierWithLabel(double[][] features,double[] labels,String model){
        logger.info("trainMultiClassifierWithLabel():");
        trainSingleClassifierWithLabel(features,labels,model);
    }
    public void trainMultiClassifierWithoutLabel(double[][] features,String model){
        logger.info("trainMultiClassifierWithoutLabel():");
        trainSingleClassifierWithoutLabel(features,model);
    }


    /////**************TEST**********************////
    public int getCategoryID(double [] features){
        /*
        if (features == null){
            logger.error("输入为空！");
            return -1;
        }
        int colNum = features.length;
        if (colNum <= 0){
            logger.error("输入样本集为空！");
            return -1;
        }
        if(m_cls == null){
            logger.error("分类器未加载！");
            return -1;
        }
        try {
            // 生成Instance数据
            Instance inst = new DenseInstance(1.0, features);

            double testClassIndex = m_cls.classifyInstance(inst);
            String strTestLabel = m_classAttribute.value((int) testClassIndex);
            int testLabel = (int) Double.parseDouble(strTestLabel);
            return testLabel;
        }catch (Exception e){
            logger.error("测验类别错误！",e);
            return -1;
        }
        */
        return -1;
    }
    public String getCategoryName(double [] features){
        /*
        if (features == null){
            logger.error("输入为空！");
            return "";
        }
        int colNum = features.length;
        if (colNum <= 0){
            logger.error("输入样本集为空！");
            return "";
        }
        if(m_cls == null){
            logger.error("分类器未加载！");
            return "";
        }
        try {
            // 生成Instance数据
            Instance inst = new DenseInstance(1.0, features);

            double testClassIndex = m_cls.classifyInstance(inst);
            String strTestLabel = m_classAttribute.value((int) testClassIndex);
            return strTestLabel;
        }catch (Exception e){
            logger.error("测验类别错误！",e);
            return "";
        }
        */
        return "";
    }
}
