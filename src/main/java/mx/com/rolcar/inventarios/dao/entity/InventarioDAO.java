package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.Numero;
import com.atcloud.util.Reflector;
import com.atcloud.web.WebException;
import java.util.Date;
import mx.com.rolcar.inventarios.business.InventarioBussines;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class InventarioDAO implements DatabaseRecord, DatabaseRecordABC {
    public Integer flinventario = 0;
    public String compania = "";
    public int almacen = 0;
    public String descripcion = "";
    public Date feinicio = null;
    public Date fetermino = null;
    public String status = "";
    public String fase = "";
    public Date fefase = null;
    public Date feprimerconteo = null;
    public Date fesegundoconteo = null;
    public Date fetercerconteo = null;

    public InventarioDAO() {
    }

    public InventarioDAO(int flinventario) {
        this.flinventario = flinventario;
    }

    @Override
    public String getTable() {
        return "Inventario";
    }

    @Override
    public String getOrder() {
        return "flinventario";
    }

    @Override
    public String getWhere() {
        return "flinventario = "+flinventario;
    }

    @Override
    public String getWhereFirst() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getWhereNext() {
        return "compania = '"+compania+"' AND flinventario > "+flinventario;
    }

    @Override
    public String getWherePrev() {
        return "compania = '"+compania+"' AND flinventario < "+flinventario;
    }

    @Override
    public String getWhereLast() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getOrderFirst() {
        return "flinventario";
    }

    @Override
    public String getOrderLast() {
        return "flinventario DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        flinventario = Numero.getIntFromString(values[0]);
    }

    @Override
    public void setValues(String[] values) throws Exception {
        flinventario = Numero.getIntFromString(values[0]);
    }

    @Override
    public String toString() {
        return flinventario+";";
    }

    public void busca(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.compania = (String)json.get("compania");

        InventarioBussines inventarioBussines = new InventarioBussines();

        InventarioDAO inventarioDAO = inventarioBussines.buscaInventario(ds, compania);
        if (inventarioDAO==null)
            throw new WebException("No existe un Inventario [PE] en esta compania.");

        Reflector.copyAllFields(inventarioDAO, this);
    }

    public void crea(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.compania = (String)json.get("compania");
        this.almacen = (int)(long)json.get("almacen");

        InventarioBussines inventarioBussines = new InventarioBussines();

        InventarioDAO inventarioDAO = inventarioBussines.creaInventario(ds, compania, almacen);

        Reflector.copyAllFields(inventarioDAO, this);
    }

    public void activaPrimero(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.flinventario = Integer.parseInt(json.get("flinventario").toString());
        boolean existe = ds.exists(this);

        if (!existe)
            throw new WebException("Este Inventario ["+this+"] no existe.");

        InventarioBussines inventarioBussines = new InventarioBussines();
        inventarioBussines.activaPrimerConteo(ds, this);
    }

    public void activaSegundo(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.flinventario = Integer.parseInt(json.get("flinventario").toString());
        boolean existe = ds.exists(this);

        if (!existe)
            throw new WebException("Este Inventario ["+this+"] no existe.");

        InventarioBussines inventarioBussines = new InventarioBussines();
        inventarioBussines.activaSegundoConteo(ds, this);
    }

    public void activaTercero(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.flinventario = Integer.parseInt(json.get("flinventario").toString());
        boolean existe = ds.exists(this);

        if (!existe)
            throw new WebException("Este Inventario ["+this+"] no existe.");

        InventarioBussines inventarioBussines = new InventarioBussines();
        inventarioBussines.activaTercerConteo(ds, this);
    }

    public void afecta(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.flinventario = Integer.parseInt(json.get("flinventario").toString());
        boolean existe = ds.exists(this);

        if (!existe)
            throw new WebException("Este Inventario ["+this+"] no existe.");

        InventarioBussines inventarioBussines = new InventarioBussines();
        inventarioBussines.afectaInventario(ds, this);
    }

    public void finaliza(DatabaseServices ds, String[] valores) throws Exception {
        JSONObject json = (JSONObject)JSONValue.parse(valores[0]);

        this.flinventario = Integer.parseInt(json.get("flinventario").toString());
        boolean existe = ds.exists(this);

        if (!existe)
            throw new WebException("Este Inventario ["+this+"] no existe.");

        InventarioBussines inventarioBussines = new InventarioBussines();
        inventarioBussines.finalizaInventario(ds, this);
    }
}
