package com.example.naruto.sharefile;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2018/8/31 0031
 * @Note
 */
public class ShareFunction implements PopupWindow.OnDismissListener {
    private PopupWindow popupWindow;
    private Context context;
    private RelativeLayout rlRoot;
    private View vCover;//遮罩层
    private String filePath;
    private String fileName;
    private onPopupWindowDismissListener onPopupWindowDismissListener;

    public ShareFunction(Context context, RelativeLayout rlRoot, View vCover, String filePath, String fileName) {
        this.context = context;
        this.rlRoot = rlRoot;
        this.vCover = vCover;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public void setOnPopupWindowDismissListener(ShareFunction.onPopupWindowDismissListener onPopupWindowDismissListener) {
        this.onPopupWindowDismissListener = onPopupWindowDismissListener;
    }

    /**
     * 显示PopupWindow
     */
    public void showPopupWindow() {
        initPopupWindow();
        popupWindow.showAtLocation(rlRoot, Gravity.BOTTOM, 0, 0);
        vCover.setVisibility(View.VISIBLE);
        vCover.startAnimation(AnimationUtils.loadAnimation(context, R.anim.cover_show));
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        if (popupWindow != null) {
            return;
        }
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_window_share, null);
        List<Map<String, Object>> shareList = new ArrayList<>();
        List<Map<String, Object>> extendList = new ArrayList<>();
        shareList.add(getShareOperationData("微信", R.drawable.ic_weixin));
        extendList.add(getShareOperationData("保存到本地", R.drawable.ic_save));

        initPopupViewRecyclerView(popupView, R.id.rv_share, shareList);
        initPopupViewRecyclerView(popupView, R.id.rv_extend, extendList);

        popupWindow = makePopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, R.id.bt_cancel);
        popupWindow.setOnDismissListener(this);
    }

    /**
     * 初始化PopupWindow内的RecyclerView
     *
     * @param popupView
     * @param recyclerViewId
     * @param dataList
     */
    private void initPopupViewRecyclerView(View popupView, int recyclerViewId, List<Map<String, Object>> dataList) {
        RecyclerView recyclerView = (RecyclerView) popupView.findViewById(recyclerViewId);
        SelectOperationAdapter.MyOnclickListener onclickListener = recyclerViewId == R.id.rv_share ? new ShareOperationClickListener() : new ExtendOperationClickListener();
        SelectOperationAdapter adapter = new SelectOperationAdapter(dataList, context, onclickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    private Map<String, Object> getShareOperationData(String text, int iconResId) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        map.put("icon", iconResId);
        return map;
    }

    /**
     * 创建PopupWindow
     *
     * @param contentView
     * @param width
     * @param height
     * @param closeButtonId
     * @return
     */
    private PopupWindow makePopupWindow(View contentView, int width, int height, int closeButtonId) {
        // 创建PopupWindow对象，指定宽度和高度
        final PopupWindow window = new PopupWindow(contentView, width, height);
        if (closeButtonId != -1) {
            contentView.findViewById(closeButtonId).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    window.dismiss();
                }
            });
        }

        // 设置动画
        window.setAnimationStyle(R.style.popup_window_anim);

        // 设置背景
        window.setBackgroundDrawable(new ColorDrawable(0x00ffffff));

        // 设置可以获取焦点（防止按返回按钮时直接关闭当前activity）
        window.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        window.setOutsideTouchable(true);
        // 更新popupwindow的状态
        window.update();
        return window;
    }

    /**
     * 复制
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在
                InputStream inStream = new FileInputStream(oldPath);
                File n = new File(newPath);
                if (!n.exists()) {
                    n.createNewFile();
                }
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节 文件大小
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public void onDismiss() {
        android.view.animation.Animation animation = AnimationUtils.loadAnimation(context, R.anim.cover_hide);
        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                if (onPopupWindowDismissListener != null) {
                    onPopupWindowDismissListener.onDismiss();
                }
                vCover.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });
        vCover.startAnimation(animation);
    }

    /**
     * @Purpose popupWindow里面的“分享”模块的点击事件监听
     * @Author Naruto Yang
     * @CreateDate 2018/8/30 0030
     * @Note
     */
    private class ShareOperationClickListener implements SelectOperationAdapter.MyOnclickListener {

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (tag == R.drawable.ic_weixin) {
                new ShareFileUtils(context).shareToWeChatWithFile(null, filePath);
            }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }
    }

    /**
     * @Purpose popupWindow里面的“扩展”模块的点击事件监听
     * @Author Naruto Yang
     * @CreateDate 2018/8/30 0030
     * @Note
     */
    private class ExtendOperationClickListener implements SelectOperationAdapter.MyOnclickListener {

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (tag == R.drawable.ic_save) {
                String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                boolean b = copyFile(filePath, folderPath + "/" + fileName);
                if (b) {
                    Toast.makeText(context, "文件已保存至" + folderPath, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }
    }

    public static interface onPopupWindowDismissListener {
        void onDismiss();
    }
}
