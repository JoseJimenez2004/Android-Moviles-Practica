package mx.ipn.escuela.cubo3dimagenes;

import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.*;
import android.opengl.*;
import java.nio.*;
public class MiCubo {
    private FloatBuffer vb;
    private FloatBuffer tb;
    private int n = 6;
    private int[] imgs = { R.drawable.escom, R.drawable.ipn, R.drawable.f18,
            R.drawable.escom, R.drawable.mexico, R.drawable.marte };
    private int[] id = new int[n];
    private Bitmap[] bm = new Bitmap[n];

    // CAMBIO 1: Ajustar el tamaño a 1.0f para que coincida con las coordenadas
    private float tam = 1.0f;

    public MiCubo(Context cx) {

        ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4 * n);
        vbb.order(ByteOrder.nativeOrder());
        vb = vbb.asFloatBuffer();
        for (int f = 0; f < n; f++) {
            bm[f] = BitmapFactory.decodeStream(cx.getResources().openRawResource(imgs[f]));

            // CAMBIO 2: Eliminar la lógica de aspect ratio.
            // Forzamos que todas las caras sean cuadrados de 2x2 (de -1.0 a 1.0)
            // para que formen un cubo perfecto.
            float izq = -tam;
            float der = tam;
            float tope = tam;
            float base = -tam;

            float[] vertices = {
                    izq, base, 0.0f, // 0. izq-base
                    izq, tope, 0.0f, // 1. izq-tope
                    der, base, 0.0f, // 2. der-base
                    der, tope, 0.0f, // 3. der-tope
            };
            vb.put(vertices); // Poblar
        }
        vb.position(0); // Repetir

        float[] coordenadas = {
                0.0f, 1.0f, // 0. izq-base
                0.0f, 0.0f, // 1. izq-tope
                1.0f, 1.0f, // 2. der-base
                1.0f, 0.0f  // 3. der-tope
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(coordenadas.length * 4 * n);
        bb.order(ByteOrder.nativeOrder());
        tb = bb.asFloatBuffer();
        for (int c = 0; c < n; c++) {
            tb.put(coordenadas);
        }
// Rewind
        tb.position(0);
    }
    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vb);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, tb);
// frente
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[0]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glPopMatrix();
// izquierda
        gl.glPushMatrix();
        gl.glRotatef(270.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[1]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
        gl.glPopMatrix();
// atrás
        gl.glPushMatrix();
        gl.glRotatef(180.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[2]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
        gl.glPopMatrix();
// derecha
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 0f, 1f, 0f);
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[3]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
        gl.glPopMatrix();
// tope
        gl.glPushMatrix();
        gl.glRotatef(270.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[4]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
        gl.glPopMatrix();
// base
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1f, 0f, 0f);
        gl.glTranslatef(0f, 0f, tam); // tam ahora es 1.0f
        gl.glBindTexture(GL10.GL_TEXTURE_2D, id[5]);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
        gl.glPopMatrix();
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
    public void loadTexture(GL10 gl) {
        gl.glGenTextures(6, id, 0); // Generar arreglo de IDs de texture-ID de 6 IDs
        for (int face = 0; face < n; face++) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, id[face]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bm[face], 0);
            bm[face].recycle();
        }
    }
}
