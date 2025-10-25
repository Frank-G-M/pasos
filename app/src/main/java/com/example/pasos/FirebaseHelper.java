package com.example.pasos;

import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FirebaseHelper {
    private FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public interface FirestoreCallback {
        void onCallback(int steps);
        void onError(Exception e);
    }

    public interface WeeklyDataCallback {
        void onCallback(int[] weeklySteps);
        void onError(Exception e);
    }

    // Guardar pasos del día actual
    public void saveDailySteps(int steps, FirestoreCallback callback) {
        String today = getCurrentDate();
        Map<String, Object> stepData = new HashMap<>();
        stepData.put("date", today);
        stepData.put("steps", steps);
        stepData.put("timestamp", new Date());

        // Usamos la fecha como ID del documento (solo un usuario)
        db.collection("daily_steps")
                .document(today)  // Solo la fecha como ID
                .set(stepData)
                .addOnSuccessListener(aVoid -> {
                    if (callback != null) callback.onCallback(steps);
                })
                .addOnFailureListener(e -> {
                    if (callback != null) callback.onError(e);
                });
    }

    // Obtener pasos del día actual
    public void getTodaySteps(FirestoreCallback callback) {
        String today = getCurrentDate();

        db.collection("daily_steps")
                .document(today)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            Long steps = task.getResult().getLong("steps");
                            if (callback != null) callback.onCallback(steps != null ? steps.intValue() : 0);
                        } else {
                            if (callback != null) callback.onCallback(0);
                        }
                    } else {
                        if (callback != null) callback.onError(task.getException());
                    }
                });
    }

    // Obtener pasos de los últimos 7 días
    public void getWeeklySteps(WeeklyDataCallback callback) {
        // Para una implementación simple, obtenemos los últimos 7 días individualmente
        // En una app real podrías hacer una consulta por rango de fechas

        int[] weeklySteps = new int[7];
        final int[] loadedDays = {0};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        for (int i = 0; i < 7; i++) {
            Date date = new Date(currentDate.getTime() - (6 - i) * 24 * 60 * 60 * 1000);
            String dateStr = sdf.format(date);
            final int index = i;

            db.collection("daily_steps")
                    .document(dateStr)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            Long steps = task.getResult().getLong("steps");
                            weeklySteps[index] = steps != null ? steps.intValue() : 0;
                        } else {
                            weeklySteps[index] = 0;
                        }

                        loadedDays[0]++;
                        // Cuando todos los días estén cargados, llamamos al callback
                        if (loadedDays[0] == 7 && callback != null) {
                            callback.onCallback(weeklySteps);
                        }
                    });
        }

        // Si no hay datos, retornamos array de ceros después de un tiempo
        new android.os.Handler().postDelayed(() -> {
            if (loadedDays[0] < 7 && callback != null) {
                callback.onCallback(weeklySteps);
            }
        }, 3000);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}