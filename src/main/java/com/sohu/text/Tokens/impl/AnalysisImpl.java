package com.sohu.text.Tokens.impl;


import java.util.ArrayList;
import java.util.List;
import java.lang.*;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.util.FilterModifWord;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.sohu.text.Tokens.Analysis;
import com.sohu.text.MyIO.ReadDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

/**
 * Created by pengli211286 on 2016/4/21.
 */
public class AnalysisImpl implements Analysis {
    private static Logger logger = LoggerFactory.getLogger(AnalysisImpl.class);

    private static ReadDoc rd;
    public List<String> doc2words(String doc){
        try{
            String  Content = rd.Doc2String(doc);
            List<String> result = str2words(Content);
            return result;
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
            List<Term> TokensList = ToAnalysis.parse(str);
            //对分词结果进行过滤
            TokensList = FilterModifWord.modifResult(TokensList);
            //System.out.print(TokensList);
            List<String> result = new ArrayList<String>() ;
            for(Term t :TokensList){
                result.add(t.getName());
            }
            return result;
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


    public static void main(String[] args) {

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);

        String content = "           中国水法研究会在京成立\n" +
                "新华社北京５月１５日电（记者王坚）以增进对水的\n" +
                "             立法、政策和行政管理研究、促进水资源法制建设为宗旨\n" +
                "             的中国水法研究会今天在北京成立。全国政协副主席钱正\n" +
                "             英担任研究会的名誉会长。\n" +
                "                 钱正英在成立会上指出，贯彻《水法》，必须建立三\n" +
                "             个体系，即水法规体系、水管理体系和水执法体系。在这\n" +
                "             方面，有许多理论与实践的课题需要研究和探讨。中国水\n" +
                "             法研究会的成立，对促进我国水利法制建设工作具有重要\n" +
                "             意义。（完）\n" +
                "\n";
        List<Term> TokensList = ToAnalysis.parse(content);
        //对分词结果进行过滤
        TokensList = FilterModifWord.modifResult(TokensList);
        for (Term t : TokensList){
            System.out.println(t.getName()+ " " + t.termNatures() + " "+t.score());
        }
    }


}
