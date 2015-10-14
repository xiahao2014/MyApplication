package im.summerhao.com.myapplication.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import im.summerhao.com.myapplication.R;
import im.summerhao.com.myapplication.activity.MainActivity;
import im.summerhao.com.myapplication.bean.MessageBean;
import im.summerhao.com.myapplication.comm.Constant;

public class MessageFragment extends BaseFragment {

	private static final String TAG = "MessageFragment";
	private MainActivity mMainActivity ;
	private ListView mListView;
	private List<MessageBean> mMsgBean = new ArrayList<MessageBean>();
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.message_layout,
				container, false);
		Log.d(TAG, "onCreateView---->");

		return messageLayout;
	}


	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		Log.e(TAG, "onAttach-----");

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate------");
		mMsgBean.add(new MessageBean(R.drawable.message, "张三", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "李四", "哈哈", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "小明", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "王五", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "Jack", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "Jone", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "Jone", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "Jone", "吃饭没?", "昨天"));
		mMsgBean.add(new MessageBean(R.drawable.message, "Jone", "吃饭没?", "昨天"));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "onActivityCreated-------");
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Log.e(TAG, "onStart----->");
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(TAG, "onresume---->");
		MainActivity.currFragTag = Constant.FRAGMENT_FLAG_MESSAGE;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e(TAG, "onpause");
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e(TAG, "onStop");
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		Log.e(TAG, "ondestoryView");
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "ondestory");
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		Log.d(TAG, "onDetach------");

	}



}
