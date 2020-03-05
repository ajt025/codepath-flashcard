package com.example.flashcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // RQ Codes
    public static final int ADD_CARD_REQ_CODE = 100;
    public static final int EDIT_CARD_REQ_CODE = 101;

    // Database stuff
    FlashcardDatabase flashcardDatabase;

    // views
    private RelativeLayout rlMain;

    private TextView tvQuestion;
    private TextView tvAnswer;
    private TextView[] choices = new TextView[3];
    private TextView tvEmpty;
    private ImageButton btnAdd;
    private ImageButton btnHide;
    private ImageButton btnEdit;
    private ImageView ivNext;
    private ImageView ivDelete;

    // other fields
    private boolean isShowingAnswers = true;
    private int correctIndex = 0;

    private List<Flashcard> allFlashcards;
    private int currentFlashcard = 0;
    private Flashcard cardToEdit;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database init
        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();
        random = new Random();

        // Bind XML views to references
        rlMain = findViewById(R.id.rlMain);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvAnswer = findViewById(R.id.tvAnswer);
        choices[0] = findViewById(R.id.tvChoice1);
        choices[1] = findViewById(R.id.tvChoice2);
        choices[2] = findViewById(R.id.tvChoice3);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAdd = findViewById(R.id.btnAdd);
        btnHide = findViewById(R.id.btnHide);
        btnEdit = findViewById(R.id.btnEdit);
        ivNext = findViewById(R.id.ivNext);
        ivDelete = findViewById(R.id.ivDelete);

        if (allFlashcards != null && allFlashcards.size() > 0) {
            ((TextView) findViewById(R.id.tvQuestion)).setText(allFlashcards.get(currentFlashcard).getQuestion());
            ((TextView) findViewById(R.id.tvAnswer)).setText(allFlashcards.get(currentFlashcard).getAnswer());

            setupChoices();
        } else {
            tvEmpty.setVisibility(View.VISIBLE);

            tvQuestion.setVisibility(View.INVISIBLE);
            tvAnswer.setVisibility(View.INVISIBLE);

            for (int i = 0; i < choices.length; ++i) {
                choices[i].setVisibility(View.INVISIBLE);
            }
        }

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
                startActivityForResult(i, ADD_CARD_REQ_CODE);
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
                if (allFlashcards.size() > 0)
                    cardToEdit = allFlashcards.get(currentFlashcard);

                Intent i = new Intent(MainActivity.this, AddCardActivity.class);
                i.putExtra("question", tvQuestion.getText().toString());
                i.putExtra("answer1", choices[0].getText().toString());
                i.putExtra("answer2", choices[1].getText().toString());
                i.putExtra("answer3", choices[2].getText().toString());
                i.putExtra("correctIndex", correctIndex);
                startActivityForResult(i, EDIT_CARD_REQ_CODE);
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allFlashcards = flashcardDatabase.getAllCards();

                if (allFlashcards.size() == 0)
                    return;

                currentFlashcard = random.nextInt(allFlashcards.size());

                if (allFlashcards != null && allFlashcards.size() > 0) {
                    ((TextView) findViewById(R.id.tvQuestion)).setText(allFlashcards.get(currentFlashcard).getQuestion());
                    ((TextView) findViewById(R.id.tvAnswer)).setText(allFlashcards.get(currentFlashcard).getAnswer());

                    setupChoices();
                }

                // Swap visibility upon clicking
                if (tvAnswer.getVisibility() == View.VISIBLE) {
                    // Flip card
                    tvQuestion.setVisibility(View.VISIBLE);
                    tvAnswer.setVisibility(View.INVISIBLE);
                }

                // Clear choices upon clicking next
                for (int i = 0; i < choices.length; ++i) {
                    choices[i].setBackgroundColor(getResources().getColor(R.color.choice));
                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardDatabase.deleteCard(tvQuestion.getText().toString());
                allFlashcards = flashcardDatabase.getAllCards(); // update

                if (currentFlashcard > 0)
                    currentFlashcard--;

                if (allFlashcards != null && allFlashcards.size() > 0) {
                    ((TextView) findViewById(R.id.tvQuestion)).setText(allFlashcards.get(currentFlashcard).getQuestion());
                    ((TextView) findViewById(R.id.tvAnswer)).setText(allFlashcards.get(currentFlashcard).getAnswer());

                    setupChoices();
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);

                    tvQuestion.setVisibility(View.INVISIBLE);
                    tvQuestion.setText("");

                    tvAnswer.setVisibility(View.INVISIBLE);
                    tvAnswer.setText("");

                    for (int i = 0; i < choices.length; ++i) {
                        choices[i].setVisibility(View.INVISIBLE);
                        choices[i].setText("");
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { // AddCardActivity
            if (data != null) {
                String question = data.getStringExtra("question");
                String[] answers = new String[3];

                tvQuestion.setText(question);

                // populate answers array (for later flashcard ctor)
                for (int i = 0; i < choices.length; ++i) {
                    answers[i] = data.getStringExtra("answer" + (i + 1)); // for offset
                    choices[i].setText(answers[i]); // for offset
                }

                correctIndex = data.getIntExtra("correctIndex", 0);
                tvAnswer.setText(choices[correctIndex].getText());

                Snackbar.make(findViewById(R.id.tvQuestion),
                        "Successfully saved",
                        Snackbar.LENGTH_SHORT)
                        .show();

                // Separate correct answer from the rest
                HashSet<String> allChoices = new HashSet<>(Arrays.asList(answers));
                allChoices.remove(answers[correctIndex]);
                Iterator<String> iter = allChoices.iterator();

                // Save to database

                if (requestCode == ADD_CARD_REQ_CODE || cardToEdit == null) {
                    flashcardDatabase.insertCard(new Flashcard(question, answers[correctIndex], iter.next(), iter.next()));
                    cardToEdit = null;
                } else if (requestCode == EDIT_CARD_REQ_CODE) {
                    cardToEdit.setQuestion(question);
                    cardToEdit.setAnswer(answers[correctIndex]);
                    cardToEdit.setWrongAnswer1(iter.next());
                    cardToEdit.setWrongAnswer2(iter.next());

                    flashcardDatabase.updateCard(cardToEdit);
                    cardToEdit = null;
                }

                allFlashcards = flashcardDatabase.getAllCards();
            }
        }

        if (allFlashcards.size() > 0) {
            tvEmpty.setVisibility(View.GONE);

            tvQuestion.setVisibility(View.VISIBLE);
            tvAnswer.setVisibility(View.INVISIBLE);

            for (int i = 0; i < choices.length; ++i) {
                choices[i].setVisibility(View.VISIBLE);
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

    private void setupChoices() {
        String correctAnswer;
        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.add(correctAnswer = allFlashcards.get(currentFlashcard).getAnswer());
        allAnswers.add(allFlashcards.get(currentFlashcard).getWrongAnswer1());
        allAnswers.add(allFlashcards.get(currentFlashcard).getWrongAnswer2());

        // randomly assign answers to choices
        Collections.shuffle(allAnswers);

        for (int i = 0; i < allAnswers.size(); ++i) {
            choices[i].setText(allAnswers.get(i));

            if (allAnswers.get(i).equals(correctAnswer)) {
                correctIndex = i;
            }
        }
    }

}
