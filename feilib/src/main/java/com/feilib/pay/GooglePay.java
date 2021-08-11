package com.feilib.pay;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 谷歌支付
 * @Author: murphy
 * @CreateDate: 2021/8/10 4:25 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/10 4:25 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 * 使用：
 * 实例GooglePay，传入activity和回调事件
 * toPay发起支付，传入是否为一次性或订阅，商品id
 * queryPurchasesAsync()检查商品信息，onCreate和onResume调用
 * getOrder() 获取保存的订单信息
 * deleteOrder()删除订单信息，传入token即可
 */
public class GooglePay {
    private BillingClient mBillingClient;
    private Activity mActivity;
    private final String TAG = "GooglePay";
    private final String GOOGLE_ORDER = "google_order";
    private GooglePayListener mGooglePayListener;

    interface GooglePayListener {
        //购买成功并返回谷歌订单信息，完成服务器传输后，需要调用deleteOrder删除订单
        void onGooglePaySuccess(Map<String, ?> map);

        //出现错误
        void onGooglePayFail(int state, String msg);
    }

    GooglePay(Activity activity, GooglePayListener googlePayListener) {
        if (activity == null || googlePayListener == null) {
            Log.e(TAG, "GooglePay: 传入的activity和googlePayListener不能为空");
            return;
        }

        mActivity = activity;
        mGooglePayListener = googlePayListener;
        initGoogle();
    }

    /**
     * 初始化监听事件，用于监听谷歌应用回来后的处理
     */
    private void initGoogle() {
        PurchasesUpdatedListener purchasesUpdatedListener = this::dealPaySuccess;

        mBillingClient = BillingClient.newBuilder(mActivity.getApplicationContext())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();
    }

