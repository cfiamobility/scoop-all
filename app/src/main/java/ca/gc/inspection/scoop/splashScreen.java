package ca.gc.inspection.scoop;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class splashScreen extends AppCompatActivity {

    // go to the sign up page when the sign up button is tapped
    public void goToSignUp(View v) {
        startActivity(new Intent(v.getContext(), signUpScreen.class));
        finish();
    }

    // go to the login page when the login button is tapped
    public void goToLogIn(View v) {
        startActivity(new Intent(v.getContext(), loginScreen.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // set the system status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.medium_blue));

    }
}