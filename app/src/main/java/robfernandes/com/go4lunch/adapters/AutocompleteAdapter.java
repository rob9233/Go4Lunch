package robfernandes.com.go4lunch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

import robfernandes.com.go4lunch.R;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

    private List<AutocompletePrediction> autocompletePredictionList;
    private OnAutoCompleteItemClickListener onAutoCompleteItemClickListener = null;

    public AutocompleteAdapter(List<AutocompletePrediction> autocompletePredictionList) {
        this.autocompletePredictionList = autocompletePredictionList;
    }

    public void setAutocompletePredictionList(List<AutocompletePrediction>
                                                      autocompletePredictionList) {
        this.autocompletePredictionList = autocompletePredictionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.autocomplete_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        AutocompletePrediction autocompletePrediction = autocompletePredictionList.get(i);
        viewHolder.textView.setText(autocompletePrediction
                .getPrimaryText(null).toString());
        viewHolder.textViewDesc.setText(autocompletePrediction
                .getSecondaryText(null).toString());
    }

    @Override
    public int getItemCount() {
        if (autocompletePredictionList != null) {
            return autocompletePredictionList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textViewDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.autocomplete_item_text_view);
            textViewDesc = itemView.findViewById(R.id.autocomplete_item_text_view_desc);

            itemView.setOnClickListener(v -> {
                if (onAutoCompleteItemClickListener != null) {
                    AutocompletePrediction autocompletePrediction = autocompletePredictionList
                            .get(getAdapterPosition());
                    onAutoCompleteItemClickListener.onLocationClicked(autocompletePrediction);
                }
            });
        }
    }

    public void setOnAutoCompleteItemClickListener(OnAutoCompleteItemClickListener listener) {
        this.onAutoCompleteItemClickListener = listener;
    }

    public interface OnAutoCompleteItemClickListener {
        void onLocationClicked(AutocompletePrediction autocompletePrediction);
    }
}
