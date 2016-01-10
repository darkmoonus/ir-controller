package vn.fpt.ircontroller.customizes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import vn.fpt.ircontroller.application.Configs;

/**
 * Created by hunter on 12/19/2015.
 */
public class MyTextView extends TextView {

	public MyTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyTextView(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/" + Configs.DEFAULT_FONT + ".ttf"));
		}
	}

}