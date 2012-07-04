package SmartSD.DemoTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "smsreceiver";
	public static boolean on = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, ">>>>>>>onReceive start");

		StringBuilder body = new StringBuilder();
		StringBuilder number = new StringBuilder();
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] _pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] message = new SmsMessage[_pdus.length];
			for (int i = 0; i < _pdus.length; i++) {
				message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
			}
			for (SmsMessage currentMessage : message) {
				body.append(currentMessage.getDisplayMessageBody());
				number.append(currentMessage.getDisplayOriginatingAddress());
			}
			String smsBody = body.toString();
			String smsNumber = number.toString();
			if (smsNumber.contains("+86")) {
				smsNumber = smsNumber.substring(3);
			}
			Log.v(TAG, "Number:" + smsNumber);
			Log.v(TAG, "Body:" + smsBody);
			SmartSD.addSmsMessage(smsNumber, smsBody);
		}
		Log.v(TAG, ">>>>>>>onReceive end");
	}

}
