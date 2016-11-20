package edu.njnu.dailyline.widget;

import edu.njnu.dailyline.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class HistogramView extends View {

	SQLiteDatabase db;
	PackageManager pm;
	
	private Paint xLinePaint;// ������ ���� ���ʣ�
	private Paint hLinePaint;// ������ˮƽ�ڲ� ���߻���
	private Paint titlePaint;// �����ı��Ļ���
	private Paint paint;// ���λ��� ��״ͼ����ʽ��Ϣ
	int[] progress = { 0, 0, 0, 0, 0 };// 5
																			// ������ʾ������״������
	private int[] aniProgress;// ʵ�ֶ�����ֵ
	private final int TRUE = 1;// ����״ͼ����ʾ����
	private int[] text;// ���õ���¼�����ʾ��һ����״����Ϣ
	private Bitmap bitmap;
	// ��������������
	private String[] ySteps;
	// ������ײ���Ӧ����
	private String[] xWeeks = new String[] { "", "", "", "", "" };
	private int flag;// �Ƿ�ʹ�ö���

	private HistogramAnimation ani;

	public HistogramView(Context context) {
		super(context);
		init();
	}

	public HistogramView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {

		ySteps = new String[] { "60", "45", "30", "15", "0" };
		text = new int[] { 0, 0, 0, 0, 0 };
		aniProgress = new int[] { 0, 0, 0, 0, 0 };
		ani = new HistogramAnimation();
		ani.setDuration(2000);

		xLinePaint = new Paint();
		hLinePaint = new Paint();
		titlePaint = new Paint();
		paint = new Paint();

		// ������������ɫ
		xLinePaint.setColor(Color.DKGRAY);
		hLinePaint.setColor(Color.LTGRAY);
		titlePaint.setColor(Color.BLACK);

		// ���ػ�ͼ
		bitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.column);
		//start(2);
	}

	public void start(FragmentActivity ac, int code, int flag) {
		searchDb(ac, code);
		this.flag = flag;
		this.startAnimation(ani);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = getWidth();
		int height = getHeight() - dp2px(50);
		// ���Ƶײ�������
		canvas.drawLine(dp2px(30), height + dp2px(0), width - dp2px(30), height
				+ dp2px(0), xLinePaint);

		int leftHeight = height - dp2px(5);// ������ܵ� ��Ҫ���ֵĸ߶ȣ�

		int hPerHeight = leftHeight / 4;// �ֳ��Ĳ���

		hLinePaint.setTextAlign(Align.CENTER);
		// ������������
		for (int i = 0; i < 4; i++) {
			canvas.drawLine(dp2px(30), dp2px(10) + i * hPerHeight, width
					- dp2px(30), dp2px(10) + i * hPerHeight, hLinePaint);
		}

		// ���� Y ������
		titlePaint.setTextAlign(Align.RIGHT);
		titlePaint.setTextSize(sp2px(12));
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		// �����󲿵�����
		for (int i = 0; i < ySteps.length; i++) {
			canvas.drawText(ySteps[i], dp2px(25), dp2px(13) + i * hPerHeight,
					titlePaint);
		}

		// ���� X �� ������
		int xAxisLength = width - dp2px(30);
		int columCount = xWeeks.length + 1;
		int step = xAxisLength / columCount;

		// ���õײ������֣��ײ���ͼ�꣬ע����ѭ����
		for (int i = 0; i < columCount - 1; i++) {
			// text, baseX, baseY, textPaint
			titlePaint.setTextSize(20);
			if(xWeeks[i].length() > 4){
				titlePaint.setTextSize(13);
			}
			canvas.drawText(xWeeks[i], dp2px(25) + step * (i + 1), height
					+ dp2px(20), titlePaint);
		}
		
		// ���ƾ���
		if (aniProgress != null && aniProgress.length > 0) {
			for (int i = 0; i < aniProgress.length; i++) {// ѭ��������7����״ͼ�λ�����
				int value = aniProgress[i];
				paint.setAntiAlias(true);// �����Ч��
				paint.setStyle(Paint.Style.FILL);
				paint.setTextSize(sp2px(15));// �����С
				paint.setColor(Color.parseColor("#6DCAEC"));// ������ɫ
				Rect rect = new Rect();// ��״ͼ����״

				rect.left = step * (i + 1);
				rect.right = dp2px(30) + step * (i + 1);
				int rh = (int) (leftHeight - leftHeight * (value / 10000.0));
				rect.top = rh + dp2px(10);
				rect.bottom = height;

				canvas.drawBitmap(bitmap, null, rect, paint);
				// �Ƿ���ʾ��״ͼ�Ϸ�������
				if (this.text[i] == TRUE) {
					if(value > 200){	//�����ж�value�Ƿ���ֵ
						canvas.drawText("  " + (value*3/500-1), dp2px(15) + step * (i + 1)
								- dp2px(15), rh + dp2px(5), paint);	//��ȥ��200��ɵ����
				
					}
				}
			}
		}

	}

	private int dp2px(int value) {
		float v = getContext().getResources().getDisplayMetrics().density;
		return (int) (v * value + 0.5f);
	}
	
	private int sp2px(int value) {
		float v = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (v * value + 0.5f);
	}

	/**
	 * ���õ���¼����Ƿ���ʾ����
	 */
	public boolean onTouchEvent(MotionEvent event) {
		int step = (getWidth() - dp2px(30)) / 8;
		int x = (int) event.getX();
		for (int i = 0; i < 5; i++) {
			if (x > (dp2px(15) + step * (i + 1) - dp2px(15))
					&& x < (dp2px(15) + step * (i + 1) + dp2px(15))) {
				text[i] = 1;
				for (int j = 0; j < 5; j++) {
					if (i != j) {
						text[j] = 0;
					}
				}
				if (Looper.getMainLooper() == Looper.myLooper()) {
					invalidate();
				} else {
					postInvalidate();
				}
			}
		}
		return super.onTouchEvent(event);
	}
	
	//������
	private class HistogramAnimation extends Animation {
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f && flag == 2) {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = (int) (progress[i] * interpolatedTime);
				}
			} else {
				for (int i = 0; i < aniProgress.length; i++) {
					aniProgress[i] = progress[i];
				}
			}
			invalidate();
		}
	}

	//���ݿ��ѯ
	private void searchDb (FragmentActivity ac, int code){
		Time time = new Time("Asia/Hong_Kong");
		time.setToNow();
		int searHour = time.hour - code;	//��Ҫ������Сʱ
		//��¼��СʱӦ�����Լ�ʱ��
		String[] useApp = new String[60];
		int[] useTime = new int[60];
		//��ʱ�洢
		String packName = null;
		
		db = ac.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //��SQLite���ݿ�
		//��������track���½��ñ�
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)");
		//���ҵ��ո�Сʱ��������
		Cursor c = db.rawQuery("SELECT * FROM track WHERE day = ? and hour = ?", new String[]{time.monthDay + "", searHour + ""});
		
		//���������Ӹ�Ӧ�ü�¼��ͳ�ƣ�
		if(c.moveToLast()){	//���ƶ�����һ�У����α겻Ϊ�գ����ҵ�һ�����Ϸ�����������
			while(!c.isBeforeFirst()){	//��֤�α�ָ�������
				packName = c.getString(c.getColumnIndex("appname"));
				if(!packName.equals("")){	//�ų�ȱ��ֵ
					if(!packName.contains("launcher")){	//�ų�ϵͳ����������
						int i = 0;
						while(useTime[i] > 0){
							if(useApp[i].equals(packName)){	//�ҵ���ͬ������¼
								useTime[i] ++;
								break;
							}
							i++;
						}
						if(useTime[i] == 0){	//δ������ͬ������¼
							useApp[i] = packName;
							useTime[i] = 1;
						}
					}
				}
				c.moveToPrevious();	//�α�ǰ��
			}
		}
		setRank(useApp, useTime, ac);
		c.close();
		db.close();
	}
	
	private void setRank(String [] useApp, int [] useTime, FragmentActivity ac){
		//�Ӵ�Сð������
		int tempTime;   
		String tempApp;
	    int size = useTime.length;   
	    for (int i = 0; i < size - 1; i++) {   
	        for (int j = i; j < size - 1; j++) {   
	            if (useTime[i] < useTime[j]) {
	            	tempTime = useTime[i];
	            	tempApp = useApp[i];
	                useTime[i] = useTime[j];  
	                useApp[i] = useApp[j]; 
	                useTime[j] = tempTime;   
	                useApp[j] = tempApp;
	            }   
	        }   
	    }
	    //����״ͼ��ֵ
	    for(int i = 0; i < 5; i++){
	    	if(useTime[i] == 0){	//��Ϊ��ֵ����200
	    		break;
	    	}
	    	progress[i] = useTime[i]*(500/3)+200;	//��200��������������ȥ��ֵ
	    	String temp = getProgramNameByPackageName(useApp[i], ac);
	    	xWeeks[i] = temp;	//��ȡ
	    }
	}
	
	//��ȡӦ������
	public String getProgramNameByPackageName(String packname, FragmentActivity ac) {
	 	pm = ac.getPackageManager();
	 	String name = null;
	 	try {
	 		name = pm.getApplicationLabel(
	 				pm.getApplicationInfo(packname,
	 						PackageManager.GET_META_DATA)).toString();
	 	} catch (NameNotFoundException e) {
	 		e.printStackTrace();
	 	}
	 	return name;
	}
}