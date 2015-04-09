# PageControl  
PageControl for Android  
In XML:  

	<com.suiyiyi.pagecontrol.PageControl  
	android:id="@+id/pagecontrol"  
	android:layout_width="wrap_content"  
	android:layout_height="wrap_content"  
	android:layout_alignParentBottom="true"  
	android:layout_centerHorizontal="true"  
	android:layout_marginBottom="20dp"  
	android:background="@android:color/transparent"/>
        
In Code:
      if you do not need viewPage's onPageChangeListen and OnTouchListener,just{
      
		pageControl.setPointCount(tvList.length);
		pageControl.setOnPageControlListener(viewPager, pageControl);
		
      }else{
      
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
      
      
      }
