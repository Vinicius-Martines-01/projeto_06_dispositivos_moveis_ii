package br.gov.sp.cps.combustivel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CombustivelPrefs";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_LAST_ODOMETER = "last_odometer";

    private EditText editPrevDate, editCurrDate;
    private EditText editPrevOdometer, editCurrOdometer;
    private EditText editLiters, editPricePerLiter;
    private RadioGroup radioGroupFuel;

    private View layoutResults;
    private TextView txtAvgConsumption, txtCostPerKm, txtKmPerDay, txtExtraDetails;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        initViews();
        setupDatePickers();
        loadSavedData();
    }

    private void initViews() {
        editPrevDate = findViewById(R.id.editPrevDate);
        editCurrDate = findViewById(R.id.editCurrDate);
        editPrevOdometer = findViewById(R.id.editPrevOdometer);
        editCurrOdometer = findViewById(R.id.editCurrOdometer);
        editLiters = findViewById(R.id.editLiters);
        editPricePerLiter = findViewById(R.id.editPricePerLiter);
        radioGroupFuel = findViewById(R.id.radioGroupFuel);

        layoutResults = findViewById(R.id.layoutResults);
        txtAvgConsumption = findViewById(R.id.txtAvgConsumption);
        txtCostPerKm = findViewById(R.id.txtCostPerKm);
        txtKmPerDay = findViewById(R.id.txtKmPerDay);
        txtExtraDetails = findViewById(R.id.txtExtraDetails);

        Button btnCalculate = findViewById(R.id.btnCalculate);
        btnCalculate.setOnClickListener(v -> calculateResults());

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> clearForm());

        Button btnGoToComparison = findViewById(R.id.btnGoToComparison);
        btnGoToComparison.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ComparisonActivity.class);
            startActivity(intent);
        });
    }

    private void setupDatePickers() {
        editPrevDate.setOnClickListener(v -> showDatePicker(editPrevDate));
        editCurrDate.setOnClickListener(v -> showDatePicker(editCurrDate));

        if (editCurrDate.getText().toString().trim().isEmpty()) {
            editCurrDate.setText(dateFormat.format(new Date()));
        }
    }

    private void showDatePicker(EditText targetField) {
        Calendar calendar = Calendar.getInstance();

        String currentText = targetField.getText().toString().trim();
        if (!currentText.isEmpty()) {
            try {
                Date parsed = dateFormat.parse(currentText);
                if (parsed != null) {
                    calendar.setTime(parsed);
                }
            } catch (ParseException ignored) {
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(y, m, d);
            targetField.setText(dateFormat.format(selected.getTime()));
        }, year, month, day);

        dialog.show();
    }

    private void loadSavedData() {
        String lastDate = preferences.getString(KEY_LAST_DATE, "");
        String lastOdo = preferences.getString(KEY_LAST_ODOMETER, "");

        if (!lastDate.isEmpty() && editPrevDate.getText().toString().isEmpty()) {
            editPrevDate.setText(lastDate);
        }
        if (!lastOdo.isEmpty() && editPrevOdometer.getText().toString().isEmpty()) {
            editPrevOdometer.setText(lastOdo);
        }
    }

    private void calculateResults() {
        String prevDateStr = editPrevDate.getText().toString().trim();
        String currDateStr = editCurrDate.getText().toString().trim();
        String prevOdoStr = editPrevOdometer.getText().toString().trim();
        String currOdoStr = editCurrOdometer.getText().toString().trim();
        String litersStr = editLiters.getText().toString().trim();
        String priceStr = editPricePerLiter.getText().toString().trim();

        if (prevDateStr.isEmpty() || currDateStr.isEmpty() || prevOdoStr.isEmpty()
                || currOdoStr.isEmpty() || litersStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double prevOdo = Double.parseDouble(prevOdoStr);
            double currOdo = Double.parseDouble(currOdoStr);
            double liters = Double.parseDouble(litersStr);
            double pricePerLiter = Double.parseDouble(priceStr);

            if (currOdo <= prevOdo) {
                Toast.makeText(this, "O hodômetro atual deve ser maior que o anterior.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (liters <= 0 || pricePerLiter <= 0) {
                Toast.makeText(this, "Litros e valor devem ser maiores que zero.", Toast.LENGTH_SHORT).show();
                return;
            }

            Date prevDate = dateFormat.parse(prevDateStr);
            Date currDate = dateFormat.parse(currDateStr);

            if (prevDate == null || currDate == null) {
                Toast.makeText(this, "Data inválida.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currDate.before(prevDate)) {
                Toast.makeText(this, "A data atual não pode ser anterior à data anterior.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cálculos
            double distanceKm = currOdo - prevOdo;
            double avgConsumption = distanceKm / liters;
            double totalCost = liters * pricePerLiter;
            double costPerKm = totalCost / distanceKm;

            long diffInMillis = currDate.getTime() - prevDate.getTime();
            long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            if (days <= 0) {
                days = 1; // mesmo dia
            }

            double kmPerDay = distanceKm / (double) days;

            int checkedRadioId = radioGroupFuel.getCheckedRadioButtonId();
            RadioButton selectedRadio = findViewById(checkedRadioId);
            String fuelType = selectedRadio != null ? selectedRadio.getText().toString() : "Gasolina";

            txtAvgConsumption.setText(String.format(Locale.getDefault(), "Consumo médio: %.2f km/l", avgConsumption));
            txtCostPerKm.setText(String.format(Locale.getDefault(), "Custo efetivo: R$ %.2f / km", costPerKm));
            txtKmPerDay.setText(String.format(Locale.getDefault(), "Média diária percorrida: %.2f km/dia", kmPerDay));

            txtExtraDetails.setText(String.format(Locale.getDefault(),
                    "Combustível: %s | Distância percorrida: %.1f km (%d dias) | Total gasto: R$ %.2f",
                    fuelType, distanceKm, days, totalCost));

            layoutResults.setVisibility(View.VISIBLE);

            // Salva último registro para próximo abastecimento
            preferences.edit()
                    .putString(KEY_LAST_DATE, currDateStr)
                    .putString(KEY_LAST_ODOMETER, currOdoStr)
                    .apply();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Insira valores numéricos válidos.", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de data inválido (dd/mm/aaaa).", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        editPrevDate.setText("");
        editCurrDate.setText(dateFormat.format(new Date()));
        editPrevOdometer.setText("");
        editCurrOdometer.setText("");
        editLiters.setText("");
        editPricePerLiter.setText("");
        radioGroupFuel.check(R.id.radioGasoline);
        layoutResults.setVisibility(View.GONE);
    }
}
