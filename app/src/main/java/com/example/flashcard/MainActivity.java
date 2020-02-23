package com.example.flashcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // views
    private RelativeLayout rlMain;

    private TextView tvQuestion;
    private TextView tvAnswer;
    private TextView[] choices = new TextView[3];
    private ImageButton btnAdd;
    private ImageButton btnHide;
    private ImageButton btnEdit;

    // other fields
    private boolean isShowingAnswers = true;
    private int correctIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind XML views to references
        rlMain = findViewById(R.id.rlMain);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvAnswer = findViewById(R.id.tvAnswer);
        choices[0] = findViewById(R.id.tvChoice1);
        choices[1] = findViewById(R.id.tvChoice2);
        choices[2] = findViewById(R.id.tvChoice3);
        btnAdd = findViewById(R.id.btnAdd);
        btnHide = findViewById(R.id.btnHide);
        btnEdit = findViewById(R.id.btnEdit);

        // LISTENER SETUP
        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear choices upon clicking outside choices
                for (int i = 0; i < choices.length; ++i) {
                    choices[i].setBackgroundColor(getResources().getColor(R.color.choice));
                }
            }
        });

        // toggle visibility of question and answer cards
        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Flip card
                tvQuestion.setVisibility(View.INVISIBLE);
                tvAnswer.setVisibility(View.VISIBLE);
            }
        });

        tvAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Flip card
                tvQuestion.setVisibility(View.VISIBLE);
                tvAnswer.setVisibility(View.INVISIBLE);
            }
        });

        // Create reference to answer checker for multiple application
        View.OnClickListener correctCheck = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((TextView) view).getText().equals(tvAnswer.getText()))
                    view.setBackgroundColor(getResources().getColor(R.color.correctAnswer));
                else
                    view.setBackgroundColor(getResources().getColor(R.color.incorrectAnswer));

                // hardcode for example
                choices[correctIndex].setBackgroundColor(getResources().getColor(R.color.correctAnswer));
            }
        };
        // Assign checker to each TextView choices
        for (int i = 0; i < choices.length; ++i) {
            choices[i].setOnClickListener(correctCheck);
        }

        // Send to AddCard screen with no prior data
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddCardActivity.class);
                startActivityForResult(i, 100);
            }
        });

        // Hide and show answers, updates the icon's drawable
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Swap visibility upon clicking
                if (isShowingAnswers)
                    hideAnswers();
                else
                    showAnswers();
            }
        });

        // Pass current data to AddCard activity for autopopulation
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddCardActivity.class);
                i.putExtra("question", tvQuestion.getText().toString());
                i.putExtra("answer1", choices[0].getText().toString());
                i.putExtra("answer2", choices[1].getText().toString());
                i.putExtra("answer3", choices[2].getText().toString());
                i.putExtra("correctIndex", correctIndex);
                startActivityForResult(i, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 100) { // AddCardActivity
            if (data != null) {
                tvQuestion.setText(data.getStringExtra("question"));

                for (int i = 0; i < choices.length; ++i) {
                    choices[i].setText(data.getStringExtra("answer" + (i + 1))); // for offset
                }

                correctIndex = data.getIntExtra("correctIndex", 0);
                tvAnswer.setText(choices[correctIndex].getText());

                Snackbar.make(findViewById(R.id.tvQuestion),
                        "Successfully saved",
                        Snackbar.LENGTH_SHORT)
                .show();
            }
        }
    }

    // helper methods
    private void hideAnswers() {
        for (int i = 0; i < choices.length; ++i) {
            choices[i].setVisibility(View.INVISIBLE);
        }

        // change btn icon
        btnHide.setBackgroundResource(R.drawable.ic_eye_off);

        isShowingAnswers = false;
    }

    private void showAnswers() {
        for (int i = 0; i < choices.length; ++i) {
            choices[i].setVisibility(View.VISIBLE);
        }

        // change btn icon
        btnHide.setBackgroundResource(R.drawable.ic_eye);

        isShowingAnswers = true;
    }
}
