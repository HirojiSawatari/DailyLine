package edu.njnu.dailyline;

import java.io.File;
import java.util.List;

import javax.crypto.spec.PSource;

import edu.njnu.dailyline.activity.PushMsgDetailActivity;
import edu.njnu.dailyline.adapter.PushMsgAdapter;
import edu.njnu.dailyline.domain.PushMsg;
import edu.njnu.dailyline.services.GetImgService;
import edu.njnu.dailyline.services.PushMshService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.FullNameStyle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentPage4 extends Fragment {

	SharedPreferences wea_info;
	private ListView lv;
	private List<PushMsg> msgs;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;

			switch (what) {
			case 0:
				lv.setAdapter(new PushMsgAdapter(getActivity(), msgs));

				pd.dismiss();

				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						PushMsg msg = msgs.get(arg2);
						String url = msg.getURL();

						// 打开新界面访问url
						// Intent intent = new
						// Intent(getActivity(),PushMsgDetailActivity.class);
						// intent.putExtra("url", url);
						// startActivity(intent);

						Uri uri = Uri.parse(url);
						Intent it = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(it);
					}
				});
				break;
			}
		};
	};
	private ProgressDialog pd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_4, null);
		lv = (ListView) view.findViewById(R.id.lv_pushmsg);

		// 加载PushMsg
		new Thread() {
			public void run() {
				PushMshService pushMshService = new PushMshService(
						getActivity());
				try {
					msgs = pushMshService.getPushMsg();

					// TODO：存到本地数据库

					// 根据PicPath下载图片
					for (int i = 0; i < msgs.size(); i++) {
						PushMsg msg = msgs.get(i);
						String path = msg.getPicPath();
						int start = path.lastIndexOf("/");
						String iconname = path.substring(start + 1,
								path.length());
						File file = new File(
								Environment.getExternalStorageDirectory(),
								iconname);
						// 如果没有下载过，则重新下载
						if (!file.exists()) {
							GetImgService.getImg(msg.getPicPath());
							System.out.println(i + ":save img success");
						}
					}

					Message message = new Message();
					message.what = 0;
					handler.sendMessage(message);

				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

		pd = new ProgressDialog(getActivity());
		pd.setTitle("加载数据");
		pd.setMessage("正在加载数据，请稍...");
		pd.show();

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		wea_info = getActivity().getSharedPreferences("wea_info", 0); // 开启sp
		TextView headText = (TextView) getActivity().findViewById(
				R.id.TextView1);
		// 默认阴天界面
		int weaCode = wea_info.getInt("wea_code", 0);
		if (weaCode == 1) { // 晴天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_orange));
		}
		if (weaCode == 3) { // 雨天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_gray));
		}
		if (weaCode == 4) { // 雪天
			headText.setBackground(getResources().getDrawable(
					R.drawable.skinpic_blue));
		}
	}
}