package br.gov.sp.cps.combustivel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ComparisonActivity extends AppCompatActivity {

    private EditText editGasolinePrice, editEthanolPrice;
    private View layoutComparisonResult;
    private TextView txtRecommendationTitle, txtComparisonRatio, txtComparisonExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        editGasolinePrice = findViewById(R.id.editGasolinePrice);
        editEthanolPrice = findViewById(R.id.editEthanolPrice);
        layoutComparisonResult = findViewById(R.id.layoutComparisonResult);
        txtRecommendationTitle = findViewById(R.id.txtRecommendationTitle);
        txtComparisonRatio = findViewById(R.id.txtComparisonRatio);
        txtComparisonExplanation = findViewById(R.id.txtComparisonExplanation);

        Button btnCompare = findViewById(R.id.btnCompare);
        btnCompare.setOnClickListener(v -> comparePrices());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void comparePrices() {
        String gasStr = editGasolinePrice.getText().toString().trim();
        String ethStr = editEthanolPrice.getText().toString().trim();

        if (gasStr.isEmpty() || ethStr.isEmpty()) {
            Toast.makeText(this, "Informe os preços da gasolina e do etanol.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double gasPrice = Double.parseDouble(gasStr);
            double ethanolPrice = Double.parseDouble(ethStr);

            if (gasPrice <= 0 || ethanolPrice <= 0) {
                Toast.makeText(this, "Os preços devem ser maiores que zero.", Toast.LENGTH_SHORT).show();
                return;
            }

            double percentage = (ethanolPrice / gasPrice) * 100.0;

            if (percentage <= 70.0) {
                txtRecommendationTitle.setText("Abastecimento mais vantajoso: ETANOL");
                txtComparisonRatio.setText(String.format(Locale.getDefault(),
                        "O etanol custa %.1f%% do valor da gasolina.", percentage));
                txtComparisonExplanation.setText("Relação igual ou menor que 70%: compensa utilizar etanol.");
            } else {
                txtRecommendationTitle.setText("Abastecimento mais vantajoso: GASOLINA");
                txtComparisonRatio.setText(String.format(Locale.getDefault(),
                        "O etanol custa %.1f%% do valor da gasolina.", percentage));
                txtComparisonExplanation.setText("Relação superior a 70%: compensa utilizar gasolina.");
            }

            layoutComparisonResult.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Digite valores numéricos válidos.", Toast.LENGTH_SHORT).show();
        }
    }
}
