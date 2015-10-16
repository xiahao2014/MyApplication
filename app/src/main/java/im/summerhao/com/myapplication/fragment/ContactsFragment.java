package im.summerhao.com.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.jivesoftware.smackx.packet.DiscoverItems;

import java.util.ArrayList;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.ChatActivity;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.bean.ChartHisBean;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.fragment.adapter.ContacterExpandAdapter;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;

public class ContactsFragment extends BaseFragment {

    private ExpandableListView contacterList = null;
    private ContacterExpandAdapter expandAdapter = null;

    private List<ChartHisBean> inviteNotices = new ArrayList<ChartHisBean>();
    private List<DiscoverItems.Item> chatRoomList = new ArrayList<DiscoverItems.Item>();
    private List<String> groupNames;
    private List<ContacterManager.MRosterGroup> rGroups;
    private List<String> roomNames = new ArrayList<String>();
    private MainActivity mMainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contactsLayout = inflater.inflate(R.layout.contacts_layout,
                container, false);

        mMainActivity = (MainActivity) getActivity();
        // 我的好友
        contacterList = (ExpandableListView) contactsLayout.findViewById(R.id.main_expand_list);
        expandAdapter = new ContacterExpandAdapter(mMainActivity, rGroups);
        contacterList.setAdapter(expandAdapter);

        contacterList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                createChat((User) v.findViewById(R.id.username).getTag());
                return false;
            }
        });

        return contactsLayout;
    }

    private void initData() {

        try {
            /** 获取好友列表 */
            groupNames = ContacterManager.getGroupNames(XmppConnectionManager
                    .getInstance().getConnection().getRoster());
            rGroups = ContacterManager.getGroups(XmppConnectionManager
                    .getInstance().getConnection().getRoster());

//            /** 获取聊天室列表 */
//            Iterator<?> it = ServiceDiscoveryManager
//                    .getInstanceFor(
//                            XmppConnectionManager.getInstance().getConnection())
//                    .discoverItems("conference.wangxc").getItems();
//            while (it.hasNext()) {
//                DiscoverItems.Item item = (DiscoverItems.Item) it.next();
//                chatRoomList.add(item);
//                roomNames.add(item.getName());
//            }
        } catch (Exception e) {
            groupNames = new ArrayList<String>();
            rGroups = new ArrayList<ContacterManager.MRosterGroup>();
        }


    }

    /**
     * 创建一个聊天
     *
     * @param user
     */
    protected void createChat(User user) {
        Intent intent = new Intent(mMainActivity, ChatActivity.class);
        intent.putExtra("to", user.getJID());
        intent.putExtra("from", user.getFrom());

        startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity.currFragTag = Constant.FRAGMENT_FLAG_CONTACTS;
    }
}
