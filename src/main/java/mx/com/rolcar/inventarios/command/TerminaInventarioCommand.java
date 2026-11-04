package mx.com.rolcar.inventarios.command;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.atcloud.commerce.services.JSON;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.web.WebCommandInterface;
import com.atcloud.web.WebException;
import com.atcloud.to.ErrorTO;
import com.atcloud.util.Fecha;
import com.atcloud.util.Numero;
import com.atcloud.util.Reflector;
import java.util.Date;
import mx.com.rolcar.inventarios.business.InventarioBussines;
import mx.com.rolcar.inventarios.dao.entity.InventarioCapturaDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioCapturaDetalleDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoUbicacionDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleUbicacionDAO;
import mx.com.rolcar.inventarios.to.TerminaInventarioResponse;
import mx.com.rolcar.inventarios.util.Constantes;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author jbecerra
 */
public class TerminaInventarioCommand implements WebCommandInterface {
    static Logger log = Logger.getLogger(TerminaInventarioCommand.class.getName());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, DatabaseServices ds) throws WebException {
        try {
            String compania = request.getParameter("compania");
            String folio = request.getParameter("folio");
            String fecreacion = request.getParameter("fecreacion");
            String femodificacion = request.getParameter("femodificacion");
            String fetermino = request.getParameter("fetermino");
            String terminal = request.getParameter("terminal");
            String usuario = request.getParameter("usuario");
            String detalles = request.getParameter("detalles");

            log.debug(detalles);
            JSONArray array = (JSONArray)JSONValue.parse(detalles);

            TerminaInventarioResponse terminaInventarioResponse = new TerminaInventarioResponse();
            terminaInventarioResponse.detalles = array.size();

            //
            //
            //
            InventarioBussines inventarioBussines = new InventarioBussines();
            InventarioDAO inventarioDAO = inventarioBussines.buscaInventario(ds, compania);
            if (inventarioDAO==null)
                throw new WebException("No existe Status de Inventario de la Compania ["+compania+"].");

            if (inventarioDAO.fase==null)
                throw new WebException("No se a realizado la Carga del Inventario ["+inventarioDAO.status+"].");
            if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0
                    ||inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0
                    ||inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_3ERCONTEO)==0))
                throw new WebException("La fase del Inventario es incorrecta ["+inventarioDAO.fase+"].");

            InventarioCapturaDAO inventarioCapturaDAO = new InventarioCapturaDAO();
            inventarioCapturaDAO.flcapturainventario = null;
            inventarioCapturaDAO.fechatransmision = new Date();
            inventarioCapturaDAO.flinventario = inventarioDAO.flinventario;
            inventarioCapturaDAO.fase = inventarioDAO.fase;
            inventarioCapturaDAO.compania = compania;
            inventarioCapturaDAO.terminal = terminal;
            inventarioCapturaDAO.usuario = usuario;
            inventarioCapturaDAO.folio = Numero.getIntFromString(folio);
            inventarioCapturaDAO.status = inventarioDAO.status;
            inventarioCapturaDAO.fecreacion = getDate(fecreacion);
            inventarioCapturaDAO.femodificacion = getDate(femodificacion);
            inventarioCapturaDAO.fetermino = getDate(fetermino);

            try {
                ds.beginTransaction();

                ds.insert(inventarioCapturaDAO);

                inventarioCapturaDAO.flcapturainventario = (Integer)ds.aggregate(inventarioCapturaDAO, "max", "flcapturainventario");

                //
                // Detalles
                //
                for (int i=0; i<array.size(); i++) {
                    JSONObject detalle = (JSONObject)array.get(i);

                    InventarioCapturaDetalleDAO inventarioCapturaDetalleDAO = new InventarioCapturaDetalleDAO();
                    inventarioCapturaDetalleDAO.fldcapturainventario = null;
                    inventarioCapturaDetalleDAO.flcapturainventario = inventarioCapturaDAO.flcapturainventario;
                    inventarioCapturaDetalleDAO.flinventario = inventarioCapturaDAO.flinventario;
                    inventarioCapturaDetalleDAO.ubicacion = (String)detalle.get("ubicacion");
                    inventarioCapturaDetalleDAO.clave = (String)detalle.get("sku");
                    inventarioCapturaDetalleDAO.ean = (String)detalle.get("ean");
                    inventarioCapturaDetalleDAO.descripcion = (String)detalle.get("descripcion");
                    inventarioCapturaDetalleDAO.cantidad = Double.parseDouble(detalle.get("cantidad").toString());

                    terminaInventarioResponse.capturas ++;
                    terminaInventarioResponse.cantidad = Numero.redondea(terminaInventarioResponse.cantidad + inventarioCapturaDetalleDAO.cantidad);

                    InventarioDetalleDAO inventarioDetalleDAO = (InventarioDetalleDAO)ds.first(new InventarioDetalleDAO(),
                            "flinventario = "+inventarioCapturaDetalleDAO.flinventario+" AND clave = '"+inventarioCapturaDetalleDAO.clave+"'");
                    if (inventarioDetalleDAO==null)
                        throw new WebException("No existe este Detalle de Inventario ["+inventarioCapturaDetalleDAO.clave+"]");

                    InventarioConteoDAO inventarioConteoDAO = (InventarioConteoDAO)ds.first(new InventarioConteoDAO(),
                            "flinventario = "+inventarioCapturaDetalleDAO.flinventario+" AND clave = '"+inventarioCapturaDetalleDAO.clave+"'");
                    if (inventarioConteoDAO==null)
                        throw new WebException("No existe este Conteo de Inventario ["+inventarioCapturaDetalleDAO.clave+"]");
                    if (inventarioConteoDAO.status.compareTo("AN")==0)
                        throw new WebException("Este Conteo de Inventario ["+inventarioCapturaDetalleDAO.clave+"] esta en Análisis.");

                    //
                    inventarioCapturaDetalleDAO.noparte = inventarioDetalleDAO.noparte;

                    ds.insert(inventarioCapturaDetalleDAO);

                    //
                    // Conteo por Ubicacion
                    //
                    InventarioConteoUbicacionDAO inventarioConteoUbicacionDAO = (InventarioConteoUbicacionDAO)ds.first(new InventarioConteoUbicacionDAO(),
                            "flinventario = "+inventarioCapturaDetalleDAO.flinventario+" AND clave = '"
                                    +inventarioCapturaDetalleDAO.clave+"' AND ubicacion = '"+inventarioCapturaDetalleDAO.ubicacion+"'");

                    double existenciaUbicacion = 0.0d;

                    if (inventarioConteoUbicacionDAO==null) {
                        //
                        // Se crea la existencia del conteo por ubicación.
                        //
                        InventarioDetalleUbicacionDAO inventarioDetalleUbicacionDAO = (InventarioDetalleUbicacionDAO)ds.first(new InventarioDetalleUbicacionDAO(),
                            "flinventario = "+inventarioCapturaDetalleDAO.flinventario+" AND clave = '"
                                    +inventarioCapturaDetalleDAO.clave+"' AND ubicacion = '"+inventarioCapturaDetalleDAO.ubicacion+"'");
                        double existenciainicial = inventarioDetalleUbicacionDAO==null ? 0.0d : inventarioDetalleUbicacionDAO.existencia;

                        inventarioConteoUbicacionDAO = new InventarioConteoUbicacionDAO();
                        Reflector.copyAllFields(inventarioConteoDAO, inventarioConteoUbicacionDAO);
                        inventarioConteoUbicacionDAO.flduinventario = null;
                        inventarioConteoUbicacionDAO.ubicacion = inventarioCapturaDetalleDAO.ubicacion;
                        inventarioConteoUbicacionDAO.existenciainicial = existenciainicial;
                        inventarioConteoUbicacionDAO.existencia = inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoUbicacionDAO.terminal = terminal;
                        inventarioConteoUbicacionDAO.usuario = usuario;

                        ds.insert(inventarioConteoUbicacionDAO);

                    } else {
                        //
                        // Se reemplaza la existencia en el conteo por ubicación.
                        //
                        existenciaUbicacion = inventarioConteoUbicacionDAO.existencia;

                        inventarioConteoUbicacionDAO.existencia = inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoUbicacionDAO.terminal = terminal;
                        inventarioConteoUbicacionDAO.usuario = usuario;

                        // Suma la Existencia en el Primer Conteo
                        if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0)
                            inventarioConteoUbicacionDAO.existencia = existenciaUbicacion + inventarioCapturaDetalleDAO.cantidad;

                        ds.update(inventarioConteoUbicacionDAO, new String[] {"existencia", "terminal", "usuario"});
                    }

                    //
                    // El 1er Conteo
                    //
                    if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0) {
                        // Suma la Existencia en el Primer Conteo
                        inventarioConteoDAO.existenciac += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.existencia1 += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.conteos1 ++;

                        ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia1", "conteos1"});
                    }

                    //
                    // El 2do Conteo
                    //
                    else if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0) {
                        //
                        // La Existencia del Conteo se reemplaza:
                        //      1ro se resta la que estaba.
                        //      2do se suma la que viene en el conteo.
                        //

                        // Resta y luego Suma la Existencia es un REEMPLAZO de Existencia
                        inventarioConteoDAO.existenciac -= existenciaUbicacion;
                        inventarioConteoDAO.existencia2 -= existenciaUbicacion;

                        inventarioConteoDAO.existenciac += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.existencia2 += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.conteos2 ++;

                        ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia2", "conteos2"});
                    }
                    //
                    // El 3er Conteo
                    //
                    else if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_3ERCONTEO)==0) {
                        //
                        // La Existencia del Conteo se reemplaza:
                        //      1ro se resta la que estaba.
                        //      2do se suma la que viene en el conteo.
                        //

                        // Resta y luego Suma la Existencia es un REEMPLAZO de Existencia
                        inventarioConteoDAO.existenciac -= existenciaUbicacion;
                        inventarioConteoDAO.existencia3 -= existenciaUbicacion;

                        inventarioConteoDAO.existenciac += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.existencia3 += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.conteos3 ++;

                        ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia3", "conteos3"});
                    }

                }

                ds.commit();
            } catch(Exception e) {
                ds.rollback();

                log.error(e.getMessage(), e);
                throw e;
            }
            //
            //
            //

            terminaInventarioResponse.msg = "OK";

            try (PrintWriter out = response.getWriter()) {
                JSON.writeObject(out, terminaInventarioResponse);
            }

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

    private Date getDate(String fechahora) {
        if (fechahora==null||fechahora.isEmpty())
            return null;
        if (fechahora.length()!=19)
            return null;
        try {
            return Fecha.getDate(fechahora);
        } catch(Exception e) {
            return null;
        }
    }
}
