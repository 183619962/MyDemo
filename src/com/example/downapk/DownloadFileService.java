package com.example.downapk;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
/**
 * �ļ����ص�service
 * @author ������
 *
 */
public class DownloadFileService extends Service {

        private DownloadFileUtils downloadFileUtils;//�ļ����ع�����
        private String filePath;//�����ڱ��ص�·��
        private NotificationManager notificationManager;//״̬��֪ͨ������
        private Notification notification;//״̬��֪ͨ
        private RemoteViews remoteViews;//״̬��֪ͨ��ʾ��view
        private final int notificationID = 1;//֪ͨ��id
        private final int updateProgress = 1;//����״̬�������ؽ���
        private final int downloadSuccess = 2;//���سɹ�
        private final int downloadError = 3;//����ʧ��
        private final String TAG = "DownloadFileService";
        private Timer timer;//��ʱ�������ڸ������ؽ���
        private TimerTask task;//��ʱ��ִ�е�����
        @Override
        public IBinder onBind(Intent intent) {
                
                return null;
        }

        @Override
        public void onCreate() {
                init();
        }
        
        private void init(){
                filePath = Environment.getExternalStorageDirectory() + "/mydownapp";
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notification = new Notification();
                notification.icon = R.drawable.ic_launcher;//����֪ͨ��Ϣ��ͼ��
                notification.tickerText = "�������ء�����";//����֪ͨ��Ϣ�ı���
                remoteViews = new RemoteViews(getPackageName(), R.layout.down_notification);
                remoteViews.setImageViewResource(R.id.IconIV, R.drawable.ic_launcher);
                timer = new Timer();
                task = new TimerTask() {
                        
                        @Override
                        public void run() {
                                handler.sendEmptyMessage(updateProgress);
                        }
                };
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
                new Thread(new Runnable() {
                        
                        @Override
                        public void run() {
                                downloadFileUtils = new DownloadFileUtils("http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk", filePath, "abc.apk", 1,callback);
                                downloadFileUtils.downloadFile();
                        }
                }).start();
                timer.schedule(task, 0, 500);
                return super.onStartCommand(intent, flags, startId);
        }
        
        
        
        @Override
        public void onDestroy() {
                Log.i(TAG, TAG+" is onDestory...");
                super.onDestroy();
        }


        
        Handler handler = new Handler(){

                @Override
                public void handleMessage(Message msg) {
                        if (msg.what == updateProgress) {//�������ؽ���
                                long fileSize = downloadFileUtils.getFileSize();
                                long totalReadSize = downloadFileUtils.getTotalReadSize();
                                if(totalReadSize > 0){
                                        float size = (float) totalReadSize * 100 / (float) fileSize;
                                        DecimalFormat format = new DecimalFormat("0.00");
                                        String progress = format.format(size);
                                        remoteViews.setTextViewText(R.id.progressTv, "������" + progress+ "%");
                                        remoteViews.setProgressBar(R.id.progressBar, 100, (int) size,false);
                                        notification.contentView = remoteViews;
                                        notificationManager.notify(notificationID, notification);
                                }
                        } else if (msg.what == downloadSuccess) {//�������
                                remoteViews.setTextViewText(R.id.progressTv, "�������");
                                remoteViews.setProgressBar(R.id.progressBar, 100, 100, false);
                                notification.contentView = remoteViews;
                                notificationManager.notify(notificationID, notification);
                                if(timer != null && task != null){
                                        timer.cancel();
                                        task.cancel();
                                }
                                stopService(new Intent(getApplicationContext(),DownloadFileService.class));//stop service
                        } else if (msg.what == downloadError) {//����ʧ��
                                if(timer != null && task != null){
                                        timer.cancel();
                                        task.cancel();
                                }
                                notificationManager.cancel(notificationID);
                                stopService(new Intent(getApplicationContext(),DownloadFileService.class));//stop service
                        }
                }
                
        };
        /**
         * ���ػص�
         */
        DownloadFileCallback callback = new DownloadFileCallback() {
                
                @Override
                public void downloadSuccess(Object obj) {
                        handler.sendEmptyMessage(downloadSuccess);
                }
                
                @Override
                public void downloadError(Exception e, String msg) {
                        handler.sendEmptyMessage(downloadError);
                }
        };
        

}
