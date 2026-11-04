package mx.com.rolcar.inventarios.to;

import com.atcloud.to.TransferObject;
import java.io.Serializable;

public class TerminaInventarioResponse implements TransferObject, Serializable {
    public int detalles = 0;
    public int capturas = 0;
    public double cantidad = 0.0;
    public String msg = "";
}
