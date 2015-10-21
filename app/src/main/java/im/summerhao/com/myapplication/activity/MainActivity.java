package im.summerhao.com.myapplication.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smackx.packet.DiscoverItems;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.bean.ChartHisBean;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.fragment.BaseFragment;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.ui.BottomControlPanel;

public class MainActivity extends ActivitySupport implements BottomControlPanel.BottomPanelCallback {


    BottomControlPanel bottomPanel = null;
    public static String currFragTag = "";
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private TextView title;
    private ImageButton add_group_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        fragmentManager = getSupportFragmentManager();
        setDefaultFirstFragment(Constant.FRAGMENT_FLAG_CONTACTS);

    }

    private void initUI() {

        bottomPanel = (BottomControlPanel) findViewById(R.id.bottom_layout);
        if (bottomPanel != null) {
            bottomPanel.initBottomPanel();
            bottomPanel.setBottomCallback(this);
        }
    }


    private void setDefaultFirstFragment(String tag) {
        Log.i("yan", "setDefaultFirstFragment enter... currFragTag = " + currFragTag);
        setTabSelection(tag);
        bottomPanel.defaultBtnChecked();
        Log.i("yan", "setDefaultFirstFragment exit...");
    }


    private void commitTransactions(String tag) {
        if (fragmentTransaction != null && !fragmentTransaction.isEmpty()) {
            fragmentTransaction.commit();
            currFragTag = tag;
            fragmentTransaction = null;
        }
    }

    private FragmentTransaction ensureTransaction() {
        if (fragmentTransaction == null) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        }
        return fragmentTransaction;

    }

    /**
     * 先判断这个fragment是不是被detach掉的，如果是的话意味着之前曾被add过，
     * 所以只需attach就ok了。否则的话，意味着这是第一次，进行add.这里记录下
     *
     * @param layout
     * @param f
     * @param tag
     */
    private void attachFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                fragmentTransaction.attach(f);

            } else if (!f.isAdded()) {
                ensureTransaction();
                fragmentTransaction.add(layout, f, tag);
            }
        }
    }

    private Fragment getFragment(String tag) {

        Fragment f = fragmentManager.findFragmentByTag(tag);

        if (f == null) {
            Toast.makeText(getApplicationContext(), "fragment = null tag = " + tag, Toast.LENGTH_SHORT).show();
            f = BaseFragment.newInstance(getApplicationContext(), tag);
        }
        return f;

    }


    private void detachFragment(Fragment f) {

        if (f != null && !f.isDetached()) {
            ensureTransaction();
            fragmentTransaction.detach(f);
        }
    }


    /**
     * 切换fragment
     *
     * @param tag
     */
    private void switchFragment(String tag) {
        if (TextUtils.equals(tag, currFragTag)) {
            return;
        }
        //把上一个fragment detach掉
        if (currFragTag != null && !currFragTag.equals("")) {
            detachFragment(getFragment(currFragTag));
        }
        attachFragment(R.id.fragment_content, getFragment(tag), tag);
        commitTransactions(tag);
    }

    /**
     * 设置选中的Tag
     *
     * @param tag
     */
    public void setTabSelection(String tag) {
        // 开启一个Fragment事务
        fragmentTransaction = fragmentManager.beginTransaction();
        switchFragment(tag);

    }
    @Override
    public void onCreateCustomToolBar(Toolbar toolbar) {
        super.onCreateCustomToolBar(toolbar);
        toolbar.showOverflowMenu();
        View inflate = getLayoutInflater().inflate(R.layout.toobar_button, toolbar);
        title = (TextView) inflate.findViewById(R.id.title);
        title.setText("联系人");
        add_group_chat = (ImageButton) inflate.findViewById(R.id.add_group_chat);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExit();
            }
        });
    }


    @Override
    public void onBottomPanelClick(int itemId) {
        String tag = "";
        if ((itemId & Constant.BTN_FLAG_MESSAGE) != 0) {
            tag = Constant.FRAGMENT_FLAG_MESSAGE;
            title.setText(Constant.FRAGMENT_FLAG_MESSAGE);
            add_group_chat.setVisibility(View.VISIBLE);
            //toolbar.setTitle(Constant.FRAGMENT_FLAG_MESSAGE);
            //setSupportActionBar(toolbar);
        } else if ((itemId & Constant.BTN_FLAG_CONTACTS) != 0) {
            tag = Constant.FRAGMENT_FLAG_CONTACTS;
            title.setText(Constant.FRAGMENT_FLAG_CONTACTS);
            add_group_chat.setVisibility(View.INVISIBLE);
            //toolbar.setTitle(Constant.FRAGMENT_FLAG_CONTACTS);
            //setSupportActionBar(toolbar);
        } else if ((itemId & Constant.BTN_FLAG_NEWS) != 0) {
            tag = Constant.FRAGMENT_FLAG_NEWS;
            title.setText(Constant.FRAGMENT_FLAG_NEWS);
            add_group_chat.setVisibility(View.INVISIBLE);
           // toolbar.setTitle(Constant.FRAGMENT_FLAG_NEWS);
           // setSupportActionBar(toolbar);
        } else if ((itemId & Constant.BTN_FLAG_SETTING) != 0) {
            tag = Constant.FRAGMENT_FLAG_SETTING;
            title.setText(Constant.FRAGMENT_FLAG_SETTING);
            add_group_chat.setVisibility(View.INVISIBLE);
           // toolbar.setTitle(Constant.FRAGMENT_FLAG_SETTING);
           // setSupportActionBar(toolbar);
        }
        setTabSelection(tag); //切换Fragment
    }

    @Override
    public void onBackPressed() {
        isExit();
    }
}
