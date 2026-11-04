package mx.com.rolcar.inventarios.business;

import com.atcloud.dao.engine.DatabaseRecordEntity;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.util.Numero;
import com.atcloud.web.WebException;
import mx.com.rolcar.inventarios.dao.entity.CodigoDAO;
import java.util.ArrayList;
import java.util.Date;
import mx.com.rolcar.inventarios.dao.entity.CompaniaDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleDAO;
import mx.com.rolcar.inventarios.dao.entity.ProductoDAO;
import mx.com.rolcar.inventarios.util.Constantes;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleUbicacionDAO;
import mx.com.rolcar.inventarios.dao.entity.UbicacionDAO;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

public class InventarioBussines {
    static Logger log = Logger.getLogger(InventarioBussines.class.getName());

    public InventarioDAO buscaInventario(DatabaseServices ds, String compania) throws Exception {
        ArrayList<InventarioDAO> array = ds.select(new InventarioDAO(),
                "compania = '"+compania+"' AND status = '"+Constantes.ESTADO_PENDIENTE+"'", "flinventario DESC");
        if (array.isEmpty())
            return null;
        if (array.size()>1)
            throw new WebException("Existe mas de un Inventario Pendiente de la Compania ["+compania+"].");

        return (InventarioDAO)array.get(0);
    }

    public InventarioDAO creaInventario(DatabaseServices ds, String compania, int almacen) throws Exception {
        ArrayList<InventarioDAO> array = ds.select(new InventarioDAO(),
                "compania = '"+compania+"' AND status = '"+Constantes.ESTADO_PENDIENTE+"'", "flinventario DESC");
        if (!array.isEmpty())
            throw new WebException("Ya existe un Inventario [PE] en esta compania.");

        InventarioDAO inventarioDAO = new InventarioDAO();
        inventarioDAO.flinventario = null;
        inventarioDAO.compania = compania;
        inventarioDAO.almacen = 0;
        inventarioDAO.descripcion = "";
        inventarioDAO.feinicio = new Date();
        inventarioDAO.fetermino = null;
        inventarioDAO.status = Constantes.ESTADO_PENDIENTE;
        inventarioDAO.fase = null;
        inventarioDAO.fefase = null;
        inventarioDAO.feprimerconteo = null;
        inventarioDAO.fesegundoconteo = null;
        inventarioDAO.fetercerconteo = null;

        ds.insert(inventarioDAO);

        inventarioDAO.flinventario = (Integer)ds.aggregate(inventarioDAO, "max", "flinventario");

        return inventarioDAO;
    }

    public void cargaInventario(DatabaseServices ds, InventarioDAO inventarioDAO, byte[] archivo) throws Exception {
        // Valido el estado del inventario
        if (!(inventarioDAO.fase==null||inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_CARGAINICIAL)==0))
            throw new WebException("Solo se puede hacer la carga del Inventario en la fase inicial o con el estado "+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+".");

        // Actualizo el Catalogo de Productos
        CompaniaDAO companiaDAO = new CompaniaDAO();
        companiaDAO.compania = inventarioDAO.compania;
        if (!ds.exists(companiaDAO))
            throw new WebException("No existe esta compania "+companiaDAO+".");

        // Limpio los productos
        ds.delete(new ProductoDAO(), companiaDAO.getWhere());
        ds.delete(new UbicacionDAO(), companiaDAO.getWhere());
        ds.delete(new CodigoDAO(), companiaDAO.getWhere());
        ds.delete(new InventarioDetalleDAO(), inventarioDAO.getWhere());
        ds.delete(new InventarioDetalleUbicacionDAO(), inventarioDAO.getWhere());

        // Leo el archivo CSV y cargo todos los productos
        log.debug("Leo los archivo CSV ...");

        ByteArrayInputStream bis = new ByteArrayInputStream(archivo);
        InputStreamReader isr = new InputStreamReader(bis, "ISO-8859-1");

