package com.tejalabs.notacalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvDisplay, tvPrevious;
    private String currentInput = "0";
    private String operator = "";
    private String firstOperand = "";
    private boolean isNewInput = true;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupButtonListeners();
    }

    private void initializeViews() {
        tvDisplay = findViewById(R.id.tvDisplay);
        tvPrevious = findViewById(R.id.tvPrevious);
    }

    private void setupButtonListeners() {
        // Number buttons
        findViewById(R.id.btn0).setOnClickListener(this);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn3).setOnClickListener(this);
        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);
        findViewById(R.id.btn7).setOnClickListener(this);
        findViewById(R.id.btn8).setOnClickListener(this);
        findViewById(R.id.btn9).setOnClickListener(this);

        // Operator buttons
        findViewById(R.id.btnAdd).setOnClickListener(this);
        findViewById(R.id.btnSubtract).setOnClickListener(this);
        findViewById(R.id.btnMultiply).setOnClickListener(this);
        findViewById(R.id.btnDivide).setOnClickListener(this);

        // Function buttons
        findViewById(R.id.btnEquals).setOnClickListener(this);
        findViewById(R.id.btnClear).setOnClickListener(this);
        findViewById(R.id.btnDecimal).setOnClickListener(this);
        findViewById(R.id.btnPlusMinus).setOnClickListener(this);
        findViewById(R.id.btnPercent).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String buttonText = button.getText().toString();

        int id = v.getId();

        if (id == R.id.btn0 || id == R.id.btn1 || id == R.id.btn2 || id == R.id.btn3 ||
            id == R.id.btn4 || id == R.id.btn5 || id == R.id.btn6 || id == R.id.btn7 ||
            id == R.id.btn8 || id == R.id.btn9) {
            handleNumberInput(buttonText);
        } else if (id == R.id.btnAdd || id == R.id.btnSubtract ||
                   id == R.id.btnMultiply || id == R.id.btnDivide) {
            handleOperatorInput(buttonText);
        } else if (id == R.id.btnEquals) {
            handleEqualsInput();
        } else if (id == R.id.btnClear) {
            handleClearInput();
        } else if (id == R.id.btnDecimal) {
            handleDecimalInput();
        } else if (id == R.id.btnPlusMinus) {
            handlePlusMinusInput();
        } else if (id == R.id.btnPercent) {
            handlePercentInput();
        }
    }

    private void handleNumberInput(String number) {
        if (isNewInput || currentInput.equals("0")) {
            currentInput = number;
            isNewInput = false;
        } else {
            currentInput += number;
        }
        updateDisplay();
    }

    private void handleOperatorInput(String op) {
        if (!operator.isEmpty() && !isNewInput) {
            handleEqualsInput();
        }

        firstOperand = currentInput;
        operator = op;
        isNewInput = true;

        // Show the operation in progress
        tvPrevious.setText(firstOperand + " " + operator);
    }

    private void handleEqualsInput() {
        if (operator.isEmpty() || firstOperand.isEmpty()) {
            return;
        }

        // Show the complete calculation
        String calculation = firstOperand + " " + operator + " " + currentInput + " =";
        tvPrevious.setText(calculation);

        // Generate a fun random result instead of the correct one
        String result = generateFunResult(firstOperand, operator, currentInput);

        currentInput = result;
        operator = "";
        firstOperand = "";
        isNewInput = true;
        updateDisplay();
    }

    private String generateFunResult(String first, String op, String second) {
        try {
            double num1 = Double.parseDouble(first);
            double num2 = Double.parseDouble(second);
            double correctResult = 0;

            // Calculate the correct result first
            switch (op) {
                case "+":
                    correctResult = num1 + num2;
                    break;
                case "-":
                    correctResult = num1 - num2;
                    break;
                case "×":
                    correctResult = num1 * num2;
                    break;
                case "÷":
                    if (num2 != 0) {
                        correctResult = num1 / num2;
                    } else {
                        return "Error";
                    }
                    break;
            }

            // Now generate a fun wrong result
            return generateWrongResult(correctResult, num1, num2, op);

        } catch (NumberFormatException e) {
            return "Error";
        }
    }

    private String generateWrongResult(double correct, double num1, double num2, String op) {
        // Different strategies for generating funny wrong results
        int strategy = random.nextInt(6);
        double wrongResult;

        switch (strategy) {
            case 0: // Off by one
                wrongResult = correct + (random.nextBoolean() ? 1 : -1);
                break;
            case 1: // Multiply/divide by small number
                double factor = 2 + random.nextInt(3); // 2, 3, or 4
                wrongResult = random.nextBoolean() ? correct * factor : correct / factor;
                break;
            case 2: // Add/subtract a percentage
                double percentage = 0.1 + (random.nextDouble() * 0.4); // 10% to 50%
                wrongResult = random.nextBoolean() ?
                    correct * (1 + percentage) : correct * (1 - percentage);
                break;
            case 3: // Swap operands result (when it makes sense)
                if (op.equals("-")) {
                    wrongResult = num2 - num1;
                } else if (op.equals("÷") && num1 != 0) {
                    wrongResult = num2 / num1;
                } else {
                    wrongResult = correct + random.nextInt(10) + 1;
                }
                break;
            case 4: // Round to funny numbers
                if (correct > 10) {
                    wrongResult = Math.round(correct / 10) * 10 + random.nextInt(9) + 1;
                } else {
                    wrongResult = correct + random.nextInt(5) + 1;
                }
                break;
            default: // Random nearby number
                int range = Math.max(1, (int)(Math.abs(correct) * 0.2));
                wrongResult = correct + (random.nextInt(range * 2) - range);
                if (wrongResult == correct) wrongResult += 1; // Make sure it's wrong
                break;
        }

        // Format the result nicely
        if (wrongResult == (long) wrongResult) {
            return String.valueOf((long) wrongResult);
        } else {
            return String.format("%.2f", wrongResult);
        }
    }

    private void handleClearInput() {
        currentInput = "0";
        operator = "";
        firstOperand = "";
        isNewInput = true;
        tvPrevious.setText("");
        updateDisplay();
    }

    private void handleDecimalInput() {
        if (isNewInput) {
            currentInput = "0.";
            isNewInput = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    private void handlePlusMinusInput() {
        if (!currentInput.equals("0")) {
            if (currentInput.startsWith("-")) {
                currentInput = currentInput.substring(1);
            } else {
                currentInput = "-" + currentInput;
            }
            updateDisplay();
        }
    }

    private void handlePercentInput() {
        try {
            double value = Double.parseDouble(currentInput);
            value = value / 100;
            currentInput = formatResult(value);
            updateDisplay();
        } catch (NumberFormatException e) {
            // Do nothing if invalid number
        }
    }

    private void updateDisplay() {
        tvDisplay.setText(currentInput);
    }

    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.format("%.6f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
        }
    }
}