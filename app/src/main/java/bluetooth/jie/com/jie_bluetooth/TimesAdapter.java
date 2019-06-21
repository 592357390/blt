package bluetooth.jie.com.jie_bluetooth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    Context mcontext;
    private ArrayList<Integer> data = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public TimesAdapter(Context context) {
        this.mcontext = context;
        layoutInflater = LayoutInflater.from(mcontext);
        data.add(1);
        data.add(2);
        data.add(3);
        data.add(5);
        data.add(10);
    }

    public void setData(Integer times) {
        data.add(times);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_times, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Integer integer = data.get(i);
        viewHolder.times_name.setText(integer + "s/次");

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int data);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView times_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            times_name = itemView.findViewById(R.id.times_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(data.get(getLayoutPosition()));
                    }
                }
            });
        }
    }
}
