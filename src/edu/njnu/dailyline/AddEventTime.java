package edu.njnu.dailyline;

import java.util.ArrayList;
import java.util.Calendar;

import edu.njnu.dailyline.widget.TosGallery;
import edu.njnu.dailyline.widget.WheelUtils;
import edu.njnu.dailyline.widget.WheelView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.textservice.TextInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @since 1.0
 * @version 1.0
 * @author He Tao of NNU
 */

public class AddEventTime extends Activity {
    ArrayList<TextInfo> mMonths = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mYears = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mDates = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mHours = new ArrayList<TextInfo>();
    ArrayList<TextInfo> mMins = new ArrayList<TextInfo>();

    TextView mSelDateTxt = null;
    WheelView mDateWheel = null;
    WheelView mMonthWheel = null;
    WheelView mYearWheel = null;
    WheelView mHourWheel = null;
    WheelView mMinWheel = null;

    int mCurDate = 0;
    int mCurMonth = 0;
    int mCurYear = 0;
    int mCurHour = 0;
    int mCurMin = 0;

	SharedPreferences wea_info;
	
    private TosGallery.OnEndFlingListener mListener = new TosGallery.OnEndFlingListener() {
        @Override
        public void onEndFling(TosGallery v) {
            int pos = v.getSelectedItemPosition();

            if (v == mDateWheel) {
                TextInfo info = mDates.get(pos);
                setDate(info.mIndex);
            } else if (v == mMonthWheel) {
                TextInfo info = mMonths.get(pos);
                setMonth(info.mIndex);
            } else if (v == mYearWheel) {
                TextInfo info = mYears.get(pos);
                setYear(info.mIndex);
            } else if (v == mHourWheel) {
                TextInfo info = mHours.get(pos);
                setHour(info.mIndex);
            } else if (v == mMinWheel) {
                TextInfo info = mMins.get(pos);
                setMin(info.mIndex);
            }

            mSelDateTxt.setText(formatDate());
        }
    };
    

    private String formatDate() {
        return String.format("%d 年 %02d 月 %02d 日", mCurYear, mCurMonth + 1, mCurDate);
    }

    private void setDate(int date) {
        if (date != mCurDate) {
            mCurDate = date;
        }
    }

    private void setYear(int year) {
        if (year != mCurYear) {
            mCurYear = year;
        }
    }

    private void setMonth(int month) {
        if (month != mCurMonth) {
            mCurMonth = month;
        }
    }
    
    private void setHour(int hour) {
        if (hour != mCurHour) {
            mCurHour = hour;
        }
    }
    
