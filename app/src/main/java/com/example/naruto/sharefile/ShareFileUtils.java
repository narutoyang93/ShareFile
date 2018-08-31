package com.example.naruto.sharefile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/8/29 0029
 * @Note 7.0及以上版本，需要在在Application.onCreate加入如下代码，置入一个不设防的VmPolicy，防止出现FileUriExposedException异常：
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
 * StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
 * StrictMode.setVmPolicy(builder.build());
 * //builder.detectFileUriExposure();
 * }
 */
public class ShareFileUtils {
    /**
     * 上下文
     */
    private Context context;

    private final static String PACKAGE_NAME_WE_CHAT = "com.tencent.mm";
    private final static String ACTIVITY_NAME_WE_CHAT = "com.tencent.mm.ui.tools.ShareImgUI";
    private final static String APP_NAME_WE_CHAT = "微信";

    public ShareFileUtils(Context context) {
        this.context = context;
    }

    /**
     * 分享到微信朋友圈(分享朋友圈一定需要图片)
     *
     * @param msgTitle (分享标题)
     * @param msgText  (分享内容)
     * @param bitmap   (分享图片) BitmapFactory.decodeResource(context.getResources(), R.mipmap.create_company)
     */
    public void shareToWeChatFriendCircle(String msgTitle, String msgText, Bitmap bitmap) {
        sharePicture("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI",
                "微信", msgTitle, msgText, null, bitmap);
    }

    private void shareToQQ(String msgTitle, String msgText, String type, Uri uri) {
        share("com.tencent.mobileqq",
                "com.tencent.mobileqq.activity.JumpActivity", "QQ", msgTitle,
                msgText, type, uri);
    }

    public void shareToWeChatWithText(String msgTitle, String msgText) {
        shareText(PACKAGE_NAME_WE_CHAT, ACTIVITY_NAME_WE_CHAT, APP_NAME_WE_CHAT, msgTitle, msgText);
    }

    public void shareToWeChatWithPicture(String msgTitle, String msgText, Uri uri, Bitmap bitmap) {
        sharePicture(PACKAGE_NAME_WE_CHAT, ACTIVITY_NAME_WE_CHAT, APP_NAME_WE_CHAT, msgTitle, msgText, uri, bitmap);
    }

    public void shareToWeChatWithFile(Uri uri, String path) {
        shareFile(PACKAGE_NAME_WE_CHAT, ACTIVITY_NAME_WE_CHAT, APP_NAME_WE_CHAT, "", "", uri, path);
    }

    public void shareText(String packageName, String activityName, String appname, String msgTitle, String msgText) {
        share(packageName, activityName, appname, msgTitle, msgText, "text/plain", null);
    }

    public void sharePicture(String packageName, String activityName, String appname, String msgTitle, String msgText, Uri uri, Bitmap bitmap) {
        if (uri == null) {
            if (bitmap == null) {
                Toast.makeText(context, "图片数据异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null, null));
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "获取分享内容异常", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (uri != null) {
            share(packageName, activityName, appname, msgTitle, msgText, "image/*", uri);
        }
    }

    public void shareFile(String packageName, String activityName, String appname, String msgTitle, String msgText, Uri uri, String path) {
        if (uri == null) {
            if (TextUtils.isEmpty(path)) {
                Toast.makeText(context, "文件路径异常", Toast.LENGTH_LONG).show();
            } else {
                try {
                    uri = getFileUri(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "获取分享内容异常", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (uri != null) {
            share(packageName, activityName, appname, msgTitle, msgText, "application/*", uri);
        }
    }

    /**
     * 分享
     *
     * @param packageName  (包名,跳转的应用的包名)
     * @param activityName (类名,跳转的页面名称)
     * @param appname      (应用名,跳转到的应用名称)
     * @param msgTitle     (标题)
     * @param msgText      (内容)
     * @param type         (类型)
     * @param uri
     */
    private void share(String packageName, String activityName, String appname, String msgTitle, String msgText, String type, Uri uri) {
        if (!packageName.isEmpty() && !isAvilible(context, packageName)) {// 判断APP是否存在
            Toast.makeText(context, "请先安装" + appname, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (uri != null) {
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }
        if (!packageName.isEmpty()) {
            intent.setComponent(new ComponentName(packageName, activityName));
            context.startActivity(intent);
        } else {
            context.startActivity(Intent.createChooser(intent, msgTitle));
        }
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    private boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * 根据文件路径获取Uri
     *
     * @param filePath
     * @return
     */
    public static Uri getFileUri(String filePath) {
        File file = new File(filePath);
        Uri uri = null;
        if (file.exists()) {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
