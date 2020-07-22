package com.example.handsomeboy.e_sms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Display extends Activity {

    TextView num, cipher;
    EditText key;
    Button submit;
    private String code, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);

        num = (TextView) findViewById(R.id.senderPhone);
        key = (EditText) findViewById(R.id.secretKey);
        cipher = (TextView) findViewById(R.id.txtCtext);
        submit = (Button) findViewById(R.id.submitBtn);

        SharedPreferences memory = getSharedPreferences("code", MODE_PRIVATE);
        code = memory.getString("code", null);
        from = memory.getString("from", null);
        Log.d("code", "from is : " + from + "\n code: " + code);

        //set the message received and the sender number to the text fields
        num.setText(from);
        cipher.setText(code);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String secretKeyString = key.getText().toString();
                cipher.setText(cipher.getText().toString() + "\n");

                if (secretKeyString.length() == 16) {
                    try {
                        cipher.setText(decrypt(code, secretKeyString));
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Wrong key entered, Enter the correct key and try agein",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else Toast.makeText(getApplicationContext(), "Key length must be 16 characters long",
                        Toast.LENGTH_LONG).show();


            }
        });

    }

    private String decrypt(String outputString, String password) throws Exception {
        SecretKeySpec key= MainActivity.generateKey(password);
        Cipher c= Cipher.getInstance(MainActivity.AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedVal= Base64.decode(outputString, Base64.DEFAULT);
        byte[] decVal= c.doFinal(decodedVal);
        String decryptedVal = new String(decVal);
        return decryptedVal;
    }

}