        int count = 0;
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(isr);
        for (CSVRecord record : records) {
            count++;

            String sku = record.get(0);
            if (sku.isEmpty())
                continue;

            String descripcion = "";
            String noparte = record.get(1);
            String costo = record.get(2);
            String existencia = record.get(3);
            String ubicacion = record.get(4);

            log.debug("["+count+"] SKU ["+sku+"] NoParte ["+noparte+"]");

            ArrayList<DatabaseRecordEntity> arrayMaestro =
                    ds.collection("SELECT clproducto, numero, descripcion FROM rolcar.CMAESTRO WHERE clproducto = '"+sku+"'");
            if (arrayMaestro.isEmpty()) {
                descripcion = noparte;
            } else {
                DatabaseRecordEntity recordMaestro = arrayMaestro.get(0);
                descripcion = recordMaestro.getString("descripcion")+" "+recordMaestro.getString("numero");
            }

            log.debug("["+count+"] Descripcion ["+descripcion+"]");

            ProductoDAO productoDAO = new ProductoDAO();
            productoDAO.compania = inventarioDAO.compania;
            productoDAO.clave = sku;
            productoDAO.descripcion = descripcion;
            productoDAO.precio = Numero.getDoubleFromString(costo);

            ds.save(productoDAO);

            UbicacionDAO ubicacionDAO = new UbicacionDAO();
            ubicacionDAO.compania = inventarioDAO.compania;
            ubicacionDAO.ubicacion = ubicacion;
            ubicacionDAO.descripcion = ubicacion;

            ds.save(ubicacionDAO);

            InventarioDetalleDAO inventarioDetalleDAO = (InventarioDetalleDAO)ds.first(new InventarioDetalleDAO(),
                    "flinventario = "+inventarioDAO.flinventario+" AND clave = '"+sku+"'");
            if (inventarioDetalleDAO==null) {
                inventarioDetalleDAO = new InventarioDetalleDAO();
                inventarioDetalleDAO.fldinventario = null;
                inventarioDetalleDAO.flinventario = inventarioDAO.flinventario;
                inventarioDetalleDAO.clave = sku;
                inventarioDetalleDAO.noparte = noparte;
                inventarioDetalleDAO.descripcion = descripcion;
                inventarioDetalleDAO.precio = Numero.getDoubleFromString(costo);
                inventarioDetalleDAO.existencia = Numero.getDoubleFromString(existencia);

                ds.insert(inventarioDetalleDAO);

                inventarioDetalleDAO.fldinventario = (Integer)ds.aggregate(inventarioDetalleDAO, "MAX", "fldinventario");

            } else {
                inventarioDetalleDAO.existencia += Numero.getDoubleFromString(existencia);

                ds.update(inventarioDetalleDAO, new String[] {"existencia"});
            }

            InventarioDetalleUbicacionDAO inventarioDetalleUbicacionDAO = new InventarioDetalleUbicacionDAO();
            inventarioDetalleUbicacionDAO.flduinventario = null;
            inventarioDetalleUbicacionDAO.fldinventario = inventarioDetalleDAO.fldinventario;
            inventarioDetalleUbicacionDAO.flinventario = inventarioDetalleDAO.flinventario;
            inventarioDetalleUbicacionDAO.clave = sku;
            inventarioDetalleUbicacionDAO.noparte = noparte;
            inventarioDetalleUbicacionDAO.descripcion = descripcion;
            inventarioDetalleUbicacionDAO.precio = Numero.getDoubleFromString(costo);
            inventarioDetalleUbicacionDAO.ubicacion = ubicacion;
            inventarioDetalleUbicacionDAO.existencia = Numero.getDoubleFromString(existencia);

            ds.insert(inventarioDetalleUbicacionDAO);
        }

        isr.close();
        bis.close();

