package ru.alexander.homestorage.utils;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import ru.alexander.homestorage.R;
import ru.alexander.homestorage.services.ProfileManager;

public class QR {
    private static FirebaseAuth auth;

    public static void init(FirebaseAuth auth) {
        QR.auth = auth;
    }
    public static ActivityResultLauncher<Intent> register(Fragment fragment) {
       return fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if(result.getData() == null || result.getResultCode() == RESULT_CANCELED){
                        Toast.makeText(fragment.getContext(), "QR code reading error!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            ProfileManager.Path path = ProfileManager.transfer(
                                    result.getData().getStringExtra("SCAN_RESULT").getBytes(StandardCharsets.UTF_8),
                                    fragment.getContext()
                            );
                            if (path == null) return;
                            View view = fragment.getView();
                            if (path.getItem() != null) {
                                Bundle args = new Bundle();
                                args.putSerializable("storage_system", path.getSystem());
                                args.putSerializable("storage", path.getStorage());
                                args.putSerializable("item", path.getItem());
                                args.putString("user", auth.getCurrentUser().getUid());

                                Navigation.findNavController(view).navigate(R.id.itemFragment, args);
                            } else if (path.getStorage() != null) {
                                Bundle args = new Bundle();
                                args.putSerializable("storage_system", path.getSystem());
                                args.putSerializable("storage", path.getStorage());
                                args.putString("user", auth.getCurrentUser().getUid());

                                Navigation.findNavController(view).navigate(R.id.storageFragment, args);
                            } else if (path.getSystem() != null) {
                                Bundle args = new Bundle();
                                args.putSerializable("storage_system", path.getSystem());
                                args.putString("user", auth.getCurrentUser().getUid());

                                Navigation.findNavController(view).navigate(R.id.storageSystemFragment, args);
                            }
                        } catch (DataFormatException e) {
                            Toast.makeText(fragment.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public static void generate(String fileName, String data) {
        try {
            ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 0, 0, hints);


            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File qrCodeFile = new File(downloadsDirectory, fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(qrCodeFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}
