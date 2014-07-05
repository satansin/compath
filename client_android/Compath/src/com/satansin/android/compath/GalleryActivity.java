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
 * @author ��ɽ����
 * GalleryͼƬҳ�棬ͨ��Intent�õ�GridView��������ͼƬλ�ã�����ͼƬ��������������
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
		// ���Gallery����	
		GalleryExt  g = (GalleryExt) findViewById(R.id.ga);
		//ͨ��Intent�õ�GridView��������ͼƬλ��
		Intent intent = getIntent();
		i_position = intent.getIntExtra("position", 0);	 
		picCount = intent.getIntExtra("picCount", 0);
		// ���ImageAdapter��Gallery����
		ImageAdapter ia=new ImageAdapter(this);		
		ia.setCount(picCount);
		g.setAdapter(ia);
	 	g.setSelection(i_position); 	
	 	
	 	//���ض���
	 	Animation an= AnimationUtils.loadAnimation(this,R.anim.scale );
        g.setAnimation(an); 

	} 
}