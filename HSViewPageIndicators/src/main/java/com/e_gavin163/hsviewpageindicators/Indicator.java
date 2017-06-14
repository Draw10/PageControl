package com.e_gavin163.hsviewpageindicators;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Indicator extends View {
    private Paint paintStroke, paintFill;
    private Path path;
    private int colorMovingPoint, colorPoints;
    private int nums;
    private boolean isFill;
    private float space;
    private float angle;
    private float radiusNormal;
    private float radiusFocus;
    private HSPoint[] points;
    private HSPoint movingPoint;
    private HSPoint stayPoint;
    private int strokeWidth;
    private float oldAngle;

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;
    private final Handler updateHandler = new Handler();

    public Indicator(Context context) {
        super(context);
        init(context, null);
    }

    public Indicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Indicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        strokeWidth = 2;
        path = new Path();
        paintStroke = new Paint();
        paintStroke.setStrokeWidth(strokeWidth);
        paintStroke.setAntiAlias(true);
        paintFill = new Paint();
        paintFill.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFill.setStrokeWidth(strokeWidth);
        paintFill.setAntiAlias(true);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PageControl, 0, 0);
        try {
            colorMovingPoint = a.getColor(R.styleable.PageControl_colorMovingPoint, Color.RED);
            colorPoints = a.getColor(R.styleable.PageControl_colorPoints, Color.RED);
            nums = a.getInt(R.styleable.PageControl_nums, 3);
            space = a.getDimension(R.styleable.PageControl_space, 5);
            angle = a.getDimension(R.styleable.PageControl_angle, (float) (135 * Math.PI / 180));
            radiusNormal = a.getDimension(R.styleable.PageControl_radiusNormal, getResources()
                    .getDimension(R.dimen.default_pagecontrol_radius_normal));
            radiusFocus = a.getDimension(R.styleable.PageControl_radiusFocus, getResources().getDimension(R.dimen.default_pagecontrol_radius_focus));
            isFill = a.getBoolean(R.styleable.PageControl_isFill, false);
        } finally {
            a.recycle();
        }
        if (nums > 1)
            initData();
    }

    private void setPointCount(int count) {
        this.nums = count;
        if (nums > 1)
            initData();
    }

    private void initData() {
        oldAngle = angle;
        if (isFill) {
            paintStroke.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            paintStroke.setStyle(Paint.Style.STROKE);
        }
        paintStroke.setColor(colorPoints);
        paintFill.setColor(colorMovingPoint);
        points = new HSPoint[nums];

        for (int i = 0; i < points.length; i++) {
            HSPosition position = new HSPosition(radiusFocus + i * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
            HSPoint point = new HSPoint(position);
            point.setRadius(radiusNormal);
            points[i] = point;
        }
        initTwoPoints(0);
    }

    private void initTwoPoints(int index) {
        HSPosition position = new HSPosition(radiusFocus + index * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
        movingPoint = new HSPoint(position);
        movingPoint.setRadius(0);
        HSPosition position2 = new HSPosition(radiusFocus + index * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
        stayPoint = new HSPoint(position2);
        stayPoint.setRadius(radiusFocus);
        calculate(index, index);
        angle = oldAngle;
    }

    private void calculate(int fromIndex, int toIndex) {
        path.reset();
        if (fromIndex > toIndex) {
            path.moveTo(movingPoint.getPositionUpRight().getPositionX(), movingPoint.getPositionUpRight().getPositionY());
            HSPosition middlePosition = getIntersectPoint(movingPoint.getPositionUpRight(), stayPoint.getPositionUpLeft());
            path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), stayPoint.getPositionUpLeft().getPositionX(), stayPoint
                    .getPositionUpLeft().getPositionY());
            path.lineTo(stayPoint.getPositionDownLeft().getPositionX(), stayPoint.getPositionDownLeft().getPositionY());
            middlePosition = getIntersectPoint(stayPoint.getPositionDownLeft(), movingPoint.getPositionDownRight());
            path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), movingPoint.getPositionDownRight().getPositionX(), movingPoint
                    .getPositionDownRight().getPositionY());
            path.lineTo(movingPoint.getPositionUpRight().getPositionX(), movingPoint.getPositionUpRight().getPositionY());
        } else {
            path.moveTo(stayPoint.getPositionUpRight().getPositionX(), stayPoint.getPositionUpRight().getPositionY());
            HSPosition middlePosition = getIntersectPoint(stayPoint.getPositionUpRight(), movingPoint.getPositionUpLeft());
            path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), movingPoint.getPositionUpLeft().getPositionX(), movingPoint
                    .getPositionUpLeft().getPositionY());
            path.lineTo(movingPoint.getPositionDownLeft().getPositionX(), movingPoint.getPositionDownLeft().getPositionY());
            middlePosition = getIntersectPoint(movingPoint.getPositionDownLeft(), stayPoint.getPositionDownRight());
            path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), stayPoint.getPositionDownRight().getPositionX(), stayPoint
                    .getPositionDownRight().getPositionY());
            path.lineTo(stayPoint.getPositionUpRight().getPositionX(), stayPoint.getPositionUpRight().getPositionY());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth, measuredHeight;
        measuredWidth = (int) (radiusFocus * 2 * nums + (nums - 1) * space) + 2 * strokeWidth;
        measuredHeight = (int) (radiusFocus * 2) + 2 * strokeWidth;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        if (nums <= 1)
            return;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
        canvas.drawPath(path, paintFill);
        for (int i = 0; i < points.length; i++) {
            HSPoint p = points[i];
            canvas.drawCircle(p.getPosition().getPositionX(), p.getPosition().getPositionY(), p.getRadius(), paintStroke);
        }
        canvas.drawCircle(movingPoint.getPosition().getPositionX(), movingPoint.getPosition().getPositionY(), movingPoint.getRadius(), paintFill);
        canvas.drawCircle(stayPoint.getPosition().getPositionX(), stayPoint.getPosition().getPositionY(), stayPoint.getRadius(), paintFill);
        super.onDraw(canvas);
    }

    private HSPosition getIntersectPoint(HSPosition p1, HSPosition p2) {
        double a1, b1, c1, a2, b2, c2;
        a1 = -Math.tan(Math.PI / 2 + angle / 2.0);
        b1 = -1;
        c1 = p1.getPositionY() - a1 * p1.getPositionX();

        a2 = -Math.tan(Math.PI / 2 - angle / 2.0);
        b2 = -1;
        c2 = p2.getPositionY() - a2 * p2.getPositionX();

        double m = a1 * b2 - a2 * b1;
        if (m == 0) {
            return null;
        }
        float x = (float) ((c2 * b1 - c1 * b2) / m);
        float y = (float) ((c1 * a2 - c2 * a1) / m);
        return new HSPosition(x, y);
    }

    private void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    private void setMoveingDistance(float moveingDistance) {
        this.space = moveingDistance;
        for (int i = 0; i < points.length; i++) {
            HSPoint p = points[i];
            HSPosition position = new HSPosition(radiusFocus + i * (radiusFocus * 2 + space), radiusFocus);
            p.setPosition(position);
        }
        invalidate();
    }

    float distancePercent;
    int oldFromIndex, oldToIndex;
    boolean newStart = false;

    private void moving(int fromIndex, int toIndex, float distancePercent, boolean newStart) {
        this.newStart = newStart;
        this.distancePercent = distancePercent;
        if (fromIndex < toIndex) {
            oldFromIndex = fromIndex;
            oldToIndex = toIndex;
            stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
            movingPoint.setRadius(radiusFocus * distancePercent);
            HSPosition p = movingPoint.getPosition();
            p.setPositionX(stayPoint.getPosition().getPositionX() + (space + radiusFocus * 2) * distancePercent);
            movingPoint.setPosition(p);
            calculate(fromIndex, toIndex);
        } else if (fromIndex > toIndex) {
            oldFromIndex = fromIndex;
            oldToIndex = toIndex;
            distancePercent = 1 - distancePercent;
            stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
            movingPoint.setRadius(radiusFocus * distancePercent);
            HSPosition p = movingPoint.getPosition();
            p.setPositionX(stayPoint.getPosition().getPositionX() - (space + radiusFocus * 2) * distancePercent);
            movingPoint.setPosition(p);
            calculate(fromIndex, toIndex);
        } else {
            distancePercent = 1 - distancePercent;
            stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
            movingPoint.setRadius(radiusFocus * distancePercent);
            HSPosition p = movingPoint.getPosition();
            if (oldFromIndex > oldToIndex) {
                p.setPositionX(stayPoint.getPosition().getPositionX() - (space + radiusFocus * 2) * (1 - distancePercent));
            } else {
                p.setPositionX(stayPoint.getPosition().getPositionX() + (space + radiusFocus * 2) * (1 - distancePercent));
            }

            movingPoint.setPosition(p);
            calculate(oldFromIndex, oldToIndex);
        }
        invalidate();
    }

    private void startLoop() {
        stopLoop();
        if (!executorService.isShutdown()) {
            scheduledFuture = executorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            updateHandler.post(updateTask);
                        }
                    }, 0,
                    10, TimeUnit.MILLISECONDS);
        }
    }

    private void stopLoop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
    }

    private enum MovingSchedule {
        moveTail, leftAnim
    }

    private MovingSchedule movingSchedule;

    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            switch (movingSchedule) {
                case moveTail:
                    if (!newStart && moveingPercent > 0) {
                        moveingPercent -= 0.1;
                        float pe = (float) ((moveingPercent * 100.0f) / 120.0);
                        float middlePositionX = distancePercent * pe;
                        HSPosition p = stayPoint.getPosition();
                        p.setPositionX(movingPoint.getPosition().getPositionX() - middlePositionX);
                        stayPoint.setPosition(p);
                        calculate(oldFromIndex, oldToIndex);
                        invalidate();
                    } else {
                        stopLoop();
                        initTwoPoints(movingToIndex);
                        calculate(movingToIndex, movingToIndex);
                        invalidate();
                    }
                    break;
                case leftAnim:
                    if (!newStart && distancePercent > 0 && distancePercent < 1) {
                        if (movingFromIndex == movingToIndex) {//BACK
                            distancePercent -= 0.05;
                            if (distancePercent < 0)
                                distancePercent = 0;
                            moving(movingFromIndex, movingToIndex, distancePercent, false);
                        } else {//JUMP
                            if (movingFromIndex < movingToIndex) {
                                distancePercent += 0.05;
                                if (distancePercent > 1)
                                    distancePercent = 1;
                            } else {
                                distancePercent -= 0.05;
                                if (distancePercent < 0)
                                    distancePercent = 0;
                            }
                            moving(movingFromIndex, movingToIndex, distancePercent, false);
                        }
                    } else {
                        stopLoop();
                        moveTail(movingToIndex);
                    }

                    break;
            }

        }
    };

    private float moveingPercent = 1;
    private int movingToIndex;
    private int movingFromIndex;


    private void moveTail(final int toIndex) {
        if (newStart) {
            initTwoPoints(toIndex);
            calculate(toIndex, toIndex);
            invalidate();
            return;
        }
        moveingPercent = 1;
        movingSchedule = MovingSchedule.moveTail;
        this.movingToIndex = toIndex;
        distancePercent = movingPoint.getPosition().getPositionX() - stayPoint.getPosition().getPositionX();
        float reduceAngle = (float) (35 * Math.PI / 180);
        if (oldAngle > reduceAngle) {
            angle = oldAngle - reduceAngle;
        }
        startLoop();
    }

    private void leftActionAnimation(final int fromIndex, final int toIndex) {
        newStart = false;
        this.movingFromIndex = fromIndex;
        this.movingToIndex = toIndex;
        movingSchedule = MovingSchedule.leftAnim;
        startLoop();
    }

    int currentItem = 0;
    int nextItem = 0;
    boolean isMoving = false;
    private float mLastPositionOffsetSum;

    private void setOnPageControlListener(final ViewPager viewPager, final OnPageChangeListener onPageChangeListener) {
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (null != onPageChangeListener) {
                    onPageChangeListener.onPageSelected(arg0);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (null != onPageChangeListener) {
                    onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
                float currentPositionOffsetSum = position + positionOffset;
                if (mLastPositionOffsetSum <= currentPositionOffsetSum) {
                    nextItem = currentItem + 1;
                } else {
                    nextItem = currentItem - 1;
                }

                if (isMoving) {
                    moving(currentItem, nextItem, positionOffset, true);
                }
                mLastPositionOffsetSum = currentPositionOffsetSum;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (null != onPageChangeListener) {
                    onPageChangeListener.onPageScrollStateChanged(arg0);
                }
                switch (arg0) {
                    case 0://滑动结束
                        currentItem = viewPager.getCurrentItem();
                        break;
                    case 1://滑动开始
                        isMoving = true;
                        break;
                    case 2://滑动手指离开屏幕
                        isMoving = false;
                        leftActionAnimation(currentItem, viewPager.getCurrentItem());
                        currentItem = viewPager.getCurrentItem();
                        break;
                }
            }
        });
    }

    public void setup(Builder builder) {
        if (null == builder.viewPager) {
            throw new IllegalStateException("viewPager is required");
        }
        if (null != builder.adapter) {
            builder.count = builder.adapter.getCount();
            builder.viewPager.setAdapter(builder.adapter);
        }
        setPointCount(builder.count);
        setOnPageControlListener(builder.viewPager, builder.onPageChangeListener);
    }

    public static final class Builder {
        private int count;
        private ViewPager viewPager;
        private PagerAdapter adapter;
        private OnPageChangeListener onPageChangeListener;

        public Builder() {
        }

        public Builder setAdapter(PagerAdapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder setCount(int count) {
            this.count = count;
            return this;
        }

        public Builder bindViewPager(ViewPager viewPager) {
            this.viewPager = viewPager;
            return this;
        }

        public Builder setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
            this.onPageChangeListener = onPageChangeListener;
            return this;
        }
    }
}
