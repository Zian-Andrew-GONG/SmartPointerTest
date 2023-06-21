package com.example.smartpointertest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.smartpointertest.databinding.ActivityMainBinding;

import java.util.Random;

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

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (sb.toString().getBytes().length < 102400) {
            char c = (char) (random.nextInt(26) + 'a');
            sb.append(c);
        }
        String largeString = sb.toString();

        new Thread(()->{
            while (true) {
                UserOption userOption = new UserOption(largeString, largeString);
                User user = User.create(userOption);
                Log.i("SmartPointerTest", "user id: " + user.getId());
                user.destroy();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    /**
     * A native method that is implemented by the 'smartpointertest' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}