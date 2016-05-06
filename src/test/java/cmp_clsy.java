import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.LogLevel;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

/**
 * Created by pengli211286 on 2016/5/6.
 */
public class cmp_clsy {
    private static Logger logger = LoggerFactory.getLogger(test_Meka.class);

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J(LogLevel.INFO, LogLevel.ERROR);


        Instances all_data = null;
        Classifier cls = null;
        try {
            all_data = (Instances) SerializationHelper.read("all_instances");
        } catch (Exception e) {
            logger.error("加载失败！");
        }

        //cls = new NaiveBayes();


        try {
//            Logistic ls = new Logistic();
//            String []Options = {"-C"} ;
//            ls.setOptions(Options);
//            cls = (Classifier)ls;

            //cls = new BayesNet();
            cls = new NaiveBayes();

            //cls = new IBk(5);
            logger.info("相加-所有词-无权重！");
            logger.info(cls.toString());
            logger.info("开始交叉验证！");
            Evaluation eval = new Evaluation(all_data);
            eval.crossValidateModel(cls, all_data, 10, new Random(1));
            System.out.print(eval.toClassDetailsString());
            System.out.print(eval.toSummaryString("\nResults\n======\n", false));
            System.out.print(eval.toMatrixString());

            logger.info("结束交叉验证！");
        }catch (Exception e){
            logger.error("交叉验证出现错误！",e);
        }

        /*
        try {
            logger.info("开始训练");
            cls = new NaiveBayes();
            cls.buildClassifier(train);
            logger.info("训练成功");
        } catch (Exception e) {
            logger.error("训练失败！", e);
        }

        try {
            SerializationHelper.write("naiveBayes", cls);
        } catch (Exception e) {
            logger.error("保存失败", e);
        }


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
                if (outIndex == trueIndex) {
                    ++right;
                }
            }
            writer.close();
            double rate = right / test.numInstances();
            logger.info("分类结束");
            logger.info("rate: " + rate);
        } catch (Exception e) {
            logger.error("分类错误！", e);
        }
        */

    }
}
