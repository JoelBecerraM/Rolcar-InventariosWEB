package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import java.util.Date;

public class InformacionUsuarioDAO implements DatabaseRecord, java.io.Serializable {
    public String compania = "";
    public String usuario = "";
    public String password = "";
    public Date feultsincronizacion = new Date(0);
    
    public InformacionUsuarioDAO() {
    }
    
    public InformacionUsuarioDAO(String compania, String usuario) {
        this.compania = compania;
        this.usuario = usuario;
    }
    
    @Override
    public String getTable() {
        return "InformacionUsuario";
    }
    
    @Override
    public String getOrder() {
        return "compania, usuario";
    }
    
    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND usuario = '"+usuario+"'";
    }
    
    @Override
    public String toString() {
        return compania+";"+usuario;
    }
}
