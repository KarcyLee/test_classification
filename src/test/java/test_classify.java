import com.sohu.text.Classification.impl.ClassifyByMEKA;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords;

import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords1;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByWords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by pengli211286 on 2016/5/4.
 * 构建特征向量：词向量相加。每个词向量计算权重。
 * 注意：权重可以选score 或者 freq 。
 */
public class test_classify {
    private static Logger logger = LoggerFactory.getLogger(test_Meka.class);

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);



        structData train_data = genTrainData("train_samples");
        structData test_data = genTrainData("test_samples");
        structData all_data = genTrainData("all_samples");

        Instances a = ClassifyByMEKA.doubleToInstances(train_data.features,train_data.labels);
        Instances b = ClassifyByMEKA.doubleToInstances(test_data.features,test_data.labels);
        Instances c = ClassifyByMEKA.doubleToInstances(all_data.features,all_data.labels);

        try {
            logger.info("写数据");
            SerializationHelper.write("train_instances", a);
            SerializationHelper.write("test_instances", b);
            SerializationHelper.write("all_instances", c);
        }catch (Exception e){
            logger.error("写入数据失败",e);
        }

    }


    private static class structData{
        double [][] features = null;
        double [] labels = null;
    }
    private static structData genTrainData(String fileList){

        structData result = new structData();

        //生成特征向量的接口
        //ConstructVecSpace vecSpace = new ConstructVecByKeywords(10 ,"vector.mod",false);
        //ConstructVecByKeywords1 vecSpace = new ConstructVecByKeywords1(10 ,"vector.mod",false);
        ConstructVecByWords vecSpace = new ConstructVecByWords(-1 ,"vector.mod",false);
        //vecSpace.setIsMultiplyScore(true);

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
                    //if (cur_features.length != 200 * 10) {
                    if (cur_features.length != 200) {
                        System.out.printf("error!维度不对！\n %s, %d\n", ls[0], cur_features.length);
                        continue;
                    }

                    arrayListFeatures.add(cur_features);
                    arrayListLabels.add(Double.parseDouble(ls[1]));

                }

            }
            br.close();
            if(arrayListFeatures.size() != arrayListLabels.size()){
                logger.error("写特征标签尺寸不一致！");
                return result;
            }
            result.features = new double[arrayListFeatures.size()][];
            result.labels = new double[arrayListLabels.size()];
            for(int i = 0; i < arrayListFeatures.size(); ++i){
                result.features[i] = arrayListFeatures.get(i);
                result.labels[i] = arrayListLabels.get(i);
            }
            return result;
        } catch (Exception e) {
            logger.error("genTrainData() 失败！",e);
            return result;
        }
    }

}
