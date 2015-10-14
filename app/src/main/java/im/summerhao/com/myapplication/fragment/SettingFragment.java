package im.summerhao.com.myapplication.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.comm.Constant;

public class SettingFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);
		return settingLayout;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MainActivity.currFragTag = Constant.FRAGMENT_FLAG_SETTING;

	}


}
