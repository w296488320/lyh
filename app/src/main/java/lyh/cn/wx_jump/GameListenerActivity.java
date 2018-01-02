package lyh.cn.wx_jump;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.util.List;

import cn.lyh.wx_jump.R;

public class GameListenerActivity extends Activity implements View.OnTouchListener{
    RocketService myBinder;
    private LinearLayout mLinearLayout;
    private TextView mLocation;
    boolean isFirst;
    private View mView;
    private RocketService.MyHandler mHandler;

     float OriginX=0;//起跳点
     float OriginY=0;
     float TargetX=0;//目标点
     float TargetY=0;
    private MyApplication mApplication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        isFirst=true;
        setContentView(R.layout.activity_main);
        init();
        super.onCreate(savedInstanceState);
    }




    private void init() {

        mApplication = (MyApplication) getApplication();
        mHandler = RocketService.getHandler();
        mLinearLayout = (LinearLayout) findViewById(R.id.ll_all);
        mLocation = (TextView) findViewById(R.id.tv_location);

        mLinearLayout.setOnTouchListener(this);
        LogUtils.e("onCreate","初始化完成");

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isFirst) {
                    // LogUtils.e("First","第一次进入");
                    OriginX = event.getX();
                    OriginY = event.getY();

                    ToastUtils.showToast(MyApplication.getContext(), "起跳点坐标为" + OriginX + "   " + OriginY);
                      isFirst=false;
                    mLocation.setText("请单击目标点");
                    //return true;
                    return true;
                }

                TargetX = event.getX();
                TargetY = event.getY();
                ToastUtils.showToast(MyApplication.getContext(),"目标点为"+TargetX+"   "+TargetY);

                finish();
                //LogUtils.e("销毁","销毁");
                int distance = getDistance(OriginX, OriginY, TargetX, TargetY);
                Log.e("距离",distance+"");
                int time=(int)(distance/0.68);

                executeCMD(time);


                break;

        }
        return true;
    }

    private void executeCMD(final int time) {

        new  Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("time",time+"");
                String cmd="input swipe "+OriginX+" "+OriginY+" "+TargetX+" "+TargetY+" "+time;
                CommandExecution.execCommand(cmd,true);

                mHandler.sendEmptyMessage(2);
            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //点击的是返回键
            LogUtils.e("返回键","11111111111111");
            finish();
            mHandler.sendEmptyMessage(1);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        Message message = new Message();
        message.what=1;
        mHandler.sendMessage(message);
        super.onDestroy();
    }

    int getDistance(float x1, float y1, float x2, float y2){
       double v = Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2);
       return (int)Math.sqrt(v);
   }


    public static void doCmds(List<String> cmds) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(process.getOutputStream());

        for (String tmpCmd : cmds) {
            os.writeBytes(tmpCmd+"\n");
        }

        os.writeBytes("exit\n");
        os.flush();
        os.close();

        process.waitFor();
    }
}
