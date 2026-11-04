package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class CodigoDAO implements DatabaseRecord {
    public String compania = "";
    public String codigo = "";
    public String clave = "";
    public int piezas = 0;
    
    public CodigoDAO() {
    }
    
    public CodigoDAO(String compania, String codigo) {
        this.compania = compania;
        this.codigo = codigo;
    }
    
    @Override
    public String getTable() {
        return "Codigo";
    }
    
    @Override
    public String getOrder() {
        return "compania, codigo";
    }
    
    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND codigo = '"+codigo+"'";
    }        
    
    @Override
    public String toString() {
        return compania+";"+codigo+";"+clave;
    }
}
