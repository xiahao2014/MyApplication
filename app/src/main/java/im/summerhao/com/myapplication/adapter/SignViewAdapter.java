package im.summerhao.com.myapplication.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import im.summerhao.com.myapplication.R;


/**
 * Created by lenovo on 2015/10/16.
 */
public class SignViewAdapter extends PagerAdapter {
    private View.OnClickListener clickListener;
    private List<View> views;

    public SignViewAdapter(List<View> views) {
        this.views = views;
    }

    public int getCount() {
        return (views == null) ? 0 : views.size();
    }

    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        final View view;
        switch (position) {
            case 0:
                view = views.get(0);
                view.findViewById(R.id.ui_btn_register).setOnClickListener(clickListener);
                break;
            case 1:
                view = views.get(1);
                view.findViewById(R.id.activity_sign_view_perfect_account_commit).setOnClickListener(clickListener);
                break;
            case 2:
                view = views.get(2);
                view.findViewById(R.id.activity_sign_view_upload_avatar_commit).setOnClickListener(clickListener);
                view.findViewById(R.id.activity_sign_view_upload_avatar_avatar).setOnClickListener(clickListener);
                view.findViewById(R.id.activity_sign_view_upload_avatar_layout).setOnClickListener(clickListener);
                break;
            default:
                view = views.get(position);
        }
        container.addView(view);
        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    /**
     * 适配器内容监听器
     * @param clickListener
     */
    public void setOnSignViewClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