        ds.update("UPDATE Producto p, rolcar.CMAESTRO m SET p.numero = m.numero WHERE p.compania = '"+companiaDAO.compania+"' AND p.clave = m.clproducto");

    }

    public void activaPrimerConteo(DatabaseServices ds, InventarioDAO inventarioDAO) throws Exception {
        // Valido el estado del inventario
        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_CARGAINICIAL)==0))
            throw new WebException("Solo se puede hacer el Primer Conteo del Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");

        // Paso el 1er conteo
        ds.update("DELETE FROM InventarioConteo WHERE "+inventarioDAO.getWhere());
        ds.update("INSERT INTO InventarioConteo "
               +"SELECT fldinventario, flinventario, clave, noparte, descripcion, precio, existencia, 0, 0, 0, 0, 0, 0, 0, 'PE', NULL "
               +"FROM InventarioDetalle WHERE flinventario = "+inventarioDAO.flinventario);

        // Cambia el status del inventario
        inventarioDAO.fase = Constantes.ESTADO_INVENTARIO_1ERCONTEO;
        inventarioDAO.fefase = new Date();
        inventarioDAO.feprimerconteo = new Date();

        ds.update(inventarioDAO, new String[]{"fase", "fefase", "feprimerconteo"});
    }

    public void activaSegundoConteo(DatabaseServices ds, InventarioDAO inventarioDAO) throws Exception {
        // Valido el estado del inventario
        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_1ERCONTEO)==0))
            throw new WebException("Solo se puede hacer el 2do Conteo del Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_1ERCONTEO+"].");

        // Verifico analisis pendientes
        if (ds.count(new InventarioConteoDAO(), "flinventario = "+inventarioDAO.flinventario+" AND status IN ('AN')")>0)
            throw new WebException("Aun tiene Análisis de Productos pendientes por concluir en las terminales.");

        // Paso el 2do conteo
        ds.update("UPDATE InventarioConteo SET existencia2 = existencia1 WHERE "+inventarioDAO.getWhere());

        // Cambia el status del inventario
        inventarioDAO.fase = Constantes.ESTADO_INVENTARIO_2DOCONTEO;
        inventarioDAO.fefase = new Date();
        inventarioDAO.fesegundoconteo = new Date();

        ds.update(inventarioDAO, new String[]{"fase", "fefase", "fesegundoconteo"});
    }

    public void activaTercerConteo(DatabaseServices ds, InventarioDAO inventarioDAO) throws Exception {
        // Valido el estado del inventario
        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_2DOCONTEO)==0))
            throw new WebException("Solo se puede hacer el 3er Conteo del Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_2DOCONTEO+"].");

        // Verifico analisis pendientes
        if (ds.count(new InventarioConteoDAO(), "flinventario = "+inventarioDAO.flinventario+" AND status IN ('AN')")>0)
            throw new WebException("Aun tiene Análisis de Productos pendientes por concluir en las terminales.");

        // Paso el 3er conteo
        ds.update("UPDATE InventarioConteo SET existencia3 = existencia2 WHERE "+inventarioDAO.getWhere());

        // Cambia el status del inventario
        inventarioDAO.fase = Constantes.ESTADO_INVENTARIO_3ERCONTEO;
        inventarioDAO.fefase = new Date();
        inventarioDAO.fetercerconteo = new Date();

        ds.update(inventarioDAO, new String[]{"fase", "fefase", "fetercerconteo"});
    }

    public void afectaInventario(DatabaseServices ds, InventarioDAO inventarioDAO) throws Exception {
        // Valido el estado del inventario
        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_3ERCONTEO)==0))
            throw new WebException("Solo se puede Finalizar el Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_3ERCONTEO+"].");

        // Verifico analisis pendientes
        if (ds.count(new InventarioConteoDAO(), "flinventario = "+inventarioDAO.flinventario+" AND status IN ('AN')")>0)
            throw new WebException("Aun tiene Análisis de Productos pendientes por concluir en las terminales.");

        // Cambia el status del inventario
        inventarioDAO.fase = Constantes.ESTADO_INVENTARIO_AFECTADO;
        inventarioDAO.fefase = new Date();

        ds.update(inventarioDAO, new String[]{"fase", "fefase", "fetermino"});
    }

    public void finalizaInventario(DatabaseServices ds, InventarioDAO inventarioDAO) throws Exception {
        // Valido el estado del inventario
        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_AFECTADO)==0))
            throw new WebException("Solo se puede Finalizar el Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_AFECTADO+"].");

        // Cambia el status del inventario
        inventarioDAO.status = Constantes.ESTADO_FINALIZADO;
        inventarioDAO.fetermino = new Date();

        ds.update(inventarioDAO, new String[]{"status", "fetermino"});
    }

    private String getCellValue(Cell cell) {
        String value = null;
        if (cell==null)
            return value;
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_STRING:
                value = String.valueOf(cell.getRichStringCellValue().getString());
                break;
            default:
                value = cell.toString();
                break;
        }
        //System.out.println(cell.getColumnIndex()+";"+value);
        return value.trim();
    }
}
