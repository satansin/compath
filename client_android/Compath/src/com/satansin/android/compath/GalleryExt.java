package com.satansin.android.compath;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery; 
import android.widget.Toast;

/**
 * 
 * @author ��ɽ����
 * ��չGallery��������û���һ��ֻ����һ��ͼƬ�����ң�
 * ����ǵ�һ��ͼƬʱ�����󻬶�����ʾ���ѵ���һҳ��
 * ��������һ��ͼƬʱ�����һ�������ʾ���ѵ��ں�ҳ��
 */
public class GalleryExt extends Gallery {
    boolean is_first=false;
    boolean is_last=false;
	public GalleryExt(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public GalleryExt(Context context,AttributeSet paramAttributeSet)
	 {
	       super(context,paramAttributeSet);

	 }

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
	   {   
	    return e2.getX() > e1.getX(); 
	   }

	 @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX,
                    float distanceY) { 
 //ͨ���ع�onFling������ʹGallery�ؼ�����һ��ֻ����һ��ͼƬ
		 //��ȡ������
		 ImageAdapter ia=(ImageAdapter)this.getAdapter();
		//�õ���ǰͼƬ��ͼƬ��Դ�е�λ��
		 int p=ia.getOwnposition();
		 //ͼƬ��������
		 int count=ia.getCount(); 
		 int kEvent;  
		  if(isScrollingLeft(e1, e2)){ 
		   //Check if scrolling left  
			  if(p==0&&is_first){
				  //�ڵ�һҳ�����������ƶ���ʱ����ʾ
				  Toast.makeText(this.getContext(), "�ѵ���һҳ", Toast.LENGTH_SHORT).show();
			  }else if(p==0){
				  //�����һҳʱ����is_first����Ϊtrue
				  is_first=true;
			  }else{
				  is_last=false;
			  }
			  
		   kEvent = KeyEvent.KEYCODE_DPAD_LEFT;  
		   }  else{ 
		    //Otherwise scrolling right    
			   if(p==count-1&&is_last){
					  Toast.makeText(this.getContext(), "�ѵ����һҳ", Toast.LENGTH_SHORT).show();
				  }else if( p==count-1){
					  is_last=true;
				  }else{
					  is_first=false;
				  }
				  
		    kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;   
		    }  
		  onKeyDown(kEvent, null);  
		  return true;  
    }
	 
	 
}