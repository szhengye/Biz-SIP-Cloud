package com.bizmda.bizsip.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BizUtils {
    public static List<File> getFileList(String strPath, String suffix) {
        List<File> filelist = new ArrayList<File>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    filelist.addAll(getFileList(file.getAbsolutePath(),suffix));
                }
                String fileName = file.getName();
                if (fileName.toLowerCase().endsWith(suffix)) {
                    filelist.add(file);
                } else {
                    continue;
                }
            }

        }
        //       System.out.println("a:"+filelist.size());
        return filelist;
    }
}
