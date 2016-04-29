package com.sohu.text.MyIO.impl;

/**
 * Created by pengli211286 on 2016/4/21.
 */
import com.sohu.text.MyIO.ReadDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadDocImpl implements ReadDoc {

    private static Logger logger = LoggerFactory.getLogger(ReadDocImpl.class);
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
        try {
            InputStream inputStream = new FileInputStream(path);
            byte[] head = new byte[3];
            inputStream.read(head);
            String code = "gb2312";  //或GBK
            if (head[0] == -1 && head[1] == -2)
                code = "UTF-16";
            else if (head[0] == -2 && head[1] == -1)
                code = "Unicode";
            else if (head[0] == -17 && head[1] == -69 && head[2] == -65)
                code = "UTF-8";
            inputStream.close();
            //System.out.println(code);
            return code;
        }catch(Exception e){
            logger.error("resolveCode() error ",e);
            return "utf-8"; //默认utf-8
        }
    }


    public List<String> Doc2List(String docPath) {

            List<String> result = new ArrayList<String>();
            StringBuilder content = new StringBuilder("");
            try {
                String code = resolveCode(docPath); //计算的编码
                //File file = new File(docPath);
                //InputStream is = new FileInputStream(file);
                InputStream is = new FileInputStream(docPath);
                InputStreamReader isr = new InputStreamReader(is, code);
                BufferedReader br = new BufferedReader(isr);

                String str = "";
                while (null != (str = br.readLine())) {
                    //content.append(str);
                    result.add(str);
                }
                br.close();
            } catch (Exception e) {
                logger.error("Doc2List() error " + "读取文件:" + docPath + "失败!",e );
            }
            return result;



    }
    public String Doc2String(String docPath) {
        StringBuilder content = new StringBuilder("");
        try {
            String code = resolveCode(docPath); //计算的编码
            //File file = new File(docPath);
            //InputStream is = new FileInputStream(file);
            InputStream is = new FileInputStream(docPath);
            InputStreamReader isr = new InputStreamReader(is, code);
            BufferedReader br = new BufferedReader(isr);

            String str = "";
            while (null != (str = br.readLine())) {
                content.append(str);
            }
            br.close();
        } catch (Exception e) {
            logger.error("Doc2String() error ！" +"读取文件:" + docPath + "失败!",e);
        }
        return content.toString();
    }


}
