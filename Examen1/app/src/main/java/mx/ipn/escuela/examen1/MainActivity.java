package mx.ipn.escuela.examen1;

import androidx.appcompat.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    LinearLayout screenPattern, screenNewUser, screenDisplay;
    PatternTouchView patternView;
    EditText etEmail, etPatternEntry;
    Button btnForgotPattern, btnNewUser, btnSaveUser, btnValidatePattern, btnGoDisplay;
    Spinner spinnerColor;
    NumberPicker npRepetitions;
    DatabaseHelper db;

    // runtime
    String currentPattern = "";
    int selectedColorResId;
    int repetitions = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        // find views
        screenPattern = findViewById(R.id.screenPattern);
        screenNewUser = findViewById(R.id.screenNewUser);
        screenDisplay = findViewById(R.id.screenDisplay);

        patternView = findViewById(R.id.patternView);
        etEmail = findViewById(R.id.etEmail);
        etPatternEntry = findViewById(R.id.etPatternEntry);

        btnForgotPattern = findViewById(R.id.btnForgotPattern);
        btnNewUser = findViewById(R.id.btnNewUser);
        btnSaveUser = findViewById(R.id.btnSaveUser);
        btnValidatePattern = findViewById(R.id.btnValidatePattern);
        btnGoDisplay = findViewById(R.id.btnGoDisplay);

        spinnerColor = findViewById(R.id.spinnerColor);
        npRepetitions = findViewById(R.id.npRepetitions);

        // Spinner setup (simple color names)
        String[] colorNames = new String[]{"Rojo", "Verde", "Azul", "Negro", "Magenta"};
        final int[] colorResIds = new int[]{
                android.R.color.holo_red_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.black,
                android.R.color.holo_purple
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colorNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerColor.setAdapter(adapter);
        spinnerColor.setSelection(0);
        selectedColorResId = colorResIds[0];
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColorResId = colorResIds[position];
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // NumberPicker
        npRepetitions.setMinValue(100);
        npRepetitions.setMaxValue(100000);
        npRepetitions.setValue(1000);
        npRepetitions.setWrapSelectorWheel(false);
        npRepetitions.setOnValueChangedListener((picker, oldVal, newVal) -> repetitions = newVal);
        repetitions = npRepetitions.getValue();

        // Buttons
        btnNewUser.setOnClickListener(v -> {
            // show new user screen
            screenPattern.setVisibility(View.GONE);
            screenNewUser.setVisibility(View.VISIBLE);
        });

        btnSaveUser.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pattern = etPatternEntry.getText().toString().trim();
            if (email.isEmpty() || pattern.length() < 3) {
                Toast.makeText(this, "Email y patrón (>=3) son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.insertUser(email, pattern);
            if (id != -1) {
                Toast.makeText(this, "Usuario guardado", Toast.LENGTH_SHORT).show();
                etEmail.setText("");
                etPatternEntry.setText("");
                screenNewUser.setVisibility(View.GONE);
                screenPattern.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Error al guardar (¿email repetido?)", Toast.LENGTH_SHORT).show();
            }
        });

        btnForgotPattern.setOnClickListener(v -> {
            // mostrar toast con el patrón actual (si se conoce)
            String pat = patternView.getPatternString();
            if (pat == null || pat.length() < 1) {
                Toast.makeText(this, "No hay patrón pintado aún", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Tu patrón: " + pat, Toast.LENGTH_LONG).show();
            }
        });

        btnValidatePattern.setOnClickListener(v -> {
            // cuando usuario pinta su patrón válido, se selecciona color etc
            String pat = patternView.getPatternString();
            if (pat == null || pat.length() < 3) {
                Toast.makeText(this, "Pinta un patrón válido de al menos 3 nodos", Toast.LENGTH_SHORT).show();
                return;
            }
            currentPattern = pat;
            // Mostrar pantalla display (configuración)
            screenPattern.setVisibility(View.GONE);
            screenDisplay.setVisibility(View.VISIBLE);
        });

        btnGoDisplay.setOnClickListener(v -> {
            // Obtener color y repeticiones y lanzar el dibujo en la vista de dibujo
            repetitions = npRepetitions.getValue();
            int color = ContextCompat.getColor(this, selectedColorResId);
            DrawingCanvasView canvasView = findViewById(R.id.drawingCanvas);
            canvasView.setupAndStart(color, repetitions);
        });

        // volver desde display a pattern (botón back)
        Button btnBackFromDisplay = findViewById(R.id.btnBackFromDisplay);
        btnBackFromDisplay.setOnClickListener(v -> {
            screenDisplay.setVisibility(View.GONE);
            screenPattern.setVisibility(View.VISIBLE);
        });


        Button btnListUsers = findViewById(R.id.btnListUsers);
        btnListUsers.setOnClickListener(v -> {
            Cursor c = db.getAllUsers();
            if (c != null) {
                StringBuilder sb = new StringBuilder();
                while (c.moveToNext()) {
                    String email = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL));
                    String pat = c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_PATTERN));
                    sb.append(email).append(" → ").append(pat).append("\n");
                }
                c.close();
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Usuarios en BD").setMessage(sb.length() > 0 ? sb.toString() : "No hay usuarios").setPositiveButton("OK", null).show();
            }
        });

    }

    public static class PatternTouchView extends View {
        Paint nodePaint, nodeFillPaint, linePaint, touchPaint;
        PointF[] nodes = new PointF[3];
        List<Integer> sequence = new ArrayList<>(); // indices 0..2
        float radius = 40f;
        float widthF, heightF;

        public PatternTouchView(android.content.Context ctx) {
            super(ctx);
            init();
        }
        public PatternTouchView(android.content.Context ctx, android.util.AttributeSet attrs) {
            super(ctx, attrs);
            init();
        }
        void init() {
            nodePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            nodePaint.setStyle(Paint.Style.STROKE);
            nodePaint.setStrokeWidth(4f);

            nodeFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            nodeFillPaint.setStyle(Paint.Style.FILL);

            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(8f);

            touchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            touchPaint.setStyle(Paint.Style.FILL);
        }

        @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w,h,oldw,oldh);
            widthF = w; heightF = h;
            // colocar triángulo equilátero centrado
            float cx = w/2f;
            float cy = h/2f;
            float side = Math.min(w,h) * 0.5f;
            // vertices equilátero
            nodes[0] = new PointF(cx, cy - side/ (float)Math.sqrt(3)); // top
            nodes[1] = new PointF(cx - side/2f, cy + side/(2f*(float)Math.sqrt(3)));
            nodes[2] = new PointF(cx + side/2f, cy + side/(2f*(float)Math.sqrt(3)));
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // draw nodes
            nodePaint.setColor(0xFF000000);
            nodeFillPaint.setColor(0xFFFFFFFF);
            linePaint.setColor(0xFF2196F3);
            for (int i=0;i<3;i++){
                PointF p = nodes[i];
                canvas.drawCircle(p.x, p.y, radius, nodeFillPaint);
                canvas.drawCircle(p.x, p.y, radius, nodePaint);
            }
            // draw lines according sequence
            if (sequence.size() > 1) {
                for (int i=0;i<sequence.size()-1;i++){
                    PointF a = nodes[sequence.get(i)];
                    PointF b = nodes[sequence.get(i+1)];
                    canvas.drawLine(a.x, a.y, b.x, b.y, linePaint);
                }
            }
        }

        @Override public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX(), y = event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                // check if near a node
                for (int i=0;i<3;i++){
                    PointF p = nodes[i];
                    float dx = x - p.x, dy = y - p.y;
                    if (dx*dx + dy*dy <= radius*radius) {
                        // add to sequence if last is different
                        if (sequence.size()==0 || sequence.get(sequence.size()-1) != i) {
                            sequence.add(i);
                            invalidate();
                        }
                    }
                }
            }
            return true;
        }

        public String getPatternString(){
            if (sequence.isEmpty()) return "";
            StringBuilder sb = new StringBuilder();
            for (Integer i: sequence) sb.append(i+1); // 1-based
            return sb.toString();
        }

        public void clearPattern(){
            sequence.clear();
            invalidate();
        }
    }

    public static class DrawingCanvasView extends View {
        Paint paint;
        PointF V1, V2, V3;
        Random rnd = new Random();
        float pxSize = 4f;
        android.graphics.Bitmap bitmap;
        Canvas bitmapCanvas;
        boolean running = false;

        public DrawingCanvasView(android.content.Context ctx) {
            super(ctx);
            init();
        }
        public DrawingCanvasView(android.content.Context ctx, android.util.AttributeSet attrs) {
            super(ctx, attrs);
            init();
        }
        void init() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
        }
        @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w,h,oldw,oldh);
            if (bitmap != null) bitmap.recycle();
            bitmap = android.graphics.Bitmap.createBitmap(Math.max(1,w), Math.max(1,h), android.graphics.Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
            setupVertices();
        }
        void setupVertices(){
            int w = getWidth(), h = getHeight();
            float cx = w/2f, cy = h/2f;
            float side = Math.min(w,h) * 0.6f;
            V1 = new PointF(cx, cy - side/ (float)Math.sqrt(3));
            V2 = new PointF(cx - side/2f, cy + side/(2f*(float)Math.sqrt(3)));
            V3 = new PointF(cx + side/2f, cy + side/(2f*(float)Math.sqrt(3)));
            // clear
            if (bitmapCanvas != null) bitmapCanvas.drawColor(0xFFFFFFFF);
            // draw the three vertices as small circles
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setStyle(Paint.Style.FILL);
            p.setColor(0xFF000000);
            bitmapCanvas.drawCircle(V1.x, V1.y, 6f, p);
            bitmapCanvas.drawCircle(V2.x, V2.y, 6f, p);
            bitmapCanvas.drawCircle(V3.x, V3.y, 6f, p);
        }

        /**
         * setup and start drawing algorithm
         * @param color color int
         * @param reps number of iterations
         */
        public void setupAndStart(int color, int reps) {
            if (getWidth() == 0 || getHeight() == 0) {
                postDelayed(() -> setupAndStart(color, reps), 50);
                return;
            }
            paint.setColor(color);
            setupVertices();
            // initial point P (outside the triangle) -> choose a point slightly below canvas center
            PointF P = new PointF(getWidth()*0.1f, getHeight()*0.1f); // outside area
            // run iterations (do in background thread to avoid blocking UI)
            new Thread(() -> {
                for (int i=0;i<reps;i++){
                    // randomly select a vertex 1..3
                    int pick = rnd.nextInt(3); // 0..2
                    PointF Vn;
                    if (pick==0) Vn = V1;
                    else if (pick==1) Vn = V2;
                    else Vn = V3;
                    // midpoint pm between Vn and P
                    float pmx = (Vn.x + P.x)/2f;
                    float pmy = (Vn.y + P.y)/2f;
                    // paint small rect as pixel
                    bitmapCanvas.drawRect(pmx, pmy, pmx+pxSize, pmy+pxSize, paint);
                    // new P becomes pm
                    P.x = pmx; P.y = pmy;
                    // occasionally update UI
                    if (i % 500 == 0) {
                        postInvalidate();
                        try { Thread.sleep(1); } catch (InterruptedException e) { /* nop */ }
                    }
                }
                postInvalidate();
            }).start();
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (bitmap != null) canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

}
