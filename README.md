# PageControl  
PageControl for Android  

###Contribute
Please do! I'm happy to review and accept pull requests.
###Developed By
* Gavin  <xujianhua.ae@gmail.com>



###Special Thanks
* Chenupt - [BezierDemo](https://github.com/chenupt/BezierDemo)


###Getting Started
#####In XML:  

	<com.suiyiyi.pagecontrol.PageControl  
		android:id="@+id/pagecontrol"  
		android:layout_width="wrap_content"  
		android:layout_height="wrap_content"  
		android:layout_alignParentBottom="true"  
		android:layout_centerHorizontal="true"  
		android:layout_marginBottom="20dp"  
		android:background="@android:color/transparent"
		app:colorMovingPoint="#00ab7d"
        app:colorPoints="#545454"
        app:isFill="true"
        app:radiusFocus="5dp"
        app:radiusNormal="3dp"/>
        
#####In Code:  
######if you do not need viewPage's onPageChangeListen and OnTouchListener,just{
      
		pageControl.setPointCount(tvList.length);
		pageControl.setOnPageControlListener(viewPager, pageControl);
######}else{
      		
      		int currentItem = 0;
	        int nextItem = 0;
	        boolean isMoving = false;
	        
		    viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			    @Override
			    public void onPageSelected(int arg0) {}

			    @Override
			    public void onPageScrolled(int arg0, float arg1, int arg2) {
				    if (isMoving) {
					    pageControl.moving(currentItem, nextItem, arg1, true);
				    }
			    }

			    @Override
			    public void onPageScrollStateChanged(int arg0) {
				    if (arg0 == 0) {// 滑动结束
					    currentItem = viewPager.getCurrentItem();
				    }else if (arg0 == 2) {// 滑动手指离开屏幕
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
	//==================================================
	//if you can not get ACTION_DOWN,just:
	//==================================================
      		int currentItem = 0;
	        int nextItem = 0;
	        boolean isMoving = false;
	        float XDown = -1;
	        
		    viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			    @Override
			    public void onPageSelected(int arg0) {}

			    @Override
			    public void onPageScrolled(int arg0, float arg1, int arg2) {
				    if (isMoving) {
					    pageControl.moving(currentItem, nextItem, arg1, true);
				    }
			    }

			    @Override
			    public void onPageScrollStateChanged(int arg0) {
				    if (arg0 == 0) {// 滑动结束
					    currentItem = viewPager.getCurrentItem();
				    }else if (arg0 == 2) {// 滑动手指离开屏幕
					    isMoving = false;
					    XDown = -1;
					    pageControl.leftActionAnimation(currentItem, viewPager.getCurrentItem());
					    currentItem = viewPager.getCurrentItem();
				    }
			    }
		    });

		    mViewPager.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						if (XDown > 0 && event.getX() > XDown && currentPageItem > 0) {
							nextItem = currentPageItem - 1;
							isMoving = true;
						} else if (XDown > 0) {
							nextItem = currentPageItem + 1;
							isMoving = true;
						}
						if (!isMoving) {
							XDown = event.getX();
						}
						break;
					}
					return false;
				}
			});

		    
      
######}

#####All xml attributes:  

	 <declare-styleable name="PageControl">
        <attr name="colorMovingPoint" format="color" />
        <attr name="colorPoints" format="color" />
        <attr name="nums" format="integer" />
        <attr name="space" format="dimension" />
        <attr name="angle" format="dimension" />
        <attr name="radiusNormal" format="dimension" />
        <attr name="radiusFocus" format="dimension" />
        <attr name="isFill" format="boolean" />
    </declare-styleable>



###Demo
![Alt Text](https://raw.githubusercontent.com/JianhuaXu/PageControl/master/demo.gif)
![Alt Text](https://raw.githubusercontent.com/JianhuaXu/PageControl/master/demo2.gif)




