package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.ServletUtilities;
import com.atcloud.web.WebException;
import mx.com.rolcar.inventarios.business.CatalogoBussines;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CompaniaDAO implements DatabaseRecord, DatabaseRecordABC {
    public String compania = "";
    public String razonsocial = "";
    public String porubicacion = "";
    
    public CompaniaDAO() {
    }
    
    public CompaniaDAO(String compania) {
        this.compania = compania;
    }
    
    @Override
    public String getTable() {
        return "Compania";
    }
    
    @Override
    public String getOrder() {
        return "compania";
    }
    
    @Override
    public String getWhere() {
        return "compania = '"+compania+"'";
    }
    
    @Override
    public String getWhereFirst() {
        return "1 = 1";
    }

    @Override
    public String getWhereNext() {
        return "compania > '"+compania+"'";
    }

    @Override
    public String getWherePrev() {
        return "compania < '"+compania+"'";
    }

    @Override
    public String getWhereLast() {
        return "1 = 1";
    }

    @Override
    public String getOrderFirst() {
        return "compania";
    }

    @Override
    public String getOrderLast() {
        return "compania DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        compania = values[0];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        compania = values[0];
    }
    
    @Override
    public String toString() {
        return compania+";"+razonsocial;
    }    

    public void save(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);
        
        this.compania = (String)json.get("compania");        
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
        boolean existe = ds.exists(this);
        
        ServletUtilities.fromJSON(this, json);
        
        if (existe) {
            ds.delete(this);
        }
    }
    
    public void busca(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.compania = (String)json.get("compania");
        
        if (!ds.exists(this))
            throw new WebException("No existe esta Compania ["+this.compania+"].");
    }
    
    public void actualizar(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);
        
        this.compania = (String)json.get("compania");

        if (!ds.exists(this))
            throw new WebException("No existe esta Compania ["+this.compania+"].");
        
        CatalogoBussines catalogoBussines = new CatalogoBussines();
        catalogoBussines.actualizaCatalogos(ds, this);
    }    
}
