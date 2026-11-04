package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import java.util.Date;

public class DispositivoDAO implements DatabaseRecord {
    public String compania = "";
    public String usuario = "";
    public String serie = "";
    public String numero = "";
    public String sim = "";
    public String imei = "";
    public String version = "";
    public Date fecha = new Date(0);

    public DispositivoDAO() {
    }

    public DispositivoDAO(String compania, String usuario, String serie) {
        this.compania = compania;
        this.usuario = usuario;
        this.serie = serie;
    }

    @Override
    public String getTable() {
        return "Dispositivo";
    }

    @Override
    public String getOrder() {
        return "compania, usuario, clserie";
    }

    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND usuario = '"+usuario+"' AND serie = '"+serie+"'";
    }

    @Override
    public String toString() {
        return compania+";"+usuario+";"+serie+";"+numero;
    }
}