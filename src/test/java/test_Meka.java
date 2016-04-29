/**
 * Created by pengli211286 on 2016/4/25.
 */

import com.sohu.text.ConstructVectorSpace.ConstructVecSpace;
import com.sohu.text.ConstructVectorSpace.impl.ConstructVecByKeywords;

import meka.classifiers.multilabel.Evaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.core.*;
import meka.classifiers.multilabel.MultiLabelClassifier;
import meka.classifiers.multilabel.PS;
import weka.classifiers.Classifier;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;


public class test_Meka {
    private static Logger logger = LoggerFactory.getLogger(test_Meka.class);
    public static void main(String[] args){
        String sysout = "";
        //prepare data
       //genTrainTestARFF("D:\\Data\\Corpus\\test_samples","test.arff","test");

        genTrainTestARFF("train_samples","test.arff","test");

        try {
            Instances dataset = new Instances(new BufferedReader(new FileReader("test.arff")));
            dataset.setClassIndex(dataset.numAttributes() -1); //设置分类属性所在行号（第一行为0号），instancesTest.numAttributes()可以取得属性总数

            Instances dataTrain = new Instances(new BufferedReader(new FileReader("test.arff")));
            dataTrain.setClassIndex(dataTrain.numAttributes() -1);

            Instances dataTest = new Instances(new BufferedReader(new FileReader("test.arff")));
            dataTest.setClassIndex(dataTest.numAttributes() -1);

            logger.info("test_Meka 加载数据成功");
            // "java meka.classifiers.multilabel.PS -P 1 -N 1 -t data.arff -W weka.classifiers.functions.SMO"

            MultiLabelClassifier ps = new PS();
            //String[] options = {"-P","1", "-N", "1", "-t", "test.arff", "-W","weka.classifiers.functions.SMO"};
            String[] options = {"-P","0", "-N", "5","-W","weka.classifiers.functions.SMO"};
            ps.setOptions(options);

            logger.info("test_Meka 开始训练分类器");
            ps.buildClassifier(dataTrain);

            logger.info("test_Meka 保存分类器");
            saveClassifier(ps,"classifier");


            float right = 0.0f;
            for(int  i = 0;i<dataTest.numInstances();i++)//测试分类结果
            {
                double testLabel = ps.classifyInstance(dataTest.instance(i));
                double truthLabel = dataTest.instance(i).classValue();

                logger.info("test: " + testLabel + "  truth: "+ truthLabel);
                System.out.printf("test: %f  truth: %f \n",testLabel,truthLabel);

                if(testLabel == truthLabel)//如果预测值和答案值相等（测试语料中的分类列提供的须为正确答案，结果才有意义）
                {
                    right++;//正确值加1
                }
            }

            logger.info("the sum is "+ dataTest.numInstances());
            logger.info("right is "+ right);
            logger.info("the rate is "+ right/dataset.numInstances());

            System.out.printf("the sum is %d,  right is %f, the rate is %f \n",dataTest.numInstances(),right,right/dataset.numInstances());
        }catch(Exception e){
            logger.error("test_Meka error! ",e);
        }




    }
    public  static void genARFF_example(){
        ArrayList<Attribute> atts;
        ArrayList<Attribute> attsRel;
        ArrayList<String> attVals;
        ArrayList<String> attValsRel;
        Instances       data;
        Instances       dataRel;
        double[]        vals;
        double[]        valsRel;
        int             i;

        // 1. set up attributes
        atts = new ArrayList<Attribute>();
        // - numeric
        atts.add(new Attribute("att1"));
        // - nominal
        attVals = new ArrayList<String>();
        for (i = 0; i < 5; i++)
            attVals.add( "val" + (i+1));
        atts.add(new Attribute("att2", attVals));
        // - string
        atts.add(new Attribute("att3", (ArrayList) null));
        // - date
        atts.add(new Attribute("att4", "yyyy-MM-dd"));
        // - relational
        attsRel = new ArrayList<Attribute>();
        // -- numeric
        attsRel.add(new Attribute("att5.1"));
        // -- nominal
        attValsRel = new ArrayList<String>();
        for (i = 0; i < 5; i++)
            attValsRel.add("val5." + (i+1));
        attsRel.add(new Attribute("att5.2", attValsRel));
        dataRel = new Instances("att5", attsRel, 0);
        atts.add(new Attribute("att5", dataRel, 0));

        // 2. create Instances object
        data = new Instances("MyRelation", atts, 0);

        // 3. fill with data
        // first instance
        vals = new double[data.numAttributes()];
        // - numeric
        vals[0] = Math.PI;
        // - nominal
        vals[1] = attVals.indexOf("val3");
        // - string
        vals[2] = data.attribute(2).addStringValue("This is a string!");
        // - date
        try {
            vals[3] = data.attribute(3).parseDate("2001-11-09");
        }catch (ParseException pe){
            pe.printStackTrace();
        }
        // - relational
        dataRel = new Instances(data.attribute(4).relation(), 0);
        // -- first instance
        valsRel = new double[2];
        valsRel[0] = Math.PI + 1;
        valsRel[1] = attValsRel.indexOf("val5.3");
        dataRel.add(new DenseInstance(1.0, valsRel));
        // -- second instance
        valsRel = new double[2];
        valsRel[0] = Math.PI + 2;
        valsRel[1] = attValsRel.indexOf("val5.2");
        dataRel.add(new DenseInstance(1.0, valsRel));
        vals[4] = data.attribute(4).addRelation(dataRel);
        // add
        data.add(new DenseInstance(1.0, vals));

        // second instance
        vals = new double[data.numAttributes()];  // important: needs NEW array!
        // - numeric
        vals[0] = Math.E;
        // - nominal
        vals[1] = attVals.indexOf("val1");
        // - string
        vals[2] = data.attribute(2).addStringValue("And another one!");
        // - date
        try {
            vals[3] = data.attribute(3).parseDate("2000-12-01");
        }catch(ParseException pe){
            pe.printStackTrace();
        }
        // - relational
        dataRel = new Instances(data.attribute(4).relation(), 0);
        // -- first instance
        valsRel = new double[2];
        valsRel[0] = Math.E + 1;
        valsRel[1] = attValsRel.indexOf("val5.4");
        dataRel.add(new DenseInstance(1.0, valsRel));
        // -- second instance
        valsRel = new double[2];
        valsRel[0] = Math.E + 2;
        valsRel[1] = attValsRel.indexOf("val5.1");
        dataRel.add(new DenseInstance(1.0, valsRel));
        vals[4] = data.attribute(4).addRelation(dataRel);
        // add
        data.add(new DenseInstance(1.0, vals));

        // 4. output data
        System.out.println(data);
    }

