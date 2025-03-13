package mx.unam.fciencias.moviles.sensores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private final List<SensorData> dataList;

        public DataAdapter(List<SensorData> dataList) {

            this.dataList = dataList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sensor_data, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SensorData data = dataList.get(position);

            String temperature = data.getTemperature();
            String humidity = data.getHumidity();

            String temperatureText = holder.itemView.getContext().getString(R.string.temperature, temperature);
            String humidityText = holder.itemView.getContext().getString(R.string.humidity, humidity);

            holder.temperatureText.setText(temperatureText);
            holder.humidityText.setText(humidityText);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView humidityText, temperatureText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                humidityText = itemView.findViewById(R.id.humidityText);
                temperatureText = itemView.findViewById(R.id.temperatureText);
            }
        }
}
