package ua.r4mstein.homepushups;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProgramsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ProgramsAdapter mProgramsAdapter;
    private SharedPreferences mPreferences;

    private String[] titles = new String[12];
    private void titlesUpdate() {
        for (int i = 0; i < 12; i++) {
            titles[i] = "Program " + (i + 1);
        }
    }

    private String[] subtitles = new String[] {"Less than 5 pushups?", "6 - 10 pushups?",
            "11 - 20 pushups?", "21 - 25 pushups?", "26 - 30 pushups?", "31 - 35 pushups?",
            "36 - 40 pushups?", "41 - 45 pushups?", "46 - 50 pushups?", "51 - 55 pushups?",
            "56 - 60 pushups?", "More than 60 pushups?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programs_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.programs_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List of Programs");

        mRecyclerView = (RecyclerView) findViewById(R.id.programs_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();

        mPreferences = getSharedPreferences(MyUtils.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private void updateUI() {
        titlesUpdate();
        List<ProgramsModel> modelList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            modelList.add(new ProgramsModel(titles[i], subtitles[i]));
        }

        mProgramsAdapter = new ProgramsAdapter(modelList);
        mRecyclerView.setAdapter(mProgramsAdapter);
    }

    private class ProgramsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgramsModel mProgramsModel;
        private TextView mTitleTextView;
        private TextView mSubtitleTextView;

        public ProgramsHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.item_title);
            mSubtitleTextView = (TextView) itemView.findViewById(R.id.item_subtitle);
        }

        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString(MyUtils.PROGRAM_NAME, mProgramsModel.getTitle());
            editor.putInt(MyUtils.PROGRAM_DAY, 0);
            editor.apply();

            Intent intent = new Intent(ProgramsListActivity.this, MyTrainingActivity.class);
            intent.putExtra("title", mProgramsModel.getTitle());
            intent.putExtra("day", 0);
            startActivity(intent);
        }

        public void bindModel(ProgramsModel programsModel) {
            mProgramsModel = programsModel;
            mTitleTextView.setText(mProgramsModel.getTitle());
            mSubtitleTextView.setText(mProgramsModel.getSubtitle());
        }
    }

    private class ProgramsAdapter extends RecyclerView.Adapter<ProgramsHolder> {

        private List<ProgramsModel> mProgramsModelList;

        public ProgramsAdapter(List<ProgramsModel> programsModelList) {
            mProgramsModelList = programsModelList;
        }

        @Override
        public ProgramsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater.inflate(R.layout.programs_item, parent, false);
            return new ProgramsHolder(view);
        }

        @Override
        public void onBindViewHolder(ProgramsHolder holder, int position) {
            ProgramsModel model = mProgramsModelList.get(position);
            holder.bindModel(model);
        }

        @Override
        public int getItemCount() {
            return mProgramsModelList.size();
        }
    }

}
