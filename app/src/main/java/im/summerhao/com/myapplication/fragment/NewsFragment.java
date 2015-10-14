package im.summerhao.com.myapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.comm.Constant;

public class NewsFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View newsLayout = inflater.inflate(R.layout.news_layout, container,
				false);
		return newsLayout;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MainActivity.currFragTag = Constant.FRAGMENT_FLAG_NEWS;
	}


}
