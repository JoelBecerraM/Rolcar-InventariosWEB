package mx.com.rolcar.inventarios.to;

import com.atcloud.to.TransferObject;
import java.io.Serializable;

public class MensajeTO implements TransferObject, Serializable {
    public String wrn = "";
    public String msg = "";
}
