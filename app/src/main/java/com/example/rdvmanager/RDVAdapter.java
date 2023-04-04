package com.example.rdvmanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RDVAdapter extends RecyclerView.Adapter<RDVAdapter.ViewHolder> {
    private List<RDV> rdvList;
    private OnItemClickListener listener;

    public RDVAdapter(List<RDV> rdvList, OnItemClickListener listener) {
        this.rdvList = rdvList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rdv, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RDV rdv = rdvList.get(position);
        holder.dateTextView.setText(rdv.getDate());
        holder.timeTextView.setText(rdv.getTime());
        holder.descriptionTextView.setText(rdv.getDescription());

        // Set onClickListener for editing an RDV
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditRDVActivity.class);
            intent.putExtra("RDV_ID", rdv.getId());
            context.startActivity(intent);
        });

        // Set onLongClickListener for deleting an RDV
        holder.itemView.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Do you want to delete this RDV?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        // Delete RDV from database
                        RDVDAO.deleteRDV(rdv.getId());

                        // Remove RDV from RecyclerView
                        rdvs.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, rdvs.size());
                    })
                    .setNegativeButton("No", null);
            builder.create().show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return rdvs.size();
    }

    public void updateRDVs(List<RDV> rdvList) {
        this.rdvList = rdvList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private TextView contactTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
        }

        public void bind(final RDV rdv, final OnItemClickListener listener) {
            titleTextView.setText(rdv.getTitle());
            dateTextView.setText(rdv.getDate());
            timeTextView.setText(rdv.getTime());
            contactTextView.setText(rdv.getContact());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(rdv);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RDV rdv);
        void onLocationButtonClick(RDV rdv);
        void onPhoneButtonClick(RDV rdv);
        void onShareButtonClick(RDV rdv);
    }
}

