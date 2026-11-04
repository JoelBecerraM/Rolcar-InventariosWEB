package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class InventarioDetalleDAO implements DatabaseRecord {
    public Integer fldinventario = 0;
    public int flinventario = 0;
    public String clave = "";
    public String noparte = "";
    public String descripcion = "";
    public double precio = 0.0;
    public double existencia = 0.0;

    public InventarioDetalleDAO() {
    }

    public InventarioDetalleDAO(int fldinventario) {
        this.fldinventario = fldinventario;
    }

    @Override
    public String getTable() {
        return "InventarioDetalle";
    }

    @Override
    public String getOrder() {
        return "fldinventario";
    }

    @Override
    public String getWhere() {
        return "fldinventario = "+fldinventario;
    }
}
