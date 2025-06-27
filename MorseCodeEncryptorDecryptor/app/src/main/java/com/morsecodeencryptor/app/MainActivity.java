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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText input_for_encryption;

    TextView result_tv;

    TextView dot_tv,dash_tv;
    String dot_symbol = ".";
    String dash_symbol = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input_for_encryption = findViewById(R.id.edit_text_for_input_for_encryption);
        result_tv = findViewById(R.id.tv_output_results_of_encryption);
        dot_tv = findViewById(R.id.input_for_dot_symbol_encryption);
        dash_tv = findViewById(R.id.input_for_dash_symbol_encryption);

        Button Encrypt = findViewById(R.id.btn_encrypt);
        Button set_default = findViewById(R.id.btn_set_defaults_symbols);
        Button save_symbols = findViewById(R.id.btn_save_symbols);
        Button save_to_clipboard = findViewById(R.id.btn_save_to_clipboard);
        Button go_to_decryption_menu = findViewById(R.id.btn_go_to_decryption);
        assignOnClickFunction(Encrypt);
        assignOnClickFunction(set_default);
        assignOnClickFunction(save_symbols);
        assignOnClickFunction(save_to_clipboard);
        assignOnClickFunction(go_to_decryption_menu);
    }

    void assignOnClickFunction(Button btn) {
        btn.setOnClickListener(this);
    }

    private String value_of_character_after_encryption(char c){
            switch (c) {
                // Alphabets
                case 'a': return ".-";
                case 'b': return "-...";
                case 'c': return "-.-.";
                case 'd': return "-..";
                case 'e': return ".";
                case 'f': return "..-.";
                case 'g': return "--.";
                case 'h': return "....";
                case 'i': return "..";
                case 'j': return ".---";
                case 'k': return "-.-";
                case 'l': return ".-..";
                case 'm': return "--";
                case 'n': return "-.";
                case 'o': return "---";
                case 'p': return ".--.";
                case 'q': return "--.-";
                case 'r': return ".-.";
                case 's': return "...";
                case 't': return "-";
                case 'u': return "..-";
                case 'v': return "...-";
                case 'w': return ".--";
                case 'x': return "-..-";
                case 'y': return "-.--";
                case 'z': return "--..";

                // Digits
                case '0': return "-----";
                case '1': return ".----";
                case '2': return "..---";
                case '3': return "...--";
                case '4': return "....-";
                case '5': return ".....";
                case '6': return "-....";
                case '7': return "--...";
                case '8': return "---..";
                case '9': return "----.";

                // Special characters
                case '.': return ".-.-.-";
                case ',': return "--..--";
                case '?': return "..--..";
                case '\'': return ".----.";
                case '!': return "-.-.--";
                case '/': return "-..-.";
                case '(': return "-.--.";
                case ')': return "-.--.-";
                case '&': return ".-...";
                case ':': return "---...";
                case ';': return "-.-.-.";
                case '=': return "-...-";
                case '+': return ".-.-.";
                case '-': return "-....-";
                case '_': return "..--.-";
                case '"': return ".-..-.";
                case '$': return "...-..-";
                case '@': return ".--.-.";
                case ' ': return "/";  // space represented as slash in Morse

                default: return "?"; // unknown character
            }
        }
    private void encryption(String expression){
        expression = expression.toLowerCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            result.append(value_of_character_after_encryption(expression.charAt(i))).append(" ");
            }
        String dot_value = getDot_symbol();
        String dash_value = getDash_symbol();
        if(dash_value.equals("-") && dot_value.equals(".")){
            result_tv.setText(result.toString().trim());
        }else{
            String main_result = result.toString();
            main_result = main_result.replace(".",dot_value);
            main_result = main_result.replace("-",dash_value);
            result_tv.setText(main_result);
        }
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
        Toast.makeText(this, "Text Copied",Toast.LENGTH_LONG).show();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text",result_tv.getText().toString());
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_set_defaults_symbols){
            default_symbols();
        }
        else if (v.getId() == R.id.btn_save_to_clipboard){
            save_to_clipboard();
        }
        else if (v.getId() == R.id.btn_go_to_decryption){
            try {
            Intent intent = new Intent(MainActivity.this, Decryption.class);
            startActivity(intent);
            } catch (Exception e){
                Toast.makeText(this, "error in loading other files",Toast.LENGTH_LONG).show();
            }
        }
        else {
            if(dash_tv.getText().toString().equals(dot_tv.getText().toString()) && (dot_symbol.equals(".") && dash_symbol.equals("-")) && (!dash_tv.getText().toString().isEmpty() && !dot_tv.getText().toString().isEmpty())){
                Toast.makeText(this, "Both of the characters can't be same.",Toast.LENGTH_LONG).show();
            } else {
                if (v.getId() == R.id.btn_encrypt) {
                    encryption(input_for_encryption.getText().toString());
                } else if (v.getId() == R.id.btn_save_symbols){
                    set_saved_symbols(dot_tv.getText().toString(),dash_tv.getText().toString());
                }
            }
        }
    }
}




