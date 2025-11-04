package mx.ipn.escuela.figuras3d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CubeView extends View {

    // ===== Config =====
    private static final int RADIAL_SEGMENTS = 24;   // círculo/cono/cilindro
    private static final int SPHERE_LONG = 28;       // meridianos
    private static final int SPHERE_LAT  = 14;       // paralelos
    private static final float R = 1f;               // radio base de figuras
    private static final float H = 2f;               // altura base

    // ===== Pintura y estilo =====
    private final Paint edge = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint border = new Paint(Paint.ANTI_ALIAS_FLAG);

    // ===== 4 objetos independientes =====
    private final Obj3D[] objs = new Obj3D[4];
    private final boolean[] autoRotate = new boolean[]{true, true, true, true};
    private int active = -1; // índice del cuadrante activo (0..3)

    // Gestos
    private final ScaleGestureDetector scaleDetector;
    private final GestureDetector gestureDetector;

    // Tick de animación
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            for (int i = 0; i < 4; i++) {
                if (autoRotate[i]) {
                    objs[i].theta += 0.012f;
                    objs[i].phi   += 0.006f;
                }
            }
            invalidate();
            postOnAnimation(this);
        }
    };

    public CubeView(Context ctx) {
        super(ctx);

        edge.setColor(Color.WHITE);
        edge.setStrokeWidth(3f);
        edge.setStyle(Paint.Style.STROKE);

        border.setColor(Color.WHITE);
        border.setStrokeWidth(2f);
        border.setStyle(Paint.Style.STROKE);

        setBackgroundColor(Color.BLACK); // amarillo suave

        // Crear 4 figuras
        objs[0] = Obj3D.cube();                 // cubo
        objs[1] = Obj3D.cone(R, H, RADIAL_SEGMENTS);     // cono
        objs[2] = Obj3D.cylinder(R, H, RADIAL_SEGMENTS); // cilindro
        objs[3] = Obj3D.sphere(R, SPHERE_LONG, SPHERE_LAT); // esfera

        // Gestos
        scaleDetector = new ScaleGestureDetector(ctx, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override public boolean onScale(ScaleGestureDetector d) {
                if (active < 0) return false;
                Obj3D o = objs[active];
                float f = 1f / d.getScaleFactor();
                o.rho = clamp(o.rho * f, o.objSize * 3f, o.objSize * 40f);
                invalidate();
                return true;
            }
        });

        gestureDetector = new GestureDetector(ctx, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapConfirmed(MotionEvent e) {
                active = quadrantOf(e.getX(), e.getY());
                if (active >= 0) autoRotate[active] = !autoRotate[active];
                return true;
            }
            @Override public boolean onDoubleTap(MotionEvent e) {
                active = quadrantOf(e.getX(), e.getY());
                if (active >= 0) objs[active].reset();
                invalidate();
                return true;
            }
        });

        postOnAnimation(tick);
    }

    private static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ======= Layout: 4 cuadrantes =======
    private RectF quadRect(int idx) {
        float w2 = getWidth()/2f, h2 = getHeight()/2f;
        switch (idx) {
            case 0: return new RectF(0,   0,   w2,  h2); // arriba-izq
            case 1: return new RectF(w2,  0,   getWidth(), h2); // arriba-der
            case 2: return new RectF(0,   h2,  w2,  getHeight()); // abajo-izq
            default:return new RectF(w2,  h2,  getWidth(), getHeight()); // abajo-der
        }
    }
    private int quadrantOf(float x, float y) {
        boolean right = x >= getWidth()/2f;
        boolean bottom= y >= getHeight()/2f;
        if (!right && !bottom) return 0;
        if ( right && !bottom) return 1;
        if (!right &&  bottom) return 2;
        return 3;
    }

    // ======= Dibujo =======
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        // bordes de cuadrantes
        c.drawLine(getWidth()/2f, 0, getWidth()/2f, getHeight(), border);
        c.drawLine(0, getHeight()/2f, getWidth(), getHeight()/2f, border);

        for (int i = 0; i < 4; i++) drawObjectInQuad(c, objs[i], quadRect(i));
    }

    private void drawObjectInQuad(Canvas canvas, Obj3D obj, RectF r) {
        float cx = (r.left + r.right)/2f;
        float cy = (r.top  + r.bottom)/2f;
        float side = Math.min(r.width(), r.height());

        obj.d = obj.rho * side / obj.objSize;
        obj.eyeAndScreen();

        for (int[] e : obj.edges) {
            Point2D p = obj.vScr.get(e[0]);
            Point2D q = obj.vScr.get(e[1]);
            canvas.drawLine(cx + p.x, cy - p.y, cx + q.x, cy - q.y, edge);
        }
    }

    // ======= Interacción (rotación manual) =======
    private float lastX = -1, lastY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                active = quadrantOf(ev.getX(), ev.getY());
                lastX = ev.getX();
                lastY = ev.getY();
                if (active >= 0) autoRotate[active] = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (active >= 0 && ev.getPointerCount() == 1 && lastX >= 0) {
                    float dx = (ev.getX() - lastX) / getWidth();
                    float dy = (ev.getY() - lastY) / getHeight();
                    objs[active].theta += dx * 3.0f;
                    objs[active].phi   += dy * 3.0f;
                    lastX = ev.getX(); lastY = ev.getY();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastX = lastY = -1;
                break;
        }
        return true;
    }

    // ======= Matemáticas y modelos =======
    static class Point2D { float x,y; Point2D(float x,float y){this.x=x; this.y=y;} }
    static class Point3D { float x,y,z; Point3D(float x,float y,float z){this.x=x; this.y=y; this.z=z;} }

    static class Obj3D {
        float rho, theta=0.3f, phi=1.3f, d, objSize;
        float v11,v12,v13,v21,v22,v23,v32,v33,v43;

        final List<Point3D> w = new ArrayList<>();
        final List<Point2D> vScr = new ArrayList<>();
        final List<int[]> edges = new ArrayList<>();

        void reset() { theta = 0.3f; phi = 1.3f; rho = 5f * objSize; }

        private void initPersp() {
            float costh=(float)Math.cos(theta), sinth=(float)Math.sin(theta);
            float cosph=(float)Math.cos(phi),   sinph=(float)Math.sin(phi);
            v11=-sinth; v12=-cosph*costh; v13= sinph*costh;
            v21= costh; v22=-cosph*sinth; v23= sinph*sinth;
            v32= sinph; v33= cosph; v43=-rho;
        }

        void eyeAndScreen() {
            initPersp();
            vScr.clear();
            for (Point3D p3 : w) {
                float x = v11*p3.x + v21*p3.y;
                float y = v12*p3.x + v22*p3.y + v32*p3.z;
                float z = v13*p3.x + v23*p3.y + v33*p3.z + v43;
                vScr.add(new Point2D(-d*x/z, -d*y/z));
            }
        }

        // ===== utilidades de construcción =====
        static Obj3D cube() {
            Obj3D o = new Obj3D();
            float s = 1f;
            o.w.add(new Point3D( s,-s,-s)); o.w.add(new Point3D( s, s,-s));
            o.w.add(new Point3D(-s, s,-s)); o.w.add(new Point3D(-s,-s,-s));
            o.w.add(new Point3D( s,-s, s)); o.w.add(new Point3D( s, s, s));
            o.w.add(new Point3D(-s, s, s)); o.w.add(new Point3D(-s,-s, s));
            int[][] es = {
                    {0,1},{1,2},{2,3},{3,0},
                    {4,5},{5,6},{6,7},{7,4},
                    {0,4},{1,5},{2,6},{3,7}
            };
            for (int[] e: es) o.edges.add(e);
            o.objSize = (float)Math.sqrt(12.0);
            o.reset();
            return o;
        }

        static Obj3D cone(float r, float h, int n) {
            Obj3D o = new Obj3D();
            float zBase = -h/2f, zApex = h/2f;
            int baseStart = o.w.size();
            for (int i=0;i<n;i++){
                double a = 2*Math.PI*i/n;
                o.w.add(new Point3D((float)(r*Math.cos(a)), (float)(r*Math.sin(a)), zBase ));
            }
            int apex = o.w.size();
            o.w.add(new Point3D(0,0,zApex));

            for (int i=0;i<n;i++){
                o.edges.add(new int[]{baseStart+i, baseStart+((i+1)%n)});
                o.edges.add(new int[]{baseStart+i, apex});
            }

            o.objSize = (float)Math.sqrt((2*r)*(2*r) + (2*r)*(2*r) + h*h);
            o.reset();
            return o;
        }

        static Obj3D cylinder(float r, float h, int n) {
            Obj3D o = new Obj3D();
            float z0 = -h/2f, z1 = h/2f;
            int b0 = o.w.size();
            for (int i=0;i<n;i++){
                double a = 2*Math.PI*i/n;
                float x = (float)(r*Math.cos(a)), y = (float)(r*Math.sin(a));
                o.w.add(new Point3D(x,y,z0));
            }
            int b1 = o.w.size();
            for (int i=0;i<n;i++){
                double a = 2*Math.PI*i/n;
                float x = (float)(r*Math.cos(a)), y = (float)(r*Math.sin(a));
                o.w.add(new Point3D(x,y,z1));
            }
            for (int i=0;i<n;i++){
                int ni = (i+1)%n;
                o.edges.add(new int[]{b0+i, b0+ni});
                o.edges.add(new int[]{b1+i, b1+ni});
                o.edges.add(new int[]{b0+i, b1+i});
            }
            o.objSize = (float)Math.sqrt((2*r)*(2*r) + (2*r)*(2*r) + h*h);
            o.reset();
            return o;
        }

        static Obj3D sphere(float r, int longs, int lats) {
            Obj3D o = new Obj3D();
            int north = o.w.size(); o.w.add(new Point3D(0,0,r));
            int south = o.w.size(); o.w.add(new Point3D(0,0,-r));

            int[][] idx = new int[lats-1][longs];
            for (int j=1; j<lats; j++){
                float phi = (float)(Math.PI*j/lats);
                float z = r*(float)Math.cos(phi);
                float rr = r*(float)Math.sin(phi);
                for (int i=0;i<longs;i++){
                    double a = 2*Math.PI*i/longs;
                    int id = o.w.size();
                    o.w.add(new Point3D((float)(rr*Math.cos(a)), (float)(rr*Math.sin(a)), z));
                    idx[j-1][i] = id;
                }
            }

            for (int i=0;i<longs;i++){
                o.edges.add(new int[]{north, idx[0][i]});
                o.edges.add(new int[]{south, idx[lats-2][i]});
            }

            for (int j=0;j<lats-1; j++){
                for (int i=0;i<longs;i++){
                    int ni = (i+1)%longs;
                    o.edges.add(new int[]{idx[j][i], idx[j][ni]});
                    if (j+1<lats-1){
                        o.edges.add(new int[]{idx[j][i], idx[j+1][i]});
                    }
                }
            }

            o.objSize = 2*r*(float)Math.sqrt(3);
            o.reset();
            return o;
        }
    }
}
