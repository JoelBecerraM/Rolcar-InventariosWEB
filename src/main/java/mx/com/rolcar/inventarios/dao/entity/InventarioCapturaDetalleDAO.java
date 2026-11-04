package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class InventarioCapturaDetalleDAO implements DatabaseRecord {
    public Integer fldcapturainventario;
    public int flcapturainventario = 0;
    public int flinventario = 0;
    public String ubicacion = "";
    public String clave = "";
    public String noparte = "";
    public String ean = "";
    public String descripcion = "";
    public double cantidad = 0.0d;

    public InventarioCapturaDetalleDAO() {
    }

    public InventarioCapturaDetalleDAO(Integer fldcapturainventario) {
        this.fldcapturainventario = fldcapturainventario;
    }

    @Override
    public String getTable() {
        return "InventarioCapturaDetalle";
    }

    @Override
    public String getOrder() {
        return "fldcapturainventario";
    }

    @Override
    public String getWhere() {
        return "fldcapturainventario = "+fldcapturainventario;
    }

    @Override
    public String toString() {
        return fldcapturainventario+";"+ubicacion+";"+clave+";"+cantidad;
    }
}
