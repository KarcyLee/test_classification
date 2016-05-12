/**
 * Created by pengli211286 on 2016/4/21.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sohu.text.Tokens.impl.AnalysisImpl;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.alibaba.fastjson.JSONObject;
import com.ansj.vec.Learn;
import com.ansj.vec.Word2VEC;

import love.cq.util.IOUtil;
import love.cq.util.StringUtil;

import com.sohu.text.Tokens.Analysis;
import org.ansj.util.FilterModifWord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class w2v_Test {
    private static Logger logger = LoggerFactory.getLogger(w2v_Test.class);

    private static final File CorpusFile = new File("result.txt");
    public static void main(String[] args) throws IOException {
        try {
            //File[] files = new File("D:\\Data\\Corpus\\tc-corpus-answer\\answer\\C3-Art").listFiles();
            //File[] files = new File("/home/karl/Corpus").listFiles();

            //List<File> files = getAllFiles(root);
            List<File> files = new ArrayList<File>();
            readAllFileFromParentCatalog("/home/karl/Corpus",files);



            //构建语料
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(CorpusFile);
                for (File file : files) {
                    //if (file.canRead() && file.getName().endsWith(".txt")) {
                    if (file.canRead() ) {
                        myParserFile(fos, file);
                    }
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("构建语料完毕！");

            //进行分词训练
            //Learn lean = new Learn() ;
            Learn lean = new Learn(false, 200, 5, 0.025, 0.001);
            lean.learnFile(CorpusFile);
            logger.info("分词训练完毕！");

            lean.saveModel(new File("vector.mod"));
            logger.info("词模型保存完毕！");

            //加载测试
            Word2VEC w2v = new Word2VEC();
            w2v.loadJavaModel("vector.mod");
            logger.info("词模型加载成功！");

            System.out.println(w2v.distance("煤矿"));

        }catch (Exception e){
            logger.error("w2v_test_main() error! ",e);
        }

    }

    private static void parserFile(FileOutputStream fos, File file){
        // TODO Auto-generated method stub
        BufferedReader br = null;
        try{
            br = IOUtil.getReader(file.getAbsolutePath(), IOUtil.UTF8);
            String temp = null;
            JSONObject parse = null;
            while ((temp = br.readLine()) != null) {
                parse = JSONObject.parseObject(temp);
                paserStr(fos, parse.getString("title"));
                paserStr(fos, StringUtil.rmHtmlTag(parse.getString("content")));
            }
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void paserStr(FileOutputStream fos, String title) throws IOException {
        List<Term> parse2 = ToAnalysis.parse(title) ;
        StringBuilder sb = new StringBuilder() ;
        for (Term term : parse2) {
            sb.append(term.getName()) ;
            sb.append(" ");
        }
        fos.write(sb.toString().getBytes()) ;
        fos.write("\n".getBytes()) ;
    }

    private static void myParserFile(FileOutputStream fos, File file) {
        // TODO Auto-generated method stub
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder() ;
        Analysis as = new AnalysisImpl();
        try{
            //br = IOUtil.getReader(file.getAbsolutePath(), IOUtil.GBK);
            br = IOUtil.getReader(file.getAbsolutePath(), IOUtil.UTF8);
            //分词器过滤词性
            FilterModifWord.insertStopNatures("v") ;
            FilterModifWord.insertStopNatures("w") ;
            FilterModifWord.insertStopNatures("uj") ;
            FilterModifWord.insertStopNatures("ul") ;
            FilterModifWord.insertStopNatures("m") ;
            FilterModifWord.insertStopWord("");

            List<String> all_words_list = new ArrayList<String>();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                //分词
                List<Term> TokensList = ToAnalysis.parse(temp) ;
                //对分词结果进行过滤
                TokensList = FilterModifWord.modifResult(TokensList);
                //System.out.printf("分词： ");
                for(Term t: TokensList){
                    sb.append(t.getName());
                    //System.out.printf("%s ",t.getName());
                    sb.append(" ");
                }
                //System.out.print("\n");
                sb.append("\n");
            }
            fos.write(sb.toString().getBytes());
            br.close();
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    final static void showAllFiles(File dir){
        File[] fs = dir.listFiles();
        for(int i=0; i < fs.length; i++){
            System.out.println(fs[i].getAbsolutePath());
            if(fs[i].isDirectory()){
                try{
                    showAllFiles(fs[i]);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    final static List<File> getAllFiles(File dir) {
        List<File> result = new ArrayList<File>();
        File[] fs = dir.listFiles();
        for(int i=0; i < fs.length; i++){
            result.add(fs[i]);
            System.out.println(fs[i].getAbsolutePath());
            if(fs[i].isDirectory()){
                try{
                    List<File> child_files =getAllFiles(fs[i]);
                    for (File f : child_files){
                        result.add(f);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static void readAllFileFromParentCatalog(String path,List<File> result){

        File[] files = new File(path).listFiles(); // 得到f文件夹下面的所有文件。
        if(null == result) {
            result = new ArrayList<File>();
        }
        for (File file : files) {
            if(file.isDirectory()) {
                //如何当前路劲是文件夹，则循环读取这个文件夹下的所有文件
                readAllFileFromParentCatalog(file.getAbsolutePath(),result);
            } else {
                result.add(file);
            }
        }

    }

}
