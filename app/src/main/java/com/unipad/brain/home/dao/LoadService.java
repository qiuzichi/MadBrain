package com.unipad.brain.home.dao;

import android.annotation.TargetApi;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.unipad.brain.R;
import com.unipad.utils.ToastUtil;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LoadService extends Service {
	private Context context;
	private DownloadManager manager;
	private DownloadCompleteReceiver receiver;


	// 在服务中 更新软件
	public Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case 1:
				String path = (String) msg.obj;
//				load(path);
				break;
			default:
				break;
			}
		}
	};
	private Long loaddownId;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		context = getApplicationContext();
		manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		receiver = new DownloadCompleteReceiver();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String path = intent.getStringExtra("loadPath");
		load(path);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


	/* 下载 */
	@SuppressWarnings("deprecation")
	private void load(String path) {
		//删除以前下载的apk 文件
		deleteApkFile();
		String apkName = path.substring(path.lastIndexOf("/") + 1,
				path.length());
		DownloadManager.Request down = new DownloadManager.Request(
				Uri.parse(path));
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
				| DownloadManager.Request.NETWORK_WIFI);
		down.setTitle("正在下载" + apkName);
		// 发出通知，显示下载进度条
		down.setShowRunningNotification(true);

		//下载完成后  隐藏notication
		down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		// 显示下载界面
		down.setVisibleInDownloadsUi(false);
		// 设置下载后文件存放的位置 Environment.getExternalStorageDirectory().getPath() +
		down.setDestinationInExternalPublicDir("MLC/upload", apkName);
		// 将下载请求放入队列
		loaddownId = manager.enqueue(down);

		try {
			context.registerReceiver(receiver, new IntentFilter(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		} catch (Exception e) {
		}
	}

	private void deleteApkFile() {

		File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MLC/upload" );
		if (file.exists()) {
			//删除以前存在的apk结尾文件
			File[] filename = file.listFiles();
			for (int i = 0; i < filename.length; i++) {
				if (filename[i].isFile()) {
					if (filename[i].getName().endsWith("apk")) {
						filename[i].delete();
					}
				}
			}
		}
	}

	// 接受下载完成后的intent
	class DownloadCompleteReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				String fileName = "";
				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);// 设置过滤状态：成功
				Cursor cursor = manager.query(query);// 查询以前下载过的‘成功文件’
				if (cursor.moveToFirst()) {// 移动到最新下载的文件
					fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
				}
				File f = new File(fileName.replace("file://", ""));// 过滤路径
					try {
						if (f.exists()) {
							openFile(f);
						} else {
							ToastUtil.showToast("网络原因 ，下载失败");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}



	/**
	 * 打开文件
	 * 
	 * @param file
	 */
	private void openFile(File file) {
		// Uri uri = Uri.parse("file://"+file.getAbsolutePath());
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型+
		String type = "application/vnd.android.package-archive";
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		// 跳转
		try {
			startActivity(intent);
		} catch (Exception e) {

		}
		//关闭服务
		stopSelf();
		context.unregisterReceiver(receiver);
	}

//	private void updateViews(final long downlaodId) {
//		final Timer myTimer = new Timer();
//		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//		RemoteViews mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_contains_btn_layout);
//		mRemoteViews.setImageViewResource(R.id.iv_notification_icon, R.drawable.ic_launcher);
//
//		mRemoteViews.setTextViewText(R.id.tv_custom_notify_title, getString(R.string.update_version_title) + versionBean.getVersion()
//
//
//				myTimer.schedule(new TimerTask() {
//
//					@Override
//					public void run() {
//						DownloadManager.Query q = new DownloadManager.Query();
//						q.setFilterById(downlaodId);
//						Cursor cursor = ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).query(q);
//						cursor.moveToFirst();
//						int bytes_downloaded = cursor
//								.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//						int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//						cursor.close();
//						final int dl_progress = (bytes_downloaded * 100 / bytes_total);
//						if (dl_progress == 100) {
//							myTimer.cancel();
//							m
//						} else {
//							runOnUiThread(new Runnable() {
//								@Override
//								public void run() {
//									mProgressBar.setProgress(dl_progress);
//									mDownloadFileBtn.setText(dl_progress + "%");
//								}
//							});
//
//						}
//
//					}
//
//				}, 0, 10);
//
//	}
}
