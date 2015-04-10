package com.example.downapk;

/**
 * 下载回调
 * @author 宋云星
 *
 */
public interface DownloadFileCallback {
        void downloadSuccess(Object obj);//下载成功
        void downloadError(Exception e,String msg);//下载失败
}
