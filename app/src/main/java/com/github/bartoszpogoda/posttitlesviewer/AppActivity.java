package com.github.bartoszpogoda.posttitlesviewer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.bartoszpogoda.posttitlesviewer.entity.Post;
import com.github.bartoszpogoda.posttitlesviewer.service.SampleApiService;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    private SampleApiService sampleApiService;

    private Button fetchPostTitlesBtn;

    private ListView postsListView;

    private EditText usernameTextBox;

    private List<String> arrayList;

    private ArrayAdapter<String> adapter;

    private TextView minTitleLengthValue;

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        sampleApiService = new SampleApiService();

        // init UI controls
        initList();
        initBtn();
        initUsernameTextBox();
        initLoadingSpinner();
        initSlider();
    }


    private void initList() {
        postsListView = findViewById(R.id.postsListView);

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(AppActivity.this, android.R.layout.simple_list_item_1, arrayList);
        postsListView.setAdapter(adapter);
    }

    private void initBtn() {
        fetchPostTitlesBtn = findViewById(R.id.fetchPostTitlesBtn);

        fetchPostTitlesBtn.setEnabled(false);
        fetchPostTitlesBtn.setOnClickListener(this::handleFetchPostTitlesBtnClick);
    }

    @SuppressLint("CheckResult")
    private void handleFetchPostTitlesBtnClick(View click) {
        hideKeyboard();
        arrayList.clear();
        adapter.notifyDataSetChanged();
        spinner.setVisibility(View.VISIBLE);

        final int postTitleMinLength = Integer.valueOf(minTitleLengthValue.getText().toString());
        final String username = usernameTextBox.getText().toString();

        sampleApiService.getUserByUsername(username)
                .flatMapObservable(sampleApiService::getPostsOfUser)
                .filter(post -> post.getTitle().length() >= postTitleMinLength)
                .map(Post::getTitle)
                .toList()
                .subscribe(postTitles -> {
                    spinner.setVisibility(View.GONE);
                    arrayList.addAll(postTitles);
                    adapter.notifyDataSetChanged();
                }, error -> {
                    spinner.setVisibility(View.GONE);
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setTitle("User not found")
                            .setMessage(username + " doesn't stand for valid user.")
                            .show();
                });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void initLoadingSpinner() {

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
    }

    private void initUsernameTextBox() {
        usernameTextBox = findViewById(R.id.usernameTextBox);
        usernameTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !editable.toString().isEmpty()) {
                    fetchPostTitlesBtn.setEnabled(true);
                } else {
                    fetchPostTitlesBtn.setEnabled(false);
                }
            }
        });
    }


    private void initSlider() {
        SeekBar minTitleLengthSeekBar = findViewById(R.id.titleMinLengthBar);
        minTitleLengthValue = findViewById(R.id.minLengthBarValue);

        minTitleLengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                minTitleLengthValue.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });
    }

}
