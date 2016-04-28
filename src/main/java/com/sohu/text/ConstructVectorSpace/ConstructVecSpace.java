package com.sohu.text.ConstructVectorSpace;

import java.util.List;

/**
 * Created by pengli211286 on 2016/4/22.
 */
public interface ConstructVecSpace {
    public float[] genVecFromDoc(String doc);
    public float[] genVecFromString(String content);

}