    private void setMin(int min) {
        if (min != mCurMin) {
            mCurMin = min;

            Calendar calendar = Calendar.getInstance();
            int date = calendar.get(Calendar.DATE);
            prepareDayData(mCurYear, mCurMonth, date);
        }
    }
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_time);
		
		wea_info = getSharedPreferences("wea_info", 0); // 开启sp
		TextView headText = (TextView) findViewById(
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
		
		ImageButton imageButton1 = (ImageButton) findViewById(R.id.imageButton1);
		ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
		
		imageButton1.setOnClickListener(new OnClickListener() {	//确定
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				//回传值
				Intent rIntent = new Intent();
				rIntent.putExtra("year", mCurYear);
				rIntent.putExtra("month", mCurMonth + 1);
				rIntent.putExtra("day", mCurDate);
				rIntent.putExtra("hour", mCurHour);
				rIntent.putExtra("min", mCurMin);
				AddEventTime.this.setResult(0, rIntent);
        		AddEventTime.this.finish();
			}	
		});
		
		imageButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
        		AddEventTime.this.finish();
			}	
		});
		
		mSelDateTxt = (TextView) findViewById(R.id.cityName);

        mDateWheel = (WheelView) findViewById(R.id.wheel_date);
        mMonthWheel = (WheelView) findViewById(R.id.wheel_month);
        mYearWheel = (WheelView) findViewById(R.id.wheel_year);
        mHourWheel = (WheelView) findViewById(R.id.wheel_hour);
        mMinWheel = (WheelView) findViewById(R.id.wheel_min);
		
        mDateWheel.setOnEndFlingListener(mListener);
        mMonthWheel.setOnEndFlingListener(mListener);
        mYearWheel.setOnEndFlingListener(mListener);
        mHourWheel.setOnEndFlingListener(mListener);
        mMinWheel.setOnEndFlingListener(mListener);

        mDateWheel.setSoundEffectsEnabled(true);
        mMonthWheel.setSoundEffectsEnabled(true);
        mYearWheel.setSoundEffectsEnabled(true);
        mHourWheel.setSoundEffectsEnabled(true);
        mMinWheel.setSoundEffectsEnabled(true);

        mDateWheel.setAdapter(new WheelTextAdapter(this));
        mMonthWheel.setAdapter(new WheelTextAdapter(this));
        mYearWheel.setAdapter(new WheelTextAdapter(this));
        mHourWheel.setAdapter(new WheelTextAdapter(this));
        mMinWheel.setAdapter(new WheelTextAdapter(this));

        prepareData();

        mSelDateTxt.setText(formatDate());
        
	}
	private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private static final String[] MONTH_NAME = { "一月", "二月", "三月", "四月", "五月", "六月", "七月",
            "八月", "九月", "十月", "十一月", "十二月", };

    private boolean isLeapYear(int year) {
        return ((0 == year % 4) && (0 != year % 100) || (0 == year % 400));
    }

    private void prepareData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        int startYear = 2012;
        int endYear = 2038;

        //获取系统时间
		Time time = new Time("Asia/Hong_Kong");
		time.setToNow();
		int hour = time.hour;
		int min = time.minute;
        
        mCurDate = day;
        mCurMonth = month;
        mCurYear = year;
        mCurHour = hour;
        mCurMin = min;

        for (int i = 0; i < MONTH_NAME.length; ++i) {
            mMonths.add(new TextInfo(i, MONTH_NAME[i], (i == month)));
        }

        for (int i = startYear; i <= endYear; ++i) {
            mYears.add(new TextInfo(i, String.valueOf(i), (i == year)));
        }
        
        for (int i = 0 ; i <= 23; i++) {
            mHours.add(new TextInfo(i, String.valueOf(i), (i == hour)));
        }
        
        for (int i = 0; i <= 59; i++) {
        	if(i>=0 && i<=9){
        		mMins.add(new TextInfo(i, "0" + String.valueOf(i), (i == min)));
        	}
        	else{
        		mMins.add(new TextInfo(i, String.valueOf(i), (i == min)));
        	}
        }

        ((WheelTextAdapter) mMonthWheel.getAdapter()).setData(mMonths);
        ((WheelTextAdapter) mYearWheel.getAdapter()).setData(mYears);
        ((WheelTextAdapter) mHourWheel.getAdapter()).setData(mHours);
        ((WheelTextAdapter) mMinWheel.getAdapter()).setData(mMins);

        prepareDayData(year, month, day);

        mMonthWheel.setSelection(month);
        mYearWheel.setSelection(year - startYear);
        mDateWheel.setSelection(day - 1);
        mHourWheel.setSelection(hour);
        mMinWheel.setSelection(min);
    }

    private void prepareDayData(int year, int month, int curDate) {
        mDates.clear();

        int days = DAYS_PER_MONTH[month];

        // The February.
        if (1 == month) {
            days = isLeapYear(year) ? 29 : 28;
        }

        for (int i = 1; i <= days; ++i) {
            mDates.add(new TextInfo(i, String.valueOf(i), (i == curDate)));
        }

        ((WheelTextAdapter) mDateWheel.getAdapter()).setData(mDates);
    }

    protected class TextInfo {
        public TextInfo(int index, String text, boolean isSelected) {
            mIndex = index;
            mText = text;
            mIsSelected = isSelected;

            if (isSelected) {
                mColor = Color.BLUE;
            }
        }

        public int mIndex;
        public String mText;
        public boolean mIsSelected = false;
        public int mColor = Color.BLACK;
    }

    protected class WheelTextAdapter extends BaseAdapter {
        ArrayList<TextInfo> mData = null;
        int mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int mHeight = 50;
        Context mContext = null;

        public WheelTextAdapter(Context context) {
            mContext = context;
            mHeight = (int) WheelUtils.pixelToDp(context, mHeight);
        }

        public void setData(ArrayList<TextInfo> data) {
            mData = data;
            this.notifyDataSetChanged();
        }

        public void setItemSize(int width, int height) {
            mWidth = width;
            mHeight = (int) WheelUtils.pixelToDp(mContext, height);
        }

        @Override
        public int getCount() {
            return (null != mData) ? mData.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;

            if (null == convertView) {
                convertView = new TextView(mContext);
                convertView.setLayoutParams(new TosGallery.LayoutParams(mWidth, mHeight));
                textView = (TextView) convertView;
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                textView.setTextColor(Color.BLACK);
            }

            if (null == textView) {
                textView = (TextView) convertView;
            }

            TextInfo info = mData.get(position);
            textView.setText(info.mText);
            textView.setTextColor(info.mColor);

            return convertView;
        }
    }
	
}