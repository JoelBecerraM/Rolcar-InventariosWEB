package mx.com.rolcar.inventarios.collection;

import com.atcloud.collection.engine.CollectionRecord;

/**
 *
 * @author joelbecerram
 */
public class PermisoPerfilCollection implements CollectionRecord {
    public int permiso = 0;
    public String perfil = "";
    public String nombre = "";
    public String descripcion = "";

    @Override
    public String getSQL() {
        return getSQL(getWhere());
    }

    @Override
    public String getSQL(String where) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pp.permiso, pp.perfil, pe.nombre, p.descripcion ")
            .append("FROM PermisoPerfil pp INNER JOIN Permiso pe ON pp.permiso = pe.permiso ")
            .append("INNER JOIN Perfil p ON pp.perfil = p.perfil ")
            .append("WHERE ").append(where).append(" ")
            .append("ORDER BY pp.permiso, pp.perfil");
        return sql.toString();
    }

    @Override
    public String getWhere() {
        return "pp.permiso = "+permiso;
    }
}
