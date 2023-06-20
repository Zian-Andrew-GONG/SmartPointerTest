package com.example.smartpointertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.smartpointertest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'smartpointertest' library on application startup.
    static {
        System.loadLibrary("smartpointertest");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        UserOption userOption = new UserOption("id", "name");
        User user = User.create(userOption);
        Log.i("SmartPointerTest", "user id: " + user.getId());
        user.destroy();
    }

    /**
     * A native method that is implemented by the 'smartpointertest' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}