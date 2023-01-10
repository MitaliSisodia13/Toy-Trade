package com.example.toytrade.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toytrade.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity implements PaymentResultListener {

    private EditText name,phone,address,city;
    private Button confirm,confirm2;

    private String totamount = " ";
    private int tamnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totamount = getIntent().getStringExtra("Total Price");
        tamnt = Math.round(Float.parseFloat(totamount) * 100);

        name = (EditText) findViewById(R.id.shippment_name);
        phone = (EditText) findViewById(R.id.shippment_phone);
        address = (EditText) findViewById(R.id.shippment_address);
        city = (EditText) findViewById(R.id.shippment_city);
        confirm = (Button)findViewById(R.id.shippment_confirm);
        confirm2 = (Button)findViewById(R.id.shippment_confirm2);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                check();
            }
        });

        Checkout.preload(getApplicationContext());

        confirm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLink("https://commerce.coinbase.com/checkout/ad361455-8080-4eaa-85a4-1631718f6938");
            }
//            public void onClick(View v){
//                Intent intent = new Intent(ConfirmFinalOrderActivity.this, Coinbase.class);
//                startActivity(intent);
//            }

        });

    }

    public void goLink(String s){
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }



    private void check()
    {
        if(TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this , "Please provide Your Full Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phone.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this , "Please provide Your Phone Number", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this , "Please provide Your Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name.getText().toString())){
            Toast.makeText(ConfirmFinalOrderActivity.this , "Please provide Your City", Toast.LENGTH_SHORT).show();
        }
        else{
            makepayment();

        }
    }

    private void makepayment()
    {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_uF0qInUZD1BBFP");

        checkout.setImage(R.drawable.logo);


        final Activity activity = this;


        try {
            JSONObject options = new JSONObject();

            options.put("name", "Toy-Bazaar");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", tamnt);//pass amount in currency subunits
            options.put("prefill.email", "toy-Baazar@gmail.com");
            options.put("prefill.contact","9909018054");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }

    }

    private void confirmorder()
    {
        final String saveCurrentTime, saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("TotalAmount", totamount);
        ordersMap.put("Name", name.getText().toString());
        ordersMap.put("Phone", phone.getText().toString());
        ordersMap.put("Address", address.getText().toString());
        ordersMap.put("City", city.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("State", "Not Shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>(){

            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
               if(task.isSuccessful()){
                   FirebaseDatabase.getInstance().getReference().child("Cart List")
                           .child("User View")
                           .child(Prevalent.currentOnlineUser.getPhone())
                           .removeValue()
                           .addOnCompleteListener(new OnCompleteListener<Void>(){

                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful()){

                                       Toast.makeText(ConfirmFinalOrderActivity.this, "Your Final Order Has Been Placed Successfully", Toast.LENGTH_SHORT).show();
                                       Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                       startActivity(intent);
                                       finish();
                                   }
                               }
                           });
               }
            }
        });
    }


    @Override
    public void onPaymentSuccess(String s) {
//        Toast.makeText(ConfirmFinalOrderActivity.this, "Your Final Order Has Been Placed Successfully", Toast.LENGTH_SHORT).show();
        confirmorder();
        

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(ConfirmFinalOrderActivity.this, "Payment Failed, try again", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}