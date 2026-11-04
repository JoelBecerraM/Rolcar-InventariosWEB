package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.ServletUtilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UsuarioDAO implements DatabaseRecord, DatabaseRecordABC {
    public String usuario = "";
    public String password = "";
    public String perfil = "";
    public String estado = "";
    public String nombre = "";
    public String email = "";

    public UsuarioDAO() {
    }

    public UsuarioDAO(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public String getTable() {
        return "Usuario";
    }

    @Override
    public String getWhere() {
        return "usuario = '"+usuario+"'";
    }

    @Override
    public String getOrder() {
        return "usuario";
    }

    @Override
    public String getWhereFirst() {
        return "1 = 1";
    }

    @Override
    public String getWhereNext() {
        return "usuario > '"+usuario+"'";
    }

    @Override
    public String getWherePrev() {
        return "usuario < '"+usuario+"'";
    }

    @Override
    public String getWhereLast() {
        return "1 = 1";
    }

    @Override
    public String getOrderFirst() {
        return "usuario";
    }

    @Override
    public String getOrderLast() {
        return "usuario DESC";
    }

    @Override
    public String toString() {
        return usuario;
    }

    @Override
    public void setKey(String[] values) throws Exception {
        usuario = values[0];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        usuario = values[0];
    }

    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.usuario = (String)json.get("usuario");
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

        this.usuario = (String)json.get("usuario");
        boolean existe = ds.exists(this);

        ServletUtilities.fromJSON(this, json);

        if (existe) {
            ds.delete(this);
        }
    }
}
