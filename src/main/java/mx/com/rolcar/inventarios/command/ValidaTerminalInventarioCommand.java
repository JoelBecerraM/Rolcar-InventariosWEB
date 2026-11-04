package mx.com.rolcar.inventarios.command;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.atcloud.commerce.services.JSON;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.web.WebCommandInterface;
import com.atcloud.web.WebException;
import com.atcloud.to.ErrorTO;
import com.atcloud.util.Numero;
import mx.com.rolcar.inventarios.business.InventarioBussines;
import mx.com.rolcar.inventarios.dao.entity.InventarioCapturaDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.to.MensajeTO;
import mx.com.rolcar.inventarios.util.Constantes;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author jbecerra
 */
public class ValidaTerminalInventarioCommand implements WebCommandInterface {
    static Logger log = Logger.getLogger(ValidaTerminalInventarioCommand.class.getName());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, DatabaseServices ds) throws WebException {
        try {
            String compania = request.getParameter("compania");
            String folio = request.getParameter("folio");
            String terminal = request.getParameter("terminal");

            //
            //
            //
            InventarioBussines inventarioBussines = new InventarioBussines();
            InventarioDAO inventarioDAO = inventarioBussines.buscaInventario(ds, compania);
            if (inventarioDAO==null)
                throw new WebException("No existe Status de Inventario de la Compania ["+compania+"].");
            if (inventarioDAO.flinventario!=Numero.getIntFromString(folio))
                throw new WebException("El folio del Inventario ["+folio+"] no corresponde al Inventario de la Compania ["+inventarioDAO+"].");

            if (inventarioDAO.fase==null)
                throw new WebException("No se a realizado la Carga del Inventario ["+inventarioDAO.status+"].");
            if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0
                    ||inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0
                    ||inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_3ERCONTEO)==0))
                throw new WebException("La fase del Inventario es incorrecta ["+inventarioDAO.fase+"].");

            ArrayList capturas = ds.select(new InventarioCapturaDAO(), "flinventario = "+inventarioDAO.flinventario+" AND terminal = '"+terminal+"'");
            if (capturas.isEmpty())
                throw new WebException("No hay ninguna Captura de Inventario con esta clave de Terminal ["+terminal+"].");

            //
            //
            //

            MensajeTO mensajeTO = new MensajeTO();
            mensajeTO.msg = "OK";

            PrintWriter out = response.getWriter();
            JSON.writeObject(out, mensajeTO);
            out.close();

        } catch(WebException e) {
            ErrorTO errorTO = new ErrorTO();
            errorTO.fromException(e);

            try {
                PrintWriter out = response.getWriter();
                JSON.writeObject(out, errorTO);
                out.close();

            } catch(Exception ex) {
                throw new WebException(ex.getMessage());
            }

        } catch(Exception e) {
            e.printStackTrace();

            ErrorTO errorTO = new ErrorTO();
            errorTO.fromException(e);

            try {
                PrintWriter out = response.getWriter();
                JSON.writeObject(out, errorTO);
                out.close();

            } catch(Exception ex) {
                throw new WebException(ex.getMessage());
            }
        }
    }
}
