package com.antaresnav.navigation.demo;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.antaresnav.navigation.demo.StyleUrlListAdapter.OnItemClickListener;

import java.util.List;

public class StyleActivity extends AppCompatActivity {

    public static final String EXTRA_STYLE_URL = "com.antaresnav.maps.preview.STYLE_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);

        final StyleUrlViewModel styleUrlViewModel = ViewModelProviders.of(this).get(StyleUrlViewModel.class);
        final StyleUrlListAdapter adapter = new StyleUrlListAdapter(this, styleUrlViewModel);
        styleUrlViewModel.getAll().observe(this, new Observer<List<StyleUrl>>() {
            @Override
            public void onChanged(@Nullable final List<StyleUrl> styleUrls) {
                adapter.setStyleUrls(styleUrls);
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, StyleUrl styleUrl) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_STYLE_URL, styleUrl);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        FloatingActionButton addFab = findViewById(R.id.addFab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dialogView = LayoutInflater.from(StyleActivity.this).inflate(R.layout.dialog_add_style, null);
                final EditText styleNameET = dialogView.findViewById(R.id.styleNameET);
                final EditText styleUrlET = dialogView.findViewById(R.id.styleUrlET);
                final AlertDialog addDialog = new Builder(StyleActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setView(dialogView)
                        .setPositiveButton(R.string.dialog_save, null)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .show();
                addDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String styleName = String.valueOf(styleNameET.getText());
                        String styleUrl = String.valueOf(styleUrlET.getText());
                        boolean valid = true;
                        if (TextUtils.isEmpty(styleName)) {
                            styleNameET.setError(getString(R.string.style_name_required_error));
                            valid = false;
                        }
                        if (TextUtils.isEmpty(styleUrl)) {
                            styleUrlET.setError(getString(R.string.style_url_required_error));
                            valid = false;
                        } else if (!Patterns.WEB_URL.matcher(styleUrl).matches()) {
                            styleUrlET.setError(getString(R.string.style_url_invalid_error));
                            valid = false;
                        }
                        if (valid) {
                            styleUrlViewModel.insert(new StyleUrl(styleName, styleUrl));
                            addDialog.dismiss();
                        }
                    }
                });

            }
        });
    }

}
