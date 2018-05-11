package id.sch.smkn13bdg.adhi.brilinkarkanfoto.volley;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import id.sch.smkn13bdg.adhi.brilinkarkanfoto.MainActivity;
import id.sch.smkn13bdg.adhi.brilinkarkanfoto.getset.UserController;

/**
 * Created by adhi on 09/05/18.
 */

public class SharedPrefManager {

    //the constants
    private static final String SHARED_PREF_NAME = "brilinkarkan";
    private static final String KEY_NAME = "nama_pelanggan";
    private static final String KEY_CARD = "no_kartu";
    private static final String KEY_ID = "id_pelanggan";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(UserController user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId_pelanggan());
        editor.putString(KEY_NAME, user.getNama_pelanggan());
        editor.putString(KEY_CARD, user.getNo_kartu());
        editor.apply();
    }

    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAME, null) != null;
    }

    //this method will give the logged in user
    public UserController getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserController(
                sharedPreferences.getString(KEY_ID, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_CARD, null)
        );
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, MainActivity.class));
    }
}
