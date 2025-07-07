package com.orcapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.camera.core.Preview;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;


public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageButton btnEscanear, btnSeleccionarImagen, btnFlash;
    private ExecutorService cameraExecutor;
    private boolean escaneando = false;
    private Camera camera;
    private boolean flashEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setControls();
        setEvents();
        requestPermissions();
    }

    // Inicializar controles
    private void setControls() {
        previewView = findViewById(R.id.previewView);
        btnEscanear = findViewById(R.id.btnEscanear);
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnFlash = findViewById(R.id.btnFlash);
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    // Eventos de los botones
    public void setEvents() {
        btnEscanear.setOnClickListener(v -> scanTextLive());

        btnSeleccionarImagen.setOnClickListener(v -> seleccionarImagen());

        btnFlash.setOnClickListener(v -> {
            if (camera != null) {
                flashEnabled = !flashEnabled;
                camera.getCameraControl().enableTorch(flashEnabled);

                // Alternar icono de encendido y apagado
                if (flashEnabled) {
                    btnFlash.setImageResource(R.drawable.ic_flash_on);
                } else {
                    btnFlash.setImageResource(R.drawable.ic_flash_off);
                }
            }
        });

    }

    // Permisos necesarios
    public void requestPermissions() {
        ActivityResultLauncher<String[]> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Boolean cameraGranted = result.getOrDefault(Manifest.permission.CAMERA, false);
                    if (cameraGranted != null && cameraGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show();
                    }
                });

        requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
    }

    // Iniciar cámara en tiempo real
    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder().build();
                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysis);

            } catch (Exception e) {
                Log.e("CameraX", "Error al iniciar cámara", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Análisis desde la cámara (solo si escaneando = true)
    @OptIn(markerClass = ExperimentalGetImage.class)
    public void analyzeImage(@NonNull ImageProxy imageProxy) {
        if (!escaneando) {
            imageProxy.close();
            return;
        }
        //EL ML kit escanea de izquierda a derecha y note que si giro la
        //imagen 90° a la izquierda(osea 270° en sentido normal) la lectura
        //es mas precisa gracias a que lee filas de texto rectas dando un mejor resultado
        @SuppressWarnings("UnsafeOptInUsageError")
        int rotacion = (imageProxy.getImageInfo().getRotationDegrees() + 90) % 360;
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), rotacion);

        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(result -> {
                    String texto = result.getText();
                    imageProxy.close();
                    escaneando = false;

                    if (!texto.isEmpty()) {
                        abrirResultado(texto);
                    } else {
                        Toast.makeText(this, "No se detectó texto", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OCR", "Fallo OCR", e);
                    imageProxy.close();
                    escaneando = false;
                });
    }

    // Activar OCR al presionar botón
    public void scanTextLive() {
        escaneando = true;
        Toast.makeText(this, "Escaneando texto...", Toast.LENGTH_SHORT).show();
    }

    // Lanzador para seleccionar imagen de galería
    public final ActivityResultLauncher<Intent> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imagenUri = result.getData().getData();
                    try {
                        // Obtener bitmap de la imagen
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);

                        //Rotar 90° a la izquierda (270° en sentido horario)
                        //EL ML kit escanea de izquierda a derecha y note que si giro la
                        //imagen 90° a la izquierda(osea 270° en sentido normal) la lectura
                        // es mas precisa gracias a que lee filas de texto rectas dando un mejor resultado
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(
                                bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight(),
                                matrix, true
                        );

                        // Convertir a InputImage con rotación ya aplicada
                        InputImage image = InputImage.fromBitmap(rotatedBitmap, 0);
                        procesarImagenOCR(image);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al leer la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    // Abrir archivos para buscar imagen
    public void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        galeriaLauncher.launch(Intent.createChooser(intent, "Selecciona una imagen"));
    }

    // OCR desde imagen de galería
    public void procesarImagenOCR(InputImage image) {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(result -> {
                    String texto = result.getText();
                    if (!texto.isEmpty()) {
                        abrirResultado(texto);
                    } else {
                        Toast.makeText(this, "No se detectó texto en la imagen", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("OCR-Galeria", "Fallo OCR", e);
                    Toast.makeText(this, "Fallo al procesar imagen", Toast.LENGTH_SHORT).show();
                });
    }

    // Ir a ResultadoActivity con texto detectado
    public void abrirResultado(String texto) {
        Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
        intent.putExtra("textoDetectado", texto);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
