package com.example.qr_scanner_generator;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class QRreader {

    public static String decodeQRImage(String path) {
        Bitmap initialBitmap = BitmapFactory.decodeFile(path);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(initialBitmap, 100,100,false);
        String decoded = null;

        int[] intArray = new int[resizedBitmap.getWidth() * resizedBitmap.getHeight()];
        resizedBitmap.getPixels(intArray, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(),
                resizedBitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(resizedBitmap.getWidth(),
                resizedBitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType,Object>  HINTS = new EnumMap<DecodeHintType,Object>(DecodeHintType.class);
        HINTS.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
        Map<DecodeHintType,Object> HINTS_PURE = new EnumMap<DecodeHintType,Object>(HINTS);
        HINTS_PURE.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE); // this effective
        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(binaryBitmap,HINTS);
            decoded = result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
        return decoded;
    }
}
