package com.morsecodeencryptor.app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Decryption extends AppCompatActivity implements View.OnClickListener {
    EditText input_for_decryption;

    TextView result_tv;

    TextView dot_tv,dash_tv;
    String dot_symbol = ".";
    String dash_symbol = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decryption_app_layout);


        input_for_decryption = findViewById(R.id.edit_text_for_input_for_decryption);
        result_tv = findViewById(R.id.tv_output_results_of_decryption);
        dot_tv = findViewById(R.id.input_for_dot_symbol_decryption);
        dash_tv = findViewById(R.id.input_for_dash_symbol_decryption);

        Button Encrypt = findViewById(R.id.btn_decrypt);
        Button set_default = findViewById(R.id.btn_set_defaults_symbols);
        Button save_symbols = findViewById(R.id.btn_save_symbols);
        Button save_to_clipboard = findViewById(R.id.btn_save_to_clipboard);
        Button move_to_encryption = findViewById(R.id.btn_go_to_encryption);

        assignOnClickFunction(Encrypt);
        assignOnClickFunction(set_default);
        assignOnClickFunction(save_symbols);
        assignOnClickFunction(save_to_clipboard);
        assignOnClickFunction(move_to_encryption);


        String dashSymbolFromIntent = getIntent().getStringExtra("dashSymbol");
        String dotSymbolFromIntent = getIntent().getStringExtra("dotSymbol");

        if (dashSymbolFromIntent != null && dotSymbolFromIntent != null){
            dot_tv.setText(dotSymbolFromIntent);
            dash_tv.setText(dashSymbolFromIntent);
            setDash_symbol(dashSymbolFromIntent);
            setDot_symbol(dotSymbolFromIntent);
            set_saved_symbols(dotSymbolFromIntent,dashSymbolFromIntent);
        }

    }

    void assignOnClickFunction(Button btn) {
        btn.setOnClickListener(this);
    }
    private char value_of_string_after_decryption(String c){
        switch (c) {
            // Alphabets
            case ".-": return 'a';
            case "-...": return 'b';
            case "-.-.": return 'c';
            case "-..": return 'd';
            case "." : return 'e';
            case "..-.": return 'f';
            case "--.": return 'g';
            case "....": return 'h';
            case "..": return 'i';
            case ".---": return 'j';
            case "-.-": return 'k';
            case ".-..": return 'l';
            case "--": return 'm';
            case "-.": return 'n';
            case "---": return 'o';
            case ".--.": return 'p';
            case "--.-": return 'q';
            case ".-.": return 'r';
            case "...": return 's';
            case "-": return 't';
            case "..-": return 'u';
            case "...-": return 'v';
            case ".--": return 'w';
            case "-..-": return 'x';
            case "-.--": return 'y';
            case "--..": return 'z';

            // Digits
            case "-----": return '0';
            case ".----": return '1';
            case "..---": return '2';
            case "...--": return '3';
            case "....-": return '4';
            case ".....": return '5';
            case "-....": return '6';
            case "--...": return '7';
            case "---..": return '8';
            case "----.": return '9';

            // Special characters

            case ".-.-.-": return '.';
            case "--..--": return ',';
            case "..--..": return '?';
            case ".----.": return '\'';
            case "-.-.--": return '!';
            case "-..-.":  return '/';
            case "-.--.":  return '(';
            case "-.--.-": return ')';
            case ".-...":  return '&';
            case "---...": return ':';
            case "-.-.-.": return ';';
            case "-...-":  return '=';
            case ".-.-.":  return '+';
            case "-....-": return '-';
            case "..--.-": return '_';
            case ".-..-.": return '"';
            case "...-..-": return '$';
            case ".--.-.":  return '@';
            case "/":       return ' ';   // slash represents a space

            default: return '❓'; // unknown character
        }
    }
    private void decryption(String expression){
        String dot_value = getDot_symbol();
        String dash_value = getDash_symbol();
        if (!dash_value.equals("-") || !dot_value.equals(".")){
            expression = expression.replace(dot_value,".");
            expression = expression.replace(dash_value,"-");
        }
        StringBuilder result = new StringBuilder();
        int j = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == ' '){
                result.append(value_of_string_after_decryption(expression.substring(j,i)));
                j = i+1;
            }
        }
        if (j < expression.length()){
            result.append(value_of_string_after_decryption(expression.substring(j)));
        }
        result_tv.setText(result.toString());

    }
    public String getDot_symbol() {
        return dot_symbol;
    }
    public String getDash_symbol(){
        return dash_symbol;
    }
    public void setDash_symbol(String dash_symbol) {
        this.dash_symbol = dash_symbol;
    }
    public void setDot_symbol(String dot_symbol) {
        this.dot_symbol = dot_symbol;
    }
    private void default_symbols(){
        setDash_symbol("-");
        setDot_symbol(".");
        dash_tv.setText("-");
        dot_tv.setText(".");
    }
    private void set_saved_symbols(String dot, String dash){
        if (dot.equals(dash)){
            Toast.makeText(this, "Both of the characters can't be same.",Toast.LENGTH_LONG).show();
        } else {
            setDot_symbol(dot);
            setDash_symbol(dash);
        }
    }
    private void save_to_clipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text",result_tv.getText().toString());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_set_defaults_symbols) {
            default_symbols();
        } else if (v.getId() == R.id.btn_save_to_clipboard) {
            save_to_clipboard();
        }
        else if (v.getId() == R.id.btn_go_to_encryption){
            Intent intent = new Intent(Decryption.this, MainActivity.class);
            startActivity(intent);
        }
        else {
            if(dash_tv.getText().toString().equals(dot_tv.getText().toString()) && (dot_symbol.equals(".") && dash_symbol.equals("-")) && (!dash_tv.getText().toString().isEmpty() && !dot_tv.getText().toString().isEmpty())){
                Toast.makeText(this, "Both of the characters can't be same.",Toast.LENGTH_LONG).show();
            } else {
                if (v.getId() == R.id.btn_decrypt) {
                    decryption(input_for_decryption.getText().toString().trim());
                } else if (v.getId() == R.id.btn_save_symbols) {
                    set_saved_symbols(dot_tv.getText().toString(), dash_tv.getText().toString());
                }
            }
        }
    }
}
