package com.example.rdvmanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        RDV rdv = rdvList.get(holder.getAdapterPosition());
        holder.dateTextView.setText(rdv.getDate());

        // Set onClickListener for editing an RDV
        Context context = holder.itemView.getContext();;
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, EditRDVActivity.class);
            intent.putExtra("RDV_ID", rdv.getId());
            context.startActivity(intent);
        });

        // Set onLongClickListener for deleting an RDV
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation de suppression");
                builder.setMessage("Êtes-vous sûr de vouloir supprimer ce RDV ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // supprimer le RDV de la base de données et de la liste
                        RDVDAO rdvDAO = new RDVDAO(context);
                        rdvDAO.deleteRDV(rdv);
                        rdvList.remove(position);
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return rdvList.size();
    }

    public void updateRDVs(List<RDV> rdvList) {
        this.rdvList = rdvList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView dateTextView;
        private TextView contactTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.textview_rdv_title);
            dateTextView = itemView.findViewById(R.id.textview_rdv_datetime);
            contactTextView = itemView.findViewById(R.id.textview_rdv_contact);
        }

        public void bind(final RDV rdv, final OnItemClickListener listener) {
            titleTextView.setText(rdv.getTitle());
            dateTextView.setText(rdv.getDate());
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

