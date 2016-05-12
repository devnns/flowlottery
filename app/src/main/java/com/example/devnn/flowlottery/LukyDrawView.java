package com.example.devnn.flowlottery;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devnn on 16/1/25.
 * <p>
 * 实现效果:
 * 设定50个奖品,只显示10个奖品,一行显示一个奖品,开始显示前10个奖品,奖品会向下滚动
 * 上一行滚到下一行时,把上一行补起来,补的奖品是上一个奖品。
 * <p>
 * 算法思想:
 * 假设一行字符串的高度是100
 * 画10行字符串,假设它们在数组中的下标变量名依次index1,...,index10,那么初始值依次是0,...,9
 * 设定一个顶部偏移量y,开始是0,10个字符串的偏移量依次是0*100+y,1*100+y,2*100+y,...
 * 开设一个线程每次将y增加一点,休眠一段时间
 * 当y的偏移量等于100的时候,将y置0,为什么要置0呢,你总不能将y无限增大吧!所以要在这个临界点的时候,初始化一下,把第一个空缺的奖品补起来,看到的效果就是向下滚动的。
 * 变量i自增一个,范围在0到49,i=(i+1)%50;(变量i表示第一行奖品在数组中的下标,为什么要变量i,下面会讲到的)。
 * 同时,把第一个空缺的奖品补起来,奖品是数组中的上一个奖品,下标是index1=(50- i) %50;
 * 这个时候，第二个奖品下标是什么呢?自然会想到index1+1了,显示不对,所以要设定10个下标变量。
 * 第二个奖品是第一个奖品,这个时候第一个奖品下标已经被覆盖了,所以在覆盖之前要把它的值赋给第二个下标变量，同理第三个到第十个一样的。
 * 变量i就是第一行奖品在数组中的下标,但是它不能直接作为index1使用,因为index2你就没法表示了,所以要另设一个变量i
 * 思想已经讲完了,后来我发现,第一行的奖品下标i已经有了,其实第二行到第十行奖品的下标可以用一个公式表示出来,这个公式就是
 * (50 - i + j) % 50
 * j就是0到9,表示要画10个字符串
 */
public class LukyDrawView extends View implements Runnable, Handler.Callback {
    private String TAG = this.getClass().getSimpleName();
    private Context context;
    private Paint paint;
    private Rect textBound;
    private Thread thread;
    private boolean flag;
    private List<String> prizes = new ArrayList<String>();
    private Handler handler = new Handler(this);
    private int i = 0;
    private DecimalFormat format = new DecimalFormat("00");
    private int y = 0;//偏移量
    private int rate = 3;//速度


    public LukyDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        textBound = new Rect();
        for (int i = 1; i <= 50; i++) {
            prizes.add("奖品" + format.format(i));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        paint.setTextSize(100);
        paint.setTextScaleX(1);
        paint.getTextBounds(prizes.get(prizes.size() - 1), 0, prizes.get(prizes.size() - 1).length(), textBound);
    }

    int marginTop = 0;
    int textHeight = 100;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画边框
        paint.setColor(ContextCompat.getColor(context, R.color.color_deep_green));
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        //画箭头
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.color_orange));
        paint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(getWidth() / 2 - textBound.width() / 2 - 150, textHeight * 5 - 10);
        path.lineTo(getWidth() / 2 - textBound.width() / 2 - 150, textHeight * 5 - 10 - 50);
        path.lineTo(getWidth() / 2 - textBound.width() / 2 - 30, textHeight * 5 - 10 - 25);
        path.close();
        canvas.drawPath(path, paint);
        paint.setStrokeWidth(12);
        canvas.drawLine(getWidth() / 2 - textBound.width() / 2 - 250, textHeight * 5 - 10 - 25, getWidth() / 2 - textBound.width() / 2 - 150, textHeight * 5 - 10 - 25, paint);

        //画奖品
        paint.setColor(ContextCompat.getColor(context, R.color.color_deep_green));
        for (int j = 0; j < 10; j++) {
            canvas.drawText(prizes.get((prizes.size() - i + j) % prizes.size()), getWidth() / 2 - textBound.width() / 2, marginTop + y + textHeight * j, paint);
        }
        if (y >= textHeight) {
            y = 0;
            i = (i + 1) % prizes.size();
        }
    }


    public void start() {
        flag = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        flag = false;
    }

    public String getPrize() {
        return prizes.get((prizes.size() - i + 5) % prizes.size());
    }

    @Override
    public void run() {
        try {
            while (true) {
                y += 8;
                if (flag == false) {
                    y = 0;
                    break;
                }
                Thread.sleep(rate);
                handler.sendEmptyMessage(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 10) {
            invalidate();
        }
        return false;
    }
}
