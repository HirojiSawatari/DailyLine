package edu.njnu.dailyline.widget;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import edu.njnu.dailyline.R;

import android.app.Dialog;  
import android.content.Context;  
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup.LayoutParams;  
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
  
public class MoreInfoDialog extends Dialog {  
	
    public MoreInfoDialog(Context context) {  
        super(context);  
    }  
  
    public MoreInfoDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder implements OnGetGeoCoderResultListener {  
    	private TextView[] addressTextView = new TextView[5];
    	private int priCode = 0;	//���ʱ����ֵ  
    	GeoCoder mSearch = null; // ����ģ��
    	SQLiteDatabase db;
    	SharedPreferences wea_info;
        private Context context;  
        private String time;;
        
        public Builder(Context context) {  
            this.context = context;  
        }  
        
        public Builder setTime(String time) {  
            this.time = time;  
            return this;  
        } 
        
        public MoreInfoDialog create(FragmentActivity ac, int code) {	//code������list��listView�е����к�
		
        	LayoutInflater inflater = (LayoutInflater) context  
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final MoreInfoDialog dialog = new MoreInfoDialog(context,R.style.Dialog);  
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);  
            dialog.addContentView(layout, new 
            		LayoutParams(  
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            ((TextView) layout.findViewById(R.id.timeView)).setText(time);
            HistogramView hisView = (HistogramView) layout.findViewById(R.id.hisView);
            hisView.start(ac, code, 2);	//2��ʾ��״ͼ������ʼ
            
            wea_info = ac.getSharedPreferences("wea_info", 0); // ����sp
    		TextView headText = (TextView) layout.findViewById(
    				R.id.TextView1);
    		// Ĭ���������
    		int weaCode = wea_info.getInt("wea_code", 0);
    		if (weaCode == 1) { // ����
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_orange));
    		}
    		if (weaCode == 3) { // ����
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_gray));
    		}
    		if (weaCode == 4) { // ѩ��
    			headText.setBackground(ac.getResources().getDrawable(
    					R.drawable.skinpic_blue));
    		}
            
            //��ȡ��ǰ������
    		Time time = new Time("Asia/Hong_Kong");
    		time.setToNow();
    		
    		int searHour = time.hour - code;	//��Ҫ������Сʱ
            
    		db = ac.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
            db.execSQL("CREATE TABLE IF NOT EXISTS event" +  
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
	                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, " +
	                "uri VARCHAR, content VARCHAR)"); 
            //���ҵ��ո�Сʱ��������
    		Cursor c = db.rawQuery("SELECT * FROM event WHERE day = ? and hour = ?", new String[]{time.monthDay + "", searHour + ""});

    		//���������Ӹ��¼���¼
    		if(c.moveToLast()){	//���ƶ������һ�У����α겻Ϊ�գ����ҵ�һ�����Ϸ�����������
    			LinearLayout infoLayout = (LinearLayout) layout.findViewById(R.id.infoLayout);
    			//����
    			TextView title = new TextView(ac);
    			title.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 70));
    			title.setGravity(Gravity.CENTER);
    			title.setTextColor(Color.BLACK);
    			title.setText("�û��¼���Ϣ");
				infoLayout.addView(title);
				SDKInitializer.initialize(ac.getApplicationContext());	//�ٶȵ�ͼsdk��ʼ��
				//ʵ����GeoCoder����
				mSearch = GeoCoder.newInstance();
				//ע�����������
				mSearch.setOnGetGeoCodeResultListener(this);
				LatLng searPoint;	//��λ��
				int setCode = 0;	//����ʱ����ֵ
    			while(!c.isBeforeFirst()){	//��֤�α�ָ�������
    				//�¼�����
    				final LinearLayout eveTable = new LinearLayout(ac);
    				eveTable.setOrientation(LinearLayout.VERTICAL);
    				eveTable.setBackgroundColor(Color.WHITE);
    				eveTable.setPadding(30, 30, 30, 30);
					LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams
							(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					margin.setMargins(40, 35, 40, 0);	//���ò��־����������Ҿ���
					//�����ܲ���
    				infoLayout.addView(eveTable, margin);
    				
    				//��ַ
    				addressTextView[setCode] = new TextView(ac);
    				addressTextView[setCode].setTextColor(Color.BLACK);
    				eveTable.addView(addressTextView[setCode]);
                	setCode ++;	//����ֵ��һ
    				searPoint = new LatLng(Double.valueOf(c.getString(c.getColumnIndex("lat"))), Double.valueOf(c.getString(c.getColumnIndex("lon"))));
    				// ��Geo����
                	mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                		.location(searPoint));
                	
                	//��ϸ��Ϣ
                	TextView eventInfo = new TextView(ac);
                	eventInfo.setTextColor(Color.BLACK);
                	eventInfo.setText("�¼����飺" + c.getString(c.getColumnIndex("content")));
                	eveTable.addView(eventInfo);
                	
    				//ͼƬ
    				ImageView eventPic = new ImageView(ac);
    				eventPic.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    				eventPic.setImageURI(Uri.parse(c.getString(c.getColumnIndex("uri"))));
    				eveTable.addView(eventPic);

    				c.moveToPrevious();
    			}
    		}
    		c.close();
    		db.close();
    		
            dialog.setContentView(layout);  
            return dialog;  
        }

		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			// TODO �Զ����ɵķ������
			
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			// TODO �Զ����ɵķ������
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				addressTextView[priCode].setText("��ϸ��ַ�������ַ");
				priCode ++;	//����ֵ��һ
				return;
	        }
			addressTextView[priCode].setText("��ϸ��ַ��" + result.getAddress());
			priCode ++;
			return;
		}  
    }  
} 