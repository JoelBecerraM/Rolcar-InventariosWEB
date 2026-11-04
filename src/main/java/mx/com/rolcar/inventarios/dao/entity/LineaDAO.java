package mx.com.rolcar.inventarios.dao.entity;

import com.atcloud.dao.engine.DatabaseRecord;
import com.atcloud.dao.engine.DatabaseRecordABC;

public class LineaDAO implements DatabaseRecord, DatabaseRecordABC {
    public String compania = "";
    public String linea = "";
    public String descripcion = "";
    public String status = "";

    public LineaDAO() {
    }

    public LineaDAO(String compania, String linea) {
        this.compania = compania;
        this.linea = linea;
    }

    @Override
    public String getTable() {
        return "Linea";
    }

    @Override
    public String getOrder() {
        return "compania, linea";
    }

    @Override
    public String getWhere() {
        return "compania = '"+compania+"' AND linea = '"+linea+"'";
    }

    @Override
    public String getWhereFirst() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getWhereNext() {
        return "compania = '"+compania+"' AND linea > '"+linea+"'";
    }

    @Override
    public String getWherePrev() {
        return "compania = '"+compania+"' AND linea < '"+linea+"'";
    }

    @Override
    public String getWhereLast() {
        return "compania = '"+compania+"'";
    }

    @Override
    public String getOrderFirst() {
        return "compania, linea";
    }

    @Override
    public String getOrderLast() {
        return "compania, linea DESC";
    }

    @Override
    public void setKey(String[] values) throws Exception {
        compania = values[0];
        linea = values[1];
    }

    @Override
    public void setValues(String[] values) throws Exception {
        compania = values[0];
        linea = values[1];
    }

    @Override
    public String toString() {
        return compania+";"+linea+";"+descripcion;
    }
}
