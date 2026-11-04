package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class InventarioConteoUbicacionDAO implements DatabaseRecord {
    public Integer flduinventario = 0;
    public int fldinventario = 0;
    public int flinventario = 0;
    public String clave = "";
    public String noparte = "";
    public String descripcion = "";
    public double precio = 0.0;
    public String ubicacion = "";
    public double existenciainicial = 0.0;
    public double existencia = 0.0;
    public String terminal = "";
    public String usuario = "";

    public InventarioConteoUbicacionDAO() {
    }

    public InventarioConteoUbicacionDAO(int flduinventario) {
        this.flduinventario = flduinventario;
    }

    @Override
    public String getTable() {
        return "InventarioConteoUbicacion";
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
