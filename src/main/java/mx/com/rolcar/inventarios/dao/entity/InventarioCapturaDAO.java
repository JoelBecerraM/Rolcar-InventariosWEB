package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import java.util.Date;

public class InventarioCapturaDAO implements DatabaseRecord {
    public Integer flcapturainventario;
    public Date fechatransmision = new Date(0);
    public int flinventario = 0;
    public String fase = "";
    public String compania = "";
    public String terminal = "";
    public String usuario = "";
    public int folio = 0;
    public String status = "";
    public Date fecreacion = null;
    public Date femodificacion = null;
    public Date fetermino = null;

    public InventarioCapturaDAO() {
    }

    public InventarioCapturaDAO(Integer flcapturainventario) {
        this.flcapturainventario = flcapturainventario;
    }

    @Override
    public String getTable() {
        return "InventarioCaptura";
    }

    @Override
    public String getOrder() {
        return "flcapturainventario";
    }

    @Override
    public String getWhere() {
        return "flcapturainventario = "+flcapturainventario;
    }

    @Override
    public String toString() {
        return flcapturainventario+";"+compania+";"+usuario;
    }
}
