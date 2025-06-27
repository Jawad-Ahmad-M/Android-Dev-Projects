package com.calculator.app;

import android.os.Bundle;
import android.view.View;
//import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;
//import android.widget.Toast;

//import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;

//import com.google.android.material.button.MaterialButton;
//import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView result_tv, solution_tv;
    Double first_no;

    String operator = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator_contraint_layout);
        Button button0 = findViewById(R.id.btn0);
        Button button1 = findViewById(R.id.btn1);
        Button button2 = findViewById(R.id.btn2);
        Button button3 = findViewById(R.id.btn3);
        Button button4 = findViewById(R.id.btn4);
        Button button5 = findViewById(R.id.btn5);
        Button button6 = findViewById(R.id.btn6);
        Button button7 = findViewById(R.id.btn7);
        Button button8 = findViewById(R.id.btn8);
        Button button9 = findViewById(R.id.btn9);
        Button buttonMinus = findViewById(R.id.btnMinus);
        Button buttonPlus = findViewById(R.id.btnPlus);
        Button buttonEqual = findViewById(R.id.btnEquals);
        Button buttonDelete = findViewById(R.id.btnDelete);
        Button buttonClear = findViewById(R.id.btnClear);
        Button buttonDot = findViewById(R.id.btnDot);
        Button buttonMultiply = findViewById(R.id.btnMultiply);
        Button buttonDivide = findViewById(R.id.btnDivide);

//        result_tv = findViewById(R.id.Text2);
        solution_tv = findViewById(R.id.Text1);

        assignOnClickFunction(button0);
        assignOnClickFunction(button1);
        assignOnClickFunction(button2);
        assignOnClickFunction(button3);
        assignOnClickFunction(button4);
        assignOnClickFunction(button5);
        assignOnClickFunction(button6);
        assignOnClickFunction(button7);
        assignOnClickFunction(button8);
        assignOnClickFunction(button9);
        assignOnClickFunction(buttonPlus);
        assignOnClickFunction(buttonMinus);
        assignOnClickFunction(buttonMultiply);
        assignOnClickFunction(buttonDivide);
        assignOnClickFunction(buttonDelete);
        assignOnClickFunction(buttonDot);
        assignOnClickFunction(buttonClear);
        assignOnClickFunction(buttonEqual);

    }

    void assignOnClickFunction(Button btn) {
        btn.setOnClickListener(this);
    }


    /*
     * library calculation
     *
     * */

