package com.chart_demo1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.DoubleUtil;
import util.LogUtil;
import util.Screen;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
 /**
  * 柱状图
  * @author xueying.gao
  * @date 2016-07-01 16:22
  */
public class BarChartView extends View {
    //画笔
    private Paint mPaint;
    //标题大小
    private float titleSize;
    //X坐标轴刻度线数量
    private int axisDivideSizeX = 7;
    /*Y坐标轴最大值*/
    private double maxAxisValueY;
    /*Y坐标轴刻度线数量*/
    private int axisDivideSizeY = 5;
    //视图宽度
    private int width;
    //视图高度
    private int height;
    //坐标原点位置
    private final int originX = dip2px(getContext(), 50);
    private final int originY = dip2px(getContext(), 180);
    //柱状图数据
    private List<Double> data = new ArrayList<Double>();
    //X轴月份
    private List<String> monthList = new ArrayList<String>();
    //柱状图数据颜色
    private int[] columnColors = new int[]{Color.parseColor("#fa7b48"),Color.parseColor("#fca640"),Color.parseColor("#f5bb47"),Color.parseColor("#99b943"),Color.parseColor("#63c3db"),Color.parseColor("#649cdb")};
    //单位
    private int unit;		//单位系数
	private int unitNum;	//位数
	private String unitDesc;//单位
	
	private boolean onDraw = false;
 
    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //创建画笔
        mPaint = new Paint();
        //获取配置的属性值
        titleSize = sp2px(getContext(), 14);
    }
 
	public void setData(List<Double> data) {
		this.data = data;
		maxAxisValueY = Collections.max(data);
		aniProgress = new double[data.size()];
		for (int i = 0; i < data.size(); i++) {
			aniProgress[i] = 0;
		}
        ani = new HistogramAnimation();
        ani.setDuration(500);
	}
	
	public void setMonthList(List<String> monthList) {
		this.monthList = monthList;
	}

    public void setOnDraw(boolean onDraw) {
        this.onDraw = onDraw; 
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Screen.initScreen(getContext());
        width = Screen.getInstance().widthPixels - originX*3/2;
        height = MeasureSpec.getSize(heightMeasureSpec) - dip2px(getContext(), 80);
        LogUtil.w(width+"/"+height);
    }
 
    @Override
    public void onDraw(Canvas canvas) {
    	if (onDraw) {
    		drawAxisX(canvas, mPaint);
            drawAxisScaleMarkX(canvas, mPaint);
            drawBankGroundLines(canvas, mPaint);
            drawAxisScaleMarkValueX(canvas, mPaint);
            drawAxisScaleMarkValueY(canvas, mPaint);
            drawColumn(canvas, mPaint);
            drawTitle(canvas, mPaint);
            drawChart(canvas,mPaint);
		}
    }
 
    /**
     * 绘制横坐标轴（X轴）
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisX(Canvas canvas, Paint paint) {
        paint.setColor(Color.parseColor("#c0c0c0"));
        //设置画笔宽度
        paint.setStrokeWidth(3);
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //画横轴(X)
        canvas.drawLine(originX, originY, originX + width, originY, paint);
    }
 
    /**
     * 绘制纵坐标轴(Y轴)
     *
     * @param canvas
     * @param paint
     */