    /**
     * 删除订单
     *
     * @param token 订单token
     */
    public void deleteOrder(String token) {
        if (mActivity == null || TextUtils.isEmpty(token)) {
            return;
        }
        SharedPreferences sharedPreferences = mActivity.getApplicationContext().getSharedPreferences(GOOGLE_ORDER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.remove(token);
        edit.apply();
    }

    public void toPay(boolean isDisposable, String productIds) {
        toPay(isDisposable, new String[]{productIds});
    }

    public void toPay(boolean isDisposable, String[] productIds) {
        querySku(isDisposable, productIds);
    }

    /**
     * 查询或者支付等谷歌操作前，先判断是否已经完成谷歌连接
     */
    private void startTask(boolean isPay, Runnable runnable) {
        boolean isConnect = mBillingClient.getConnectionState() == BillingClient.ConnectionState.CONNECTED;
        if (!isConnect) {
            mBillingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(@NotNull BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        if (isPay) {
                            ThreadManager.getInstance().executeMain(runnable);
                        } else {
                            ThreadManager.getInstance().executeChild(runnable);
                        }
                    } else {
                        Log.e(TAG, "onBillingSetupFinished: 错误码：" + billingResult.getResponseCode()
                                + " 信息：" + billingResult.getDebugMessage());
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    Log.i(TAG, "onBillingServiceDisconnected: 谷歌支付连接断开");
                }
            });
        } else {
            if (isPay) {
                ThreadManager.getInstance().executeMain(runnable);
            } else {
                ThreadManager.getInstance().executeChild(runnable);
            }
        }
    }

    /**
     * 查阅商品后发起购买
     *
     * @param isDisposable 是否为一次性商品
     */
    private void querySku(boolean isDisposable, String[] productIds) {
        startTask(false, () -> {
            List<String> skuList = new ArrayList<>(Arrays.asList(productIds));

            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            //一次性商品或订阅
            params.setSkusList(skuList).setType(isDisposable ? BillingClient.SkuType.INAPP : BillingClient.SkuType.SUBS);
            mBillingClient.querySkuDetailsAsync(params.build(), (billingResult, skuDetailsList) -> {
                // Process the result.
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && skuDetailsList != null && skuDetailsList.size() != 0) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        startPay(skuDetails);
                    }
                } else {
                    Log.e(TAG, "querySku: 查询发生错误，错误码：" + billingResult.getResponseCode()
                            + " 信息：" + billingResult.getDebugMessage());
                    mGooglePayListener.onGooglePayFail(billingResult.getResponseCode(), billingResult.getDebugMessage());
                }
            });
        });
    }

    /**
     * 启动购买
     *
     * @param skuDetails 商品详情
     */
    private void startPay(SkuDetails skuDetails) {
        Runnable runnable = () -> {
            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
            queryPurchasesAsync(skuDetails.getType().equals(BillingClient.SkuType.INAPP));

            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            int responseCode = mBillingClient.launchBillingFlow(mActivity, billingFlowParams).getResponseCode();
            String responseMsg = mBillingClient.launchBillingFlow(mActivity, billingFlowParams).getDebugMessage();
            if (responseCode == BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "startPay发起谷歌购买成功");
            } else {
                Log.e(TAG, "startPay发起谷歌购买成功: 谷歌购买回调失败 错误码" + responseCode + " 信息：" + responseMsg);
                mGooglePayListener.onGooglePayFail(responseCode, responseMsg);
            }
        };

        startTask(true, runnable);
    }


    /**
     * 消耗商品
     *
     * @param purchase 商品信息
     */
    private void handlePurchase(Purchase purchase) {
        Runnable runnable = () -> {
            // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
            // Verify the purchase.
            // Ensure entitlement was not already granted for this purchaseToken.
            // Grant entitlement to the user.
            ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();

            ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
                try {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                        //消耗商品信息成功
                        Log.i(TAG, "handlePurchase: 消耗商品成功");
                    } else {
                        Log.e(TAG, "onConsumeResponse: 消耗商品信息失败,错误码：" + billingResult.getResponseCode()
                                + " 错误信息：" + billingResult.getDebugMessage());
                        mGooglePayListener.onGooglePayFail(billingResult.getResponseCode(), billingResult.getDebugMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            mBillingClient.consumeAsync(consumeParams, listener);
        };

        startTask(false, runnable);
    }

    /**
     * 自行查询未处理交易商品
     * 在onCreate、onResume回来时候调用
     */
    public void queryPurchasesAsync(boolean isDisposable) {
        PurchasesResponseListener purchasesResponseListener = this::dealPaySuccess;
        mBillingClient.queryPurchasesAsync(isDisposable ? BillingClient.SkuType.INAPP : BillingClient.SkuType.SUBS, purchasesResponseListener);
    }


    /**
     * 处理交易后及未处理的商品
     */
    private void dealPaySuccess(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.i(TAG, "dealPaySuccess: ");
            if (purchases.size() == 0) {
                return;
            }

            for (Purchase purchase : purchases) {
                //保存订单并且直接消耗商品
                saveOrder(purchase.getPurchaseToken(), purchase);
                handlePurchase(purchase);
                mGooglePayListener.onGooglePaySuccess(getOrder());
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.i(TAG, "onPurchasesUpdated: 用户取消购买");
            mGooglePayListener.onGooglePayFail(billingResult.getResponseCode(), billingResult.getDebugMessage());
        } else {
            Log.e(TAG, "dealPaySuccess: 处理交易后信息失败。错误码：" + billingResult.getResponseCode()
                    + " 信息:" + billingResult.getDebugMessage());
            mGooglePayListener.onGooglePayFail(billingResult.getResponseCode(), billingResult.getDebugMessage());
        }
    }

    /**
     * 保存订单
     */
    private void saveOrder(String purchaseToken, Purchase purchase) {
        if (mActivity == null) {
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("order_id", purchase.getOrderId());
            json.put("purchase_token", purchase.getPurchaseToken());
            json.put("original_json", purchase.getOriginalJson());
            json.put("package_name", purchase.getPackageName());
            json.put("purchase_state", purchase.getPurchaseState());
            json.put("purchase_time", purchase.getPurchaseTime());
            json.put("quantity", purchase.getQuantity());
            json.put("signature", purchase.getSignature());
            json.put("skus", purchase.getSkus());
            json.put("developer_payload", purchase.getDeveloperPayload());
            json.put("account_identifiers", purchase.getAccountIdentifiers());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = mActivity.getApplicationContext().getSharedPreferences(GOOGLE_ORDER, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(purchaseToken, json.toString());
        edit.apply();
    }

    /**
     * 获取订单
     *
     * @return 订单
     */
    public Map<String, ?> getOrder() {
        if (mActivity == null) {
            return new HashMap<>();
        }

        SharedPreferences sharedPreferences = mActivity.getApplicationContext().getSharedPreferences(GOOGLE_ORDER, Activity.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }
}
