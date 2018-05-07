package com.example.administrator.hotfixdemo;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;

import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

public class MyApplication extends Application{

    private final String TAG ="MyApplication";


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        initHotfix();
    }

   private void  initHotfix(){
       String appVersion;
       try {
           appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
       } catch (Exception e) {
           appVersion = "1.0.0";
       }
       final SophixManager instance=  SophixManager.getInstance();
       instance.setContext(this)
               .setAppVersion(appVersion)
               .setSecretMetaData(null,null,null)
               .setAesKey(null) //可选
               .setEnableDebug(true)//可选
               .setEnableFullLog()
               .setEnableDebug(true)
               .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                   @Override
                   public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                       // 补丁加载回调通知
                       if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                           Log.i(TAG, "sophix load patch success!");
                           // 表明补丁加载成功
                       } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                           Log.i(TAG, "sophix preload patch success. restart app to make effect.");
                           instance.killProcessSafely();

                           // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                           // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                       } else {
                           // 其它错误信息, 查看PatchStatus类说明
                       }
                   }
               }).initialize();
// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中

   }

    @Override
    public void onCreate() {
        super.onCreate();

    }


}
