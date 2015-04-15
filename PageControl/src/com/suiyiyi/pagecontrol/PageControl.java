package com.suiyiyi.pagecontrol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PageControl extends View {

	private Paint paintStroke, paintFill;
	private Path path;
	private int colorMovingPoint, colorPoints;
	private int nums;
	private boolean isFill;
	private float space;
	private float angle;
	private float radiusNormal;
	private float radiusFocus;
	private Point[] points;
	private Point movingPoint;
	private Point stayPoint;
	private int strokeWidth;
	private float oldAngle;

	public PageControl(Context context) {
		super(context);
		init(context, null);
	}

	public PageControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public PageControl(Context context, AttributeSet attrs, int defStyleAttr) {
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

	public void setPointCount(int count) {
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
		points = new Point[nums];

		for (int i = 0; i < points.length; i++) {
			Position position = new Position(radiusFocus + i * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
			Point point = new Point(position);
			point.setRadius(radiusNormal);
			points[i] = point;
		}
		initTwoPoints(0);
	}

	private void initTwoPoints(int index) {
		Position position = new Position(radiusFocus + index * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
		movingPoint = new Point(position);
		movingPoint.setRadius(0);
		Position position2 = new Position(radiusFocus + index * (radiusFocus * 2 + space) + strokeWidth, radiusFocus + strokeWidth);
		stayPoint = new Point(position2);
		stayPoint.setRadius(radiusFocus);
		calculate(index, index);
		angle = oldAngle;
	}

	private void calculate(int fromIndex, int toIndex) {
		path.reset();
		if (fromIndex > toIndex) {
			path.moveTo(movingPoint.getPositionUpRight().getPositionX(), movingPoint.getPositionUpRight().getPositionY());
			Position middlePosition = getIntersectPoint(movingPoint.getPositionUpRight(), stayPoint.getPositionUpLeft());
			path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), stayPoint.getPositionUpLeft().getPositionX(), stayPoint
					.getPositionUpLeft().getPositionY());
			path.lineTo(stayPoint.getPositionDownLeft().getPositionX(), stayPoint.getPositionDownLeft().getPositionY());
			middlePosition = getIntersectPoint(stayPoint.getPositionDownLeft(), movingPoint.getPositionDownRight());
			path.quadTo(middlePosition.getPositionX(), middlePosition.getPositionY(), movingPoint.getPositionDownRight().getPositionX(), movingPoint
					.getPositionDownRight().getPositionY());
			path.lineTo(movingPoint.getPositionUpRight().getPositionX(), movingPoint.getPositionUpRight().getPositionY());
		} else {
			path.moveTo(stayPoint.getPositionUpRight().getPositionX(), stayPoint.getPositionUpRight().getPositionY());
			Position middlePosition = getIntersectPoint(stayPoint.getPositionUpRight(), movingPoint.getPositionUpLeft());
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
			Point p = points[i];
			canvas.drawCircle(p.getPosition().getPositionX(), p.getPosition().getPositionY(), p.getRadius(), paintStroke);
		}
		canvas.drawCircle(movingPoint.getPosition().getPositionX(), movingPoint.getPosition().getPositionY(), movingPoint.getRadius(), paintFill);
		canvas.drawCircle(stayPoint.getPosition().getPositionX(), stayPoint.getPosition().getPositionY(), stayPoint.getRadius(), paintFill);
		super.onDraw(canvas);
	}

	public Position getIntersectPoint(Position p1, Position p2) {
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
		return new Position(x, y);
	}

	public void setAngle(float angle) {
		this.angle = angle;
		invalidate();
	}

	public void setDistance(float distance) {
		this.space = distance;
		for (int i = 0; i < points.length; i++) {
			Point p = points[i];
			Position position = new Position(radiusFocus + i * (radiusFocus * 2 + space), radiusFocus);
			p.setPosition(position);
		}
		invalidate();
	}

	float distancePercent;
	int oldFromIndex, oldToIndex;
	boolean newStart = false;

	public void moving(int fromIndex, int toIndex, float distancePercent, boolean newStart) {
		this.newStart = newStart;
		this.distancePercent = distancePercent;
		if (fromIndex < toIndex) {
			oldFromIndex = fromIndex;
			oldToIndex = toIndex;
			stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
			movingPoint.setRadius(radiusFocus * distancePercent);
			Position p = movingPoint.getPosition();
			p.setPositionX(stayPoint.getPosition().getPositionX() + (space + radiusFocus * 2) * distancePercent);
			movingPoint.setPosition(p);
			calculate(fromIndex, toIndex);
		} else if (fromIndex > toIndex) {
			oldFromIndex = fromIndex;
			oldToIndex = toIndex;
			distancePercent = 1 - distancePercent;
			stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
			movingPoint.setRadius(radiusFocus * distancePercent);
			Position p = movingPoint.getPosition();
			p.setPositionX(stayPoint.getPosition().getPositionX() - (space + radiusFocus * 2) * distancePercent);
			movingPoint.setPosition(p);
			calculate(fromIndex, toIndex);
		} else {
			distancePercent = 1 - distancePercent;
			stayPoint.setRadius(radiusFocus - radiusFocus * distancePercent);
			movingPoint.setRadius(radiusFocus * distancePercent);
			Position p = movingPoint.getPosition();
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

	private void moveTail(final int toIndex) {
		if (newStart) {
			initTwoPoints(toIndex);
			calculate(toIndex, toIndex);
			invalidate();
			return;
		}
		final int MESSAGE_PERCENT = 0;
		final int MESSAGE_FINISH = 1;
		final float distance = movingPoint.getPosition().getPositionX() - stayPoint.getPosition().getPositionX();
		float reduceAngle = (float) (35 * Math.PI / 180);
		if (oldAngle > reduceAngle)
			angle = oldAngle - reduceAngle;
		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (MESSAGE_PERCENT == msg.what) {
					float percent = (float) ((msg.arg1) / 120.0);
					float middlePositionX = distance * percent;
					Position p = stayPoint.getPosition();
					p.setPositionX(movingPoint.getPosition().getPositionX() - middlePositionX);
					stayPoint.setPosition(p);
					calculate(oldFromIndex, oldToIndex);
					invalidate();
				} else if (MESSAGE_FINISH == msg.what) {
					initTwoPoints(toIndex);
					calculate(toIndex, toIndex);
					invalidate();
				}
			};
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				float percent = 1;
				while (!newStart && percent > 0) {
					percent -= 0.1;
					Message msg = Message.obtain();
					msg.what = MESSAGE_PERCENT;
					msg.arg1 = (int) (percent * 100);
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessage(MESSAGE_FINISH);
			}
		}).start();
	}

	public void leftActionAnimation(final int fromIndex, final int toIndex) {
		newStart = false;
		final int MESSAGE_BACK = 0;
		final int MESSAGE_JUMP = 1;
		final int MESSAGE_FINISH = 2;
		final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (MESSAGE_BACK == msg.what) {
					distancePercent -= 0.05;
					if (distancePercent < 0)
						distancePercent = 0;
					moving(fromIndex, toIndex, distancePercent, false);
				} else if (MESSAGE_JUMP == msg.what) {
					if (fromIndex < toIndex) {
						distancePercent += 0.05;
						if (distancePercent > 1)
							distancePercent = 1;
					} else {
						distancePercent -= 0.05;
						if (distancePercent < 0)
							distancePercent = 0;
					}
					moving(fromIndex, toIndex, distancePercent, false);
				} else if (MESSAGE_FINISH == msg.what) {
					moveTail(toIndex);
				}
			}
		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!newStart && distancePercent > 0 && distancePercent < 1) {
					if (fromIndex == toIndex)
						mHandler.sendEmptyMessage(MESSAGE_BACK);
					else
						mHandler.sendEmptyMessage(MESSAGE_JUMP);
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessage(MESSAGE_FINISH);
			}
		}).start();
	}

	int currentItem = 0;
	int nextItem = 0;
	boolean isMoving = false;

	public void setOnPageControlListener(final ViewPager viewPager, final PageControl pageControl) {
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				if (isMoving) {
					pageControl.moving(currentItem, nextItem, arg1, true);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				/*
				 * arg0 == 0:滑动结束 arg0 == 1:滑动开始 arg0 == 2 滑动手指离开屏幕
				 */
				if (arg0 == 0) {// 滑动结束
					currentItem = viewPager.getCurrentItem();
					// pageControl.resetMovingPoints(currentItem);
				}
				if (arg0 == 2) {// 滑动手指离开屏幕
					isMoving = false;
					pageControl.leftActionAnimation(currentItem, viewPager.getCurrentItem());
					currentItem = viewPager.getCurrentItem();
				}
			}
		});

		viewPager.setOnTouchListener(new OnTouchListener() {
			float XDown;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					XDown = event.getX();
					isMoving = true;
					break;
				case MotionEvent.ACTION_MOVE:
					if (event.getX() > XDown && currentItem > 0) {
						nextItem = currentItem - 1;
					} else {
						nextItem = currentItem + 1;
					}
					break;
				}
				return false;
			}
		});
	}

	private class Point {
		private Position position;
		private Position positionUpLeft;
		private Position positionDownLeft;
		private Position positionUpRight;
		private Position positionDownRight;
		private float radius;

		public Point(Position position) {
			this.position = position;
		}

		public Point(Position position, float radius) {
			this.position = position;
			this.radius = radius;
		}

		public Position getPosition() {
			return position;
		}

		public void setPosition(Position position) {
			this.position = position;
		}

		public float getRadius() {
			return radius;
		}

		public void setRadius(float radius) {
			this.radius = radius;
		}

		public Position getPositionUpLeft() {
			float x = (float) (position.getPositionX() - getRadius() * Math.cos(angle / 2.0));
			float y = (float) (position.getPositionY() - getRadius() * Math.sin(angle / 2.0));
			positionUpLeft = new Position(x, y);
			return positionUpLeft;
		}

		public void setPositionUpLeft(Position positionUpLeft) {
			this.positionUpLeft = positionUpLeft;
		}

		public Position getPositionDownLeft() {
			float x = (float) (position.getPositionX() - getRadius() * Math.cos(angle / 2.0));
			float y = (float) (position.getPositionY() + getRadius() * Math.sin(angle / 2.0));
			positionDownLeft = new Position(x, y);
			return positionDownLeft;
		}

		public void setPositionDownLeft(Position positionDownLeft) {
			this.positionDownLeft = positionDownLeft;
		}

		public Position getPositionUpRight() {
			float x = (float) (position.getPositionX() + getRadius() * Math.cos(angle / 2.0));
			float y = (float) (position.getPositionY() - getRadius() * Math.sin(angle / 2.0));
			positionUpRight = new Position(x, y);
			return positionUpRight;
		}

		public void setPositionUpRight(Position positionUpRight) {
			this.positionUpRight = positionUpRight;
		}

		public Position getPositionDownRight() {
			float x = (float) (position.getPositionX() + getRadius() * Math.cos(angle / 2.0));
			float y = (float) (position.getPositionY() + getRadius() * Math.sin(angle / 2.0));
			positionDownRight = new Position(x, y);
			return positionDownRight;
		}

		public void setPositionDownRight(Position positionDownRight) {
			this.positionDownRight = positionDownRight;
		}

	}

	private class Position {
		private float positionX;
		private float positionY;

		public Position(float positionX, float positionY) {
			this.positionX = positionX;
			this.positionY = positionY;
		}

		public float getPositionX() {
			return positionX;
		}

		public void setPositionX(float positionX) {
			this.positionX = positionX;
		}

		public float getPositionY() {
			return positionY;
		}

		public void setPositionY(float positionY) {
			this.positionY = positionY;
		}
	}

}
