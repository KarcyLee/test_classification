package com.sohu.text.ConstructVectorSpace.impl;

import com.ansj.vec.Word2VEC;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.MyIO.ReadDoc;
import com.sohu.text.MyIO.impl.ReadDocImpl;
import com.sohu.text.Tokens.Analysis;
import com.sohu.text.Tokens.impl.AnalysisImpl;
import org.ansj.app.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.lang.Math.min;

/**
 * Created by pengli211286 on 2016/5/6.
 * 提取N个关键词，并按关键词得分顺序进行词向量相加。
 */
public class ConstructVecByKeywords1 extends ConstructVecByKeywords implements ConstructVecSpace  {
    private static Logger logger = LoggerFactory.getLogger(ConstructVecByKeywords1.class);


    public  ConstructVecByKeywords1(){
        keywordsNum = 10;
        w2vModel = "vector.mod";
        hasLoadW2VModel = false;
        isMultiplyScore = false;
        w2v = null;
        loadW2VModel(w2vModel);
    }
    public  ConstructVecByKeywords1(int nKeywordsNum, String w2vModelPath, boolean isMulScore){
        //System.out.println("ConstructVecByKeywords 测试out");
        try {
            keywordsNum = nKeywordsNum;
            isMultiplyScore = isMulScore;
            loadW2VModel(w2vModelPath);
        }catch (Exception e){
            logger.info("ConstructVecByKeywords() error ",e);
        }
    }
    /////***********************接口部分***********************************/////
    @Override
    public double[] genVecFromDoc(String doc){
        try {
            double[] result = null;
            //读取文档内容。
            ReadDoc rd = new ReadDocImpl();
            String content = rd.Doc2String(doc);
            //
            result = this.genVecFromString(content);
            return result;
        }catch(Exception e){
            logger.error("genVecFromDoc error ",e);
            return null;
        }
    }

    @Override
    public double[] genVecFromString(String content){
        try {
            double[] result = null;
            if (content.length() == 0) {
                logger.info("输入内容为空");
                return result;
            }
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
            result = new double[vecLength];//关键词个数小于keywordsNum的的，以0补足

            //申请同长度每一维度求和数组，为归一化做准备；
            double[] arrSum = new double[vecLength];

            //为结果数组赋值,即为各个词向量的级联。
            double count  = 0;
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
                for (int j = 0; j < min(vec.length, vecLength); ++j) {
                    if (isMultiplyScore) {
                        result[ j] = score * vec[j];
                    } else {
                        result[j] = vec[j];
                    }
                    arrSum[j] += result[j];
                }
                ++count;
            }
            //归一化
            if(count != 0){
                for (int j = 0; j < vecLength; ++j){
                    //result[j] = result[j] - (arrSum[j] / count);
                    result[j] /= count;
                }
            }else {
                logger.info("参与文档向量构建的词向量为0");
            }

            return result;
        }catch (Exception e){
            logger.error("genVecFromString error ",e);
            return null;
        }

    }
}
