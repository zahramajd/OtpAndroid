package ir.taban.otp.ui;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ir.taban.otp.R;
import ir.taban.otp.api.User;

public class MainRecycleViewAdapter extends RecyclerView.Adapter<MainRecycleViewAdapter.DataObjectHolder> {

    private ArrayList<User> mDataset;


    public static class DataObjectHolder extends RecyclerView.ViewHolder {

        RelativeLayout ll;
        AutoResizeTextView secret_key;
        AutoResizeTextView address;

        public TextView txtProgress;
        public ProgressBar progressBar;

        public DataObjectHolder(View itemView) {
            super(itemView);
            ll = (RelativeLayout) itemView.findViewById(R.id.llrow);
            secret_key = (AutoResizeTextView) itemView.findViewById(R.id.secret_key);
            address = (AutoResizeTextView) itemView.findViewById(R.id.address_);
            txtProgress = (TextView) itemView.findViewById(R.id.txtProgress);
            progressBar = (ProgressBar) itemView.findViewById(R.id.final_progressBar);
        }
    }


    public MainRecycleViewAdapter(ArrayList<User> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_item, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {
        holder.ll.getLayoutParams().height = 250;
        holder.address.setText(mDataset.get(position).getEmail());
        holder.secret_key.setText(mDataset.get(position).getCurrentOTP());
        int timertest = mDataset.get(position).getRemaining();
        holder.progressBar.setProgress(timertest);
        holder.txtProgress.setText(timertest + "");
    }


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
