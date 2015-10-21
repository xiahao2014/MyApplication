package im.summerhao.com.myapplication.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;

import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.bean.User;
import im.summerhao.com.myapplication.comm.Constant;
import im.summerhao.com.myapplication.manager.ContacterManager;
import im.summerhao.com.myapplication.manager.XmppConnectionManager;
import im.summerhao.com.myapplication.utils.StringUtil;

public class SettingFragment extends BaseFragment {
	private EditText friend_user,friend_nick_name;
	private Button add_friend;
	private MainActivity mMainActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);


		friend_user = (EditText) settingLayout.findViewById(R.id.friend_user);
		friend_nick_name = (EditText) settingLayout.findViewById(R.id.friend_nick_name);
		add_friend = (Button) settingLayout.findViewById(R.id.add_friend);

		add_friend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String userName = friend_user.getText().toString();
				String nickname_in = friend_nick_name.getText().toString();
				if (StringUtil.empty(userName)) {
//					showToast(getResources().getString(
//							R.string.username_not_null));

					Toast.makeText(mMainActivity, getResources().getString(
							R.string.username_not_null), Toast.LENGTH_SHORT).show();

					return;
				}
				userName = StringUtil.doEmpty(userName);
				if (StringUtil.empty(nickname_in)) {
					nickname_in = null;
				}

				if (isExitJid(StringUtil.getJidByName(userName),
						ContacterManager.getGroups(XmppConnectionManager
								.getInstance().getConnection().getRoster()))) {
//					showToast(getResources().getString(
//							R.string.username_exist));

					Toast.makeText(mMainActivity,getResources().getString(
							R.string.username_exist), Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					createSubscriber(StringUtil.getJidByName(userName),
							nickname_in, null);
				} catch (XMPPException e) {
				}
			}
		});



		return settingLayout;
	}
	/**
	 * 添加一个联系人
	 *
	 * @param userJid
	 *            联系人JID
	 * @param nickname
	 *            联系人昵称
	 * @param groups
	 *            联系人添加到哪些组
	 * @throws XMPPException
	 */
	protected void createSubscriber(String userJid, String nickname,
									String[] groups) throws XMPPException {
		XmppConnectionManager.getInstance().getConnection().getRoster()
				.createEntry(userJid, nickname, groups);
	}

	/**
	 * 判断用户名是否存在
	 *
	 * @param userJid
	 * @param groups
	 * @return
	 */
	protected boolean isExitJid(String userJid, List<ContacterManager.MRosterGroup> groups) {
		for (ContacterManager.MRosterGroup g : groups) {
			List<User> users = g.getUsers();
			if (users != null && users.size() > 0) {
				for (User u : users) {
					if (u.getJID().equals(userJid)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMainActivity = (MainActivity) getActivity();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MainActivity.currFragTag = Constant.FRAGMENT_FLAG_SETTING;

	}
}
