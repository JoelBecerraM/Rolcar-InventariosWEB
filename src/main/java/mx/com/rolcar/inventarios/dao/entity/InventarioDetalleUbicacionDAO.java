package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class InventarioDetalleUbicacionDAO implements DatabaseRecord {
    public Integer flduinventario = 0;
    public int fldinventario = 0;
    public int flinventario = 0;
    public String clave = "";
    public String noparte = "";
    public String descripcion = "";
    public double precio = 0.0;
    public String ubicacion = "";
    public double existencia = 0.0;

    public InventarioDetalleUbicacionDAO() {
    }

    public InventarioDetalleUbicacionDAO(int flduinventario) {
        this.flduinventario = flduinventario;
    }

    @Override
    public String getTable() {
        return "InventarioDetalleUbicacion";
    }

    @Override
    public String getOrder() {
        return "flduinventario";
    }

    @Override
    public String getWhere() {
        return "flduinventario = "+flduinventario;
    }
}