//    private void drawAxisY(Canvas canvas, Paint paint) {
//        画竖轴(Y)
//        canvas.drawLine(originX, originY, originX, originY - height, paint);//参数说明：起始点左边x,y，终点坐标x,y，画笔
//    }
 
    /**
     * 数据向上取整处理
     * @param cellValue
     * @return
     */
    private int getCellValue(float cellValue){
    	unitNum = 0;
		int tempValue = 1;
		for (int i = 0; true; i++) {
			tempValue *= 10;
			unitNum = i + 1;
			if ((cellValue / tempValue) < 10) {
				break;
			}
		}
		switch (unitNum) {
		case 1:
			if (cellValue % 10 != 0) {
				cellValue = ((int)Math.ceil(cellValue /10))*10;
			}
			unit = 0;
			unitNum = 0;
			unitDesc = "元";
			break;
		case 2:// 以百为单位
			if (cellValue % 100 != 0) {
				cellValue = ((int)Math.ceil(cellValue /100))*100;
			}
			unit = 100;
			unitDesc = "百元";
			break;
		case 3:// 以千为单位
			if (cellValue % 1000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /1000))*1000;
			}
			unit = 1000;
			unitDesc = "千元";
			break;
		case 4:// 以万为单位
			if (cellValue % 10000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /10000))*10000;
			}
			unit = 10000;
			unitDesc = "万元";
			break;
		case 5:// 以十万为单位 
			if (cellValue % 100000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /100000))*100000;
			}
			unit = 100000;
			unitDesc = "十万元";
			break;
		case 6:// 以百万为单位 
			if (cellValue % 1000000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /1000000))*1000000;
			}
			unit = 1000000;
			unitDesc = "百万元";
			break;
		case 7:// 以千万元为单位 
			if (cellValue % 10000000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /10000000))*10000000;
			}
			unit = 10000000;
			unitDesc = "千万元";
			break;
		case 8:// 以亿元为单位 
			if (cellValue % 100000000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /100000000))*100000000;
			}
			unit = 100000000;
			unitDesc = "亿元";
			break;
		case 9:// 以十亿元为单位 
			if (cellValue % 1000000000 != 0) {
				cellValue = ((int)Math.ceil(cellValue /1000000000))*1000000000;
			}
			unit = 1000000000;
			unitDesc = "十亿元";
			break;
		default:
			break;
	}
		return (int)cellValue;
    }
 
    /**
     * 绘制纵坐标轴刻度值(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkValueY(Canvas canvas, Paint paint) {
    	if (maxAxisValueY == 0) {
        	maxAxisValueY = 5;
		}
        float cellHeight = (height- topY) / axisDivideSizeY;
        float cellValue = (float)(maxAxisValueY / axisDivideSizeY);
        
        cellValue = getCellValue(cellValue);
        paint.setColor(Color.parseColor("#B8B8B8"));
        for (int i = 0; i < axisDivideSizeY+1; i++) {
        	String valueY = "0";
        	if (i != 0) {
        		valueY = String.valueOf((int)cellValue * i).substring(0,  String.valueOf((int)cellValue * i).length() - unitNum);
			}
        	float textWith = paint.measureText(valueY);
            canvas.drawText(valueY, 
            		(originX - textWith)/2, 
            		originY - cellHeight * i , 
            		paint);
        }
    }
    
    /**
     * 绘制纵坐标轴背景线(Y轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawBankGroundLines(Canvas canvas, Paint paint) {
		paint.setStrokeWidth(dip2px(getContext(), 0.8f));
    	paint.setColor(Color.parseColor("#e6e6e6"));
        float cellHeight = (height- topY)/ axisDivideSizeY;
        for (int i = 0; i < axisDivideSizeY; i++) {
            canvas.drawLine(
            		originX, 
            		(originY - cellHeight * (i+1)), 
            		originX + width , 
            		(originY - cellHeight * (i+1)), 
            		paint);
        }
    }
    
    private final int topY = dip2px(getContext(), 120);
 
    /**
     * 绘制柱状图
     *
     * @param canvas
     * @param paint
     */
    private void drawColumn(Canvas canvas, Paint paint) {
        if(data == null)
            return;
        float cellWidth = (width)/data.size();
        float ColumnWidth = dip2px(getContext(), 18);
        //Y轴最大刻度
        float MaxAxisYValue = getCellValue((float)(maxAxisValueY / axisDivideSizeY))*axisDivideSizeY;
        
        if (aniProgress != null && aniProgress.length > 0) {
	        for (int i = 0; i < aniProgress.length; i++) {
	            paint.setColor(columnColors[i]);
	            float leftTopY = (float) (originY - (height-topY) * aniProgress[i] / MaxAxisYValue);
	            
	            canvas.drawRect(
	            		originX+cellWidth*i+(cellWidth - ColumnWidth)/2, 
	            		leftTopY, 
	            		originX+cellWidth*i+(cellWidth - ColumnWidth)/2+ColumnWidth, 
	            		originY, 
	            		mPaint);//左上角x,y右下角x,y，画笔
	            
	//            float textWidth = paint.measureText(shList.get(i)+"");
	//            canvas.drawText(shList.get(i)+"",
	//            		originX + cellWidth * i + (cellWidth - textWidth)/2, 
	//            		leftTopY - dip2px(getContext(), 10), 
	//            		paint);
	        }
        }
    }
    
    /**
     * 绘制横坐标轴刻度线(X轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkX(Canvas canvas, Paint paint) {
        float cellWidth = (width)/data.size();
        for (int i = 0; i < axisDivideSizeX; i++) {
            canvas.drawLine(
            		cellWidth * i + originX, 
            		originY, 
            		cellWidth * i + originX, 
            		originY + dip2px(getContext(), 4), 
            		paint);
        }
    }
 
    /**
     * 绘制横坐标轴刻度值(X轴)
     *
     * @param canvas
     * @param paint
     */
    private void drawAxisScaleMarkValueX(Canvas canvas, Paint paint) {
        //设置画笔绘制文字的属性
        paint.setColor(Color.parseColor("#B8B8B8"));
        paint.setTextSize(sp2px(getContext(), 12));
        paint.setFakeBoldText(true);
 
        float cellWidth = (width)/data.size();
        for (int i = 0; i < monthList.size(); i++) {
        	float textWidth = paint.measureText(monthList.get(i)+"月");
            canvas.drawText(monthList.get(i)+"月", 
            		originX + cellWidth * i + (cellWidth - textWidth)/2, 
            		originY + dip2px(getContext(), 15), 
            		paint);
        }
    }
 
    /**
     * 绘制标题
     *
     * @param canvas
     * @param paint
     */
    private void drawTitle(Canvas canvas, Paint paint) {
 
            //设置画笔绘制文字的属性
            mPaint.setColor(Color.parseColor("#949494"));
            mPaint.setTextSize(titleSize);
            mPaint.setFakeBoldText(true);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_icon_deal);
            canvas.drawBitmap(bitmap, dip2px(getContext(), 10), dip2px(getContext(), 10), paint);
            
            canvas.drawText("月交易额", 
            		dip2px(getContext(), 30), 
            		dip2px(getContext(), 20), paint);
            paint.setColor(Color.parseColor("#B8B8B8"));
            mPaint.setTextSize(sp2px(getContext(), 12));
            canvas.drawText("（"+unitDesc+"）", 
            		dip2px(getContext(), 30), 
            		dip2px(getContext(), 40), paint);
    }
    
    /**
     * 绘制下方表数据
     * @param canvas
     * @param mPaint
     */
    private void drawChart(Canvas canvas,Paint mPaint){
    	mPaint.setStrokeWidth(dip2px(getContext(), 0.8f));
    	mPaint.setColor(Color.parseColor("#e6e6e6"));
    	//外矩形
    	RectF r2=new RectF();
        r2.left=originX/2;
        r2.top=originY+dip2px(getContext(), 30);
        r2.right=width+originX; 
        r2.bottom=originY + dip2px(getContext(), 130);
    	canvas.drawRoundRect(r2, 10, 10, mPaint); 
    	//内矩形
    	mPaint.setColor(Color.WHITE);
    	r2=new RectF();
        r2.left   = originX/2+dip2px(getContext(), 0.5f);
        r2.top    = originY+dip2px(getContext(), 30)+dip2px(getContext(), 0.5f);
        r2.right  = width+originX-+dip2px(getContext(), 0.5f); 
        r2.bottom = originY + dip2px(getContext(), 130)-dip2px(getContext(), 0.5f);
    	canvas.drawRoundRect(r2, 10, 10, mPaint);
    	//横线1
    	mPaint.setStrokeWidth(dip2px(getContext(), 0.8f));
    	mPaint.setColor(Color.parseColor("#e6e6e6"));
    	canvas.drawLine(originX/2, 
    			originY+dip2px(getContext(), 30)+dip2px(getContext(), 100)/3, 
    			width+originX, 
    			originY+dip2px(getContext(), 30)+dip2px(getContext(), 100)/3, 
    			mPaint);
    	//横线2
    	canvas.drawLine(originX/2, 
    			originY+dip2px(getContext(), 30)+2*dip2px(getContext(), 100)/3, 
    			width+originX, 
    			originY+dip2px(getContext(), 30)+2*dip2px(getContext(), 100)/3, 
    			mPaint);
    	//竖线1
    	canvas.drawLine((width+originX+originX/2)/2, 
    			originY+dip2px(getContext(), 30), 
    			(width+originX+originX/2)/2,
    			originY + dip2px(getContext(), 130), 
    			mPaint);
    	
    	mPaint.setTextSize(sp2px(getContext(), 12));
    	//画表中的数据
    	for (int i = 0; i < monthList.size(); i++) {
			if (i < 3) {
				mPaint.setColor(Color.parseColor("#666666"));
				canvas.drawText(monthList.get(i)+"月", 
		    			originX/2+dip2px(getContext(), 10), 
		    			originY+dip2px(getContext(), 40)+i*dip2px(getContext(), 100)/3
		    			+dip2px(getContext(), 12), 
		    			mPaint);
				mPaint.setColor(columnColors[i]);
				canvas.drawText("¥"+DoubleUtil.formetDouble(data.get(i)), 
						(width+originX+originX/2)/2 - mPaint.measureText("¥"+DoubleUtil.formetDouble(data.get(i)))-dip2px(getContext(), 10), 
		    			originY+dip2px(getContext(), 40)+i*dip2px(getContext(), 100)/3
		    			+dip2px(getContext(), 12), 
		    			mPaint);
			}else{
				mPaint.setColor(Color.parseColor("#666666"));
				canvas.drawText(monthList.get(i)+"月", 
						(width+originX+originX/2)/2+dip2px(getContext(), 10), 
		    			originY+dip2px(getContext(), 40)+(i-3)*dip2px(getContext(), 100)/3
		    			+dip2px(getContext(), 12), 
		    			mPaint);
				mPaint.setColor(columnColors[i]);
				canvas.drawText("¥"+DoubleUtil.formetDouble(data.get(i)), 
						width+originX - mPaint.measureText("¥"+DoubleUtil.formetDouble(data.get(i)))-dip2px(getContext(), 10), 
		    			originY+dip2px(getContext(), 40)+(i-3)*dip2px(getContext(), 100)/3
		    			+dip2px(getContext(), 12), 
		    			mPaint);
			}
		}
    }
    
    int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	
	private double[] aniProgress;// 实现动画的值
	private HistogramAnimation ani;
	
	public void start() {
        this.startAnimation(ani);
    }
	
	/**
     * 集成animation的一个动画类
     */
    private class HistogramAnimation extends Animation {
        protected void applyTransformation(float interpolatedTime,
                Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            
            if (interpolatedTime < 1.0f ) {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = (int) (data.get(i) * interpolatedTime);
                }
            } else {
                for (int i = 0; i < aniProgress.length; i++) {
                    aniProgress[i] = data.get(i);
                }
            }
            
            invalidate();
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	return super.onTouchEvent(event);
    }

}