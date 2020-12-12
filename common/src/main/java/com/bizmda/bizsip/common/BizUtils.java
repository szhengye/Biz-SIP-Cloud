package com.bizmda.bizsip.common;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class BizUtils {
    private static ExecutorService executorService = ThreadUtil.newExecutor(5);

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

    public static String getELStringResult(String express, JSONObject data) throws BizException {
//        ExecutorService service = ThreadUtil.newSingleExecutor();
        BizMessage bizMessage;
        Future<BizMessage> future = executorService.submit(new ELThread(express,data,false));
        try {
            bizMessage = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        if (bizMessage.getCode() != 0) {
            throw new BizException(bizMessage);
        }
        return (String)bizMessage.getData();
    }

    public static Boolean getELBooleanResult(String express, JSONObject data) throws BizException {
//        ExecutorService service = ThreadUtil.newExecutor(5);
        BizMessage bizMessage;
        Future<BizMessage> future = executorService.submit(new ELThread(express,data,true));
        try {
            bizMessage = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException(BizResultEnum.EL_CALCULATE_ERROR,e,
                    StrFormatter.format("EL表达式计算出错:{}",express));
        }
        if (bizMessage.getCode() != 0) {
            throw new BizException(bizMessage);
        }
        return (Boolean) bizMessage.getData();
    }
}
