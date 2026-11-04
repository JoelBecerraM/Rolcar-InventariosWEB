package mx.com.rolcar.inventarios.collection;

import com.atcloud.collection.engine.CollectionRecord;
import java.util.Date;

/**
 *
 * @author joelbecerram
 */
public class AuditoriaInventariosCollection implements CollectionRecord {
    public int flinventario = 0;
    public int folio = 0;
    public String fase = "";
    public Date fechatransmision = new Date(0);
    public String compania = "";
    public String terminal = "";
    public String usuario = "";
    public Date fecreacion = null;
    public Date femodificacion = null;
    public Date fetermino = null;
    public String ubicacion = "";
    public Long lineas = 0l;
    public Double cantidad = 0.0;

    @Override
    public String getSQL() {
        return getSQL(getWhere());
    }

    @Override
    public String getSQL(String where) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ic.flinventario, ic.folio, ic.fase, ic.fechatransmision, ic.compania, ic.terminal, ic.usuario, ")
            .append("ic.fecreacion, ic.femodificacion, ic.fetermino, ")
            .append("icd.ubicacion, COALESCE(COUNT(icd.fldcapturainventario), 0) AS lineas, COALESCE(SUM(cantidad), 0.0) AS cantidad ")
            .append("FROM InventarioCaptura ic ")
            .append("INNER JOIN InventarioCapturaDetalle icd ON icd.flcapturainventario = ic.flcapturainventario ")
            .append("WHERE ").append(where).append(" ")
            .append("GROUP BY ic.flinventario, ic.folio, ic.fase, ic.fechatransmision, ic.compania, ic.terminal, ic.usuario, ")
            .append("ic.fecreacion, ic.femodificacion, ic.fetermino, icd.ubicacion ")
            .append("ORDER BY ic.fechatransmision DESC");
        return sql.toString();
    }

    @Override
    public String getWhere() {
        return "ic.folio = "+folio;
    }
}
