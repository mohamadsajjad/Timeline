package com.example.timeline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import mohamdsajjad.timelinelibrary.StructTitleTimeLine;
import mohamdsajjad.timelinelibrary.TimeLine;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        TimeLine timeLine = findViewById(R.id.timeline_view);

        ArrayList<StructTitleTimeLine> arrayList = new ArrayList<>();
        for (int i=0;i<15;i++){
            StructTitleTimeLine item = new StructTitleTimeLine();
            item.setTitle0(String.valueOf(i));
            item.setTitle1("Title "+i);
            item.setDesc0("desc 0");
            item.setDesc1("desc 1");
            arrayList.add(item);
        }

        timeLine.setListener(new TimeLine.TimeLineListener() {
            @Override
            public void onItemListener(int pos) {
                Toast.makeText(MainActivity.this,"item "+pos+" clicked",Toast.LENGTH_SHORT).show();
            }
        });

        timeLine.start(arrayList);

    }
}