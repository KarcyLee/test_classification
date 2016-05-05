import com.sohu.text.Classification.impl.ClassifyByMEKA;
import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords;
import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.classifiers.multilabel.PS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.*;
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

        logger.info("开始读取文本，提取关键词特征！");
        structData train_data = genTrainData("train_samples");
        logger.info("提取完特征！开始转arff文件");
        ClassifyByMEKA.doubleToARFF(train_data.features,train_data.labels,"new.arff");
        logger.info("转arff文件完毕！");


        /*
        try {
            //Instances dataset = new Instances(new BufferedReader(new FileReader("new.arff")));
            dataset = ClassifyByMEKA.doubleToInstances(train_data.features,train_data.labels);
            logger.info("test_classify 加载数据成功");
        }catch (Exception e) {
            logger.error("加载数组失败！", e);
            return;
        }

        ps = new PS();
        try{
            String[] options = {"-P","1", "-N", "1","-W","weka.classifiers.functions.SMO"};
            ps.setOptions(options);
        }catch(Exception e){
            logger.error("设置分类器错误！",e);
        }

        ClassifyByMEKA csy = new ClassifyByMEKA();
        csy.setClassifier(ps);
        try {
            logger.info("开始训练分类器");
            csy.trainMultiClassifierWithLabel(train_data.features, train_data.labels, "model");
            logger.info("训练分类器完毕");
        }catch(Exception e){
            logger.error("训练分类器失败！",e);
        }

        //加载分类器和Attribute
        try {
            logger.info("加载分类器");
            csy.loadClassifier("model");
            logger.info("加载分类器完毕");
        }catch(Exception e){
            logger.error("加载分类器错误！",e);
        }

        try {
            SerializationHelper.write("classifier", ps);
            SerializationHelper.write("instances", dataset);
            logger.info("保存文件完毕！");
        }catch(Exception e){
            logger.error("保存文件失败！",e);
        }
        */
        try {
            ps = (MultiLabelClassifier) SerializationHelper.read("classifier");
            dataset = (Instances) SerializationHelper.read("instances");
        }catch (Exception e){
            logger.error("加载失败！");
        }


        try {
            //预测分类
            logger.info("开始预测分类");
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream("result.txt"), "UTF-8");

            BufferedWriter bufWriter = new BufferedWriter(writer);
            int colNum = train_data.features[0].length;
            //double []instFea = new double[colNum + 1];
            double []instFea = null;
            double right = 0;
            for (int i = 0; i < train_data.labels.length; ++i) {

                double truth = train_data.labels[i];

                instFea = new double[colNum + 1];

                for (int k = 0; k < colNum;++k){
                    instFea[k] = train_data.features[i][k];
                }
                Instance inst = new DenseInstance(1.0, instFea);
                inst.setDataset(dataset);

                double testClassIndex = ps.classifyInstance(inst);

                String category = dataset.classAttribute().value( (int) testClassIndex );
                double id = Double.parseDouble(category);


                bufWriter.write(Double.toString(truth) + " " + category +" " + "\n");

                if (Math.abs(id - truth) < 0.1) {
                    ++ right;
                }
            }
            bufWriter.close();
            double rate = right / (double)train_data.labels.length;
            logger.info("分析完毕！比率为 " +Double.toString(rate));
        }catch ( Exception e){
            logger.error("测试错误！",e);
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
