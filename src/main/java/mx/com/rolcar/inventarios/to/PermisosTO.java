package mx.com.rolcar.inventarios.to;

import java.util.ArrayList;

public class PermisosTO {
    private ArrayList<String> permisos = new ArrayList<>();

    public void addPermiso(String permiso) {
        permisos.add(permiso);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("[");
        for(String permiso : permisos) {
            ret.append("\"").append(permiso).append("\",");
        }
        ret.append("]");
        return ret.toString();
    }
}
