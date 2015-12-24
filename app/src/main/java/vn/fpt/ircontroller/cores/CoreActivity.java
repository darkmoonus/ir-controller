package vn.fpt.ircontroller.cores;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import vn.fpt.ircontroller.application.IRApplication;
import vn.fpt.ircontroller.ble.ChooseDeviceActivity;
import vn.fpt.ircontroller.ble.UartService;
import vn.fpt.ircontroller.dialogs.DialogAddDevice;
import vn.fpt.ircontroller.dialogs.DialogAddRoom;
import vn.fpt.ircontroller.dialogs.DialogScanBLE;
import vn.fpt.ircontroller.interfaces.DialogAddDeviceListener;
import vn.fpt.ircontroller.interfaces.DialogAddRoomListener;
import vn.fpt.ircontroller.interfaces.DialogScanBLEListener;
import vn.fpt.ircontroller.models.Room;

public abstract class CoreActivity extends AppCompatActivity implements Serializable, OnClickListener {
	private static final long serialVersionUID = -6161155222390513466L;
	private static String TAG = "Core Activity";

	public void showSnackBar(View v, String msg, String action, OnClickListener o) {
		Snackbar.make(v, msg, Snackbar.LENGTH_LONG).setAction(action, o).show();
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	}

	public void saveRoomListSharedPreference() {
		Gson gson = new Gson();
		String json = gson.toJson(IRApplication.mRoomList);
		saveSharedPreferences("IRController", "rooms", json);
	}
	public void readRoomListSharedPreference() {
		Type type = new TypeToken<List<Room>>(){}.getType();
		ArrayList<Room> roomList = new Gson().fromJson(readSharedPreferences("IRController", "rooms"), type);
		if(roomList != null) {
			IRApplication.mRoomList = roomList;
		}
	}
	public DialogFragment showScanBLEDialog(final DialogScanBLEListener mListener) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				removePreviousDialog();
				mDialog = DialogScanBLE.newInstance(CoreActivity.this, mListener);
				mDialog.show(getSupportFragmentManager(), TAG);
			}
		});
		return mDialog;
	}

	public DialogFragment showAddRoomDialog(final DialogAddRoomListener mListener) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				removePreviousDialog();
				mDialog = DialogAddRoom.newInstance(CoreActivity.this, mListener);
				mDialog.show(getSupportFragmentManager(), TAG);
			}
		});
		return mDialog;
	}
	public DialogFragment showAddDeviceDialog(final DialogAddDeviceListener mListener) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				removePreviousDialog();
				mDialog = DialogAddDevice.newInstance(CoreActivity.this, mListener);
				mDialog.show(getSupportFragmentManager(), TAG);
			}
		});
		return mDialog;
	}

	public void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = getCurrentFocus();
		if (view == null) {
			view = new View(this);
		}
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void hide_keyboard() {
	    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	    View view = getCurrentFocus();
	    if(view == null) {
	        view = new View(this);
	    }
	    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	public void showKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
	}
	public void loge(final Object msg) {
		if(msg == null) {
			Log.e(TAG, "NULL");
		} else {
			Log.e(TAG, msg.toString());
		}
	}
	public void logd(final Object msg) {
		if(msg == null) {
			Log.d(TAG, "NULL");
		} else {
			Log.d(TAG, msg.toString());
		}
	}
	public void logi(final Object msg) {
		if(msg == null) {
			Log.i(TAG, "NULL");
		} else {
			Log.i(TAG, msg.toString());
		} 
	}

	public void showToastShort(final Object message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(message == null) {
					Toast.makeText(CoreActivity.this, "NULL", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CoreActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		logi(new StringBuilder().append("Done. Show short Toast: ").append(message).toString());
	}
	public void showToastLong(final Object message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(message == null) {
					Toast.makeText(CoreActivity.this, "NULL", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(CoreActivity.this, message.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
		logi(new StringBuilder().append("Done. Show long Toast: ").append(message).toString());
	}
	
	public String readSharedPreferences(String preferencesName, String elementName) {
		SharedPreferences pre = getSharedPreferences(preferencesName, MODE_PRIVATE);
		String s = pre.getString(elementName, "");
		logi(new StringBuilder().append("Done. Read shared preferences [ ")
				.append(preferencesName).append("]:[").append(elementName)
				.append("]: ").append(s).toString());
		return s;
	}
	public void saveSharedPreferences(String preferencesName, String elementName, String data) {
		SharedPreferences pre = getSharedPreferences(preferencesName, MODE_PRIVATE);
		SharedPreferences.Editor edit = pre.edit();
		edit.putString(elementName, data);
		edit.commit();
		logi(new StringBuilder().append("Done. Write shared preferences [ ")
				.append(preferencesName).append("]:[").append(elementName)
				.append("]: ").append(data).toString());
	}
	
	private android.support.v4.app.FragmentManager mFragmentManager = getSupportFragmentManager();
	public void removePreviousDialog() {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		Fragment prev = mFragmentManager.findFragmentByTag(TAG);
		if (prev != null) ft.remove(prev);
		ft.commit();
		logi(new StringBuilder().append("Done. Remove previous dialog. TAG = ").append(TAG).toString());
	}
	private DialogFragment mDialog;
//	public DialogFragment showProgressDialog(final String msg) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				removePreviousDialog();
//				mDialog = DialogProgress.newInstance(CoreActivity.this, msg);
//				mDialog.show(getSupportFragmentManager(), TAG);
//				logi(new StringBuilder().append("Done. Show progressDialog : ").append(msg).toString());
//			}
//		});
//		return mDialog;
//	}
//	public DialogFragment showYesNoDialog(final String msg, final OnDialogYesNoListener mListener) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				removePreviousDialog();
//				mDialog = DialogYesNo.newInstance(CoreActivity.this, msg, mListener);
//				mDialog.show(getSupportFragmentManager(), TAG);
//				logi(new StringBuilder().append("Done. Show yesNoDialog : ").append(msg).toString());
//			}
//		});
//		return mDialog;
//	}
	
	public String TAG_JSONOBJ_REQUEST = "jsonobject_request";
	public String TAG_JSONARR_REQUEST = "jsonarrayobject_request";
	public String TAG_STRING_REQUEST = "string_request";
	public void cancelAllRequestWithTag(String tag) {
		IRApplication.getInstance().getRequestQueue().cancelAll(tag);
	}
//	public void makeJsonObjectRequest(String url, final JSONObjectRequestListener mListener){
//		mListener.onBefore();
//		JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Method.GET, url, null,
//				new Response.Listener<JSONObject>() {
//					@Override
//					public void onResponse(JSONObject response) {
//						mListener.onResponse(response);
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						mListener.onError(error);
//					}
//				}){
//			/* Passing some request headers*/
//			@Override
//			public Map<String, String> getHeaders()throws AuthFailureError {
//				HashMap<String, String> headers = new HashMap<String, String>();
//				headers.put("Content-Type", "application/json");
//				return headers;
//			}
//
//			@Override
//			protected Map<String, String> getParams()throws AuthFailureError {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("name", "Androidhive");
//				params.put("email", "abc@androidhive.info");
//				params.put("pass", "password123");
//				return  params;
//		   }
//		} ;
//		IRApplication.getInstance().addToRequestQueue(jsonObjRequest, TAG_JSONOBJ_REQUEST);
//	}
//	public void makeJsonArrayRequest(String url, final JSONArrayRequestListener mListener){
//		mListener.onBefore();
//		JsonArrayRequest jsonArrRequest = new JsonArrayRequest(url,
//				new Response.Listener<JSONArray>() {
//					@Override
//					public void onResponse(JSONArray response) {
//						mListener.onResponse(response);
//					}
//				}, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						mListener.onError(error);
//					}
//				});
//		IRApplication.getInstance().addToRequestQueue(jsonArrRequest, TAG_JSONARR_REQUEST);
//	}
//	public void makeStringRequest(String url, final StringRequestListener mListener){
//		mListener.onBefore();
//		StringRequest stringRequest = new StringRequest(Method.GET, url,
//				new Response.Listener<String>() {
//			@Override
//			public void onResponse(String response) {
//				mListener.onResponse(response);
//			}
//		}, new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				mListener.onErrorResponse(error);
//			}
//		});
//		IRApplication.getInstance().addToRequestQueue(stringRequest, TAG_STRING_REQUEST);
//	}
//
//	public void makeImageRequest(String url, final ImageRequestListener mListener){
//		mListener.onBefore();
//		ImageLoader imageLoader = IRApplication.getInstance().getImageLoader();
//		imageLoader.get(url, new ImageListener() {
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				mListener.onErrorResponse(error);
//			}
//			@Override
//			public void onResponse(ImageContainer paramImageContainer, boolean paramBoolean) {
//				mListener.onResponse(paramImageContainer, paramBoolean);
////				imageView.setImageBitmap(paramImageContainer.getBitmap());
//			}
//		});
//	}

	/*
	 * Get latitude and longtitude from address.
	 * @return lattitude|longtitude or null if invalid
	 */
	@SuppressWarnings("finally")
	public String getAddressFromLocation(final String locationAddress,
			final Context context) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		String result = null;
		try {
			List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
			if (addressList != null && addressList.size() > 0) {
				Address address = addressList.get(0);
				StringBuilder sb = new StringBuilder();
				sb.append(address.getLatitude()).append(",");
				sb.append(address.getLongitude());
				result = sb.toString();
			}
		} catch (IOException e) {
			loge("Unable to connect to Geocoder");
		} finally {
			return result;
		}
	}
	
	protected abstract void initViews();
	protected abstract void initModels();
	protected abstract void initListeners();
	protected abstract void initAnimations();


}
