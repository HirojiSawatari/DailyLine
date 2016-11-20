package edu.njnu.dailyline.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.njnu.dailyline.R;
import edu.njnu.dailyline.domain.PushMsg;
import edu.njnu.dailyline.services.GetImgService;

public class PushMsgAdapter extends BaseAdapter {

	private Context context;
	private List<PushMsg> msgs;
	
	//	private Handler hander =new Handler(){
	//		public void handleMessage(android.os.Message msg) {
	//			
	//			int what = msg.what;
	//			switch (what) {
	//			case 0:
	//
	//				iv_item.setImageBitmap((Bitmap)msg.obj);
	//				break;
	//			}
	//		};
	//	};

	public PushMsgAdapter(Context context, List<PushMsg> msgs) {
		this.context = context;
		this.msgs = msgs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return msgs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return msgs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		View view = View.inflate(context, R.layout.item_push_msg, null);

		ImageView iv_item= (ImageView) view.findViewById(R.id.iv_item);
		TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
		TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
		
		final PushMsg msg = msgs.get(arg0);
		
		//		System.out.println(msg);
				
		//		new Thread(){
		//			public void run() {
		//				
		//				try {
		//					System.out.println(msg.getPicPath());
		//					Bitmap bitmap =  GetImgService.getImg(msg.getPicPath());
		//					
		//					Message message = new Message();
		//					message.what = 0;
		//					message.obj = bitmap;
		//					hander.sendMessage(message);
		//					
		//				} catch (Exception e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//			};
		//		}.start();
		
		String path = msg.getPicPath();
		int start = path.lastIndexOf("/");
		String iconname = path.substring(start+1,path.length());
		File file = new File(Environment.getExternalStorageDirectory(),iconname);
		
		if(file.exists()){
			iv_item.setImageURI(Uri.fromFile(file));
			System.out.println("使用缓存");
		}else{
			System.out.println("使用默认图片");
		}
		
//		System.out.println("time:"+msg.getDateTime());
		
		tv_text.setText(msg.getText());
		tv_time.setText(msg.getDateTime());

		return view;
	}

}
