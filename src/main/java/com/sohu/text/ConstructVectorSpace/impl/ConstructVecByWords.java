package com.sohu.text.ConstructVectorSpace.impl;

import com.ansj.vec.Word2VEC;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.MyIO.ReadDoc;
import com.sohu.text.MyIO.impl.ReadDocImpl;
import com.sohu.text.Tokens.Analysis;
import com.sohu.text.Tokens.impl.AnalysisImpl;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.FilterModifWord;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Math.min;

/**
 * Created by pengli211286 on 2016/5/6.
 */
public class ConstructVecByWords extends ConstructVecByKeywords implements ConstructVecSpace {
    private static Logger logger = LoggerFactory.getLogger(ConstructVecByWords.class);

    //////*****************成员部分**********************
    private static final Map<String, Double> POS_SCORE = new HashMap();
    int keywordsNum = -1;// < 0 表示对词个数不做限制

    ////*********方法部分******************
    public  ConstructVecByWords(){
        keywordsNum = 10;
        w2vModel = "vector.mod";
        hasLoadW2VModel = false;
        isMultiplyScore = false;
        w2v = null;
        loadW2VModel(w2vModel);
    }
    public  ConstructVecByWords( int nWordsNum,String w2vModelPath, boolean isMulScore ){
        //System.out.println("ConstructVecByKeywords 测试out");
        try {
            keywordsNum = nWordsNum;
            isMultiplyScore = isMulScore;
            loadW2VModel(w2vModelPath);
        }catch (Exception e){
            logger.info("ConstructVecByKeywords() error ",e);
        }
    }
    public void setWordsNum(int nWordsNum){
        keywordsNum = nWordsNum;
    }


    private List<Keyword> computeArticleTfidf(String content, int titleLength) {
        //不考虑时序
        HashMap tm = new HashMap();
        List <Term> parse = NlpAnalysis.parse(content);
        parse = FilterModifWord.updateNature(parse, new Forest[0]);
        parse = FilterModifWord.modifResult(parse);
        
        Iterator treeSet = parse.iterator();

        while(treeSet.hasNext()) {
            Term arrayList = (Term)treeSet.next();
            double weight = this.getWeight(arrayList, content.length(), titleLength);
            if(weight != 0.0D) {
                Keyword keyword = (Keyword)tm.get(arrayList.getName());
                if(keyword == null) {
                    keyword = new Keyword(arrayList.getName(), arrayList.natrue().allFrequency, weight);
                    tm.put(arrayList.getName(), keyword);
                } else {
                    keyword.updateWeight(1);
                }
            }
        }

        TreeSet treeSet1 = new TreeSet(tm.values());
        ArrayList arrayList1 = new ArrayList(treeSet1);
        if(this.keywordsNum < 0 || tm.size() <= this.keywordsNum){
            return arrayList1;
        } else {
            return arrayList1.subList(0, this.keywordsNum );
        }
    }
    public  List<Keyword> computeArticleTfidf(String title, String content) {
        if(StringUtil.isBlank(title)) {
            title = "";
        }

        if(StringUtil.isBlank(content)) {
            content = "";
        }

        return this.computeArticleTfidf(title + "\t" + content, title.length());
    }
    public  List<Keyword> computeArticleTfidf(String content) {
        return this.computeArticleTfidf(content, 0);
    }
    private double getWeight(Term term, int length, int titleLength) {
        if(term.getName().trim().length() < 2) {
            return 0.0D;
        } else {
            String pos = term.natrue().natureStr;
            Double posScore = (Double)POS_SCORE.get(pos);
            if(posScore == null) {
                posScore = Double.valueOf(1.0D);
            } else if(posScore.doubleValue() == 0.0D) {
                return 0.0D;
            }
            return titleLength > term.getOffe()?5.0D * posScore.doubleValue():(double)(length - term.getOffe()) * posScore.doubleValue() / (double)length;
        }
    }


    @Deprecated
    public void setKeywordsNum(int nKeywordsNum){
        keywordsNum = nKeywordsNum;
    }

    static {
        POS_SCORE.put("", Double.valueOf(0.0D));
        POS_SCORE.put("null", Double.valueOf(0.0D));
        POS_SCORE.put("w", Double.valueOf(0.0D));
        POS_SCORE.put("en", Double.valueOf(0.0D));
        POS_SCORE.put("m", Double.valueOf(0.0D));
        POS_SCORE.put("num", Double.valueOf(0.0D));
        POS_SCORE.put("nr", Double.valueOf(3.0D));
        POS_SCORE.put("nrf", Double.valueOf(3.0D));
        POS_SCORE.put("nw", Double.valueOf(3.0D));
        POS_SCORE.put("nt", Double.valueOf(3.0D));
        POS_SCORE.put("l", Double.valueOf(0.2D));
        POS_SCORE.put("a", Double.valueOf(0.2D));
        POS_SCORE.put("nz", Double.valueOf(3.0D));
        POS_SCORE.put("v", Double.valueOf(0.2D));
        POS_SCORE.put("kw", Double.valueOf(6.0D));
    }
    static{
        FilterModifWord.insertStopNatures("v") ;
        FilterModifWord.insertStopNatures("w") ;
        FilterModifWord.insertStopNatures("uj") ;
        FilterModifWord.insertStopNatures("ul") ;
        FilterModifWord.insertStopNatures("m") ;
        FilterModifWord.insertStopNatures("en") ;
        FilterModifWord.insertStopNatures("null") ;
        FilterModifWord.insertStopNatures("p") ;
        FilterModifWord.insertStopWord("");
    }
    /////***********************接口部分***********************************/////

    @Override
    public double[] genVecFromString(String content){
        try {
            double[] result = null;
            if (content.length() == 0) {
                logger.info("输入内容为空");
                return result;
            }
            //分词
            List<Keyword> words = this.computeArticleTfidf(content);

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
            for (int i = 0; i < words.size(); ++i) {
                Keyword tKW = words.get(i);
                //获取对应关键词的向量
                float [] vec = w2v.getWordVector(tKW.getName());
                double score = 1;
                //若模型中没有该词，返回向量长度为0；
                if (vec == null) {
                    logger.info(" 模型中没有 " + tKW + " ");
                    continue;
                }
                for (int j = 0; j < min(vec.length, vecLength); ++j) {
                    if (isMultiplyScore) {
                        result[ j] = tKW.getScore() * vec[j];
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
