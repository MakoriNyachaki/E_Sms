package com.example.handsomeboy.e_sms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    EditText no, key, msg;
    public static String AES="AES";
    Button send, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        no=(EditText)findViewById(R.id.edtNum);
        key=(EditText)findViewById(R.id.edtKey);
        msg=(EditText)findViewById(R.id.edtMsg);
        send=(Button)findViewById(R.id.btnSend);
        cancel=(Button) findViewById(R.id.btnCancel);

        SharedPreferences prefs = getSharedPreferences("code", MODE_PRIVATE);
        if (prefs.getString("from", null) != null){
            Intent intent= new Intent(this, Display.class);
            startActivity(intent);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get data from the fields

                String phoneNo = no.getText().toString();
                String sms = msg.getText().toString();
                String sKey= key.getText().toString();
                String outputString = "";

                //test the fields are null

                if (sKey.length()>0 && sKey.length()==16 && msg.length() != 0){
                    try {
                        //encrypt

                        outputString = encrypt(sms, sKey);
                        //send

                        sendingMessage(phoneNo, outputString);

                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), "Enter all the required fields, they are required.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(getApplicationContext(), "Enter the text or a 16 length key and try again!",
                        Toast.LENGTH_LONG).show();


            }});
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });


    }


    private String encrypt(String Text, String password) throws Exception {
        SecretKeySpec key= generateKey(password);
        Cipher c= Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptText = c.doFinal(Text.getBytes());
        String encryptedText = Base64.encodeToString(encryptText, Base64.DEFAULT);
        return encryptedText;
    }

    public static SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec= new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


    private void sendingMessage(String phoneNo, String sms){

        if(phoneNo.length()>0 && sms.length() !=0){

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                Toast.makeText(getApplicationContext(), "Message sent successfully",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Message did not send. Try again!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }else {
            Toast.makeText(getApplication(), "Enter the phone number or key, or message",
                    Toast.LENGTH_LONG).show();
        }
    }

}
