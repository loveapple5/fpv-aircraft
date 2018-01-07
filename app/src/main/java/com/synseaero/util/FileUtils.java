package com.synseaero.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


public class FileUtils {

    private static final String TAG = FileUtils.class.getName();

    /**
     * //     * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
     * //     *
     * //     * @param context
     * //     *            上下文
     * //     * @param inPath
     * //     *            文件目录，要拷贝的目录
     * //     * @param outPath
     * //     *            目标文件夹位置如：/sdcrad/mydir
     * //
     */
    public static boolean copyFiles(Context context, String inPath, String outPath) {
        Log.i(TAG, "copyFiles() inPath:" + inPath + ", outPath:" + outPath);
        String[] fileNames = null;
        try {// 获得Assets一共有几多文件
            fileNames = context.getAssets().list(inPath);
        } catch (IOException e1) {
            return false;
        }
        if (fileNames.length > 0) {//如果是目录
            File fileOutDir = new File(outPath);
            if (fileOutDir.isFile()) {
                boolean ret = fileOutDir.delete();
                if (!ret) {
                    Log.e(TAG, "delete() FAIL:" + fileOutDir.getAbsolutePath());
                }
            }
            if (!fileOutDir.exists()) { // 如果文件路径不存在
                if (!fileOutDir.mkdirs()) { // 创建文件夹
                    Log.e(TAG, "mkdirs() FAIL:" + fileOutDir.getAbsolutePath());
                    return false;
                }
            }
            for (String fileName : fileNames) { //递归调用复制文件夹
                String inDir = inPath;
                String outDir = outPath + File.separator;
                if (!inPath.equals("")) { //空目录特殊处理下
                    inDir = inDir + File.separator;
                }
                copyFiles(context, inDir + fileName, outDir + fileName);
            }
            return true;
        } else {//如果是文件
            try {
                File fileOut = new File(outPath);
                if (fileOut.exists()) {

                    boolean ret = fileOut.delete();
                    if (!ret) {
                        Log.e(TAG, "delete() FAIL:" + fileOut.getAbsolutePath());
                    }
                }
                boolean ret = fileOut.createNewFile();
                if (!ret) {
                    Log.e(TAG, "createNewFile() FAIL:" + fileOut.getAbsolutePath());
                }
                FileOutputStream fos = new FileOutputStream(fileOut);
                InputStream is = context.getAssets().open(inPath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }


    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
