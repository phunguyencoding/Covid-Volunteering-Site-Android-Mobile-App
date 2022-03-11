package com.example.cosc2657_a2_s3811248_nguyentranphu;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreateAdapter extends RecyclerView.Adapter<CreateAdapter.ViewHolder> {
    private Context context;
    private List<SiteModel> data;
    FireBaseHandler fireBaseHandler = new FireBaseHandler();

    public CreateAdapter(Context context, List<SiteModel> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Set variables for the main activity
        holder.location.setText("" + data.get(position).getLocation());
        holder.thumbnail.setImageResource(data.get(position).getThumbnail());
        holder.host.setText("" + data.get(position).getHost());
        holder.date.setText("" + data.get(position).getDate());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_update,null);
                TextView host = view.findViewById(R.id.host);
                TextView location = view.findViewById(R.id.locationTextInputEditText);
                TextView date = view.findViewById(R.id.dateTextInputEditText);
                EditText testedPeople = view.findViewById(R.id.tested_people);
                TextView participants = view.findViewById(R.id.participantList);
                TextView title = view.findViewById(R.id.title);
                Button removeBtn = view.findViewById(R.id.remove_button);
                Button updateBtn = view.findViewById(R.id.update_button);

                host.setText(holder.host.getText());
                location.setText(holder.location.getText());
                date.setText(holder.date.getText());
                testedPeople.setText(String.valueOf(data.get(position).getTestedPeople()));
                title.setText("Site Covid Info");

                StringBuilder stringBuilder = new StringBuilder("");
                for (int i = 0; i < data.get(position).getParticipants().size(); i++) {
                    stringBuilder.append(data.get(position).getParticipants().get(i) + "\n");
                }
                if (!stringBuilder.equals("")) {
                    participants.setText(stringBuilder);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);
                AlertDialog alertDialog = builder.create();

                updateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String people =testedPeople.getText().toString();
                        fireBaseHandler.updateSiteTestedPeople(data.get(position).getLocation(), Integer.parseInt(people));
                        data.get(position).setTestedPeople(Integer.parseInt(people));
                        alertDialog.dismiss();
                    }
                });

                removeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fireBaseHandler.deleteSite(data.get(position).getLocation());
                        data.remove(position);
                        notifyItemRemoved(position);
                        alertDialog.dismiss();
                    }
                });

                if(alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }

                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(int position, int testedPeople) {
        data.get(position).setTestedPeople(testedPeople);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        //Appear on card view
        TextView location;
        CardView cardView;
        ImageView thumbnail;
        TextView date;
        TextView host;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = (TextView) itemView.findViewById(R.id.locationTextInputEditText);
            date = (TextView) itemView.findViewById(R.id.dateTextInputEditText);
            host = (TextView) itemView.findViewById(R.id.host);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }

}
