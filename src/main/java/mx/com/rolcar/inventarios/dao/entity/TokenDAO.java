package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import java.util.Date;

public class TokenDAO implements DatabaseRecord {
    public String compania = "";
    public String usuario = "";
    public String token = "";
    public Date fecha = new Date(0);

    public TokenDAO() {
    }

    public TokenDAO(String compania, String usuario) {
        this.compania = compania;
        this.usuario = usuario;
    }

    @Override
    public String getTable() {
        return "Token";
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
        return compania+";"+usuario+";"+token;
    }
}