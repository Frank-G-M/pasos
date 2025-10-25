package com.example.pasos;

public class StepDetector {
    private static final float ACCELERATION_THRESHOLD = 12.0f;
    private static final int STEP_TIME_THRESHOLD = 200;

    private long lastStepTime = 0;
    private int stepCount = 0;

    public interface StepListener {
        void onStep();
    }

    private StepListener stepListener;

    public void setStepListener(StepListener listener) {
        this.stepListener = listener;
    }

    public void updateAcceleration(float x, float y, float z) {
        float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

        long currentTime = System.currentTimeMillis();

        if (acceleration > ACCELERATION_THRESHOLD &&
                (currentTime - lastStepTime) > STEP_TIME_THRESHOLD) {

            lastStepTime = currentTime;
            stepCount++;

            if (stepListener != null) {
                stepListener.onStep();
            }
        }
    }

    public int getStepCount() {
        return stepCount;
    }

    public void resetStepCount() {
        stepCount = 0;
    }
}