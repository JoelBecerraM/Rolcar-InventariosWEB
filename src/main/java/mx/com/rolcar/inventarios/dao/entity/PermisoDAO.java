package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.Numero;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PermisoDAO implements DatabaseRecord, DatabaseRecordABC, java.io.Serializable {
    public int permiso = 0;
    public String nombre = "";

    public PermisoDAO() {
    }

    public PermisoDAO(int permiso) {
        this.permiso = permiso;
    }

    @Override
    public String getTable() {
        return "Permiso";
    }

    @Override
    public String getOrder() {
        return "permiso";
    }

    @Override
    public String getWhere() {
        return "permiso = "+permiso;
    }

    @Override
    public String toString() {
        return permiso+";"+nombre;
    }

    @Override
    public String getWhereFirst() {
        return "1 = 1";
    }

    @Override
    public String getWhereNext() {
        return "permiso > "+permiso;
    }

    @Override
    public String getWherePrev() {
        return "permiso < "+permiso;
    }

    @Override
    public String getWhereLast() {
        return "1 = 1";
    }

    @Override
    public String getOrderFirst() {
        return "permiso";
    }

    @Override
    public String getOrderLast() {
        return "permiso DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        permiso = Numero.getIntFromString(values[0]);
    }

    @Override
    public void setValues(String[] values) throws Exception {
        permiso = Numero.getIntFromString(values[0]);
        nombre = values[1];
    }

    public void add(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.nombre = json.get("nombre").toString();
        this.permiso = (Integer)ds.aggregate(this, "max", "permiso") + 1;

        ds.insert(this);
    }
}
