package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // views
    private RelativeLayout rlMain;

    private TextView tvQuestion;
    private TextView tvAnswer;

    private TextView[] choices = new TextView[3];
    private ImageButton btnHide;

    // other fields
    private boolean isShowingAnswers = true;

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
        btnHide = findViewById(R.id.btnHide);

        // Setup listeners
        rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear choices upon clicking outside choices
                for (int i = 0; i < choices.length; ++i) {
                    choices[i].setBackgroundColor(getResources().getColor(R.color.choice));
                }
            }
        });

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
                choices[1].setBackgroundColor(getResources().getColor(R.color.correctAnswer));
            }
        };

        // Assign checker to each TextView choices
        for (int i = 0; i < choices.length; ++i) {
            choices[i].setOnClickListener(correctCheck);
        }

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
