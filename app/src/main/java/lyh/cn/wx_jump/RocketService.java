package lyh.cn.wx_jump;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.lyh.wx_jump.R;

public class RocketService extends Service implements OnTouchListener{

	private  WindowManager.LayoutParams mParams ;
	private WindowManager mWM;
	private View viewToast;
	public static int mScreenWidth;
	public static int mScreenHeight;
	int startX;
	int startY;
	private Button mButton;
	int btnFlag=0;
	private boolean isDrag;
	private Intent GameIntent;
	private int BUTTONTEXT_CHANGE=1;
	private int CREATE_ACTIVITY=2;
	public static MyHandler mHandler;
	private LinearLayout mLayout;


//	private MyLinearLayout mLinearLayout;
//	private TextView mLocation;
//	private View mView;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//if(v.getId()==mButton.getId()) {
			//对开关按钮进行拖拽事件
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//获取按下的xy坐标
					isDrag = false;
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					//LogUtils.e("开启位置", startX + "   " + startY);
					break;
				case MotionEvent.ACTION_MOVE:
					isDrag = true;
					//获取移动xy坐标和按下的xy坐标做差,做差得到的值移动的距离
					//移动过程中做容错处理
					//第一次移动到的位置,作为第二次移动的初始位置
					int moveX = (int) event.getRawX();
					int moveY = (int) event.getRawY();
					//LogUtils.e("移动位置", moveX + "   " + moveY);
					int disX = moveX - startX;
					int disY = moveY - startY;
					//这里修复一些手机无法触发点击事件的问题
					int distance = (int) Math.sqrt(disX * disX + disY * disY);
					//Log.e("distance---->",distance+"");
					//LogUtils.e("distance", distance + "");
					mParams.x = mParams.x + disX;
					mParams.y = mParams.y + disY;

					//在窗体中仅仅告知吐司的左上角的坐标
					if (mParams.x < 0) {
						mParams.x = 0;
					}
					if (mParams.y < 0) {
						mParams.y = 0;
					}
					if (mParams.x > mScreenWidth - viewToast.getWidth()) {
						mParams.x = mScreenWidth - viewToast.getWidth();
					}

					if (mParams.y > mScreenHeight - 22 - viewToast.getHeight()) {
						mParams.y = mScreenHeight - 22 - viewToast.getHeight();
					}
					//告知吐司在窗体上刷新
					mWM.updateViewLayout(viewToast, mParams);
					//在第一次移动完成后,将最终坐标作为第二次移动的起始坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					//return true;
					if (distance == 0) {
						//给个容错范围，不然有部分手机还是无法点击
						isDrag = false;
						break;
					}
					break;

			}
			return isDrag;
//		}
//		else {
//			//对 按钮开关的监控区进行处理
//			float OriginX=0;//起跳点
//			float OriginY=0;
//			float TargetX;//目标点
//			float TargetY;
//			switch(event.getAction()){
//				case MotionEvent.ACTION_DOWN:
//					if(isFirst){
//						OriginX=  event.getX();
//						OriginY= event.getY();
//						isFirst=false;
//						mLocation.setText("请单击目标点");
//						return false;
//					}
//					TargetX = event.getX();
//					TargetY = event.getY();
//					//mLocation.setText("起始位置为："+"("+event.getX()+" , "+event.getY()+")");
//					int distance = getDistance(OriginX, OriginY, TargetX, TargetX);
//					//0.3-0.5之间
//					int time=(int)(distance/0.37);
//					// adb shell input touchscreen swipe x1 y1 x2 y2 time
//					//String cmd="adb shell input touchscreen "
//					// CommandExecution.execCommand()
//					break;
//			}
//			return false;
//		}
	}
//	boolean isFirst=false;

	@Override
	public void onCreate() {
		//初始化窗体对象
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams= new WindowManager.LayoutParams();
		//获取屏幕宽度
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		
		//初始化布局
		showRocket();
		mHandler = new MyHandler();

		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onDestroy() {
		if(mWM!=null && viewToast!=null){
			mWM.removeView(viewToast);
		}
		super.onDestroy();
	}
	
	private void showRocket() {
		//给吐司定义出来的参数(宽高,类型,触摸方式)

		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE	因为吐司需要根据手势去移动,所以必须要能触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		mParams.format = PixelFormat.TRANSLUCENT;
		//层级关系 永远保持在最上层
		//		应用 Window	1~99
		//		子 Window	1000~1999
		//		系统 Window	2000~2999
		mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		//将吐司放置在左上角显示
		mParams.gravity=Gravity.TOP+Gravity.LEFT;
		//定义吐司布局xml--->view挂载到屏幕上
		viewToast = View.inflate(this, R.layout.rocket_view, null);
		mButton = (Button) viewToast.findViewById(R.id.bt_switch);

		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					if(btnFlag==0){
						startControl();
						btnFlag=1;
						mButton.setText("已开启");
					}else {
						stopControl();
						btnFlag=0;
						mButton.setText("开启");
					}
			}
		});
		mButton.setOnTouchListener(this);
		mWM.addView(viewToast, mParams);
	}

	private void stopControl() {

	//	if(mLayout!=null){
			//mLayout.setVisibility(View.VISIBLE);
		//}else {
			ActivityManager am = (ActivityManager) MyApplication.getContext().getSystemService(MyApplication.getContext().ACTIVITY_SERVICE);
			List<ActivityManager.AppTask> appTasks = am.getAppTasks();
			if(appTasks.size()>0){
				ActivityManager.AppTask appTask = appTasks.get(0);
				appTask.finishAndRemoveTask();
			}
		//}
	}

	//	ToastUtils.showToast(MyApplication.getContext(),"停止");
	//开启监控 控件
	private void startControl() {
//		if(mView==null){
//			mView = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.activity_main, null);
//		}
//		mLocation = (TextView) mView.findViewById(R.id.tv_location);
//		mLinearLayout=(MyLinearLayout) mView.findViewById(R.id.ll_all);
//		mLinearLayout.setOnTouchListener(this);
//		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
//				WindowManager.LayoutParams.MATCH_PARENT,
//				WindowManager.LayoutParams.MATCH_PARENT,
//				0, 0,
//				PixelFormat.TRANSPARENT
//		);
//
//		// flag 设置 Window 属性
//		layoutParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//		// type 设置 Window 类别（层级）
//		layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
//
//
//		layoutParams.gravity = Gravity.CENTER;
//		//WindowManager windowManager = getWindowManager();
//		mWM.addView(mView, layoutParams);

		//ToastUtils.showToast(MyApplication.getContext(),"开启");
		startGameActivity();
	}
	 private void startGameActivity(){
		//if(mLayout==null) {
			GameIntent = new Intent(MyApplication.getContext(), GameListenerActivity.class);
			GameIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(GameIntent);
		//}else {
			//mLayout.setVisibility(View.VISIBLE);
		//}
	 }



	class  MyHandler extends  Handler{
		@Override
		public void handleMessage(Message msg) {
			//返回键销毁游戏监听页面
			if(msg.what==BUTTONTEXT_CHANGE){
				mButton.setText("开启");
				btnFlag=0;
				stopControl();
			}
			else if(msg.what==CREATE_ACTIVITY){
			//	startGameActivity();
				//mLayout = (LinearLayout) msg.obj;
				//mLayout.setVisibility(View.VISIBLE);
				mButton.setText("开启");
				btnFlag=0;
			}
			super.handleMessage(msg);
		}
	}
	public static MyHandler getHandler(){
		return mHandler;
	}
}
