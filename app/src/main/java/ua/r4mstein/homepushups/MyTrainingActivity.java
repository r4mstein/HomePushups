package ua.r4mstein.homepushups;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTrainingActivity extends AppCompatActivity {

    private TextView mProgramName;
    private TextView mProgramDay;
    private TextView mProgramReps;
    private TextView mProgramTotalReps;
    private TextView mProgramTimer;
    private Button mTrainingRepsButton;
    private Button mPlusSecondsButton;
    private Button mSkipPauseButton;
    private CountDownTimer mTimer;
    private SharedPreferences mPreferences;

    private int mSetNumber = 0;
    private long mCurrentTime = 0;
    private int mReps[] = null;
    private int mDayOfProgram;
    private int mCountDaysOfProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trainig);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_training_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Do It Now");

        initVariable();

        mProgramTimer.setText(R.string.mt_timer);

        String program = getIntent().getStringExtra("title");
        mDayOfProgram = getIntent().getIntExtra("day", 0);

        currentProgram(program);
    }

    private void initVariable() {
        mProgramName = (TextView) findViewById(R.id.mt_program_name);
        mProgramDay = (TextView) findViewById(R.id.mt_day);
        mProgramReps = (TextView) findViewById(R.id.mt_reps);
        mProgramTotalReps = (TextView) findViewById(R.id.mt_total);
        mProgramTimer = (TextView) findViewById(R.id.mt_timer);
        mTrainingRepsButton = (Button) findViewById(R.id.mt_reps_button);
        mPlusSecondsButton = (Button) findViewById(R.id.mt_plus_seconds_button);
        mSkipPauseButton = (Button) findViewById(R.id.mt_skip_pause_button);
        mPreferences = getSharedPreferences(MyUtils.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void initLabels(String program, int day, int reps[]) {
        mProgramName.setText(program);

        mProgramDay.setText(getString(R.string.mt_day, (day + 1)));

        String label_reps = "";
        int total_reps = 0;
        for (int i : reps) {
            label_reps = label_reps + i + "  ";
            total_reps = total_reps + i;
        }
        mProgramReps.setText(label_reps);
        mProgramTotalReps.setText(getString(R.string.mt_total, total_reps));

        updateRepsButtonLabel();

        mPlusSecondsButton.setEnabled(false);
        mSkipPauseButton.setEnabled(false);
    }

    public void onClickRepsButton(View view) {
        if (mSetNumber < mReps.length - 1) {
            mSetNumber += 1;

            mPlusSecondsButton.setEnabled(true);
            mSkipPauseButton.setEnabled(true);
            mTrainingRepsButton.setEnabled(false);

            startTimer(60000 + 100, 1000);
        } else {
            if ((mDayOfProgram) < mCountDaysOfProgram - 1) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putInt(MyUtils.PROGRAM_DAY, (mDayOfProgram + 1));
                editor.apply();

                LinearLayout linearLayout = (LinearLayout)
                        getLayoutInflater().inflate(R.layout.custom_dialog, null);
                TextView dialogTitle = (TextView) linearLayout.findViewById(R.id.dialog_title);
                dialogTitle.setText(R.string.mt_dialog_training_complete);
                TextView dialogText = (TextView) linearLayout.findViewById(R.id.dialog_text);
                dialogText.setText(R.string.mt_dialog_next_training);
                Button buttonOk = (Button) linearLayout.findViewById(R.id.dialog_button);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setView(linearLayout)
                        .show();

            } else {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(MyUtils.PROGRAM_NAME, MyUtils.PROGRAM_END);
                editor.apply();

                LinearLayout linearLayout = (LinearLayout)
                        getLayoutInflater().inflate(R.layout.custom_dialog, null);
                TextView dialogTitle = (TextView) linearLayout.findViewById(R.id.dialog_title);
                dialogTitle.setText(R.string.mt_dialog_program_completed);
                TextView dialogText = (TextView) linearLayout.findViewById(R.id.dialog_text);
                dialogText.setText(R.string.mt_dialog_next_program);
                Button buttonOk = (Button) linearLayout.findViewById(R.id.dialog_button);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setView(linearLayout)
                        .show();
            }

        }

    }

    public void onClickSkip(View view) {
        mTimer.cancel();
        resetTimer();

        updateRepsButtonLabel();
    }

    public void onClickPlusSecondsButton(View view) {
        mTimer.cancel();

        startTimer(mCurrentTime + 30000, 1000);
    }

    private void startTimer(long time, long countDown) {
        mTimer = new CountDownTimer(time, countDown) {
            @Override
            public void onTick(long l) {
                mCurrentTime = l;
                updateTimer((int) l / 1000);
            }

            @Override
            public void onFinish() {
                resetTimer();
                MediaPlayer player = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                player.start();

                updateRepsButtonLabel();
            }
        }.start();
    }

    private void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;

        String stringMinutes = Integer.toString(minutes);
        String stringSeconds = Integer.toString(seconds);

        if (minutes <= 9) {
            stringMinutes = "0" + stringMinutes;
        }
        if (seconds <= 9) {
            stringSeconds = "0" + stringSeconds;
        }

        mProgramTimer.setText(stringMinutes + " : " + stringSeconds);
    }

    private void resetTimer() {
        mProgramTimer.setText(R.string.mt_timer);
        mPlusSecondsButton.setEnabled(false);
        mSkipPauseButton.setEnabled(false);
        mTrainingRepsButton.setEnabled(true);
    }

    private void updateRepsButtonLabel() {
        mTrainingRepsButton.setText(getString(R.string.mt_reps_button, (mSetNumber + 1),
                mReps[mSetNumber]));
    }

    private void currentProgram(String program){
        switch (program) {
            case "Program 1":
                mReps = MyUtils.program1.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program1.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 2":
                mReps = MyUtils.program2.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program2.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 3":
                mReps = MyUtils.program3.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program3.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 4":
                mReps = MyUtils.program4.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program4.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 5":
                mReps = MyUtils.program5.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program5.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 6":
                mReps = MyUtils.program6.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program6.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 7":
                mReps = MyUtils.program7.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program7.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 8":
                mReps = MyUtils.program8.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program8.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 9":
                mReps = MyUtils.program9.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program9.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 10":
                mReps = MyUtils.program10.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program10.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 11":
                mReps = MyUtils.program11.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program11.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
            case "Program 12":
                mReps = MyUtils.program12.get(mDayOfProgram);
                mCountDaysOfProgram = MyUtils.program12.size();
                initLabels(program, mDayOfProgram, mReps);
                break;
        }
    }

}
