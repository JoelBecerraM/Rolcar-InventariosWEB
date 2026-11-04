package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.Numero;
import com.atcloud.util.ServletUtilities;
import com.atcloud.web.WebException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PermisoPerfilDAO implements DatabaseRecord, DatabaseRecordABC, java.io.Serializable {
    public int permiso = 0;
    public String perfil = "";

    public PermisoPerfilDAO() {
    }

    public PermisoPerfilDAO(int permiso, String perfil) {
        this.permiso = permiso;
        this.perfil = perfil;
    }

    @Override
    public String getTable() {
        return "PermisoPerfil";
    }

    @Override
    public String getOrder() {
        return "permiso, perfil";
    }

    @Override
    public String getWhere() {
        return "permiso = "+permiso+" AND perfil = '"+perfil+"'";
    }

    @Override
    public String getWhereFirst() {
        return "permiso = "+permiso+" AND perfil = '"+perfil+"'";
    }

    @Override
    public String getWhereNext() {
        return "permiso = "+permiso+" AND perfil > '"+perfil+"'";
    }

    @Override
    public String getWherePrev() {
        return "permiso = "+permiso+" AND perfil < '"+perfil+"'";
    }

    @Override
    public String getWhereLast() {
        return "permiso = "+permiso+" AND perfil = '"+perfil+"'";
    }

    @Override
    public String getOrderFirst() {
        return "permiso, perfil";
    }

    @Override
    public String getOrderLast() {
        return "permiso, perfil DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        permiso = Numero.getIntFromString(values[0]);
        perfil = values[1];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        permiso = Numero.getIntFromString(values[0]);
        perfil = values[1];
    }

    @Override
    public String toString() {
        return permiso+";"+perfil;
    }

    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.permiso = Numero.getIntFromString(json.get("permiso").toString());
        this.perfil = json.get("perfil").toString();
        boolean existe = ds.exists(this);

        ServletUtilities.fromJSON(this, json);

        if (existe) {
            ds.update(this);
        } else {
            ds.insert(this);
        }
    }

    public void delete(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.permiso = Numero.getIntFromString(json.get("permiso").toString());
        this.perfil = json.get("perfil").toString();
        boolean existe = ds.exists(this);

        ServletUtilities.fromJSON(this, json);

        if (existe) {
            ds.delete(this);
        }
    }

    public void permisos(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        String nombre = (String)json.get("nombre");

        PermisoDAO permisoDAO = (PermisoDAO)ds.first(new PermisoDAO(), "nombre = '"+nombre+"'");
        if (permisoDAO==null)
            throw new WebException("Este Permiso ["+nombre+"] no existe.");

        this.permiso = permisoDAO.permiso;

        ds.delete(this, "permiso = "+this.permiso);

        for(Object object : json.keySet()) {
            String key = (String)object;
            if (key.startsWith("perfil_")) {
                String value = (String)json.get(key);
                if (value.compareTo("0")==0)
                    continue;

                this.perfil = key.substring(key.indexOf("_")+1);

                ds.insert(this);
            }
        }
    }

    public void permiso(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        String usuario = (String)json.get("usuario");
        String password = (String)json.get("password");

        UsuarioDAO usuarioDAO = new UsuarioDAO(usuario);
        if (!ds.exists(usuarioDAO))
            throw new WebException("No existe este Usuario ["+usuarioDAO+"].");
        if (usuarioDAO.password.compareTo(password)!=0)
            throw new WebException("La contrase&ntilde;a no es correcta.");

        String nombre = (String)json.get("nombre");

        PermisoDAO permisoDAO = (PermisoDAO)ds.first(new PermisoDAO(), "nombre = '"+nombre+"'");
        if (permisoDAO==null)
            throw new WebException("Este Permiso ["+nombre+"] no existe.");

        this.permiso = permisoDAO.permiso;
        this.perfil = usuarioDAO.perfil;
        if (!ds.exists(this))
            throw new Exception("El Usuario ["+usuarioDAO.nombre+"] no tiene este Permiso ["+nombre+"].");
    }
}
