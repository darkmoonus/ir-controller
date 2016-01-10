package vn.fpt.ircontroller.interfaces;

import com.android.volley.VolleyError;

import vn.fpt.ircontroller.cores.CoreInterface;

public interface StringRequestListener  extends CoreInterface {
	public void onBefore();
	public void onResponse(String response);
	public void onErrorResponse(VolleyError error);
}
