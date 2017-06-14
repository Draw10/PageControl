# ViewPager Indicators  
viewPager indicator for Android  

### Contribute
Please do! I'm happy to review and accept pull requests.
### Developed By
* Gavin  <xujianhua.ae@gmail.com>



### Special Thanks
* Chenupt - [BezierDemo](https://github.com/chenupt/BezierDemo)


### Getting Started
##### In XML:  

	<com.e_gavin163.hsviewpageindicators.Indicator
        android:id="@+id/pagecontrol"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="20dp"
        android:background="@android:color/transparent"
        app:colorMovingPoint="#00ab7d"
        app:colorPoints="#545454"
        app:isFill="true" />
        
##### In Code:  

	pageControl.setup(
                new Indicator.Builder()
                        .setAdapter(adapter)
                        .bindViewPager(viewPager)
        );

##### All xml attributes:  

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



### Demo
![Alt Text](https://raw.githubusercontent.com/JianhuaXu/PageControl/master/demo.gif)
![Alt Text](https://raw.githubusercontent.com/JianhuaXu/PageControl/master/demo2.gif)




