package lyh.cn.wx_jump;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataOutputStream;

import cn.lyh.wx_jump.R;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        getRoot();
        startServer();

        //init();
    }

    private void startServer() {
        //开启服务
        startService(new Intent(getApplicationContext(), RocketService.class));
        //关闭当前界面
        finish();
    }


    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd="chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    public void getRoot() {
        String apkRoot="chmod 777 "+getPackageCodePath();
        boolean b = upgradeRootPermission(apkRoot);
        if(b){
            ToastUtils.showToast(getApplicationContext(),"以获取root权限");
        }else {
            ToastUtils.showToast(getApplicationContext(),"未获取root权限 程序无法试用！");
            System.exit(0);
        }
        return ;
    }
}
