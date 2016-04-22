/**
 * Created by pengli211286 on 2016/4/21.
 */
import java.lang.*;
import java.util.*;
import java.io.*;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.ansj.util.FilterModifWord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.*;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.app.keyword.*;

import org.omg.CORBA.portable.*;


public class test4doc2vec {

    /**
     * 解析普通文本文件  流式文件 如txt
     * @param path
     * @return
     */
    public static String readTxt(String path){
        StringBuilder content = new StringBuilder("");
        try {
            String code = resolveCode(path); //计算的编码
            //File file = new File(path);
            //InputStream is = new FileInputStream(file);
            InputStream is = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(is, code);
            BufferedReader br = new BufferedReader(isr);

            String str = "";
            while (null != (str = br.readLine())) {
                content.append(str);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("读取文件:" + path + "失败!");
        }
        return content.toString();
    }

    /**
     * 判断并返回文本编码方式
     * @param path ： 文件路径
     * @return ： string，文件编码类型。
     */
    private static String resolveCode(String path) throws Exception {
//      String filePath = "D:/article.txt"; //[-76, -85, -71]  ANSI
//      String filePath = "D:/article111.txt";  //[-2, -1, 79] unicode big endian
//      String filePath = "D:/article222.txt";  //[-1, -2, 32]  unicode
//      String filePath = "D:/article333.txt";  //[-17, -69, -65] UTF-8
        InputStream inputStream = new FileInputStream(path);
        byte[] head = new byte[3];
        inputStream.read(head);
        String code = "gb2312";  //或GBK
        if (head[0] == -1 && head[1] == -2 )
            code = "UTF-16";
        else if (head[0] == -2 && head[1] == -1 )
            code = "Unicode";
        else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)
            code = "UTF-8";

        inputStream.close();

        System.out.println(code);
        return code;
    }


    /**
     * 解析普通文本文件  流式文件 如txt
     * @param
     * @return
     */
    public static boolean TokensText(String path){
        String Content = readTxt(path);

        FilterModifWord.insertStopNatures("v") ;
        FilterModifWord.insertStopNatures("w") ;
        FilterModifWord.insertStopNatures("uj") ;
        FilterModifWord.insertStopNatures("ul") ;
        FilterModifWord.insertStopNatures("m") ;
        FilterModifWord.insertStopWord("");

        List TokensList = ToAnalysis.parse(Content);
        TokensList = FilterModifWord.modifResult(TokensList);
        System.out.print(TokensList);
        return true;
    }

    public static void extractKeyword(String title,String content) {
        KeyWordComputer kwc = new KeyWordComputer(10);
        //Collection<Keyword> result = kwc.computeArticleTfidf(title, content);
        List<Keyword> result = kwc.computeArticleTfidf(title, content);

        Iterator<Keyword> it = result.iterator();
        while (it.hasNext()) {
            Keyword tmp = it.next();
            System.out.printf("name: %s , score: %f , freq: %d  \n",tmp.getName(),tmp.getScore(),tmp.getFreq());
        }

        System.out.println(result);
    }
    public static void main(String[] args) {
        String filePath = "D:\\Data\\Corpus\\tc-corpus-answer\\answer\\C3-Art\\C3-Art0002.txt";
        //String res = readTxt(filePath);
        //System.out.print(res);
        //boolean b = TokensText(filePath);
        //System.out.print(b);
        String title = "维基解密否认斯诺登接受委内瑞拉庇护";
        title = "";
        String content = "有俄罗斯国会议员，9号在社交网站推特表示，美国中情局前雇员斯诺登，已经接受委内瑞拉的庇护，不过推文在发布几分钟后随即删除。俄罗斯当局拒绝发表评论，而一直协助斯诺登的维基解密否认他将投靠委内瑞拉。　　俄罗斯国会国际事务委员会主席普什科夫，在个人推特率先披露斯诺登已接受委内瑞拉的庇护建议，令外界以为斯诺登的动向终于有新进展。　　不过推文在几分钟内旋即被删除，普什科夫澄清他是看到俄罗斯国营电视台的新闻才这样说，而电视台已经作出否认，称普什科夫是误解了新闻内容。　　委内瑞拉驻莫斯科大使馆、俄罗斯总统府发言人、以及外交部都拒绝发表评论。而维基解密就否认斯诺登已正式接受委内瑞拉的庇护，说会在适当时间公布有关决定。　　斯诺登相信目前还在莫斯科谢列梅捷沃机场，已滞留两个多星期。他早前向约20个国家提交庇护申请，委内瑞拉、尼加拉瓜和玻利维亚，先后表示答应，不过斯诺登还没作出决定。　　而另一场外交风波，玻利维亚总统莫拉莱斯的专机上星期被欧洲多国以怀疑斯诺登在机上为由拒绝过境事件，涉事国家之一的西班牙突然转口风，外长马加略]号表示愿意就任何误解致歉，但强调当时当局没有关闭领空或不许专机降落。";
        extractKeyword(title,content);
    }

}
