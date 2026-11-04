package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.ServletUtilities;
import com.atcloud.web.WebException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class UbicacionDAO implements DatabaseRecord, DatabaseRecordABC {
    public String compania = "";
    public String ubicacion = "";
    public String descripcion = "";

    public UbicacionDAO() {
    }

    public UbicacionDAO(String compania, String ubicacion) {
        this.compania = compania;
        this.ubicacion = ubicacion;
    }

    @Override
    public String getTable() {
        return "Ubicacion";
    }

    @Override
    public String getOrder() {
        return "compania, ubicacion";
    }

    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND ubicacion = '"+ubicacion+"'";
    }

    @Override
    public String getWhereFirst() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getWhereNext() {
        return "compania = '"+compania+"' AND ubicacion > '"+ubicacion+"'";
    }

    @Override
    public String getWherePrev() {
        return "compania = '"+compania+"' AND ubicacion < '"+ubicacion+"'";
    }

    @Override
    public String getWhereLast() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getOrderFirst() {
        return "compania, ubicacion";
    }

    @Override
    public String getOrderLast() {
        return "compania, ubicacion DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        compania = values[0];
        ubicacion = values[1];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        compania = values[0];
        ubicacion = values[1];
    }

    @Override
    public String toString() {
        return compania+";"+ubicacion+";"+descripcion;
    }

    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.compania = (String)json.get("compania");
        this.ubicacion = (String)json.get("ubicacion");
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

        this.compania = (String)json.get("compania");
        this.ubicacion = (String)json.get("ubicacion");
        boolean existe = ds.exists(this);

        ServletUtilities.fromJSON(this, json);

        if (existe) {
            ds.delete(this);
        }
    }

    public void busca(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.compania = (String)json.get("compania");
        this.ubicacion = (String)json.get("ubicacion");

        if (!ds.exists(this))
            throw new WebException("No existe esta Ubicacion ["+this.ubicacion+"].");
    }
}