//    String calculate_result(String data) {
//        try {
//
//            Expression expression = new ExpressionBuilder(data).build();
//            double result = expression.evaluate();
//
//            BigDecimal bd = new BigDecimal(result).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
//
//            return bd.toPlainString();
//        } catch (Exception e) {
//            return "Err";
//        }
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnEquals) {
            pressed_equal_new_with_whole_expression(solution_tv.getText().toString());
        }
        else if (v.getId() == R.id.btnClear){
            pressed_all_clear();
        }
        else if (v.getId() == R.id.btnDelete){
            pressed_del();
        }
        else if (v.getId() == R.id.btnPlus){
            pressed_any_number("+");
        }
        else if (v.getId() == R.id.btnMinus){
            pressed_any_number("-");
        }
        else if (v.getId() == R.id.btnMultiply){
            pressed_any_number("*");
        }
        else if (v.getId() == R.id.btnDivide){
            pressed_any_number("/");
        }
        else if (v.getId() == R.id.btn0) {
            pressed_any_number("0");
        }
        else if (v.getId() == R.id.btn1) {
            pressed_any_number("1");
        }
        else if (v.getId() == R.id.btn2) {
            pressed_any_number("2");
        }
        else if (v.getId() == R.id.btn3) {
            pressed_any_number("3");
        }
        else if (v.getId() == R.id.btn4) {
            pressed_any_number("4");
        }
        else if (v.getId() == R.id.btn5) {
            pressed_any_number("5");
        }
        else if (v.getId() == R.id.btn6) {
            pressed_any_number("6");
        }
        else if (v.getId() == R.id.btn7) {
            pressed_any_number("7");
        }
        else if (v.getId() == R.id.btn8) {
            pressed_any_number("8");
        }
        else if (v.getId() == R.id.btn9) {
            pressed_any_number("9");
        }
        else if (v.getId() == R.id.btnDot){
            pressed_any_number(".");
        }
    }


    private void pressed_all_clear(){
        solution_tv.setText("0");
//        result_tv.setText("0");
        first_no = null;
        operator = "";
    }

    private void pressed_del(){
        String  total_text = solution_tv.getText().toString();
        if (solution_tv.getText().length() >= 1){
            total_text = total_text.substring(0,total_text.length()-1);
            solution_tv.setText(total_text);
        }
    }

    private void pressed_equal_new_with_whole_expression(String expression) {
        List<String> no = new ArrayList<>();
        List<String> operator = new ArrayList<>();
        List<Integer> operators_index = new ArrayList<>();
        List<Double> numbers = new ArrayList<>();
        try {
            for (int i = 0; i < expression.length(); i++) {
                if (expression.charAt(i) == '+' || expression.charAt(i) == '-' || expression.charAt(i) == '*' || expression.charAt(i) == '/') {
                    operator.add(String.valueOf(expression.charAt(i)));
                    operators_index.add(i);
                }
            }
            int j = 0;
            for (int i = 0; i < operator.size(); i++) {
                no.add(expression.substring(j, operators_index.get(i)));
                j = operators_index.get(i) + 1;
            }
            no.add(expression.substring(j));

            for (int i = 0; i < no.size(); i++) {
                numbers.add(Double.parseDouble(no.get(i)));
            }
            int i = 0;
            while (i < operator.size()) {
                if (operator.get(i).equals("/")) {
                    numbers.set(i, (numbers.get(i)) / (numbers.get(i + 1)));
                    numbers.remove(i + 1);
                    operator.remove(i);
                } else if (operator.get(i).equals("*")) {
                    numbers.set(i, (numbers.get(i)) * (numbers.get(i + 1)));
                    numbers.remove(i + 1);
                    operator.remove(i);
                } else {
                    i = i + 1;
                }
            }
            j = 0;
            while (j < operator.size()) {
                if (operator.get(j).equals("+")) {
                    numbers.set(j, (numbers.get(j)) + (numbers.get(j+ 1)));
                    numbers.remove(j + 1);
                    operator.remove(j);
                } else if (operator.get(j).equals("-")) {
                    numbers.set(j, (numbers.get(j)) - (numbers.get(j + 1)));
                    numbers.remove(j + 1);
                    operator.remove(j);
                } else {
                    j = j + 1;
                }
            }

            Double result = numbers.get(0);
            BigDecimal bd = new BigDecimal(result).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
            solution_tv.setText(bd.toPlainString());
        } catch (Exception e) {
            Toast.makeText(this, "Err Detected",Toast.LENGTH_LONG).show();
        }
    }
        void pressed_any_number(String button_text) {
            String current_text = solution_tv.getText().toString();
            String new_string_after_removing_leading_zeroes = "";
            for (int i = 0; i < current_text.length(); i++) {
                if (!current_text.equals("0")) {
                    new_string_after_removing_leading_zeroes = current_text.substring(i);
                    break;
                }
            }
            String result_to_show = new_string_after_removing_leading_zeroes + button_text;
            solution_tv.setText(result_to_show);
        }

