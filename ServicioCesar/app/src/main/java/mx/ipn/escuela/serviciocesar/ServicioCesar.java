package mx.ipn.escuela.serviciocesar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ServicioCesar extends Service {

    public static final String ACTION_DESCIFRAR = "mx.ipn.escuela.servicios.action.DESCIFRAR";
    public static final String EXTRA_TEXTO_CIFRADO = "extra_texto_cifrado";
    public static final String EXTRA_CLAVE = "extra_clave";
    public static final String EXTRA_TEXTO_DESCIFRADO = "extra_texto_descifrado";
    public static final String ACTION_DESCIFRADO_COMPLETO = "mx.ipn.escuela.servicios.action.DESCIFRADO_COMPLETO";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_DESCIFRAR.equals(intent.getAction())) {
            final String textoCifrado = intent.getStringExtra(EXTRA_TEXTO_CIFRADO);
            final int clave = intent.getIntExtra(EXTRA_CLAVE, 0);

            // Iniciar el descifrado en un hilo para no bloquear el principal
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String textoDescifrado = descifrarCesar(textoCifrado, clave);
                    enviarResultado(textoDescifrado);
                }
            }).start();
        }
        return START_NOT_STICKY;
    }

    private String descifrarCesar(String textoCifrado, int clave) {
        StringBuilder descifrado = new StringBuilder();
        for (int i = 0; i < textoCifrado.length(); i++) {
            char caracter = textoCifrado.charAt(i);

            if (Character.isLetter(caracter)) {
                char base = Character.isLowerCase(caracter) ? 'a' : 'A';
                // La fórmula para descifrar es (x - n) mod 26
                int originalPos = caracter - base;
                int nuevaPos = (originalPos - clave + 26) % 26;
                descifrado.append((char) (base + nuevaPos));
            } else {
                descifrado.append(caracter); // No se alteran los caracteres que no son letras
            }
        }
        return descifrado.toString();
    }

    private void enviarResultado(String textoDescifrado) {
        Intent intent = new Intent(ServicioCesar.ACTION_DESCIFRADO_COMPLETO);
        intent.setPackage(getPackageName()); // 🔒 Garantiza que solo tu app lo reciba
        intent.putExtra(ServicioCesar.EXTRA_TEXTO_DESCIFRADO, textoDescifrado);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}