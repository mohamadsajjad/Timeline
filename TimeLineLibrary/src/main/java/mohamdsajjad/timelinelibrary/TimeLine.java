package mohamdsajjad.timelinelibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Typeface;


import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import java.util.ArrayList;

public class TimeLine extends ImageView {

    public interface TimeLineListener {
        public void onItemListener(int pos);
    }

    public TimeLineListener listener;

    public void setListener(TimeLineListener listener) {
        this.listener = listener;
    }

    private float lastPerDivTen;
    private Paint paintCircleStroke;
    private Paint paintCircleFill;
    private Paint paintText;
    private Paint paintDotted;
    private Paint paintBack;
    private Paint paintArc;
    private Paint progressPaint;
    private RectF rect;


    private SweepGradient sweepGradient;
    private LinearGradient linearGradient;
    private float lastDownY;
    private float offset;
    float STROKE_WIDTH;
    float tLineS;
    float percent;
    PathMeasure measure;
    Path partialPath;
    private Matrix rotateMatrix;
    int colorStartBackground;
    int colorEndBackground;

    private int[] colorStartCircle = new int[]{getColor(R.color.color_start_circle0), getColor(R.color.color_start_circle1), getColor(R.color.color_start_circle2), getColor(R.color.color_start_circle3), getColor(R.color.color_start_circle4), getColor(R.color.color_start_circle5), getColor(R.color.color_start_circle6), getColor(R.color.color_start_circle7), getColor(R.color.color_start_circle8), getColor(R.color.color_start_circle9)};
    private int[] colorEndCircle = new int[]{getColor(R.color.color_end_circle0), getColor(R.color.color_end_circle1), getColor(R.color.color_end_circle2), getColor(R.color.color_end_circle3), getColor(R.color.color_end_circle4), getColor(R.color.color_end_circle5), getColor(R.color.color_end_circle6), getColor(R.color.color_end_circle7), getColor(R.color.color_end_circle8), getColor(R.color.color_end_circle9)};
    private int[] colorHalfCircle = new int[]{getColor(R.color.color_half_circle0), getColor(R.color.color_half_circle1), getColor(R.color.color_half_circle2), getColor(R.color.color_half_circle3), getColor(R.color.color_half_circle4), getColor(R.color.color_half_circle5), getColor(R.color.color_half_circle6), getColor(R.color.color_half_circle7), getColor(R.color.color_half_circle8), getColor(R.color.color_half_circle9)};

    private int[] colors = new int[]{Color.RED, getColor(R.color.test0), Color.YELLOW, getColor(R.color.test1), Color.GREEN};

    public TimeLine(Context context) {
        super(context);
        initialize(context);
    }

