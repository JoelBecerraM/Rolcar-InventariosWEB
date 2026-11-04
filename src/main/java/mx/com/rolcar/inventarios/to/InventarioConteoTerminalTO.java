package mx.com.rolcar.inventarios.to;

import com.atcloud.to.TransferObject;
import java.io.Serializable;

public class InventarioConteoTerminalTO implements TransferObject, Serializable {
    public String clave = "";
    public String descripcion = "";
    public String terminal = "";
    public double existencia = 0.0;
}
