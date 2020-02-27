package com.example.noteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText textPassword;
    Encryption encryptionClass = new Encryption();
    private static final String FILE_NAME = "key.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textPassword = findViewById(R.id.textPassword);
    }

    private String readFile(String path)
    {
        File file = new File(path);
        String text = "";
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                text += line;
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private String readFromFile()
    {
        String text;
        String path = Environment.getExternalStorageDirectory() + "/hasło.txt";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions,1);
            }
            else
            {
                text = readFile(path);
                return text;
            }
        }
        else {
            text = readFile(path);
            return text; }
        return null;

    }

    private String getKey() {
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while((text = br.readLine()) != null) {
                sb.append(text);
            }
            return sb.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void nextAction(View v) {
        String inputPassword = textPassword.getEditableText().toString().trim();
        String password = readFromFile();
        String klucz = getKey();

        try {
            String decryptedPassword = encryptionClass.decrypt(password, klucz);
            if(inputPassword.equals(decryptedPassword)) {
                Toast.makeText(this, "Poprawne hasło!", Toast.LENGTH_SHORT).show();
                notePad();
            }
            else {
                textPassword.setError("Niepoprawne hasło!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            textPassword.setError("Niepoprawne hasło!");
        }
    }

    public void notePad() {
        Intent intent = new Intent(MainActivity.this, Notepad.class);
        startActivity(intent);
    }

    public void newPassword(View view) {
        Intent intent = new Intent(this, NewPassword.class);
        startActivity(intent);
    }
}
