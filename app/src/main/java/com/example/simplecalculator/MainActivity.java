package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
 TextView solutionTv, resultTv;
    MaterialButton  btnClear, btnOpenBracket, btnCloseBracket, btnDivide,
            btnMultiplication, btnMinus, btnPlus, btnAllClean, btnEquals, btnComma,
            btn9, btn8, btn7, btn6, btn5, btn4, btn3, btn2, btn1, btn0;
    char lastsym;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        solutionTv=findViewById(R.id.tvExpression);
        resultTv=findViewById(R.id.tvResult);

        initButton(btn0, R.id.btn0);
        initButton(btn1, R.id.btn1);
        initButton(btn2, R.id.btn2);
        initButton(btn3, R.id.btn3);
        initButton(btn4, R.id.btn4);
        initButton(btn5, R.id.btn5);
        initButton(btn6, R.id.btn6);
        initButton(btn7, R.id.btn7);
        initButton(btn8, R.id.btn8);
        initButton(btn9, R.id.btn9);
        initButton(btnComma, R.id.btnComma);
        initButton(btnEquals, R.id.btnEquals);
        initButton(btnPlus, R.id.btnPlus);
        initButton(btnMinus, R.id.btnMinus);
        initButton(btnMultiplication, R.id.btnMultiplication);
        initButton(btnDivide, R.id.btnDivide);
        initButton(btnOpenBracket, R.id.btnOpenBracket);
        initButton(btnCloseBracket, R.id.btnCloseBracket);
        initButton(btnClear, R.id.btnClear);
        initButton(btnAllClean, R.id.btnAllClean);

    }
    void initButton(MaterialButton button, int id){
        button=findViewById(id);
        button.setOnClickListener( this::onClick);
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();
        String dataToCalculate = solutionTv.getText().toString();

        char lastChar = dataToCalculate.isEmpty() ? ' ' : dataToCalculate.charAt(dataToCalculate.length() - 1); //последний символ

        if (buttonText.equals("(")) {   //openBracket
            if (dataToCalculate.isEmpty() || lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/') {
                dataToCalculate += "(";
            }
            else
                return;
        }

        Log.w("result ",dataToCalculate);

        if(buttonText.equals(".")&&dataToCalculate.contains("."))
            return;

        if(buttonText.equals(".") && dataToCalculate.isEmpty()) {
            dataToCalculate = "0";
        }

        if(buttonText.equals("AC")){
            solutionTv.setText("");
            resultTv.setText("");
            return;
        }else if(buttonText.equals("=")){
            solutionTv.setText(resultTv.getText());
            return;
        }else if(buttonText.equals("C")&&(!dataToCalculate.isEmpty()))
        {
            dataToCalculate = dataToCalculate.substring(0,dataToCalculate.length()-1);
            solutionTv.setText(dataToCalculate);
            return;
        }
        else if(buttonText.equals("C")&&(dataToCalculate.isEmpty())){
            return;
        }

        else {
            if(!dataToCalculate.isEmpty()) {
                char sym=dataToCalculate.charAt(dataToCalculate.length()-1);
                char sym2=buttonText.charAt(0);
                if(sym=='(' || sym==')') {
                    if (!Character.isDigit(sym) && !Character.isAlphabetic(sym) && !Character.isDigit(sym2) && !Character.isAlphabetic(sym2) && (sym2 == '(' || sym2 == ')')) {
                        dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1) + buttonText;
                        solutionTv.setText(dataToCalculate);
                        return;
                    }
                }else {
                    if (!Character.isDigit(sym) && !Character.isAlphabetic(sym) && !Character.isDigit(sym2) && !Character.isAlphabetic(sym2) ) {
                        dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1) + buttonText;
                        solutionTv.setText(dataToCalculate);
                        return;
                    }
                }
            }
            else if(dataToCalculate.isEmpty()){
                char sym2=buttonText.charAt(0);
                if((!Character.isDigit(sym2))&&(!Character.isAlphabetic(sym2))) {
                    buttonText = "";
                    return;

                }
            }
            else if(dataToCalculate.equals("C"))
                dataToCalculate="";


            dataToCalculate = dataToCalculate +buttonText;
            lastsym=buttonText.charAt(0);
        }

        if(dataToCalculate.startsWith("0")  && !dataToCalculate.endsWith(".") && !dataToCalculate.contains(".")){
            dataToCalculate=dataToCalculate.substring(1,dataToCalculate.length());
        }
        solutionTv.setText(dataToCalculate);
        if(hasUnmatchedBracket(dataToCalculate))
           dataToCalculate+=")";

        Log.w("result2",dataToCalculate);

        String finalResult = getResult(dataToCalculate);

        if(!finalResult.equals("Err")){
            resultTv.setText(finalResult);
        }


    }

    String getResult(String data) {
        Context rhino = Context.enter();

        // Устанавливаем версию JavaScript, которую будем использовать (по умолчанию актуальная)
        rhino.setOptimizationLevel(-1); // Без оптимизации для мобильных устройств
        try {
            if (data.startsWith(".")) {
                data = "0" + data; // Добавляем "0" перед точкой
            }
            if (data.isEmpty())
                return "0";

            // Создаем скриптовый объект Rhino
            Scriptable scope = rhino.initStandardObjects();
            // Выполняем выражение JavaScript
            String result = rhino.evaluateString(scope, data, "JavaScript", 1, null).toString();
            // Приводим результат к числу и возвращаем его
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            return decimalFormat.format(Double.parseDouble(result));
        } catch (Exception e) {

            return "";
        } finally {
            // Выход из контекста Rhino, освобождаем ресурсы
            Context.exit();
        }
        }

    public boolean hasUnmatchedBracket(String expression) {
        int openBrackets = 0;
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            if (currentChar == '(') {
                openBrackets++;
            }

            if (currentChar == ')') {
                openBrackets--;


                if (openBrackets < 0) {
                    return true; // Означает, что где-то была закрывающая скобка без соответствующей открывающей
                }
            }
        }

        // Если после прохода по выражению остались незакрытые скобки
        return openBrackets > 0;
    }
}