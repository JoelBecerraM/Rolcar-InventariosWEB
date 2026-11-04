package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.ServletUtilities;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ProductoDAO implements DatabaseRecord, DatabaseRecordABC {
    public String compania = "";
    public String clave = "";
    public String descripcion = "";
    public double precio = 0.0;
    public String numero = "";
    
    public ProductoDAO() {
    }
    
    public ProductoDAO(String compania, String clave) {
        this.compania = compania;
        this.clave = clave;
    }
    
    @Override
    public String getTable() {
        return "Producto";
    }
    
    @Override
    public String getOrder() {
        return "compania, clave";
    }
    
    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND clave = '"+clave+"'";
    }
    
    @Override
    public String getWhereFirst() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getWhereNext() {
        return "compania = '"+compania+"' AND clave > '"+clave+"'";
    }

    @Override
    public String getWherePrev() {
        return "compania = '"+compania+"' AND clave < '"+clave+"'";
    }

    @Override
    public String getWhereLast() {
        return "compania = '"+compania+"' ";
    }

    @Override
    public String getOrderFirst() {
        return "compania, clave";
    }

    @Override
    public String getOrderLast() {
        return "compania, clave DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        compania = values[0];
        clave = values[1];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        compania = values[0];
        clave = values[1];
    }
    
    @Override
    public String toString() {
        return compania+";"+clave+";"+descripcion;
    }    

    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);
        
        this.compania = (String)json.get("compania");        
        this.clave = (String)json.get("clave");        
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
        this.clave = (String)json.get("clave");        
        boolean existe = ds.exists(this);
        
        ServletUtilities.fromJSON(this, json);
        
        if (existe) {
            ds.delete(this);
        }
    }
}
