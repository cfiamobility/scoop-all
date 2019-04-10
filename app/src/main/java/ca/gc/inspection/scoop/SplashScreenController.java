package ca.gc.inspection.scoop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SplashScreenController {

    public static void goToMainScreen(Context context, Activity activity){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ca.gc.inspection.scoop", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("userId", "nothing").equals("nothing")){
            Intent intent = new Intent(context, mainScreen.class);
            context.startActivity(intent);
            activity.finish();
        }
    }
}