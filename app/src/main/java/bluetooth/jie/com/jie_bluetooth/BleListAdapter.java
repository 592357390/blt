package bluetooth.jie.com.jie_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BleListAdapter extends RecyclerView.Adapter<BleListAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    Context mcontext;
    private ArrayList<BluetoothDevice> data = new ArrayList<>();
    private ArrayList<String> connectAdds = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public BleListAdapter(Context context) {
        this.mcontext = context;
        layoutInflater = LayoutInflater.from(mcontext);
    }

    public void setConnectAdds(String connectAdds) {
        this.connectAdds.add(connectAdds);
        notifyDataSetChanged();
    }

    public ArrayList<String> getConnectAdds() {
        return connectAdds;
    }

    public ArrayList<BluetoothDevice> getData() {
        return data;
    }

    public void setData(BluetoothDevice device) {
        if (!data.contains(device)) {
            data.add(device);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_device, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BluetoothDevice bluetoothDevice = data.get(i);
        viewHolder.deviceName.setText(bluetoothDevice.getName());
        if (!connectAdds.contains(bluetoothDevice.getAddress())) {
            viewHolder.stateTxt.setText("未连接");
            viewHolder.stateImg.setImageResource(R.mipmap.ic_un_plane);
        } else {
            viewHolder.stateTxt.setText("已连接");
            viewHolder.stateImg.setImageResource(R.mipmap.ic_plane);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView stateTxt;
        ImageView stateImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            stateTxt = itemView.findViewById(R.id.device_state_txt);
            stateImg = itemView.findViewById(R.id.device_state_img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
