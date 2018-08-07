package com.roqos.cordova.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import org.apache.cordova.CallbackContext;
import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;

public class OnBootReceiver extends BroadcastReceiver {
  public static final String ALWAYS_ON = "alwaysOn";
  private Context mContext; 

  @Override
  public void onReceive(Context context, Intent intent) {
    // do startup tasks or start your luncher activity
    this.mContext = context;
    Toast.makeText(context, "Guardian VPN", Toast.LENGTH_SHORT).show();
    if(getAlwaysOn(context, OnBootReceiver.ALWAYS_ON)) connectDNSVPN();
  }

  private void connectDNSVPN() {
    Intent intent = VpnService.prepare(this.mContext);

    if (intent != null) {
        ((Activity)this.mContext).startActivityForResult(intent, 0);
    } else {
        onActivityResult(0, Activity.RESULT_OK, null);
    } 
  }

  public void onActivityResult(int request, int result, Intent data) {
    if (result == Activity.RESULT_OK) {
        // Toast.makeText( this.mContext,
        //         "onActivityResult", Toast.LENGTH_LONG).show();
        RoqosVPNService.primaryServer = DNSServerHelper.getAddressById(DNSServerHelper.getPrimary());
        RoqosVPNService.secondaryServer = DNSServerHelper.getAddressById(DNSServerHelper.getSecondary());
        this.mContext.startService(new Intent(this.mContext, RoqosVPNService.class).setAction(RoqosVPNService.ACTION_ACTIVATE));
    }
  }

  public static void saveAlwaysOn(Context context, boolean alwaysOn, String key) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Editor prefsedit = prefs.edit();
    prefsedit.putBoolean(key, alwaysOn);
    prefsedit.apply();
  }

  private boolean getAlwaysOn(Context context, String key) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean useStartOnBoot = prefs.getBoolean(key, false);
    return useStartOnBoot;
  }
}
