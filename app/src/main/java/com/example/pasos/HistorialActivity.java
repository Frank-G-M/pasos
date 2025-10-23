package com.example.pasos;

import android.app.Activity;

public class HistorialActivity extends Activity {
    public class HistorialItem {
        private String dia;
        private String pasos;

        public HistorialItem(String dia, String pasos) {
            this.dia = dia;
            this.pasos = pasos;
        }

        public String getDia() {
            return dia;
        }

        public String getPasos() {
            return pasos;
        }
    }

}
