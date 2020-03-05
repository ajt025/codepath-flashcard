package com.example.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AddCardActivity extends AppCompatActivity {

    EditText etQuestion;
    EditText etAnswer1;
    EditText etAnswer2;
    EditText etAnswer3;
    ImageButton btnCancel;
    ImageButton btnSave;
    RadioGroup rgAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        // Connect views to variables
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer1 = findViewById(R.id.etAnswer1);
        etAnswer2 = findViewById(R.id.etAnswer2);
        etAnswer3 = findViewById(R.id.etAnswer3);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        rgAnswers = findViewById(R.id.rgAnswers);

        // Parse for edit case -- populate fields with current values
        Intent editData = getIntent();
        if (editData != null) {
            etQuestion.setText(editData.getStringExtra("question"));
            etAnswer1.setText(editData.getStringExtra("answer1"));
            etAnswer2.setText(editData.getStringExtra("answer2"));
            etAnswer3.setText(editData.getStringExtra("answer3"));

            int correctIndex = editData.getIntExtra("correctIndex", 0);
            ((RadioButton) rgAnswers.getChildAt(correctIndex)).setChecked(true);
        }

        // Return to card screen
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Save changes and send new data back to main card screen
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = etQuestion.getText().toString();
                String answer1 = etAnswer1.getText().toString();
                String answer2 = etAnswer2.getText().toString();
                String answer3 = etAnswer3.getText().toString();
                RadioButton correctAns = findViewById(rgAnswers.getCheckedRadioButtonId());
                int correctIndex = Integer.parseInt(correctAns.getText().toString());

                // No empty strings allowed
                if (!question.isEmpty() && !answer1.isEmpty() && !answer2.isEmpty() && !answer3.isEmpty()) {
                    Intent data = new Intent();
                    data.putExtra("question", question);
                    data.putExtra("answer1", answer1);
                    data.putExtra("answer2", answer2);
                    data.putExtra("answer3", answer3);
                    data.putExtra("correctIndex", correctIndex - 1); // standard: only pass 0-based values
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(AddCardActivity.this,
                            "Please fill the text fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
