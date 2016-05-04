package com.sohu.text.ConstructVectorSpace;

import java.util.List;

/**
 * Created by pengli211286 on 2016/4/22.
 */
public interface ConstructVecSpace {
    public double[] genVecFromDoc(String doc);
    public double[] genVecFromString(String content);

}
