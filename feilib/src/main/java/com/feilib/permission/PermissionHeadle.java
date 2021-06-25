package com.feilib.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

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
    protected WeakReference<Activity> weakReference;
    private Activity mActivity;
    private onResult mOnResult;
    protected String[] mPermissions;
    protected PermissionFragment mPermissionFragment;
    public static final int REQUESTCODE = 8999;


    public PermissionHeadle(Activity activity){
        if(activity == null){
            throw new IllegalArgumentException("activity为空");
        }

        weakReference = new WeakReference<>(activity);;
        mActivity = weakReference.get();

        mPermissionFragment = new PermissionFragment();
        mActivity.getFragmentManager().beginTransaction().add(mPermissionFragment,"PermissionHeadle").commit();
        mActivity.getFragmentManager().executePendingTransactions();
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
        void onResult(boolean allGranted, ArrayList grantedList, ArrayList deniedList);
    }

    public interface onResult{
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }

    public PermissionHeadle need(String permission){
        need(new String[]{permission});
        return this;
    }

    public PermissionHeadle need(String[] permission){
        mPermissions = permission;
        return this;
    }

    public PermissionHeadle setOnResult(onResult onResult){
        mOnResult = onResult;
        return this;
    }

    public void dialog(){

    }

    public void dialog(String content, String sure, String cancel){

    }

    public void requestPremission(Subscribe subscribe){
        if(subscribe == null || mPermissions.length == 0){
            Log.e(TAG, "传入的subscribe、permissions不能为空");
            return;
        }
        mPermissionFragment.postRequestPermissions(mPermissions, subscribe);
    }

    //检查权限
    public boolean cheackPermission(String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED;
        }
        return true;
    }

    static public class PermissionFragment extends Fragment{
        private Subscribe mSubscribe;
        private onResult mOnResult;
        private ArrayList<Runnable> runnables = new ArrayList<>();

        public void setOnResult(onResult onResult){
            mOnResult = onResult;
        }

        public void postRequestPermissions(final String[] permissions, Subscribe subscribe){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mSubscribe = subscribe;
                requestPermissions(permissions, REQUESTCODE);
                if(isAdded()){
                    requestPermissions(permissions, REQUESTCODE);
                } else {
                    runnables.add(new Runnable() {
                        @Override
                        public void run() {
                            requestPermissions(permissions, REQUESTCODE);
                        }
                    });
                }
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Log.d("PermissionFragment", "延迟执行");
            while (!runnables.isEmpty()) {
                runnables.remove(0).run();
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(mOnResult != null){
                mOnResult.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }

            if(requestCode == REQUESTCODE){
                ArrayList<String> grantList = new ArrayList<>();
                ArrayList<String> deniedList = new ArrayList<>();
                for (int grant: grantResults){
                    if(grant == PackageManager.PERMISSION_DENIED){
                        grantList.add(permissions[grant]);
                    } else {
                        deniedList.add(permissions[grant]);
                    }
                }

                boolean allGranted = deniedList.size() > 0;
                mSubscribe.onResult(allGranted, grantList, deniedList);
            }
        }
    }
}
