package robfernandes.xyz.go4lunch.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

import robfernandes.xyz.go4lunch.R;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

    private List<AutocompletePrediction> autocompletePredictionList;
    private OnAutoCompleteItemClickListener onAutoCompleteItemClickListener = null;

    public AutocompleteAdapter(List<AutocompletePrediction> autocompletePredictionList) {
        this.autocompletePredictionList = autocompletePredictionList;
    }

    public void setAutocompletePredictionList(List<AutocompletePrediction> autocompletePredictionList) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textViewDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.autocomplete_item_text_view);
            textViewDesc = itemView.findViewById(R.id.autocomplete_item_text_view_desc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAutoCompleteItemClickListener != null) {
                        AutocompletePrediction autocompletePrediction = autocompletePredictionList
                                .get(getAdapterPosition());
                        onAutoCompleteItemClickListener.onLocationClicked(autocompletePrediction);
                    }
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
