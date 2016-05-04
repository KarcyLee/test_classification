package com.sohu.text.ConstructVectorSpace.impl;

import java.util.List;
import static java.lang.Math.min;

import com.ansj.vec.Word2VEC;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.MyIO.ReadDoc;
import com.sohu.text.MyIO.impl.ReadDocImpl;
import com.sohu.text.Tokens.Analysis;
import com.sohu.text.Tokens.impl.AnalysisImpl;
import org.ansj.app.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by pengli211286 on 2016/4/22.
 */
public class ConstructVecByKeywords implements ConstructVecSpace {
    private static Logger logger = LoggerFactory.getLogger(ConstructVecByKeywords.class);

    /////********成员变量部分************************/
    private int keywordsNum ; //每个文档提取关键词的数目
    private boolean isMultiplyScore ; //词向量是否考虑Score作为权重
    private String w2vModel; //"vector.mod" 模型存储全路径
    private boolean hasLoadW2VModel ; //是否加载了w2v模型
    private Word2VEC w2v; //w2v 变量

    /////***********************方法部分***********************************/////
    public  ConstructVecByKeywords(){
        keywordsNum = 10;
        w2vModel = "vector.mod";
        hasLoadW2VModel = false;
        isMultiplyScore = false;
        w2v = null;
        loadW2VModel(w2vModel);
    }
    public  ConstructVecByKeywords(int nKeywordsNum, String w2vModelPath, boolean isMulScore){
        //System.out.println("ConstructVecByKeywords 测试out");
        try {
            keywordsNum = nKeywordsNum;
            isMultiplyScore = isMulScore;
            loadW2VModel(w2vModelPath);
        }catch (Exception e){
            logger.info("ConstructVecByKeywords() error ",e);
        }
    }

    public void setKeywordsNum(int nKeywordsNum){
        keywordsNum = nKeywordsNum;
    }
    public  int getKeywordsNum(){
        return keywordsNum;
    }
    public boolean loadW2VModel(String w2vPath){
        try {
            if (hasLoadW2VModel) {
                return true;
            }
            if (w2v == null) {
                w2v = new Word2VEC();
            }
            if (w2v.loadJavaModel(w2vPath)) { //w2v.loadJavaModel("vector.mod") ;
                w2vModel = w2vPath;
                hasLoadW2VModel = true;
                return true;
            }
            return false;

        }catch(Exception e){
            logger.error("loadW2VModel error ",e);
            return false;
        }
    }
    public void setIsMultiplyScore(boolean isMulScore){
        isMultiplyScore = isMulScore;
    }
    public  boolean getIsMultiplyScore(){
        return isMultiplyScore;
    }
    public Word2VEC getW2V(){
        return w2v;
    }
    //获取每个词向量的长度，取决于w2v模型。
    public int getVecLength(){
        if(hasLoadW2VModel){
            return w2v.getSize();
        }else{
            return  0;
        }
    }


    /////***********************接口部分***********************************/////
    public double[] genVecFromDoc(String docPath) {
        try {
            double[] result = null;
            //读取文档内容。
            ReadDoc rd = new ReadDocImpl();
            String content = rd.Doc2String(docPath);
            //
            result = genVecFromString(content);
            return result;
        }catch(Exception e){
            logger.error("genVecFromDoc error ",e);
            return null;
        }
    }
    public double[] genVecFromString(String content){
        try {
            double[] result = null;
            if (content.length() == 0)
                return result;
            //分词并提取关键词
            Analysis as = new AnalysisImpl();
            List<Keyword> keywords = as.extractKeywords("", content, keywordsNum);

            //若未加载W2V 模型
            if (!hasLoadW2VModel) {
                ////加载w2v模型
                w2v = new Word2VEC();
                w2v.loadJavaModel(w2vModel); //w2v.loadJavaModel("vector.mod") ;
                hasLoadW2VModel = true;
            }

            ///获取向量长度
            int vecLength = w2v.getSize();
            ///为返回结果数组赋空间
            result = new double[vecLength * keywordsNum];//关键词个数小于keywordsNum的的，以0补足
            //为结果数组赋值,即为各个词向量的级联。
            for (int i = 0; i < keywords.size(); ++i) {
                Keyword tKW = keywords.get(i);
                //获取对应关键词的向量
                float [] vec = w2v.getWordVector(tKW.getName());
                double score = (double) tKW.getScore();
                //若模型中没有该词，返回向量长度为0；
                if (vec == null) {
                    logger.info(" 模型中没有 " + tKW.getName() + " ");
                    continue;
                }
                int startPos = i * vecLength;
                for (int j = 0; j < min(vec.length, vecLength); ++j) {
                    if (isMultiplyScore) {
                        result[startPos + j] = score * vec[j];
                    } else {
                        result[startPos + j] = vec[j];
                    }
                }
            }
            return result;
        }catch (Exception e){
            logger.error("genVecFromString error ",e);
            return null;
        }

    }

}

