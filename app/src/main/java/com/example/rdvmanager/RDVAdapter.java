package com.example.rdvmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RDVAdapter extends BaseAdapter {
    private final List<RDV> rdvList;
    private final Context context;
    private final LayoutInflater inflater;

    public RDVAdapter(Context context, List<RDV> rdvList) {
        this.context = context;
        this.rdvList = rdvList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rdvList.size();
    }

    @Override
    public Object getItem(int position) {
        return rdvList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_rdv, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RDV rdv = rdvList.get(position);

        String[] timeParts = rdv.getTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        String formattedTime = String.format("%02d:%02d", hour, minute);
        rdv.setTime(formattedTime);

        viewHolder.bind(rdv);

        return convertView;
    }


    private class ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;

        private final View statusIndicator;
        private final TextView contactTextView;
        private final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            titleTextView = itemView.findViewById(R.id.textview_rdv_title);
            dateTextView = itemView.findViewById(R.id.textview_rdv_datetime);
            contactTextView = itemView.findViewById(R.id.textview_rdv_contact);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        public void bind(final RDV rdv) {
            titleTextView.setText(rdv.getTitle());
            dateTextView.setText(rdv.getDate() +" "+rdv.getTime());
            contactTextView.setText(rdv.getContact());

            if (rdv.isDone()) {
                statusIndicator.setBackgroundColor(Color.GREEN);
            } else {
                statusIndicator.setBackgroundColor(Color.CYAN);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditRDVActivity.class);
                intent.putExtra("rdv_Id", rdv.getId());
                context.startActivity(intent);
            });

            itemView.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirmation de suppression");
                builder.setMessage("Etes-vous sûr de vouloir supprimer ce RDV ?");
                builder.setPositiveButton("Oui", (dialog, which) -> {
                    // supprimer le RDV de la base de données et de la liste
                    RDVDAO rdvDAO = new RDVDAO(context);
                    rdvDAO.open();
                    rdvDAO.deleteRDV(rdv);
                    rdvDAO.close();
                    rdvList.remove(rdv);
                    notifyDataSetChanged();
                });
                builder.setNegativeButton("Non", null);
                builder.show();
                return true;
            });
        }
    }


}