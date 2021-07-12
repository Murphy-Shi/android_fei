package com.example.fei.toolInfo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.fei.R;
import com.feilib.tool.EncryptUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/7/2 3:11 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/2 3:11 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class Encrypt {
    public void test(Activity activity){
        String str = "123456";
        System.out.println("一次加密：" + EncryptUtil.md5(str));
        System.out.println("二次加密：" + EncryptUtil.md5(str, 2));
        System.out.println("盐值加密：" + EncryptUtil.md5(str, "78"));

        Bitmap bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
        System.out.println("IO文件加密：" + EncryptUtil.md5(compressImage(bmp), EncryptUtil.MD5_TYPE_IO));
        System.out.println("NIO文件加密：" + EncryptUtil.md5(compressImage(bmp), EncryptUtil.MD5_TYPE_NIO));

        String baseStr = EncryptUtil.base64EncodeStr("123456");
        System.out.println("base64字符串加密：" + baseStr);
        System.out.println("base64字符串解密：" + EncryptUtil.base64DecodedStr(baseStr));

        bmp = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher);
        String baseFile = EncryptUtil.base64EncodeFile(compressImage(bmp));
        System.out.println("base64文件加密：" + baseFile);
        System.out.println("base64文件解密：" + EncryptUtil.base64DecodedFile("/mnt/sdcard/01.jpg", baseFile));

        String key = "1234567890123456";
        String aesStr = EncryptUtil.aes("gzejia", key, Cipher.ENCRYPT_MODE);
        System.out.println("aes对象加密： " + aesStr);
        System.out.println("aes对象解密： " + EncryptUtil.aes(aesStr, key, Cipher.DECRYPT_MODE));


        key = "12345678";
        String desStr = EncryptUtil.des("gzejia", key, Cipher.ENCRYPT_MODE);
        System.out.println("des对象加密： " + desStr);
        System.out.println("des对象解密： " + EncryptUtil.des(desStr, key, Cipher.DECRYPT_MODE));

        String shaStr = "gzejia";
        System.out.println("sha_SHA224对象加密： " + EncryptUtil.sha(shaStr, EncryptUtil.SHA224));
        System.out.println("sha_SHA256对象加密： " + EncryptUtil.sha(shaStr, EncryptUtil.SHA256));
        System.out.println("sha_SHA384对象加密： " + EncryptUtil.sha(shaStr, EncryptUtil.SHA384));
        System.out.println("sha_SHA512对象加密： " + EncryptUtil.sha(shaStr, EncryptUtil.SHA512));

        try {
            byte[] data = "gzejia有中文".getBytes();

            // 密钥与数字签名获取
            Map<String, Object> keyMap = EncryptUtil.getKeyPair();
            String publicKey = EncryptUtil.getKey(keyMap, true);
            Log.e("haha","rsa获取公钥： " + publicKey);
            String privateKey = EncryptUtil.getKey(keyMap, false);
            Log.e("haha", "rsa获取私钥： " + privateKey);

            // 公钥加密私钥解密
            byte[] rsaPublic =
                    EncryptUtil.rsa(data, publicKey, EncryptUtil.RSA_PUBLIC_ENCRYPT);
            System.out.println("rsa公钥加密： " + new String(rsaPublic));
            System.out.println("rsa私钥解密： " + new String(
                    EncryptUtil.rsa(rsaPublic, privateKey, EncryptUtil.RSA_PRIVATE_DECRYPT)));

            // 私钥加密公钥解密
            byte[] rsaPrivate =
                    EncryptUtil.rsa(data, privateKey, EncryptUtil.RSA_PRIVATE_ENCRYPT);
            System.out.println("rsa私钥加密： " + new String(rsaPrivate));
            System.out.println("rsa公钥解密： " + new String(
                    EncryptUtil.rsa(rsaPrivate, publicKey, EncryptUtil.RSA_PUBLIC_DECRYPT)));

            // 私钥签名及公钥签名校验
            String signStr = EncryptUtil.sign(rsaPrivate, privateKey);
            System.out.println("rsa数字签名生成： " + signStr);
            System.out.println("rsa数字签名校验： " +
                    EncryptUtil.verify(rsaPrivate, publicKey, signStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File compressImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 20) {  //循环判断如果压缩后图片是否大于20kb,大于继续压缩 友盟缩略图要求不大于18kb
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        //图片名
        String filename = format.format(date);

        File file = new File(Environment.getExternalStorageDirectory(), filename + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        Log.d("=-=-=-=-=-", "compressImage: " + file);
        // recycleBitmap(bitmap);
        return file;
    }
}
