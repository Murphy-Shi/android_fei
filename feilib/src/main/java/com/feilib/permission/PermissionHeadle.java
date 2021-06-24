package com.feilib.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.security.cert.CertificateFactorySpi;
import java.util.ArrayList;

/**
 * @Description: 请求权限
 * @Author: murphy
 * @CreateDate: 2021/6/22 5:07 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/6/22 5:07 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class PermissionHeadle {
    private final String TAG = "PermissionHeadle";
    protected Activity mActivity;
    protected String[] mPermissions;
    protected PermissionFragment mPermissionFragment;

    public PermissionHeadle(Activity activity){
        if(activity == null){
            throw new IllegalArgumentException("activity为空");
        }
        mPermissionFragment = new PermissionFragment();
        fragmentTransaction(activity.getFragmentManager());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public PermissionHeadle(Fragment fragment){
        if(fragment == null){
            throw new IllegalArgumentException("activity为空");
        }
        mPermissionFragment = new PermissionFragment();
        fragmentTransaction(fragment.getChildFragmentManager());
    }

    private void fragmentTransaction(FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(mPermissionFragment, PermissionFragment.class.getSimpleName());
        fragmentTransaction.commit();
        if (mPermissionFragment.isAdded()) {
            Log.d(TAG, "已经被添加到Activity");
        } else {
            Log.d(TAG, "尚未添加到Activity");
        }
    }

    /**
     * 权限订阅
     */
    public interface Subscribe{
        void onResult(boolean allGranted, String[] permissions);
    }

    public void need(String permission){
        need(new String[]{permission});
    }

    public void need(String[] permission){
        mPermissions = permission;
    }

    public void dialog(){

    }

    public void dialog(String content, String sure, String cancel){

    }


    public void requestPremission(Subscribe subscribe){
        if(subscribe == null || mActivity == null || mPermissions.length == 0){
            throw new IllegalArgumentException("传入的activity、subscribe、permissions不能为空");
        }
        PermissionFragment permissionFragment = new PermissionFragment();
        permissionFragment.postRequestPermissions(mPermissions, subscribe);
    }

    public boolean cheackPermission(String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED;
        }
        return true;
    }

    static public class PermissionFragment extends Fragment{
        private Subscribe mSubscribe;

        public void postRequestPermissions(final String[] permissions, Subscribe subscribe){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSubscribe = subscribe;
                requestPermissions(permissions, 1);

            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == 1){
                for (int grant: grantResults){
                    if(grant == PackageManager.PERMISSION_DENIED){
                        mSubscribe.onResult(false, permissions);
                    }
                }
                return;
            }
            mSubscribe.onResult(true, permissions);
        }
    }
}
