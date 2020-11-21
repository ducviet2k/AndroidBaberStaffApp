package com.example.androidbaberstaffapp.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbaberstaffapp.Adapter.MyConfirmShoppingItemAdapter;
import com.example.androidbaberstaffapp.Common.Common;
import com.example.androidbaberstaffapp.Model.BarberServices;
import com.example.androidbaberstaffapp.Model.CartItem;
import com.example.androidbaberstaffapp.Model.EventBus.DissmissFromBottomSheetEvent;
import com.example.androidbaberstaffapp.Model.FCMResponse;
import com.example.androidbaberstaffapp.Model.FCMSenData;
import com.example.androidbaberstaffapp.Model.Invoice;
import com.example.androidbaberstaffapp.Model.MyToken;
import com.example.androidbaberstaffapp.R;
import com.example.androidbaberstaffapp.Retrofit.IFCMService;
import com.example.androidbaberstaffapp.Retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TotalPriceFragment extends BottomSheetDialogFragment {
    Unbinder unbinder;

    @BindView(R.id.chip_group_services)
    ChipGroup chip_group_service;

    @BindView(R.id.recycler_view_shopping)
    RecyclerView recycler_view_shopping;

    @BindView(R.id.txt_salon_name)
    TextView txt_salon_name;

    @BindView(R.id.txt_baber_name)
    TextView txt_baber_name;

    @BindView(R.id.txt_time)
    TextView txt_time;

    @BindView(R.id.txt_total_price)
    TextView txt_total_price;

    @BindView(R.id.txt_customer_name)
    TextView txt_customer_name;

    @BindView(R.id.txt_customer_phone)
    TextView txt_customer_phone;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    HashSet<BarberServices> servicesAdded;
    //List<ShoppingItem> shoppingItemList;

    IFCMService ifcmService;
//    IBottomSheetDialogOnDismissListener iBottomSheetDialogOnDismissListener;

    AlertDialog dialog;
    String image_url;

    private static TotalPriceFragment instance;


    public static TotalPriceFragment getInstance() {
        return instance == null ? new TotalPriceFragment() : instance;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_total_price, container, false);
        unbinder = ButterKnife.bind(this, itemView);

        init();

        initView();

        getBundle(getArguments());

        setInFormation();
        return itemView;
    }

    private void setInFormation() {
        txt_salon_name.setText(Common.selected_salon.getName());
        txt_baber_name.setText(Common.currentBarber.getName());
        txt_time.setText(Common.convertTimeSlotToString(Common.currentBookinginformation.getSlot().intValue()));
        txt_customer_name.setText(Common.currentBookinginformation.getCustomerName());
        txt_customer_phone.setText(Common.currentBookinginformation.getCustomerPhone());

        if (servicesAdded.size() > 0) {
            // add to chip group
            int i = 0;
            for (BarberServices services : servicesAdded) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_item, null);
                chip.setText(services.getName());
                chip.setTag(i);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        servicesAdded.remove((int) view.getTag());
                        chip_group_service.removeView(view);

                        calculatePrice();
                    }
                });
                chip_group_service.addView(chip);
                i++;
            }
        }

        if (Common.currentBookinginformation.getCartItemList()!=null)
        {
            if (Common.currentBookinginformation.getCartItemList().size() > 0) {
                MyConfirmShoppingItemAdapter adapter = new MyConfirmShoppingItemAdapter(getContext(),Common.currentBookinginformation.getCartItemList());
                recycler_view_shopping.setAdapter(adapter);
            }
            calculatePrice();
        }
    }

    private double calculatePrice() {
        double price = Common.DEFAULT_PRICE;
        for (BarberServices services : servicesAdded)
            price += services.getPrice();

        if (Common.currentBookinginformation.getCartItemList() != null)
        {
            for (CartItem cartItem: Common.currentBookinginformation.getCartItemList())
                price += (cartItem.getProductPrice()*cartItem.getProductQuantity());
        }

        txt_total_price.setText(new StringBuilder(Common.MONEY_SIGN)
                .append(price));
        return price;
    }


    private void getBundle(Bundle arguments) {
        this.servicesAdded = new Gson()
                .fromJson(arguments.getString(Common.SERVICES_ADDED),
                        new TypeToken<HashSet<BarberServices>>() {
                        }.getType());

//        this.shoppingItemList = new Gson()
//                .fromJson(arguments.getString(Common.SHOPPING_LIST),
//                        new TypeToken<List<ShoppingItem>>() {
//                        }.getType());

        image_url = arguments.getString(Common.IMAGE_DOWNLOADABLE_URL);

    }

    private void initView() {
        recycler_view_shopping.setHasFixedSize(true);
        recycler_view_shopping.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//part25
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                // Update BookingInformation , set done =true
                final DocumentReference bookingSet = FirebaseFirestore.getInstance()
                        .collection("AllSalon")
                        .document(Common.state_name)
                        .collection("Branch")
                        .document(Common.selected_salon.getSalonId())
                        .collection("Barbers")
                        .document(Common.currentBarber.getBarberId())
                        .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                        .document(Common.currentBookinginformation.getBookingId());

                bookingSet.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        // Update
                                        Map<String, Object> dataUpdate = new HashMap<>();
                                        dataUpdate.put("done", true);
                                        bookingSet.update(dataUpdate)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            // If update is done, create invoice
                                                            createInvoice();

                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    //prart 25


    private void createInvoice() {
        ///AllSalon/HaNoi/Branch/69yELSVrUXtMOZeO7gZ6/Services
        CollectionReference invoiceRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.state_name)
                .collection("Branch")
                .document(Common.selected_salon.getSalonId())
                .collection("Invoices");

        Invoice invoice = new Invoice();
        invoice.setBarberId(Common.currentBarber.getBarberId());
        invoice.setBarberName(Common.currentBarber.getName());

        invoice.setSalonId(Common.selected_salon.getSalonId());
        invoice.setSalonName(Common.selected_salon.getName());
        invoice.setSalonAddress(Common.selected_salon.getAddress());

        invoice.setCustomerName(Common.currentBookinginformation.getCustomerName());
        invoice.setCustomerPhone(Common.currentBookinginformation.getCustomerPhone());

        invoice.setImageUrl(image_url);
        invoice.setBarberServices(new ArrayList<BarberServices>(servicesAdded));
        invoice.setShoppingItemList(Common.currentBookinginformation.getCartItemList());
        invoice.setFinalPrice(calculatePrice());

        invoiceRef.document()
                .set(invoice)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendNotificationUpdateToUser(Common.currentBookinginformation.getCustomerPhone());

                }
            }
        });


    }

    //part25
    private void sendNotificationUpdateToUser(String customerPhone) {
        //getToken of user
        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .whereEqualTo("userPhone", customerPhone)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0) {
                            MyToken myToken = new MyToken();
                            for (DocumentSnapshot tokenSnapshot : task.getResult())
                                myToken = tokenSnapshot.toObject(MyToken.class);


                            //khoi tao notification

                            FCMSenData fcmSenData = new FCMSenData();
                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("update_done", "true");
                            //infomation need  for
                            dataSend.put(Common.RATING_STATE_KEY,Common.state_name);
                            dataSend.put(Common.RATING_SALON_ID,Common.selected_salon.getSalonId());
                            dataSend.put(Common.RATING_SALON_NAME,Common.selected_salon.getName());
                            dataSend.put(Common.RATING_BARBER_ID,Common.currentBarber.getBarberId());


                            fcmSenData.setTo(myToken.getToken());
                            fcmSenData.setData(dataSend);

                            ifcmService.senNotification(fcmSenData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {
                                            dialog.dismiss();
                                            dismiss();
                                            EventBus.getDefault()
                                                    .postSticky(new DissmissFromBottomSheetEvent(true));
//                                            iBottomSheetDialogOnDismissListener.onDismissBottomSheetDialog(true);
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(getContext())
                .setCancelable(false).build();
        ifcmService = RetrofitClient.getInstance().create(IFCMService.class);
    }
}

