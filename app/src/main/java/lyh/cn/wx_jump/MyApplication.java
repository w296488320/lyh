package lyh.cn.wx_jump;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/12/31.
 */

public  class MyApplication extends Application {

    private static Context context;
    private  int mainThreadId;
    private  boolean isFirst;
    @Override
    public void onCreate() {
        super.onCreate();
        isFirst =true;
        context = getApplicationContext();

    }

    public static Context getContext() {
        return context;
    }

//    @Override
//    public void onTerminate() {
//        isFirst =false;
//        super.onTerminate();
//    }
    public  boolean getIsFirst() {
        return isFirst;
    }
    public  void setIsFirst(boolean isTeination) {
        isFirst =isTeination;
        return ;
    }
}
