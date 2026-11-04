package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.ServletUtilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PerfilDAO implements DatabaseRecord, DatabaseRecordABC, java.io.Serializable {
    public String perfil = "";
    public String descripcion = "";

    public PerfilDAO() {
    }

    public PerfilDAO(String perfil) {
        this.perfil = perfil;
    }

    @Override
    public String getTable() {
        return "Perfil";
    }

    @Override
    public String getOrder() {
        return "perfil";
    }

    @Override
    public String getWhere() {
        return "perfil = '"+perfil+"'";
    }

    @Override
    public String getWhereFirst() {
        return "1 = 1";
    }

    @Override
    public String getWhereNext() {
        return "perfil > '"+perfil+"'";
    }

    @Override
    public String getWherePrev() {
        return "perfil < '"+perfil+"'";
    }

    @Override
    public String getWhereLast() {
        return "1 = 1";
    }

    @Override
    public String getOrderFirst() {
        return "perfil";
    }

    @Override
    public String getOrderLast() {
        return "perfil DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        perfil = values[0];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        perfil = values[0];
    }

    @Override
    public String toString() {
        return perfil;
    }


    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.perfil = (String)json.get("perfil");
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

        this.perfil = (String)json.get("perfil");
        boolean existe = ds.exists(this);

        ServletUtilities.fromJSON(this, json);

        if (existe) {
            ds.delete(this);
        }
    }
}
