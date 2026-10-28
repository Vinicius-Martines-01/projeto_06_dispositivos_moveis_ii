# Controle de Combustível

Aplicativo Android desenvolvido para registro e análise de consumo de combustível, além de comparativo de viabilidade econômica entre etanol e gasolina.

## Funcionalidades

1. **Registro de Abastecimento**
   - Data do abastecimento anterior e atual.
   - Leitura do hodômetro anterior e atual (km).
   - Volume abastecido (litros).
   - Valor pago por litro (R$).
   - Seleção do tipo de combustível via `RadioGroup` (Gasolina, Etanol ou Diesel).

2. **Cálculos e Indicadores**
   - **Consumo médio (km/l):** distância percorrida dividida pelo volume abastecido.
   - **Custo efetivo (R$/km):** valor total gasto dividido pela distância percorrida.
   - **Média diária (km/dia):** distância percorrida dividida pela quantidade de dias entre os abastecimentos.

3. **Comparativo Etanol x Gasolina**
   - Tela para inserção do preço do etanol e da gasolina.
   - Avaliação baseada na relação de rendimento de 70% (etanol/gasolina <= 0.70).
   - Indicação de qual combustível oferece melhor custo-benefício.

## Estrutura do Projeto

- `MainActivity.java`: gerencia a entrada de dados do abastecimento, validações, persistência simples e cálculo dos indicadores.
- `activity_main.xml`: interface gráfica principal.
- `ComparisonActivity.java`: gerencia o cálculo comparativo entre etanol e gasolina.
- `activity_comparison.xml`: interface do comparativo de preços.

## Tecnologias

- Java 11
- Android SDK (minSdk 24, targetSdk 36)
- Material Components / AndroidX
