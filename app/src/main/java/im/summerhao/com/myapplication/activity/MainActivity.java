package im.summerhao.com.myapplication.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.bean.ChartHisBean;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.fragment.BaseFragment;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.ui.BottomControlPanel;

public class MainActivity extends AppCompatActivity implements BottomControlPanel.BottomPanelCallback {

    private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
    private List<DiscoverItems.Item> chatRoomList = new ArrayList<DiscoverItems.Item>();
    private List<String> groupNames;
    private List<ContacterManager.MRosterGroup> rGroups;
    private List<String> roomNames = new ArrayList<String>();

    BottomControlPanel bottomPanel = null;
    public static String currFragTag = "";

    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        fragmentManager = getSupportFragmentManager();
        setDefaultFirstFragment(Constant.FRAGMENT_FLAG_MESSAGE);

        initData();

    }

    private void initUI() {

        bottomPanel = (BottomControlPanel) findViewById(R.id.bottom_layout);
        if (bottomPanel != null) {
            bottomPanel.initBottomPanel();
            bottomPanel.setBottomCallback(this);
        }

    }

    private void initData() {

        try {
            /** 获取好友列表 */
            groupNames = ContacterManager.getGroupNames(XmppConnectionManager
                    .getInstance().getConnection().getRoster());
            rGroups = ContacterManager.getGroups(XmppConnectionManager
                    .getInstance().getConnection().getRoster());

            /** 获取聊天室列表 */
            Iterator<?> it = ServiceDiscoveryManager
                    .getInstanceFor(
                            XmppConnectionManager.getInstance().getConnection())
                    .discoverItems("conference.wangxc").getItems();
            while (it.hasNext()) {
                DiscoverItems.Item item = (DiscoverItems.Item) it.next();
                chatRoomList.add(item);
                roomNames.add(item.getName());
            }
        } catch (Exception e) {
            groupNames = new ArrayList<String>();
            rGroups = new ArrayList<ContacterManager.MRosterGroup>();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBottomPanelClick(int itemId) {
        String tag = "";
        if ((itemId & Constant.BTN_FLAG_MESSAGE) != 0) {
            tag = Constant.FRAGMENT_FLAG_MESSAGE;
        } else if ((itemId & Constant.BTN_FLAG_CONTACTS) != 0) {
            tag = Constant.FRAGMENT_FLAG_CONTACTS;
        } else if ((itemId & Constant.BTN_FLAG_NEWS) != 0) {
            tag = Constant.FRAGMENT_FLAG_NEWS;
        } else if ((itemId & Constant.BTN_FLAG_SETTING) != 0) {
            tag = Constant.FRAGMENT_FLAG_SETTING;
        }
        setTabSelection(tag); //切换Fragment
        // headPanel.setMiddleTitle(tag);//切换标题
    }
}