package com.example.downapk;

/**
 * ���ػص�
 * @author ������
 *
 */
public interface DownloadFileCallback {
        void downloadSuccess(Object obj);//���سɹ�
        void downloadError(Exception e,String msg);//����ʧ��
}
