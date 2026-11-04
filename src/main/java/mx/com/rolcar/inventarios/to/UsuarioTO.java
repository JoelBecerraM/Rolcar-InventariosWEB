package mx.com.rolcar.inventarios.to;

public class UsuarioTO {
    public String usuario = "";
    public String password = "";
    public String nombre = "";
    public String email = "";
    public String perfil = "";
    public String compania = "";
    public String razonsocial = "";
    public PermisosTO permisos;
    
    @Override
    public String toString() {
        return usuario+";"+nombre;
    }
}
