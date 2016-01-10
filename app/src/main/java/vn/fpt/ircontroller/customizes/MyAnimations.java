package vn.fpt.ircontroller.customizes;

import android.graphics.Point;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by hunter on 12/19/2015.
 */
public class MyAnimations extends Animation {

	public static Animation translate(Point begin, Point end, int duration) {
		Animation animation = new TranslateAnimation(begin.x, begin.y, end.x, end.y);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation slideUp(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, 0, 0, distance);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation slideDown(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, 0, 0, -distance);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation slideLeft(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, -distance, 0, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation slideRight(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, distance, 0, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fromUp(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, 0, distance, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fromDown(int distance, int duration) {
		Animation animation = new TranslateAnimation(0, 0, -distance, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fromLeft(int distance, int duration) {
		Animation animation = new TranslateAnimation(-distance, 0, 0, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fromRight(int distance, int duration) {
		Animation animation = new TranslateAnimation(distance, 0, 0, 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fromRight(int distance, int spaceFront, int duration) {
		Animation animation = new TranslateAnimation(distance, spaceFront, 0 , 0);
		animation.setDuration(duration);
		animation.setFillAfter(true);
		return animation;
	}
	
	public static Animation fade(int begin, int end) {
		return new AlphaAnimation(begin, end);
	}
	
}
