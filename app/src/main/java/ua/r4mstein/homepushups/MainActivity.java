package ua.r4mstein.homepushups;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private Button mDoItButton;
    private SharedPreferences mPreferences;

    private String mProgramName;
    private int mProgramDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);

        mInfoTextView = (TextView) findViewById(R.id.info_text_view);
        mDoItButton = (Button) findViewById(R.id.do_it_button);

        mPreferences = getSharedPreferences(MyUtils.APP_PREFERENCES, Context.MODE_PRIVATE);
        initInfo();
    }

    public void selectProgram(View view) {
        Intent intent = new Intent(MainActivity.this, ProgramsListActivity.class);
        startActivity(intent);
    }

    public void onClickDoYourProgram(View view) {
            Intent intent = new Intent(MainActivity.this, MyTrainingActivity.class);
            intent.putExtra("title", mProgramName);
            intent.putExtra("day", mProgramDay);
            startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initInfo();
    }

    private void initInfo() {
        if (mPreferences.contains(MyUtils.PROGRAM_NAME) && mPreferences.contains(MyUtils.PROGRAM_DAY)) {
            mProgramName = mPreferences.getString(MyUtils.PROGRAM_NAME, null);
            mProgramDay = mPreferences.getInt(MyUtils.PROGRAM_DAY, 0);
            if (mProgramName.equals(MyUtils.PROGRAM_END)) {
                mInfoTextView.setText(R.string.main_activity_label);
                mDoItButton.setEnabled(false);
            } else {
                mInfoTextView.setText(getString(R.string.main_activity_label_with_program,
                        mProgramName, (mProgramDay + 1)));
                mDoItButton.setEnabled(true);
            }
        } else {
            mInfoTextView.setText(R.string.main_activity_label);
            mDoItButton.setEnabled(false);
        }
    }
}
