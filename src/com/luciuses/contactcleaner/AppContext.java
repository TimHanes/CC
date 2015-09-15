package com.luciuses.contactcleaner;

import android.app.*;
import android.content.*;

public class AppContext extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        AppContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return AppContext.context;
    }
}