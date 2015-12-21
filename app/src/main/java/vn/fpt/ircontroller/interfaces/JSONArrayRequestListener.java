package vn.fpt.ircontroller.interfaces;

import org.json.JSONArray;


import com.android.volley.VolleyError;

import vn.fpt.ircontroller.cores.CoreInterface;

public interface JSONArrayRequestListener extends CoreInterface {
	public void onBefore();
	public void onResponse(JSONArray response);
	public void onError(VolleyError error);
}
