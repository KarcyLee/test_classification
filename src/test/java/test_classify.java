import com.sohu.text.Classification.impl.ClassifyByMEKA;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords;

import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.classifiers.multilabel.PS;
import meka.core.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.*;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

/**
 * Created by pengli211286 on 2016/5/4.
 */
public class test_classify {
    private static Logger logger = LoggerFactory.getLogger(test_Meka.class);

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);

        Instances dataset = null;
        MultiLabelClassifier ps = null;
        Classifier cls = null;
        try {
            dataset = (Instances) SerializationHelper.read("instances");
        }catch (Exception e){
            logger.error("加载失败！");
        }
        Instances test = new Instances(dataset);
        Instances train = dataset;
        //train.setClassIndex(dataset.numAttributes() -1);

        try{
            logger.info("开始训练");
            cls =  new NaiveBayes();
            cls.buildClassifier(train);
            logger.info("训练成功");
        }catch (Exception e){
            logger.error("训练失败！",e);
        }

        try{
            SerializationHelper.write("naiveBayes",cls);
        }catch (Exception e){
            logger.error("保存失败",e);
        }

        /*
        try {
            logger.info("开始交叉验证");
            Evaluation eval = new Evaluation(train);
            eval.evaluateModel(cls, test);
            System.out.println(eval.toSummaryString("\nResults\n======\n",false));
        }catch (Exception e){
            logger.error("评估出现错误！",e);
        }
        */
        try {
            logger.info("开始分类");
            double right = 0;
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream("result.txt"), "UTF-8");
            for (int i = 0; i < train.numInstances(); ++i) {
                double trueIndex = test.instance(i).classValue();
                String label2 = test.classAttribute().value((int) trueIndex);

                double outIndex = cls.classifyInstance(test.instance(i));
                String label1 = test.classAttribute().value((int) outIndex);

                writer.write(outIndex + " " + label1 + " " + trueIndex + " " + label2 + "\n");
                if (outIndex == trueIndex){
                    ++right;
                }
            }
            writer.close();
            double rate = right / test.numInstances();
            logger.info("分类结束");
            logger.info("rate: "+ rate);
        }catch (Exception e){
            logger.error("分类错误！",e);
        }



    }


    private static class structData{
        double [][] features = null;
        double [] labels = null;
    }
    private static structData genTrainData(String fileList){

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