    public static void genTrainTestARFF(String fileList,String arffPath,String Train) {

        //生成特征向量的接口
        ConstructVecSpace vecSpace = new ConstructVecByKeywords(10 ,"vector.mod",false);

        //arff
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        //atts.add(new Attribute("label",(ArrayList) null));//string
        atts.add(new Attribute("label"));

        for (int i = 0; i < 200 * 10; ++ i){
            atts.add(new Attribute("feature_" + (i + 1) ));
        }

        Instances data = new Instances(Train + "Set", atts, 0);

        try {
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
                    float [] cur_features = vecSpace.genVecFromDoc(ls[0]);
                    if(cur_features == null){
                        System.out.printf("error! 空特征，%s\n",ls[0]);
                        continue;
                    }
                    if (cur_features.length != 200 * 10){
                        System.out.printf("error!维度不对！\n %s, %d\n",ls[0],cur_features.length);
                        continue;
                    }
                    //doc_features.add(cur_features);
                   // label .add(ls[1]);
                    double[] vals = new double [200 * 10 + 1];
                    //System.out.printf("vals[0] : %f,  %d \n",vals[0], Integer.parseInt(ls[1]));

                    for (int j = 0; j < cur_features.length; ++j){
                        vals[j ] = cur_features[j];
                    }
                    //vals[200 * 10] = data.attribute(200 * 10).addStringValue(ls[1]); //label
                    vals[200 * 10] = Integer.parseInt(ls[1]);
                    data.add(new SparseInstance(1.0, vals));
                }


            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //保存arrf
        try {
            PrintWriter writer = new PrintWriter(arffPath, "UTF-8");
            writer.println(data);
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void saveClassifier(Classifier cls,String modelPath)throws Exception{

        // train
        //Instances inst = new Instances(new BufferedReader(new FileReader("/some/where/data.arff")));
        //inst.setClassIndex(inst.numAttributes() - 1);
        //cls.buildClassifier(inst);

        // serialize model
        //ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/some/where/j48.model"));
        //oos.writeObject(cls);
        //oos.flush();
        //oos.close();

        //version>3.5.5时， serialize model
        SerializationHelper.write(modelPath, cls);
    }
    public static Classifier loadClassifier(String modelPath) throws  Exception{
        //deserialize model
        //ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/some/where/j48.model"));
        //Classifier cls = (Classifier) ois.readObject();
        //ois.close();

        //在version>3.5.5时，deserialize model
        //Classifier cls = (Classifier) weka.core.SerializationHelper.read("modelPath");
        return (Classifier) weka.core.SerializationHelper.read("modelPath");

    }



}
