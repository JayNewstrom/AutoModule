package com.jaynewstrom.autoModuleSample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.jaynewstrom.autoModule.AutoModule;

import javax.inject.Inject;

import dagger.ObjectGraph;

@AutoModule(addsTo = ApplicationModule.class)
public final class MainActivity extends AppCompatActivity {

    @Inject String test;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectGraph rootGraph = ObjectGraph.create(new ApplicationModule());
        ObjectGraph childGraph = rootGraph.plus(getDaggerModule());
        childGraph.inject(this);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.text_view)).setText(test);
    }

    protected Object getDaggerModule() {
        return new MainActivityModule();
    }
}
