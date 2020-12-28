package com.bizmda.bizsip.common;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author 史正烨
 */
public class BizUtils {
    private static ExecutorService executorService = ThreadUtil.newExecutor(Runtime.getRuntime().availableProcessors()*2);

    public static List<File> getFileList(String strPath, String suffix) {
        List<File> filelist = new ArrayList<File>();
        File dir = new File(strPath);
        File[] files = dir.listFiles();
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
        return filelist;
    }

    public static String getElStringResult(String express, Object data) throws BizException {
        String result;
        Future<Object> future = executorService.submit(new ElThread(express,data,false));
        try {
            result = (String)future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        return result;
    }

    public static Boolean getElBooleanResult(String express, Object data) throws BizException {
        Boolean result;
        Future<Object> future = executorService.submit(new ElThread(express,data,true));
        try {
            result = (Boolean)future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        return result;
    }
}
