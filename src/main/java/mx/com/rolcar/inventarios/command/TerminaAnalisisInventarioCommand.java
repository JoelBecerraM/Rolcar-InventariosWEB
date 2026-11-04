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
import java.util.Date;
import mx.com.rolcar.inventarios.business.InventarioBussines;
import mx.com.rolcar.inventarios.collection.AnalisisInventarioCollection;
import mx.com.rolcar.inventarios.dao.entity.InventarioCapturaDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioCapturaDetalleDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoUbicacionDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleDAO;
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
public class TerminaAnalisisInventarioCommand implements WebCommandInterface {
    static Logger log = Logger.getLogger(TerminaAnalisisInventarioCommand.class.getName());

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, DatabaseServices ds) throws WebException {
        try {
            String compania = request.getParameter("compania");
            String terminal = request.getParameter("terminal");
            String usuario = request.getParameter("usuario");
            String analisis = request.getParameter("analisis");
            String detalles = request.getParameter("detalles");

            log.debug(analisis);
            JSONObject analisisInventario = (JSONObject)JSONValue.parse(analisis);

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

            AnalisisInventarioCollection analisisInventarioCollection = new AnalisisInventarioCollection();
            JSON.fromJson(analisisInventario, analisisInventarioCollection);

            if (analisisInventarioCollection.flinventario!=inventarioDAO.flinventario)
                throw new WebException("El folio de Inventario del Analisis de Inventario ["+analisisInventarioCollection.flinventario
                        +"] no es igual al folio del Inventario ["+inventarioDAO.flinventario+"].");

            InventarioCapturaDAO inventarioCapturaDAO = new InventarioCapturaDAO();
            inventarioCapturaDAO.flcapturainventario = null;
            inventarioCapturaDAO.fechatransmision = new Date();
            inventarioCapturaDAO.flinventario = inventarioDAO.flinventario;
            inventarioCapturaDAO.fase = inventarioDAO.fase;
            inventarioCapturaDAO.compania = compania;
            inventarioCapturaDAO.terminal = terminal;
            inventarioCapturaDAO.usuario = usuario;
            inventarioCapturaDAO.folio = 0;
            inventarioCapturaDAO.status = inventarioDAO.status;
            inventarioCapturaDAO.fecreacion = null;
            inventarioCapturaDAO.femodificacion = null;
            inventarioCapturaDAO.fetermino = null;

            try {
                ds.beginTransaction();

                ds.insert(inventarioCapturaDAO);

                inventarioCapturaDAO.flcapturainventario = (Integer)ds.aggregate(inventarioCapturaDAO, "max", "flcapturainventario");

                //
                // Detalles
                //
                for (int i=0; i<array.size(); i++) {
                    JSONObject detalle = (JSONObject)array.get(i);
                    // Este atributo no viene en el JSON del app movil, por eso lo pongo para evitar
                    // java.lang.IllegalArgumentException: Can not set double field mx.com.rolcar.inventarios.dao.entity.InventarioConteoUbicacionDAO.existenciainicial to null value
                    detalle.put("existenciainicial", 0);

                    InventarioConteoUbicacionDAO inventarioConteoUbicacionDAO0 = new InventarioConteoUbicacionDAO();
                    JSON.fromJson(detalle, inventarioConteoUbicacionDAO0);

                    // Si es cero flduinventario entonces se creo la ubicacion en la UI del App de Analisis de Inventario, se inserta la nueva ubicacion
                    if (inventarioConteoUbicacionDAO0.flduinventario==0) {

                        InventarioConteoDAO inventarioConteoDAO = new InventarioConteoDAO(inventarioConteoUbicacionDAO0.fldinventario);
                        if (!ds.exists(inventarioConteoDAO))
                            throw new WebException("Este Conteo de Inventario ["+inventarioConteoDAO+"] no existe.");

                        if (inventarioConteoUbicacionDAO0.flinventario!=inventarioConteoDAO.flinventario)
                            throw new WebException("El folio de Inventario del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.flinventario
                                    +"] no es igual al folio del Inventario ["+inventarioConteoDAO.flinventario+"].");
                        if (inventarioConteoUbicacionDAO0.fldinventario!=inventarioConteoDAO.fldinventario)
                            throw new WebException("El folio del Detalle de Inventario del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.fldinventario
                                    +"] no es igual al folio del Detalle de Inventario ["+inventarioConteoDAO.fldinventario+"].");
                        if (inventarioConteoUbicacionDAO0.clave.compareTo(inventarioConteoDAO.clave)!=0)
                            throw new WebException("La Clave del Detalle del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.clave
                                    +"] no es igual a la Clave del Detalle de Inventario ["+inventarioConteoDAO.clave+"].");

                        //
                        // El 1er Conteo
                        //
                        if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0) {
                            inventarioConteoDAO.existenciac += inventarioConteoUbicacionDAO0.existencia;
                            inventarioConteoDAO.existencia1 += inventarioConteoUbicacionDAO0.existencia;

                            ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia1"});
                        }
                        //
                        // El 2do Conteo
                        //
                        else if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0) {
                            inventarioConteoDAO.existenciac += inventarioConteoUbicacionDAO0.existencia;
                            inventarioConteoDAO.existencia2 += inventarioConteoUbicacionDAO0.existencia;

                            ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia2"});
                        }
                        //
                        // El 3er Conteo
                        //
                        else if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_3ERCONTEO)==0) {
                            inventarioConteoDAO.existenciac += inventarioConteoUbicacionDAO0.existencia;
                            inventarioConteoDAO.existencia3 += inventarioConteoUbicacionDAO0.existencia;

                            ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia3"});
                        }


                        inventarioConteoUbicacionDAO0.flduinventario = null;
                        inventarioConteoUbicacionDAO0.noparte = inventarioConteoDAO.noparte;
                        inventarioConteoUbicacionDAO0.existenciainicial = inventarioConteoUbicacionDAO0.existencia;
                        inventarioConteoUbicacionDAO0.terminal = terminal;
                        inventarioConteoUbicacionDAO0.usuario = usuario;

                        ds.insert(inventarioConteoUbicacionDAO0);

                        inventarioConteoUbicacionDAO0.flduinventario = (Integer)ds.aggregate(inventarioConteoUbicacionDAO0, "max", "flduinventario");
                    }

                    InventarioConteoUbicacionDAO inventarioConteoUbicacionDAO1 = new InventarioConteoUbicacionDAO();
                    inventarioConteoUbicacionDAO1.flduinventario = inventarioConteoUbicacionDAO0.flduinventario;
                    if (!ds.exists(inventarioConteoUbicacionDAO1))
                        throw new WebException("Este Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO1+"] no existe.");

                    if (inventarioConteoUbicacionDAO0.flinventario!=inventarioConteoUbicacionDAO1.flinventario)
                        throw new WebException("El folio de Inventario del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.flinventario
                                +"] no es igual al folio del Inventario ["+inventarioConteoUbicacionDAO1.flinventario+"].");
                    if (inventarioConteoUbicacionDAO0.fldinventario!=inventarioConteoUbicacionDAO1.fldinventario)
                        throw new WebException("El folio del Detalle de Inventario del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.fldinventario
                                +"] no es igual al folio del Detalle de Inventario ["+inventarioConteoUbicacionDAO1.fldinventario+"].");
                    if (inventarioConteoUbicacionDAO0.clave.compareTo(inventarioConteoUbicacionDAO1.clave)!=0)
                        throw new WebException("La Clave del Detalle del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.clave
                                +"] no es igual a la Clave del Detalle de Inventario ["+inventarioConteoUbicacionDAO1.clave+"].");
                    if (inventarioConteoUbicacionDAO0.ubicacion.compareTo(inventarioConteoUbicacionDAO1.ubicacion)!=0)
                        throw new WebException("La Ubicación del Detalle del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.ubicacion
                                +"] no es igual a la Ubicación del Detalle de Inventario ["+inventarioConteoUbicacionDAO1.ubicacion+"].");

                    if (inventarioConteoUbicacionDAO0.existencia!=inventarioConteoUbicacionDAO1.existencia)
                        throw new WebException("La Existencia del Detalle del Conteo de Inventario por Ubicacion ["+inventarioConteoUbicacionDAO0.existencia
                                +"] no es igual a la Existencia del Detalle de Inventario ["+inventarioConteoUbicacionDAO1.existencia+"].");

                    if (!detalle.containsKey("verificado"))
                        throw new WebException("El Conteo de Inventario por Ubicacion no trae el atributo de Verificado.");

                    double verificado = Double.parseDouble(detalle.get("verificado").toString());
                    double ajuste = verificado - inventarioConteoUbicacionDAO0.existencia;

                    InventarioCapturaDetalleDAO inventarioCapturaDetalleDAO = new InventarioCapturaDetalleDAO();
                    inventarioCapturaDetalleDAO.fldcapturainventario = null;
                    inventarioCapturaDetalleDAO.flcapturainventario = inventarioCapturaDAO.flcapturainventario;
                    inventarioCapturaDetalleDAO.flinventario = inventarioCapturaDAO.flinventario;
                    inventarioCapturaDetalleDAO.ubicacion = inventarioConteoUbicacionDAO0.ubicacion;
                    inventarioCapturaDetalleDAO.clave = inventarioConteoUbicacionDAO0.clave;
                    inventarioCapturaDetalleDAO.noparte = inventarioConteoUbicacionDAO0.noparte;
                    inventarioCapturaDetalleDAO.descripcion = inventarioConteoUbicacionDAO0.descripcion;
                    inventarioCapturaDetalleDAO.cantidad = verificado;

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

                    if ((inventarioConteoDAO.existenciac + ajuste) < 0)
                        throw new WebException("La Existencia del Conteo después del Ajuste ["+ajuste+";"+inventarioConteoDAO.existenciac+"] es menor a cero.");

                    //
                    inventarioCapturaDetalleDAO.noparte = inventarioDetalleDAO.noparte;
                    inventarioCapturaDetalleDAO.ean = "";

                    ds.insert(inventarioCapturaDetalleDAO);

                    //
                    // Se reemplaza la existencia en el conteo por ubicación.
                    //
                    double existenciaUbicacion = inventarioConteoUbicacionDAO0.existencia;

                    inventarioConteoUbicacionDAO0.existencia = inventarioCapturaDetalleDAO.cantidad;
                    inventarioConteoUbicacionDAO0.terminal = terminal;
                    inventarioConteoUbicacionDAO0.usuario = usuario;

                    ds.update(inventarioConteoUbicacionDAO0, new String[] {"existencia", "terminal", "usuario"});

                    //
                    // La Existencia del Conteo se reemplaza:
                    //      1ro se resta la que estaba.
                    //      2do se suma la que viene en el conteo.
                    //

                    //
                    // El 1er Conteo
                    //
                    if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0) {
                        inventarioConteoDAO.existenciac -= existenciaUbicacion;
                        inventarioConteoDAO.existencia1 -= existenciaUbicacion;

                        inventarioConteoDAO.existenciac += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.existencia1 += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.conteos1 ++;

                        ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia1", "conteos1"});
                    }
                    //
                    // El 2do Conteo
                    //
                    else if (inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0) {
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
                        inventarioConteoDAO.existenciac -= existenciaUbicacion;
                        inventarioConteoDAO.existencia3 -= existenciaUbicacion;

                        inventarioConteoDAO.existenciac += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.existencia3 += inventarioCapturaDetalleDAO.cantidad;
                        inventarioConteoDAO.conteos3 ++;

                        ds.update(inventarioConteoDAO, new String[] {"existenciac", "existencia3", "conteos3"});
                    }

                }

                //
                // Desactiva el analisis de este de inventario
                //
                ds.update("UPDATE InventarioConteo SET status = 'TE', terminal = NULL "
                        +"WHERE fldinventario = "+analisisInventarioCollection.fldinventario+" "
                        +"AND flinventario = "+analisisInventarioCollection.flinventario);

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
}
