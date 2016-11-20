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
	
	private Paint xLinePaint;// 坐标轴 轴线 画笔：
	private Paint hLinePaint;// 坐标轴水平内部 虚线画笔
	private Paint titlePaint;// 绘制文本的画笔
	private Paint paint;// 矩形画笔 柱状图的样式信息
	int[] progress = { 0, 0, 0, 0, 0 };// 5
																			// 条，显示各个柱状的数据
	private int[] aniProgress;// 实现动画的值
	private final int TRUE = 1;// 在柱状图上显示数字
	private int[] text;// 设置点击事件，显示哪一条柱状的信息
	private Bitmap bitmap;
	// 坐标轴左侧的数标
	private String[] ySteps;
	// 坐标轴底部的应用名
	private String[] xWeeks = new String[] { "", "", "", "", "" };
	private int flag;// 是否使用动画

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

		// 给画笔设置颜色
		xLinePaint.setColor(Color.DKGRAY);
		hLinePaint.setColor(Color.LTGRAY);
		titlePaint.setColor(Color.BLACK);

		// 加载画图
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
		// 绘制底部的线条
		canvas.drawLine(dp2px(30), height + dp2px(0), width - dp2px(30), height
				+ dp2px(0), xLinePaint);

		int leftHeight = height - dp2px(5);// 左侧外周的 需要划分的高度：

		int hPerHeight = leftHeight / 4;// 分成四部分

		hLinePaint.setTextAlign(Align.CENTER);
		// 设置四条虚线
		for (int i = 0; i < 4; i++) {
			canvas.drawLine(dp2px(30), dp2px(10) + i * hPerHeight, width
					- dp2px(30), dp2px(10) + i * hPerHeight, hLinePaint);
		}

		// 绘制 Y 周坐标
		titlePaint.setTextAlign(Align.RIGHT);
		titlePaint.setTextSize(sp2px(12));
		titlePaint.setAntiAlias(true);
		titlePaint.setStyle(Paint.Style.FILL);
		// 设置左部的数字
		for (int i = 0; i < ySteps.length; i++) {
			canvas.drawText(ySteps[i], dp2px(25), dp2px(13) + i * hPerHeight,
					titlePaint);
		}

		// 绘制 X 周 做坐标
		int xAxisLength = width - dp2px(30);
		int columCount = xWeeks.length + 1;
		int step = xAxisLength / columCount;

		// 设置底部的数字（底部用图标，注销此循环）
		for (int i = 0; i < columCount - 1; i++) {
			// text, baseX, baseY, textPaint
			titlePaint.setTextSize(20);
			if(xWeeks[i].length() > 4){
				titlePaint.setTextSize(13);
			}
			canvas.drawText(xWeeks[i], dp2px(25) + step * (i + 1), height
					+ dp2px(20), titlePaint);
		}
		
		// 绘制矩形
		if (aniProgress != null && aniProgress.length > 0) {
			for (int i = 0; i < aniProgress.length; i++) {// 循环遍历将7条柱状图形画出来
				int value = aniProgress[i];
				paint.setAntiAlias(true);// 抗锯齿效果
				paint.setStyle(Paint.Style.FILL);
				paint.setTextSize(sp2px(15));// 字体大小
				paint.setColor(Color.parseColor("#6DCAEC"));// 字体颜色
				Rect rect = new Rect();// 柱状图的形状

				rect.left = step * (i + 1);
				rect.right = dp2px(30) + step * (i + 1);
				int rh = (int) (leftHeight - leftHeight * (value / 10000.0));
				rect.top = rh + dp2px(10);
				rect.bottom = height;

				canvas.drawBitmap(bitmap, null, rect, paint);
				// 是否显示柱状图上方的数字
				if (this.text[i] == TRUE) {
					if(value > 200){	//首先判断value是否有值
						canvas.drawText("  " + (value*3/500-1), dp2px(15) + step * (i + 1)
								- dp2px(15), rh + dp2px(5), paint);	//消去加200造成的误差
				
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
	 * 设置点击事件，是否显示数字
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
	
	//动画类
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

	//数据库查询
	private void searchDb (FragmentActivity ac, int code){
		Time time = new Time("Asia/Hong_Kong");
		time.setToNow();
		int searHour = time.hour - code;	//需要遍历的小时
		//记录该小时应用名以及时长
		String[] useApp = new String[60];
		int[] useTime = new int[60];
		//临时存储
		String packName = null;
		
		db = ac.openOrCreateDatabase("Line_info.db", Context.MODE_PRIVATE, null);  //打开SQLite数据库
		//若不存在track表新建该表
		db.execSQL("CREATE TABLE IF NOT EXISTS track" +  
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, lat VARCHAR, lon VARCHAR, " +
                "year INTEGER, month INTEGER, day INTEGER, hour INTEGER, minute INTEGER, second INTEGER, " +
                "appname VARCHAR, step INTEGER)");
		//查找当日该小时所有数据
		Cursor c = db.rawQuery("SELECT * FROM track WHERE day = ? and hour = ?", new String[]{time.monthDay + "", searHour + ""});
		
		//向数组增加该应用记录（统计）
		if(c.moveToLast()){	//能移动至第一行，即游标不为空，即找到一个以上符合条件的行
			while(!c.isBeforeFirst()){	//保证游标指向结点存在
				packName = c.getString(c.getColumnIndex("appname"));
				if(!packName.equals("")){	//排除缺测值
					if(!packName.contains("launcher")){	//排除系统桌面启动器
						int i = 0;
						while(useTime[i] > 0){
							if(useApp[i].equals(packName)){	//找到相同包名记录
								useTime[i] ++;
								break;
							}
							i++;
						}
						if(useTime[i] == 0){	//未发现相同包名记录
							useApp[i] = packName;
							useTime[i] = 1;
						}
					}
				}
				c.moveToPrevious();	//游标前移
			}
		}
		setRank(useApp, useTime, ac);
		c.close();
		db.close();
	}
	
	private void setRank(String [] useApp, int [] useTime, FragmentActivity ac){
		//从大到小冒泡排序
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
	    //给柱状图赋值
	    for(int i = 0; i < 5; i++){
	    	if(useTime[i] == 0){	//若为空值不加200
	    		break;
	    	}
	    	progress[i] = useTime[i]*(500/3)+200;	//加200补回四舍五入略去的值
	    	String temp = getProgramNameByPackageName(useApp[i], ac);
	    	xWeeks[i] = temp;	//截取
	    }
	}
	
	//获取应用名称
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