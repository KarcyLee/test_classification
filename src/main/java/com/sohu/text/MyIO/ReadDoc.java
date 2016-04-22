package com.sohu.text.MyIO;

import java.util.List;

/**
 * Created by pengli211286 on 2016/4/21.
 */
public interface ReadDoc {
    public List<String> Doc2List(String docPath);
    public String Doc2String(String docPath);
}
