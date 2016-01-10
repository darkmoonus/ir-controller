package vn.fpt.ircontroller.customizes;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import vn.fpt.ircontroller.application.Configs;

/**
 * Created by hunter on 12/19/2015.
 */
public class MyEditText extends EditText {

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MyEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/" + Configs.DEFAULT_FONT + ".ttf"));
		}
	}

}