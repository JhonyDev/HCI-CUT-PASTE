package com.app.hcilab;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etTextView;
    String strEtTextView;

    List<String> textHistory;
    int currentIndex = 0;
    boolean isEditing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        textHistory = new ArrayList<>();
        etTextView = findViewById(R.id.et_area);
        etTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                castString();
                if (isEditing) {
                    textHistory.add(strEtTextView);
                    currentIndex = textHistory.size() - 1;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void undo(View view) {
        isEditing = false;
        castString();
        if (strEtTextView.isEmpty()) {
            Toast.makeText(this, "No text in text area", Toast.LENGTH_SHORT).show();
            return;
        }
        currentIndex--;
        if (currentIndex <= 0)
            currentIndex = 0;

        etTextView.setText(textHistory.get(currentIndex));
        etTextView.setSelection(etTextView.getText().length());
        new Handler().postDelayed(() -> isEditing = true, 1000);
    }

    private void castString() {
        strEtTextView = etTextView.getText().toString();
    }

    public void redo(View view) {
        isEditing = false;
        currentIndex++;
        if (currentIndex >= textHistory.size() - 1)
            currentIndex = textHistory.size() - 1;

        etTextView.setText(textHistory.get(currentIndex));
        etTextView.setSelection(etTextView.getText().length());
        new Handler().postDelayed(() -> isEditing = true, 1000);
    }

    public void copy(View view) {
        castString();
        String selectedText = getHighlighted();
        copyText(selectedText);
    }

    private String getHighlighted() {
        int startSelection = etTextView.getSelectionStart();
        int endSelection = etTextView.getSelectionEnd();
        return etTextView.getText().toString().substring(startSelection, endSelection);
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }

    public void cut(View view) {
        castString();
        String selectedText = getHighlighted();
        copyText(selectedText);
        castString();
        Log.i("TAG", "cut: " + selectedText);
        Log.i("TAG", "cut: " + strEtTextView);
//
//        String[] list = strEtTextView.split(" ");
//        List<String> strings = new ArrayList<>(Arrays.asList(list));
//        strings.remove(selectedText);
//        StringBuilder newStr = new StringBuilder();
//        for (String str : strings)
//            newStr.append(str).append(" ");
        isEditing = false;

        strEtTextView = strEtTextView.replaceAll(selectedText, "");
        Log.i("TAG", "cut: " + strEtTextView);

        etTextView.setText(strEtTextView);
        etTextView.setSelection(etTextView.getText().length());
        new Handler().postDelayed(() -> isEditing = true, 1000);

    }

    public void paste(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        String data = item.getText().toString();
        castString();
        String newText = strEtTextView + data;
        etTextView.setText(newText);
        etTextView.setSelection(etTextView.getText().length());

    }
}