package com.royalstone.pos.util;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: yaopoqing
 * Date: 2005-5-20
 */
public class ZipFile {
    public static void  zip(String srcFile, String destFile)throws IOException{
        ArrayList fileNames = new ArrayList(); // 存放文件名,并非含有路径的名字
        ArrayList files = new ArrayList(); // 存放文件对象

        try {
            FileOutputStream fileOut = new FileOutputStream(destFile);
            ZipOutputStream outputStream = new ZipOutputStream(fileOut);

            File rootFile = new File(srcFile);
            listFile(rootFile, fileNames, files);

           for (int loop = 0; loop < files.size(); loop++) {
              InputStream fileIn = new FileInputStream((File) files.get(loop));
                outputStream.putNextEntry(new ZipEntry((String) fileNames.get(loop)));
                byte[] buffer = new byte[4096];
                int len=0;
                while ((len=fileIn.read(buffer)) != -1) {
                     outputStream.write(buffer,0,len);
                }
               outputStream.closeEntry();
                fileIn.close();
            }

            outputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw(ioe);
        }
    }
      static void listFile(File parentFile, List nameList, List fileList) {
        if (parentFile.isDirectory()) {
            File[] files = parentFile.listFiles();
            for (int loop = 0; loop < files.length; loop++) {
                listFile(files[loop], nameList, fileList);
            }
        } else {
            fileList.add(parentFile);
            nameList.add(parentFile.getName());
        }
    }
}
