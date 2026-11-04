package mx.com.rolcar.inventarios.collection;

import com.atcloud.collection.engine.CollectionRecord;

/**
 *
 * @author joelbecerram
 */
public class AnalisisInventarioCollection implements CollectionRecord {
    public int fldinventario = 0;
    public int flinventario = 0;
    public String clave = "";
    public String descripcion = "";
    public double precio = 0.0;
    public double existencia = 0.0;
    public double existenciac = 0.0;
    public double diferencia = 0.0;
    public double valor = 0.0;
    public String status = "";
    public String terminal = "";

    @Override
    public String getSQL() {
        return getSQL(getWhere());
    }

    @Override
    public String getSQL(String where) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT fldinventario, flinventario, clave, descripcion, precio, existencia, existenciac, ")
            .append("ROUND((existenciac - existencia), 2) AS diferencia, ROUND(((existenciac - existencia) * precio), 4) AS valor, ")
            .append("status, terminal ")
            .append("FROM InventarioConteo ")
            .append("WHERE ").append(where).append(" ")
            .append("ORDER BY clave");
        return sql.toString();
    }

    @Override
    public String getWhere() {
        return "flinventario = "+flinventario;
    }
}
