package com.example.androidbaberstaffapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.androidbaberstaffapp.Common.Common;
import com.example.androidbaberstaffapp.Common.CustomLoginDialog;
import com.example.androidbaberstaffapp.Interface.IDialogClickListener;
import com.example.androidbaberstaffapp.Interface.IGetBarberListener;
import com.example.androidbaberstaffapp.Interface.IRecyclerItemSelectedListener;
import com.example.androidbaberstaffapp.Interface.IUserLoginRemeberListener;
import com.example.androidbaberstaffapp.Model.Barber;
import com.example.androidbaberstaffapp.Model.Salon;
import com.example.androidbaberstaffapp.R;
import com.example.androidbaberstaffapp.StaffHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.MyViewHolder> implements IDialogClickListener {
    Context context;
    List<Salon> salonList;
    List<CardView> itemViewList;

    IUserLoginRemeberListener iUserLoginRemeberListener;
    IGetBarberListener iGetBarberListener;


    public MySalonAdapter(Context context, List<Salon> salonList, IUserLoginRemeberListener iUserLoginRemeberListener, IGetBarberListener iGetBarberListener) {
        this.context = context;
        this.salonList = salonList;
        itemViewList = new ArrayList<>();
        this.iGetBarberListener = iGetBarberListener;
        this.iUserLoginRemeberListener = iUserLoginRemeberListener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_salon, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_salon_name.setText(salonList.get(position).getName());
        holder.txt_salon_address.setText(salonList.get(position).getAddress());

        if (!itemViewList.contains(holder.card_salon))
            itemViewList.add(holder.card_salon);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                Common.selected_salon = salonList.get(position);
                showLoginDiaLog();
            }

        });
    }

    private void showLoginDiaLog() {
        CustomLoginDialog.getInstance()
                .showLoginDialog("STAFF LOGIN",
                        "LOGIN",
                        "CANCEL",
                        context,
                        this);
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    @Override
    public void onClickPositiveButton(DialogInterface dialogInterface, String userName, String password) {
        AlertDialog loading = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();

        loading.show();

        ///AllSalon/HaNoi/Branch/69yELSVrUXtMOZeO7gZ6/Barbers/tYFB4pZ7NLIuj8eCWUty
        FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selected_salon.getSalonId())
                .collection("Barbers")
                .whereEqualTo("username", userName)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                dialogInterface.dismiss();

                                loading.dismiss();

                                iUserLoginRemeberListener.onUserLoginSuccess(userName);

                                //create Barber
                                Barber barber = new Barber();
                                for(DocumentSnapshot barberSnapshot: task.getResult())
                                {
                                    barber = barberSnapshot.toObject(Barber.class);
                                    barber.setBarberId(barberSnapshot.getId());
                                }
                                iGetBarberListener.onGetBarberSuccess(barber);


                                Intent staffHome = new Intent(context, StaffHomeActivity.class);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                staffHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(staffHome);
                            } else {
                                loading.dismiss();
                                Toast.makeText(context, "nhap user/pass", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClickNegativeButton(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView card_salon;
        TextView txt_salon_name, txt_salon_address;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_salon = (CardView) itemView.findViewById(R.id.card_salon);
            txt_salon_address = (TextView) itemView.findViewById(R.id.txt_salon_address);
            txt_salon_name = (TextView) itemView.findViewById(R.id.txt_salon_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelected(v, getAdapterPosition());
        }
    }
}