    public TimeLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public TimeLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }


    private void initialize(Context context) {
        lastPerDivTen = 0;
        rect = new RectF();
        STROKE_WIDTH = dipToPx(8);

        tLineS = dipToPx(25);

        paintCircleStroke = new Paint();
        paintCircleStroke.setColor(Color.WHITE);
        paintCircleStroke.setAntiAlias(true);
        paintCircleStroke.setTextSize(dipToPx(14));
        paintCircleStroke.setStrokeWidth(dipToPx(3));
        paintCircleStroke.setStrokeCap(Paint.Cap.ROUND);
        paintCircleStroke.setStrokeJoin(Paint.Join.ROUND);
        paintCircleStroke.setStyle(Paint.Style.FILL_AND_STROKE);


        paintCircleFill = new Paint(paintCircleStroke);
        paintCircleFill.setColor(Color.BLUE);
        paintCircleFill.setStyle(Paint.Style.FILL_AND_STROKE);
        paintCircleFill.setStrokeWidth(1);


        paintText = new Paint(paintCircleStroke);
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);
        paintText.setStrokeWidth(1);


        paintArc = new Paint(paintCircleStroke);
        paintArc.setColor(getColor(R.color.color_time_line));
        paintArc.setStrokeCap(Paint.Cap.BUTT);
        paintArc.setStrokeJoin(Paint.Join.ROUND);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(tLineS);

        paintDotted = new Paint(paintCircleFill);
        paintDotted.setStyle(Paint.Style.STROKE);
        paintDotted.setStrokeWidth(dipToPx(3));
        paintDotted.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));


        progressPaint = new Paint(paintArc);
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        /*progressPaint.setStrokeWidth(tLineS);*/
        colorStartBackground = getColor(R.color.color_start_background);
        colorEndBackground = getColor(R.color.color_end_background);

        sweepGradient = new SweepGradient(dipToPx(100), dipToPx(250), colors, null);

        rotateMatrix = new Matrix();

        measure = new PathMeasure();
        partialPath = new Path();

        paintBack = new Paint();


    }

    public void start(ArrayList<StructTitleTimeLine> titles) {
        this.titles = titles;
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 20);
        progressAnimator.setDuration(0);
        progressAnimator.setTarget(20);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();

                postInvalidate();
            }
        });
        progressAnimator.start();


        ValueAnimator progressAnimator1 = ValueAnimator.ofFloat(dipToPx(30), 0);
        progressAnimator1.setDuration(2000);
        progressAnimator1.setTarget(0);
        progressAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                insideChangeXAnimationPosition = (float) animation.getAnimatedValue();
                postInvalidate();
                Log.i("mohamad", "onAnimationUpdate: " + insideChangeXAnimationPosition);
            }
        });

        progressAnimator1.start();
    }

    float lastlast = 1;
    float insideChangeXAnimationPosition = 0;


    ArrayList<Path> paths = new ArrayList<>();

    ArrayList<StructTitleTimeLine> titles = new ArrayList<>();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float height = getHeight();
        float width = getWidth();
        float cwX = width / 2;
        float cXL = cwX / 2;
        float hCXL = cXL / 2;
        float cXR = cwX + cXL;
        float R = (cwX - cXL) - tLineS;
        //also R = cxl - tLineS;
        float cy = R + tLineS - offset;
        Path arrowPath = new Path();




        LinearGradient linearGradientBackground = new LinearGradient(0, R + tLineS - offset, 0, cy + 10 * ((R * 2) + (tLineS / 2)), colorStartBackground, colorEndBackground, Shader.TileMode.MIRROR);
        paintBack.setShader(linearGradientBackground);
        canvas.drawPaint(paintBack);


        boolean sideKey = false;
        for (int i = 0; i < titles.size(); i++) {
            float currentCX;
            float textCurrentCX;
            float insideChangeXOnClickPosition = 0;

            if (indexClicked == i) {
                insideChangeXOnClickPosition = changeXOnClickPosition;
            }
            if (sideKey) {
                currentCX = cwX + R;
                textCurrentCX = cwX - 2 * R;
                currentCX -= insideChangeXAnimationPosition;
            } else {
                currentCX = cwX - R;
                textCurrentCX = cwX + R / 2;
                currentCX += insideChangeXAnimationPosition;
            }
            float RWithTLineSt = R + tLineS / 4;
            rect.left = currentCX - RWithTLineSt;
            rect.right = currentCX + RWithTLineSt;
            rect.top = cy - RWithTLineSt;
            rect.bottom = cy + RWithTLineSt;

            if (sideKey) {
                arrowPath.moveTo(currentCX + dipToPx(4), cy - RWithTLineSt);
                arrowPath.lineTo(currentCX - R, cy - RWithTLineSt);
                arrowPath.addArc(rect, 270, 180);
                arrowPath.moveTo(currentCX + dipToPx(4), cy + RWithTLineSt);
                arrowPath.lineTo(currentCX - R, cy + RWithTLineSt);
                canvas.drawArc(rect, 270, 180, false, paintArc);
                canvas.drawLine(currentCX + dipToPx(4), cy - RWithTLineSt, currentCX - R, cy - RWithTLineSt, paintArc);
                canvas.drawLine(currentCX + dipToPx(4), cy + RWithTLineSt, currentCX - R, cy + RWithTLineSt, paintArc);
            } else {
                arrowPath.moveTo(currentCX + R, cy - RWithTLineSt);
                arrowPath.lineTo(currentCX - dipToPx(4), cy - RWithTLineSt);
                arrowPath.addArc(rect, 270, -180);
                arrowPath.moveTo(currentCX - dipToPx(4), cy + RWithTLineSt);
                arrowPath.lineTo(currentCX + R, cy + RWithTLineSt);
                canvas.drawArc(rect, 90, 180, false, paintArc);
                canvas.drawLine(currentCX - dipToPx(4), cy - RWithTLineSt, currentCX + R, cy - RWithTLineSt, paintArc);
                canvas.drawLine(currentCX - dipToPx(4), cy + RWithTLineSt, currentCX + R, cy + RWithTLineSt, paintArc);
            }

            if (sideKey) {
                currentCX -= insideChangeXOnClickPosition;
            } else {
                currentCX += insideChangeXOnClickPosition;
            }

            rect.left = currentCX - R;
            rect.right = currentCX + R;
            rect.top = cy - R;
            rect.bottom = cy + R;
            //canvas.drawPath(arrowPath,paintArc);
            paintCircleFill.setColor(colorStartCircle[i%10]);
            canvas.drawCircle(currentCX, cy, R, paintCircleStroke);
            canvas.drawCircle(currentCX, cy, R, paintCircleFill);
            paintCircleFill.setColor(colorHalfCircle[i%10]);
            canvas.drawArc(rect, 110, 140, false, paintCircleFill);


            paintText.setColor(Color.BLACK);
            paintText.setTextSize(dipToPx(14));
            canvas.drawText(titles.get(i).getTitle1(), textCurrentCX, cy - R / 2.7f, paintText);
            paintText.setColor(Color.GRAY);
            paintText.setTextSize(dipToPx(12));
            paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
            canvas.drawText(titles.get(i).getDesc0(), textCurrentCX, cy, paintText);
            canvas.drawText(titles.get(i).getDesc1(), textCurrentCX, cy + R / 3.5f, paintText);
            paintText.setTextSize(dipToPx(18));
            paintText.setColor(Color.WHITE);
            paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(titles.get(i).getTitle0(), currentCX - dipToPx(4), cy + dipToPx(4), paintText);



            cy += (R * 2) + (tLineS / 2);
            sideKey = !sideKey;
        }


        Log.i("timelineMss", "onDraw: test=" + (offset));
        LinearGradient linearGradient = new LinearGradient(0, R + tLineS - offset, 0, cy, colorEndCircle, null, Shader.TileMode.CLAMP);
        rotateMatrix.setRotate(0, getWidth() / 2f, getHeight() / 2f);
        linearGradient.setLocalMatrix(rotateMatrix);

        progressPaint.setShader(linearGradient);



        measure.setPath(arrowPath, false);

        for (int i = 0; i < percent; i++) {
            partialPath.reset();
            float insidePercent = 100;
            if (i == percent - 1) {
                insidePercent = 50;
            }

            measure.getSegment(0.0f, (measure.getLength() * insidePercent) / 100.0f, partialPath, true);
            partialPath.rLineTo(0.0f, 0.0f);

            canvas.drawPath(partialPath, progressPaint);

            boolean supop = measure.nextContour();
            if (!supop) {
                break;
            }
        }

        float CXClk = 0;
        float CYClk = 0;


        float cyA = R + tLineS - offset;
        boolean sideKeyA = false;
        for (int i = 0; i < titles.size(); i++) {
            float currentCXA;
            float insideChangeXOnClickPosition = 0;
            if (indexClicked == i) {
                insideChangeXOnClickPosition = changeXOnClickPosition;
            }
            if (sideKeyA) {
                currentCXA = cwX + R;
                currentCXA -= insideChangeXOnClickPosition;
                currentCXA -= insideChangeXAnimationPosition;
            } else {
                currentCXA = cwX - R;
                currentCXA += insideChangeXOnClickPosition;
                currentCXA += insideChangeXAnimationPosition;
            }


            rect.left = currentCXA - R;
            rect.right = currentCXA + R;
            rect.top = cyA - R;
            rect.bottom = cyA + R;

            if (indexClicked == i) {
                CXClk = currentCXA;
                CYClk = cyA;
            }

            paintCircleFill.setColor(colorStartCircle[i%10]);
            canvas.drawCircle(currentCXA, cyA, R, paintCircleStroke);
            canvas.drawCircle(currentCXA, cyA, R, paintCircleFill);

            paintCircleFill.setShader(null);
            paintCircleFill.setColor(colorHalfCircle[i%10]);
            canvas.drawArc(rect, 110, 140, false, paintCircleFill);
            paintText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("" + i, currentCXA - dipToPx(4), cyA + dipToPx(4), paintText);
            cyA += (R * 2) + (tLineS / 2);
            sideKeyA = !sideKeyA;
        }

        if (indexClicked != -1) {
            paintCircleFill.setColor(colorStartCircle[indexClicked]);
            if (CYClk > height / 2) {
                float distance = CYClk - height / 2;
                float step = distance / R;
                CYClk -= step * changeXOnClickPosition;
            } else {
                float distance = height / 2 - CYClk;
                float step = distance / R;
                CYClk += step * changeXOnClickPosition;
            }


            if (endTimeLine) {
                CYClk = endProgressAnimator;
            }


            canvas.drawCircle(CXClk, CYClk, R + changeXOnClickPosition * 10, paintCircleStroke);
            canvas.drawCircle(CXClk, CYClk, R, paintCircleFill);
            if (showToolbar) {
                Log.i("x555l", "onDraw:end= " + endProgressAnimator);
                canvas.drawRect(0, 0, width, dipToPx(80), paintCircleFill);
            }
            paintCircleFill.setColor(colorHalfCircle[indexClicked]);
            rect.left = CXClk - R;
            rect.right = CXClk + R;
            rect.top = CYClk - R;
            rect.bottom = CYClk + R;
            canvas.drawArc(rect, 110 + ((90f / R) * changeXOnClickPosition), 140, false, paintCircleFill);
            if (showToolbar) {
                canvas.drawRect(0, 0, width, dipToPx(24), paintCircleFill);
            }
            canvas.drawText("" + indexClicked, CXClk - dipToPx(4), CYClk + R - dipToPx(4), paintText);



            if (progressAnim) {
                paintDotted.setColor(colorHalfCircle[indexClicked]);
                paintDotted.setPathEffect(new DashPathEffect(new float[]{(360 - testkon) / 14, 20, (360 - testkon) / 14}, 0));
                canvas.drawArc(rect, testkon, 359, false, paintDotted);
            }
        } else {
            if (backPressed) {

            }
        }
    }


    private int CLICK_ACTION_THRESHOLD = 10;
    private float startX;
    private float startY;
    private int indexClicked = -1;
    private boolean progressAnim = false;
    private float testkon = 0;
    private float endProgressAnimator = 0;
    private float backPressedProgressAnimator = 0;
    private boolean endTimeLine = false;
    private boolean showToolbar = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float width = getWidth();
        float cwX = width / 2;
        float cXL = cwX / 2;
        float R = (cwX - cXL) - tLineS;
        //also R = cxl - tLineS;
        float cy = R + tLineS - offset;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                lastDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentDownY = event.getY();
                offset += lastDownY - currentDownY;
                //offset = (offset > 0) ? 0 : offset;
                //offset = (offset < -1800) ? -1800 : offset;
                /*int startLock = getHeight() / 2 / 18;
                startLock = startLock * 24;
                offset = (offset < -startLock) ? -startLock : offset;
                float endLock = DATA_COUNT * yStep - getHeight() / 2f;*/
                offset = Math.min(offset, ((titles.size()+6) * (R + tLineS)));
                offset = Math.max(offset, -4 * R);

                if (!clickLock)
                    invalidate();
                lastDownY = currentDownY;
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (!clickLock && isAClick(startX, endX, startY, endY)) {
                    if (endY > cy - R && endY < cy + R && endX > cwX - 2 * R && endX < cwX) {
                        indexClicked = 0;
                    } else if (endY > cy + R + tLineS / 2 && endY < cy + 3 * R + tLineS / 2 && endX > cwX && endX < cwX + 2 * R) {
                        indexClicked = 1;
                    } else if (endY > cy + 3 * R + tLineS && endY < cy + 5 * R + tLineS && endX > cwX - 2 * R && endX < cwX) {
                        indexClicked = 2;
                    } else if (endY > cy + 5 * R + tLineS * 1.5 && endY < cy + 7 * R + tLineS * 1.5 && endX > cwX && endX < cwX + 2 * R) {
                        indexClicked = 3;
                    } else if (endY > cy + 7 * R + tLineS * 2 && endY < cy + 9 * R + tLineS * 2 && endX > cwX - 2 * R && endX < cwX) {
                        indexClicked = 4;
                    } else if (endY > cy + 9 * R + tLineS * 2.5 && endY < cy + 11 * R + tLineS * 2.5 && endX > cwX && endX < cwX + 2 * R) {
                        indexClicked = 5;
                    } else if (endY > cy + 11 * R + tLineS * 3 && endY < cy + 13 * R + tLineS * 3 && endX > cwX - 2 * R && endX < cwX) {
                        indexClicked = 6;
                    } else if (endY > cy + 13 * R + tLineS * 3.5 && endY < cy + 15 * R + tLineS * 3.5 && endX > cwX && endX < cwX + 2 * R) {
                        indexClicked = 7;
                    } else if (endY > cy + 15 * R + tLineS * 4 && endY < cy + 17 * R + tLineS * 4 && endX > cwX - 2 * R && endX < cwX) {
                        indexClicked = 8;
                    } else if (endY > cy + 17 * R + tLineS * 4.5 && endY < cy + 19 * R + tLineS * 4.5 && endX > cwX && endX < cwX + 2 * R) {
                        indexClicked = 9;
                    } else {
                        indexClicked = -1;
                    }
                    clicked(R);
                }
                break;
        }
        return true;
    }

    boolean backPressed;

    public void onBackPressed() {
        indexClicked = -1;
        backPressed = true;
        clickLock = false;
        postInvalidate();
    }

    private float changeXOnClickPosition = 0;

    private void clicked(float R) {
        progressAnim = false;
        endTimeLine = false;
        showToolbar = false;
        final ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, R);
        progressAnimator.setDuration(600);
        progressAnimator.setTarget(R);
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                changeXOnClickPosition = (float) animation.getAnimatedValue();
                postInvalidate();

            }
        });

        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                progressView();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        if (indexClicked != -1)
            progressAnimator.start();
    }

    boolean clickLock = false;

    private void progressView() {
        clickLock = true;
        progressAnim = true;
        final ValueAnimator progressAnimator = ValueAnimator.ofFloat(0, 360);
        progressAnimator.setDuration(5000);
        progressAnimator.setTarget(360);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                testkon = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //progressView();
                endTimeLine();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        if (indexClicked != -1) {
            progressAnimator.start();
        }
    }

    private void endTimeLine() {
        endTimeLine = true;
        final ValueAnimator progressAnimator = ValueAnimator.ofFloat(getHeight() / 2f, dipToPx(0));
        progressAnimator.setDuration(600);
        progressAnimator.setTarget(dipToPx(0));
        progressAnimator.setInterpolator(new DecelerateInterpolator());
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                endProgressAnimator = (float) animation.getAnimatedValue();
                if (endProgressAnimator <= dipToPx(50)) {
                    showToolbar = true;
                }
                postInvalidate();
            }
        });
        progressAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                progressAnim = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //progressView();
                //endTimeLine = false;
                if (listener != null) {
                    listener.onItemListener(indexClicked);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        if (indexClicked != -1) {
            progressAnimator.start();
        }
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    private float getDimension(int id) {
        return getResources().getDimension(id);
    }

    private int getColor(int id) {
        return getResources().getColor(id);
    }

}
