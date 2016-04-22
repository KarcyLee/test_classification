package com.sohu.text.Tokens;

import java.util.List;
import org.ansj.app.keyword.*;
/**
 * Created by pengli211286 on 2016/4/21.
 */
public interface Analysis {
    public List<String> doc2words(String doc);
    public List<String> str2words(String str);
    public List<Keyword> extractKeywords(String title,String content,int KeywordsNum);
}
