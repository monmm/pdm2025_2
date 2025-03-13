package mx.unam.fciencias.moviles.sensores;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText ipInput, intervalInput;
    private Button startButton;
    private Button stopButton;
    private RecyclerView recyclerView;
    private DataAdapter adapter;
    private List<SensorData> dataList;
    private Handler handler;
    private Runnable dataFetchRunnable;
    private int interval = 5000; // Intervalo predeterminado de 5 segundos

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipInput = findViewById(R.id.ipInput);
        intervalInput = findViewById(R.id.intervalInput);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        recyclerView = findViewById(R.id.recyclerView);

        dataList = new ArrayList<>();
        adapter = new DataAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        handler = new Handler();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = ipInput.getText().toString().trim();
                String intervalText = intervalInput.getText().toString().trim();

                if (!ipAddress.isEmpty()) {
                    if (!intervalText.isEmpty()) {
                        try {
                            interval = Integer.parseInt(intervalText) * 1000; // Convertir a milisegundos
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "Ingrese un número válido para el intervalo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    startDataCollection(ipAddress);
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDataCollection();
            }
        });
    }

    private void startDataCollection(String ipAddress) {
        if (dataFetchRunnable != null) {
            handler.removeCallbacks(dataFetchRunnable);
        }

        dataFetchRunnable = new Runnable() {
            @Override
            public void run() {
                new RequestDataTask().execute(ipAddress);
                handler.postDelayed(this, interval);
            }
        };
        handler.post(dataFetchRunnable);
    }

    private void stopDataCollection() {
        if (dataFetchRunnable != null) {
            handler.removeCallbacks(dataFetchRunnable); // Detener el Runnable
            Toast.makeText(MainActivity.this, "Captura de datos detenida", Toast.LENGTH_SHORT).show();
        }
    }

    private class RequestDataTask extends AsyncTask<String, Void, SensorData> {

        // Para no tener un archivo con las direcciones IP permitidas
        private final OkHttpClient client;

        public RequestDataTask() {
            // Permitimos tráfico HTTP
            client = new OkHttpClient.Builder().connectionSpecs(
                    List.of(ConnectionSpec.CLEARTEXT)).build();
        }

        @Override
        protected SensorData doInBackground(String... params) {
            String ipAddress = params[0];
            int arduinoPort = 8080;
            String url = "http://" + ipAddress + ":" + arduinoPort + "/data";

            try {
                // Construir la solicitud HTTP
                Request request = new Request.Builder().url(url).build();

                // Ejecutar la solicitud y obtener la respuesta
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    Log.e("RequestDataTask", "Error de conexión con el servidor.");
                    return null;
                }

                // Obtener el cuerpo de la respuesta como String
                String responseBody = response.body().string();
                response.close();

                // Mostrar el cuerpo de la respuesta en Logcat para depuración
                Log.d("MyTag", responseBody);

                // Parsear la respuesta JSON
                try {
                    // Crear un objeto JSONObject a partir de la respuesta
                    JSONObject jsonObject = new JSONObject(responseBody);

                    // Extraer los datos necesarios del JSON
                    String temperature = jsonObject.getString("temperature");
                    String humidity = jsonObject.getString("humidity");

                    // Devolver los datos en un objeto SensorData
                    return new SensorData(temperature, humidity);

                } catch (Exception e) {
                    // Manejo de excepciones si el JSON es inválido o las claves no existen
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("RequestDataTask", "Error de conexión: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(SensorData result) {

            if (result == null) {
                Toast.makeText(MainActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
            if (result != null && result.getTemperature() != null && result.getHumidity() != null) {
                dataList.add(result);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Datos inválidos recibidos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
