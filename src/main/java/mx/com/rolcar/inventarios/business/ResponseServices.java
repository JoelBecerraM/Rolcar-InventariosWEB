package mx.com.rolcar.inventarios.business;

import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.Fecha;
import mx.com.rolcar.inventarios.dao.entity.DispositivoDAO;
import mx.com.rolcar.inventarios.dao.entity.InformacionUsuarioDAO;
import mx.com.rolcar.inventarios.dao.entity.TokenDAO;
import mx.com.rolcar.inventarios.to.DispositivoTO;
import mx.com.rolcar.inventarios.to.FechaUltimaSincronizacionTO;
import mx.com.rolcar.inventarios.to.TokenTO;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

public class ResponseServices {
    static Logger log = Logger.getLogger(ResponseServices.class.getName());

    public TokenTO actualizaToken(DatabaseServices ds, String compania, String usuario, String token) throws Exception {
        TokenDAO tokenDAO = new TokenDAO();
        tokenDAO.compania = compania;
        tokenDAO.usuario = usuario;
        tokenDAO.token = token;
        tokenDAO.fecha = new Date();
        if (ds.update(tokenDAO) == 0) {
            ds.insert(tokenDAO);
        }
        TokenTO tokenTO = new TokenTO();
        tokenTO.Acompania = tokenDAO.compania;
        tokenTO.Busuario = tokenDAO.usuario;
        tokenTO.Ctoken = tokenDAO.token;
        return tokenTO;
    }

    public DispositivoTO actualizaDispositivo(DatabaseServices ds, String compania, String usuario, String serie, String numero, String sim, String imei, String version) throws Exception {
        DispositivoDAO dispositivoDAO = new DispositivoDAO();
        dispositivoDAO.compania = compania;
        dispositivoDAO.usuario = usuario;
        dispositivoDAO.serie = serie;
        dispositivoDAO.numero = numero;
        dispositivoDAO.sim = sim;
        dispositivoDAO.imei = imei;
        dispositivoDAO.version = version;
        dispositivoDAO.fecha = new Date();
        if (ds.update(dispositivoDAO) == 0) {
            ds.insert(dispositivoDAO);
        }
        DispositivoTO dispositivoTO = new DispositivoTO();
        dispositivoTO.Acompania = dispositivoDAO.compania;
        dispositivoTO.Busuario = dispositivoDAO.usuario;
        dispositivoTO.Cserie = dispositivoDAO.serie;
        dispositivoTO.Dnumero = dispositivoDAO.numero;
        dispositivoTO.Esim = dispositivoDAO.sim;
        dispositivoTO.Fimei = dispositivoDAO.imei;
        dispositivoTO.Gversion = dispositivoDAO.version;
        return dispositivoTO;
    }

    public FechaUltimaSincronizacionTO actualizaFechaUltimaSincronizacion(DatabaseServices ds, String compania, String usuario) throws Exception {
        InformacionUsuarioDAO informacionUsuarioDAO = new InformacionUsuarioDAO();
        informacionUsuarioDAO.compania = compania;
        informacionUsuarioDAO.usuario = usuario;
        informacionUsuarioDAO.password = "password";
        informacionUsuarioDAO.feultsincronizacion = new Date();
        ArrayList results = ds.select(informacionUsuarioDAO, "compania = '"+compania+"' AND usuario = '"+usuario+"'");
        FechaUltimaSincronizacionTO fechaUltimaSincronizacionTO = new FechaUltimaSincronizacionTO();
        if (!results.isEmpty()) {
            informacionUsuarioDAO = (InformacionUsuarioDAO)results.get(0);
        }
        fechaUltimaSincronizacionTO.Acompania = informacionUsuarioDAO.compania;
        fechaUltimaSincronizacionTO.Busuario = informacionUsuarioDAO.usuario;
        fechaUltimaSincronizacionTO.Cpassword = informacionUsuarioDAO.password;
        fechaUltimaSincronizacionTO.Dfechaultsincronizacion = Fecha.getFechaHora(informacionUsuarioDAO.feultsincronizacion);
        fechaUltimaSincronizacionTO.Efechahora = Fecha.getFechaHora();
        if (results.isEmpty()) {
            ds.insert(informacionUsuarioDAO);
        } else {
            informacionUsuarioDAO.feultsincronizacion = new Date();
            ds.update(informacionUsuarioDAO);
        }
        return fechaUltimaSincronizacionTO;
    }
}