package mx.com.rolcar.inventarios.to;

import com.atcloud.to.TransferObject;
import java.io.Serializable;

public class FechaUltimaSincronizacionTO implements TransferObject, Serializable {
    public String Acompania;
    public String Busuario;
    public String Cpassword;
    public String Dfechaultsincronizacion;
    public String Efechahora;
}