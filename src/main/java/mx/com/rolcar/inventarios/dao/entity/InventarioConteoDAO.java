package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;

public class InventarioConteoDAO implements DatabaseRecord {
    public int fldinventario = 0;
    public int flinventario = 0;
    public String clave = "";
    public String noparte = "";
    public String descripcion = "";
    public double precio = 0.0;
    public double existencia = 0.0;
    public double existenciac = 0.0;
    public double existencia1 = 0.0;
    public int conteos1 = 0;
    public double existencia2 = 0.0;
    public int conteos2 = 0;
    public double existencia3 = 0.0;
    public int conteos3 = 0;
    public String status = "";
    public String terminal = "";

    public InventarioConteoDAO() {
    }

    public InventarioConteoDAO(int fldinventario) {
        this.fldinventario = fldinventario;
    }

    @Override
    public String getTable() {
        return "InventarioConteo";
    }

    @Override
    public String getOrder() {
        return "fldinventario";
    }

    @Override
    public String getWhere() {
        return "fldinventario = "+fldinventario;
    }

    @Override
    public String toString() {
        return fldinventario+";";
    }
}
