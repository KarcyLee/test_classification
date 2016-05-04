package com.sohu.text.ConstructVectorSpace.impl;

import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.MyIO.ReadDoc;
import com.sohu.text.MyIO.impl.ReadDocImpl;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by pengli211286 on 2016/5/3.
 */
public class ConstructVecBySelectedPhrases implements ConstructVecSpace {
    private static Logger logger = LoggerFactory.getLogger(ConstructVecBySelectedPhrases.class);
    /////*****************成员变量部分************************/
    private List<String> m_words;

    /////***********************方法部分***********************************/////
    public ConstructVecBySelectedPhrases(){
        m_words = null;

    }
    public ConstructVecBySelectedPhrases(List<String> words,String w2vModelPath){
        try {
            if (!m_words.isEmpty()) {
                m_words.clear();
            }
            m_words = words;
        }catch(Exception e){
            logger.error("ConstructVecBySelectedPhrases 构造函数错误！",e);
        }
    }
    public void setWords(List<String> words){
        m_words = words;
    }
    public List<String> getWords(){
        return m_words;
    }
    public int getWordsNum(){
        return m_words.size();
    }




    /////*************************接口部分*********************************
    public double[] genVecFromDoc(String docPath){
        try {
            double[] result = null;
            //读取文档内容。
            ReadDoc rd = new ReadDocImpl();
            String content = rd.Doc2String(docPath);
            result = genVecFromString(content);
            return result;
        }catch(Exception e){
            logger.error(" genVecFromDoc() Error! " ,e);
            return null;
        }
    }
    public double[] genVecFromString(String content){
        try {
            double[] result = null;
            if (content.length() == 0)
                return result;
            if (m_words.isEmpty()) {
                logger.info("未加载指定words列表！");
                return result;
            }
            //分词并提取关键词
            int keywordsNum = 2000;
            KeyWordComputer kwc = new KeyWordComputer(keywordsNum);
            List<Keyword> keywordList = kwc.computeArticleTfidf(content);
            HashMap<String, Double> hashMap = new HashMap<String, Double>();
            for (Keyword kw : keywordList) {
                hashMap.put(kw.getName(), kw.getScore());
            }


            ///获取单词个数
            int wordsNum = m_words.size();

            ///为返回结果数组赋空间
            result = new double[wordsNum];
            int i = 0;
            for (String t_word : m_words) {
                if (hashMap.containsKey(t_word)) {
                    double tmp = hashMap.get(t_word);
                    result[i] = (double) tmp;
                }
                i++;
            }
            return result;
        }catch(Exception e){
            logger.error("genVecFromString() Error! ",e);
            return null;
        }
    }
}
