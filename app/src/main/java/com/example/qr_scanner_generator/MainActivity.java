package com.example.qr_scanner_generator;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                Toast.makeText(MainActivity.this, "HELLLOOAODOASDA", Toast.LENGTH_SHORT).show();
                if (resultCode == RESULT_OK) {
                    String FilePath = data.getData().getPath();
                    //FilePath is your file as a string
                }
        }

    }
}