//    private void pressed_operator(String button_text){
//        String total_text_before = solution_tv.getText().toString();
//        if (!total_text_before.isEmpty() && total_text_before.matches("-?\\d+(\\.\\d+)?")) {
//            first_no = Double.parseDouble(total_text_before);
//            operator = button_text;
//            solution_tv.setText("");
//            String new_value_to_show_on_upper_screen = String.valueOf(first_no) + operator;
//            result_tv.setText(new_value_to_show_on_upper_screen);
//            solution_tv.setText(new_value_to_show_on_upper_screen);
//        }
//        else{
//            Toast.makeText(this, "Error, Enter no after Decimal",Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private void pressed_equal(){
//        if (first_no == null){
//            result_tv.setText(solution_tv.getText().toString());
//        }else {
//            String before_no = String.valueOf(first_no);
//            String next_no_pre = solution_tv.getText().toString();
//            next_no_pre = next_no_pre.substring(before_no.length() + 1);
//            if (next_no_pre.isEmpty()) {
//                Toast.makeText(this, "Error!!! No Value ", Toast.LENGTH_LONG).show();
//                return;
//            } else if (next_no_pre.equals(".") || (next_no_pre.indexOf(".") != next_no_pre.lastIndexOf("."))) {
//                Toast.makeText(this, "Error!!! Enter valid Value to Operate ", Toast.LENGTH_LONG).show();
//                return;
//            } else {
//
//
//                Double next_no = Double.parseDouble(next_no_pre);
//                BigDecimal first_no_final;
//                if (first_no != null && !operator.isEmpty()) {
//                    switch (operator) {
//                        case "+":
//                            first_no = first_no + next_no;
//                            first_no_final = new BigDecimal(first_no).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
//                            solution_tv.setText(String.valueOf(first_no_final));
//                            result_tv.setText(String.valueOf(first_no_final));
//                            return;
//                        case "-":
//                            first_no = first_no - next_no;
//                            first_no_final = new BigDecimal(first_no).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
//                            solution_tv.setText(String.valueOf(first_no_final));
//                            result_tv.setText(String.valueOf(first_no_final));
//                            return;
//                        case "*":
//                            first_no = first_no * next_no;
//                            first_no_final = new BigDecimal(first_no).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
//                            solution_tv.setText(String.valueOf(first_no_final));
//                            result_tv.setText(String.valueOf(first_no_final));
//                            return;
//                        case "/":
//                            if (next_no != 0) {
//                                first_no = first_no / next_no;
//                                first_no_final = new BigDecimal(first_no).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
//                                solution_tv.setText(String.valueOf(first_no_final));
//                                result_tv.setText(String.valueOf(first_no_final));
//                                return;
//                            } else {
//                                solution_tv.setText(R.string.zero_division_error);
//                            }
//
//                    }
//                    operator = "";
//                }
//            }
//        }
//    }




//    @Override
//    public void onClick(View view) {
//        Button button = (Button) view;
//        String button_text = button.getText().toString();
//        String data_to_calculate = solution_tv.getText().toString();
//
//        if (button_text.equals("AC")) {
//            solution_tv.setText("");
//            result_tv.setText("0");
//            return;
//        }
//        if (button_text.equals("=")) {
//            solution_tv.setText(result_tv.getText());
//            return;
//        }
//        if (button_text.equals("DEL")) {
//            if (!data_to_calculate.isEmpty() && !data_to_calculate.equals("0")) {
//                data_to_calculate = data_to_calculate.substring(0, data_to_calculate.length() - 1);
//            } else {
//                data_to_calculate = "0";
//            }
//
//        } else {
//            if (button_text.matches("[+\\-*/]")) {
//                data_to_calculate = solution_tv.getText().toString();
//                char lastChar = data_to_calculate.charAt(data_to_calculate.length() - 1);
//                if (String.valueOf(lastChar).matches("[+\\-*/]")) {
//                    data_to_calculate = data_to_calculate.substring(0, data_to_calculate.length() - 1) + button_text;
//                    solution_tv.setText(data_to_calculate);
//                    calculate_result(data_to_calculate);
//                    return;
//                }
//            }
//            data_to_calculate = data_to_calculate + button_text;
//        }
//        String final_result = calculate_result(data_to_calculate);
//        if (!final_result.equals("Err")) {
//            result_tv.setText(final_result);
//        }
//        solution_tv.setText(data_to_calculate);
//        calculate_result(data_to_calculate);
//    }


