package com.sohu.text.Tokens.impl;


import java.util.List;
import java.lang.*;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.util.FilterModifWord;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.sohu.text.Tokens.Analysis;
import com.sohu.text.MyIO.ReadDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by pengli211286 on 2016/4/21.
 */
public class AnalysisImpl implements Analysis {
    private static Logger logger = LoggerFactory.getLogger(AnalysisImpl.class);

    private static ReadDoc rd;
    public List<String> doc2words(String doc){
        try{
            String  Content = rd.Doc2String(doc);

            //分词器过滤词性
            FilterModifWord.insertStopNatures("v") ;
            FilterModifWord.insertStopNatures("w") ;
            FilterModifWord.insertStopNatures("uj") ;
            FilterModifWord.insertStopNatures("ul") ;
            FilterModifWord.insertStopNatures("m") ;
            FilterModifWord.insertStopNatures("en") ;
            FilterModifWord.insertStopNatures("null") ;
            FilterModifWord.insertStopNatures("p") ;
            FilterModifWord.insertStopWord("");
            //分词
            List TokensList = ToAnalysis.parse(Content);
            //对分词结果进行过滤
            TokensList = FilterModifWord.modifResult(TokensList);
            //System.out.print(TokensList);
            return TokensList;
        }catch (Exception e){
            logger.error( "doc2words() error " ,e);
            return null;
        }
    }
    public List<String> str2words(String str){
        try {
            //分词器过滤词性
            FilterModifWord.insertStopNatures("v") ;
            FilterModifWord.insertStopNatures("w") ;
            FilterModifWord.insertStopNatures("uj") ;
            FilterModifWord.insertStopNatures("ul") ;
            FilterModifWord.insertStopNatures("m") ;
            FilterModifWord.insertStopNatures("en") ;
            FilterModifWord.insertStopNatures("null") ;
            FilterModifWord.insertStopNatures("p") ;
            FilterModifWord.insertStopWord("");
            //分词
            List TokensList = ToAnalysis.parse(str);
            //对分词结果进行过滤
            TokensList = FilterModifWord.modifResult(TokensList);
            //System.out.print(TokensList);
            return TokensList;
        }catch (Exception e){
            logger.error("str2words() error ",e);
            return null;
        }
    }
    public List<Keyword> extractKeywords(String title, String content, int KeywordsNum){
        try {
            KeyWordComputer kwc = new KeyWordComputer(KeywordsNum);
            List<Keyword> result = kwc.computeArticleTfidf(title, content);
        /*
        // 每个元素有 name,score,freq等成员。
        Iterator<Keyword> it = result.iterator();
        while (it.hasNext()) {
            Keyword tmp = it.next();
            System.out.printf("name: %s , score: %f , freq: %d  \n",tmp.getName(),tmp.getScore(),tmp.getFreq());
        }
        */
            return result;
        }catch(Exception e){
            logger.error("extractKeywords() error ",e);
            return null;
        }


    }

}
