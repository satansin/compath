package com.satansin.android.compath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


/**
 * 
 * @author 空山不空
 * Gallery图片页面，通过Intent得到GridView传过来的图片位置，加载图片，再设置适配器
 */
public class GalleryActivity extends Activity {
	public int i_position = 0;
	private DisplayMetrics dm;
	private int picCount = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.mygallery);	 
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得Gallery对象	
		GalleryExt  g = (GalleryExt) findViewById(R.id.ga);
		//通过Intent得到GridView传过来的图片位置
		Intent intent = getIntent();
		i_position = intent.getIntExtra("position", 0);	 
		picCount = intent.getIntExtra("picCount", 0);
		// 添加ImageAdapter给Gallery对象
		ImageAdapter ia=new ImageAdapter(this);		
		ia.setCount(picCount);
		g.setAdapter(ia);
	 	g.setSelection(i_position); 	
	 	
	 	//加载动画
	 	Animation an= AnimationUtils.loadAnimation(this,R.anim.scale );
        g.setAnimation(an); 

	} 
}