//    @Override
//    public void onClick(View view){
//        Button button = (Button) view;
//        String button_text = button.getText().toString();
//        String final_result;
//        if (button_text.equals("AC")){
//            solution_tv.setText("0");
//            result_tv.setText("0");
//            first_no = null;
//            operator = "";
//            return;
//        }
//        if (button_text.equals("DEL")){
//            String  total_text = solution_tv.getText().toString();
//            if (solution_tv.getText().length() >= 1){
//                total_text = total_text.substring(0,total_text.length()-1);
//                solution_tv.setText(total_text);
//                return;
//            }
//        }
//        if ((button_text.equals("+") || button_text.equals("-") || button_text.equals("*")) || button_text.equals("/")) {
//            String total_text_before = solution_tv.getText().toString();
//            if (!total_text_before.isEmpty() && total_text_before.matches("-?\\d+(\\.\\d+)?")) {
//                first_no = Double.parseDouble(total_text_before);
//                operator = button_text;
//                solution_tv.setText("");
//                result_tv.setText(String.valueOf(first_no) + operator);
//            }
//        }
//        if (button_text.equals("=")){
//            if (solution_tv.getText().toString().isEmpty()){
//                Toast.makeText(this, "Error!!! No Value ",Toast.LENGTH_LONG).show();
//            }
//            if (solution_tv.getText().toString().equals(".") ||
//                (solution_tv.getText().toString().indexOf(".") != solution_tv.getText().toString().lastIndexOf(".") )){
//                Toast.makeText(this, "Error!!! Enter valid Value to Operate ",Toast.LENGTH_LONG).show();
//            }
//            else{
//            Double next_no = Double.parseDouble(solution_tv.getText().toString());
//            if (first_no != null && !operator.isEmpty()  ) {
//                if (operator.equals("+")){
//                    first_no = first_no + next_no;
//                    BigDecimal first_no_final = new BigDecimal(first_no).setScale(8,RoundingMode.HALF_UP).stripTrailingZeros();
//                    solution_tv.setText(String.valueOf(first_no_final));
//                    result_tv.setText(String.valueOf(first_no_final));
//                    return;
//                } else if (operator.equals("-")){
//                    first_no = first_no - next_no;
//                    BigDecimal first_no_final = new BigDecimal(first_no).setScale(8,RoundingMode.HALF_UP).stripTrailingZeros();
//                    solution_tv.setText(String.valueOf(first_no_final));
//                    result_tv.setText(String.valueOf(first_no_final));
//                    return;
//                } else if (operator.equals("*")){
//                    first_no = first_no * next_no;
//                    BigDecimal first_no_final = new BigDecimal(first_no).setScale(8,RoundingMode.HALF_UP).stripTrailingZeros();
//                    solution_tv.setText(String.valueOf(first_no_final));
//                    result_tv.setText(String.valueOf(first_no_final));
//                    return;
//                } else if (operator.equals("/")){
//                    if (next_no != 0) {
//                        first_no = first_no / next_no;
//                        BigDecimal first_no_final = new BigDecimal(first_no).setScale(8,RoundingMode.HALF_UP).stripTrailingZeros();
//                        solution_tv.setText(String.valueOf(first_no_final));
//                        result_tv.setText(String.valueOf(first_no_final));
//                        return;
//                    } else {
//                        solution_tv.setText("Zero Division Error");
//                    }
//                }
//                operator = "";
//            }
//            }
//
//        } else if(button_text.matches("[0-9.]") && first_no != null) {
//            String current_text = solution_tv.getText().toString();
//            String result_to_show = current_text+button_text;
//            solution_tv.setText(result_to_show);
//        } else if (button_text.matches("[0-9.]") && first_no == null){
//            String current_text = solution_tv.getText().toString();
//            if
//            String result_to_show = current_text+button_text;
//            solution_tv.setText(result_to_show);
//        }
//
    }
