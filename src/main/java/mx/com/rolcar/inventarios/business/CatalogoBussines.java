package mx.com.rolcar.inventarios.business;

import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.web.WebException;
import mx.com.rolcar.inventarios.dao.entity.CompaniaDAO;
import mx.com.rolcar.inventarios.dao.entity.UbicacionDAO;
import java.io.ByteArrayInputStream;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class CatalogoBussines {
    static Logger log = Logger.getLogger(CatalogoBussines.class.getName());

    public void actualizaCatalogos(DatabaseServices ds, CompaniaDAO companiaDAO) throws Exception {
    }

    public void cargaUbicaciones(DatabaseServices ds, CompaniaDAO companiaDAO, byte[] archivo) throws Exception {
        // Actualizo el Catalogo de Productos
        if (!ds.exists(companiaDAO))
            throw new WebException("No existe esta compania "+companiaDAO+".");

        // Limpio los productos
        ds.delete(new UbicacionDAO(), companiaDAO.getWhere());

        // Leo el archivo de Excel y cargo todos las ubicaciones
        log.debug("Leo los archivo de Excel ...");

        ByteArrayInputStream bis = new ByteArrayInputStream(archivo);

        Workbook workBook = WorkbookFactory.create(bis);
        Sheet sheet = workBook.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        Row row0 = sheet.getRow(0);

        for (int i=1; i<=lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row==null)
                continue;            

            String ubicacion = getCellValue(row.getCell(0));
            String descripcion = getCellValue(row.getCell(1));

            if (ubicacion==null)
                continue;
            if (descripcion==null)
                continue;
            
            if (ubicacion.endsWith(".0"))
                ubicacion = ubicacion.substring(0, ubicacion.length() - 2);

            UbicacionDAO ubicacionDAO = new UbicacionDAO();
            ubicacionDAO.compania = companiaDAO.compania;
            ubicacionDAO.ubicacion = ubicacion;
            ubicacionDAO.descripcion = descripcion;

            ds.save(ubicacionDAO);
        }

        bis.close();
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
