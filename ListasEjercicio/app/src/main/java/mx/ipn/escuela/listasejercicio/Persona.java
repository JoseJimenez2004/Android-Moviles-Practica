package mx.ipn.escuela.listasejercicio;

public class Persona {
    private final int id;
    private final String nombre;
    private final String apellido;
    private final String telefono;
    private final String correo;

    public Persona(int id, String nombre, String apellido, String telefono, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
}
