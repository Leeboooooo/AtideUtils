package com.atide.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocalService extends Service {
	private static final String TAG = "LocalService";
	/** Notification管理 */
	public NotificationManager mNotificationManager;
	/** Notification构造器 */
	private NotificationCompat.Builder mBuilder;
	/** Notification的ID */
	private int notifyId = 100;
	private int isStop=0;//0表示正常, 1表示停止

//	public static  void launch(Context context){
//		Intent intent = new Intent();
//		intent.setClass(context, LocalService.class);
//		context.startService(intent);
//	}

	/**
	 * 在 Local Service 中我们直接继承 Binder 而不是 IBinder,因为 Binder 实现了 IBinder
	 * 接口，这样我们可以少做很多工作。
	 * 
	 * @author newcj
	 */
	public class SimpleBinder extends Binder {
		/**
		 * 获取 Service 实例
		 * 
		 * @return
		 */
		public LocalService getService() {
			return LocalService.this;
		}

		public int add(int a, int b) {
			return a + b;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "service 启动");
		initNotify();
		initService();
		handler.postDelayed(runnable, 5000);
		return super.onStartCommand(intent, flags, startId);
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				handler.post(runnable);
			}
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
//			showNotify();
			showActivityNotify();
			handler.sendEmptyMessageDelayed(isStop, 5000);
		}
	};

	/** 显示通知栏点击跳转到指定Activity */
	public void showActivityNotify(){
		// Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
		mBuilder.setAutoCancel(true)//点击后让通知将消失
				.setContentTitle("测试标题")
				.setContentText("点击跳转")
				.setTicker("大永高速项目提醒你，你有最新的事物需要处理。");
		//点击的意图ACTION是跳转到Intent
		Intent resultIntent = new Intent(this, NotificationActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pendingIntent);
		mNotificationManager.notify(notifyId, mBuilder.build());
	}


	
	@Override
	public void onDestroy() {
		super.onDestroy();

		isStop = 1;
		handler.post(runnable);
		Log.e(TAG, "service 销毁");
	}

	/** 初始化通知栏 */
	private void initNotify(){
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("测试标题")
				.setContentText("测试内容")
				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
				.setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
						//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);
	}

	/** 显示通知栏 */
	public void showNotify(){
		mBuilder.setContentTitle("测试标题")
				.setContentText("测试内容")
//				.setNumber(number)//显示数量
				.setTicker("测试通知来啦");//通知首次出现在通知栏，带上升动画效果的
		mNotificationManager.notify(notifyId, mBuilder.build());
//		mNotification.notify(getResources().getString(R.string.app_name), notiId, mBuilder.build());
	}

	/**
	 * 初始化要用到的系统服务
	 */
	private void initService() {
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}


	/**
	 * @获取默认的pendingIntent,为了防止2.3及以下版本报错
	 * @flags属性:
	 * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
	 * 点击去除： Notification.FLAG_AUTO_CANCEL
	 */
	public PendingIntent getDefalutIntent(int flags){
		PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
		return pendingIntent;
	}

	public SimpleBinder sBinder;
	@Override
	public void onCreate() {
		super.onCreate();
		// 创建 SimpleBinder
		sBinder = new SimpleBinder();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 返回 SimpleBinder 对象
		return sBinder;
	}

}
