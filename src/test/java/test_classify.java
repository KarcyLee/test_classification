import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SparseInstance;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by pengli211286 on 2016/5/4.
 */
public class test_classify {
    private static Logger logger = LoggerFactory.getLogger(test_Meka.class);

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);
        ArrayList<double[]> arr = new ArrayList<double[]>();
        double[] t = {1,2};
        double[] t1 = {3,4};
        arr.add(t);
        arr.add(t1);
        double[][]shuzu = new double[arr.size()][];
        for (int i = 0; i < arr.size();++i){
            shuzu[i] = arr.get(i);
            for(int j = 0; j < shuzu[i].length; ++j){
                System.out.println(shuzu[i][j]);
            }
            //System.out.println(shuzu[i].toString());
        }


    }


    private class structData{
        double [][] features = null;
        double [] labels = null;
    }
    private structData genTrainData(String fileList){

        structData result = new structData();


        //生成特征向量的接口
        ConstructVecSpace vecSpace = new ConstructVecByKeywords(10 ,"vector.mod",false);

        try {
            ArrayList<double []> arrayListFeatures = new ArrayList<double[]>();
            ArrayList<Double> arrayListLabels = new ArrayList<Double>();

            BufferedReader br = new BufferedReader(new InputStreamReader
                    (new FileInputStream(fileList), "utf-8"));

            ////读取文档
            //List<float []> doc_features = new ArrayList<float[]>() ;
            //List<String> label = new ArrayList<String>();
            String str = "";
            while (null != (str = br.readLine())) {
                //parse str
                String [] ls = str.split(" ");
                //System.out.println(ls[0] + " " +ls[1]);
                //double a = Integer.parseInt(ls[1]);
                //System.out.println(a );

                if(ls.length != 2){
                    System.out.printf("error! parse failed !\n %s \n",str);
                }else{
                    double [] cur_features = vecSpace.genVecFromDoc(ls[0]);
                    if(cur_features == null){
                        System.out.printf("error! 空特征，%s\n",ls[0]);
                        continue;
                    }
                    if (cur_features.length != 200 * 10) {
                        System.out.printf("error!维度不对！\n %s, %d\n", ls[0], cur_features.length);
                        continue;
                    }

                    arrayListFeatures.add(cur_features);
                    arrayListLabels.add(Double.parseDouble(ls[1]));

                }


            }
            br.close();
            //result.features = arrayListFeatures.toArray(double[][]);
            //result.labels = arrayListLabels.toArray(double);
            return result;
        } catch (Exception e) {
            logger.error("genTrainData() 失败！",e);
            return result;
        }
    }
}
