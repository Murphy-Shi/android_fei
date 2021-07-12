package com.example.fei;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.feilib.ui.web.MyWebViewUtil;

public class WebActivity extends MainActivity implements AdapterView.OnItemClickListener {
    protected String[] mListName = {"界面弹出", "界面局部", "Activity弹出", "Activity竖屏", "Activity横屏", "开关进度条", "开关加载完弹出"};

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = "https://www.runoob.com/w3cnote/android-tutorial-file.html";
        switch (position) {
            case 0:
                MyWebViewUtil.getInstance().showScreen(this, url);
                break;
            case 1:
                MyWebViewUtil.getInstance().showPart(this, url, 500, 500);
                break;
            case 2:
                MyWebViewUtil.getInstance().showActivity(this, url);
                break;
            case 3:
                MyWebViewUtil.getInstance().showActivityVertical(this, url);
                break;
            case 4:
                MyWebViewUtil.getInstance().showActivityHorizontal(this, url);
                break;
            case 5:
                MyWebViewUtil.getInstance().isProgress = !MyWebViewUtil.getInstance().isProgress;
                showToat("状态isProgress：" + MyWebViewUtil.getInstance().isProgress);
                break;
            case 6:
                MyWebViewUtil.getInstance().isFinishShow = !MyWebViewUtil.getInstance().isFinishShow;
                showToat("状态isFinishShow：" + MyWebViewUtil.getInstance().isFinishShow);
                break;

            default:
                break;
        }
    }

    @Override
    protected void initListView() {
        mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListName));
        mListView.setOnItemClickListener(this);

    }
}