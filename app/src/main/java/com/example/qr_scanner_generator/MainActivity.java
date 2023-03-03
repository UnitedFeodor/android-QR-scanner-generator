package com.example.qr_scanner_generator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {

    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    private Button readQrBtn;
    private ImageView qrCodeIV;
    private EditText dataEdt;
    private Button generateQrBtn;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    final int PICKFILE_RESULT_CODE = 1488;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{READ_EXTERNAL_STORAGE},1);


        // initializing all variables.
        readQrBtn = findViewById(R.id.idBtnReadQR);
        qrCodeIV = findViewById(R.id.idIVQrcode);
        dataEdt = findViewById(R.id.idEdt);
        generateQrBtn = findViewById(R.id.idBtnGenerateQR);

        // initializing onclick listener for button.
        generateQrBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(dataEdt.getText().toString())) {
                // if the edittext inputs are empty then execute
                // this method showing a toast message.
                Toast.makeText(MainActivity.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
            } else {
                int width = 500;
                int height = 500;

                // generating dimension from width and height.
                int dimen = width < height ? width : height;
                dimen = dimen * 3 / 4;

                // setting this dimensions inside our qr code
                // encoder to generate our qr code.
                qrgEncoder = new QRGEncoder(dataEdt.getText().toString(), null, QRGContents.Type.TEXT, 1024);
                qrgEncoder.setColorBlack(Color.WHITE);
                qrgEncoder.setColorWhite(Color.BLACK);

                try {
                    // getting our qrcode in the form of bitmap.
                    bitmap = qrgEncoder.getBitmap();
                    // the bitmap is set inside our image
                    // view using .setimagebitmap method.
                    qrCodeIV.setImageBitmap(bitmap);
                } catch (Exception e) {
                    // this method is called for
                    // exception handling.
                    Log.e("Tag", e.toString());
                }
            }
        });

        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }

        readQrBtn.setOnClickListener(v -> {
            /*
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "content://media/internal/images/media"));
            startActivity(intent);*/
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            // Filter to only show results that can be "opened", such as a
            // file (as opposed to a list of contacts or timezones)
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            // Filter to show only images, using the image MIME data type.
            // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
            // To search for all documents available via installed storage providers,
            // it would be "*/*".
            intent.setType("image/*");
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File"), PICKFILE_RESULT_CODE);
            } catch (ActivityNotFoundException e) {
                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed
                    Toast.makeText(MainActivity.this, "Permission granted!!!", Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage :(", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "Trying to decode the QR...", Toast.LENGTH_SHORT).show();
                    //String filePath = data.getData().getPath();
                    String filePath = Utils.getActualPath(this, data.getData());


                    if(filePath.contains("document/raw:")){
                        filePath = filePath.replace("/document/raw:","");
                    }

                    // file path: /storage/emulated/0/Pictures/QR_code_for_mobile_English_Wikipedia.png
                    qrCodeIV.setImageBitmap(BitmapFactory.decodeFile(filePath));
                    String resDataText = QRreader.decodeQRImage(filePath);
                    if (resDataText == null) {
                        Toast.makeText(MainActivity.this, "QR not found!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "QR decoded!", Toast.LENGTH_SHORT).show();
                    }
                    dataEdt.setText(resDataText);

                }
        }

    }
}
