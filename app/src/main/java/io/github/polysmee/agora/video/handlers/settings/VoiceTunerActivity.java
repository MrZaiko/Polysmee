package io.github.polysmee.agora.video.handlers.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import io.github.polysmee.R;

public class VoiceTunerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_tuner);
        Spinner spinner = (Spinner) findViewById(R.id.voiceTunerSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.voices_tune_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune),0));
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(getResources().getString(R.string.preference_key_voice_tuner_current_voice_tune), position).apply();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //we do nothing
    }
}