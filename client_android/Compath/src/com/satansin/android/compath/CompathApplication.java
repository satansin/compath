package com.satansin.android.compath;

import java.util.ArrayList;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

public class CompathApplication extends Application {
	
	private static CompathApplication mInstance = null;
	
	public static CompathApplication getInstance() {
		return mInstance;
	}

    private ArrayList<Activity> appActivities = new ArrayList<Activity>();
    
    public void addActivity(Activity activity) {
    	appActivities.add(activity);
    }
    
    public void removeActivity(Activity activity) {
    	appActivities.remove(activity);
    }
    
    public void finishAllActivities() {
    	for (int i = 0; i < appActivities.size(); i++) {
			appActivities.get(i).finish();
		}
    	appActivities.clear();
    }
	
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
	}
	
	public Context getApplicationContext() {
		return super.getApplicationContext();
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(CompathApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
//                Toast.makeText(CompathApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
//                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
//                Toast.makeText(CompathApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
//                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
//                Toast.makeText(CompathApplication.getInstance().getApplicationContext(), 
//                        "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
//                Toast.makeText(CompathApplication.getInstance().getApplicationContext(), 
//                        "请检查您的网络连接是否正常！", Toast.LENGTH_LONG).show();
                CompathApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	CompathApplication.getInstance().m_bKeyRight = true;
//            	Toast.makeText(CompathApplication.getInstance().getApplicationContext(), 
//                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }

}
