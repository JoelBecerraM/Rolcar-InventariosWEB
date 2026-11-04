package mx.com.rolcar.inventarios.exportacion;

import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.servlets.ExportadorClass;
import com.atcloud.util.Numero;
import com.atcloud.web.WebException;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioConteoUbicacionDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.util.Constantes;
import java.awt.Color;
import java.sql.Connection;
import java.util.ArrayList;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.com.rolcar.inventarios.collection.AuditoriaInventariosCollection;
import mx.com.rolcar.inventarios.dao.entity.CompaniaDAO;
import mx.com.rolcar.inventarios.dao.entity.InventarioDetalleUbicacionDAO;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportadorExcelImp implements ExportadorClass {
    static Logger log = Logger.getLogger(ExportadorExcelImp.class);
    private DatabaseServices ds = null;

    @Override
    public void setConnection(Connection connection) {
    }

    @Override
    public void setDatabaseServices(DatabaseServices databaseServices) {
        this.ds = databaseServices;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
    }

    public void exportaDiferenciasInventarioConteo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"DiferenciasInventarioConteo.xlsx\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String flinventario = request.getParameter("flinventario");
        int conteo = Numero.getIntFromString(request.getParameter("conteo"));

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFCellStyle headerStyle = wb.createCellStyle();
        XSSFFont fontHeader = wb.createFont();
        fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        fontHeader.setColor(new XSSFColor(Color.white));
        headerStyle.setFont(fontHeader);
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(Color.lightGray));
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        makeSheetConteos(wb, headerStyle, flinventario, conteo);
        makeSheetConteosUbicacion(wb, headerStyle, flinventario, conteo);

        try (ServletOutputStream out = response.getOutputStream()) {
            wb.write(out);
        }
    }

    public void makeSheetConteosUbicacion(XSSFWorkbook wb, XSSFCellStyle headerStyle,
            String flinventario, int conteo) throws Exception {

        XSSFSheet sheet = wb.createSheet("ubicaciones");
        sheet.setSelected(true);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);
        String[] headers = {
            "CODIGO", "NOPARTE", "DESCRIPCION", "UBICACION", "CONTEO", "USUARIO"
        };

        int colNo = 0;
        for(String header : headers) {
            cell = row.createCell(colNo++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(0, (header.length()*1 * 450));
        }

        int rowNo = 1;

        XSSFCellStyle numeroStyle = wb.createCellStyle();
        XSSFDataFormat dfNumero = wb.createDataFormat();
        numeroStyle.setDataFormat(dfNumero.getFormat("#,##0.00;[Red]-#,##0.00"));

        ArrayList productos = ds.select(new InventarioConteoUbicacionDAO(), "flinventario = "+flinventario, "clave, ubicacion");

        for(Object object : productos) {
            InventarioConteoUbicacionDAO inventarioConteoUbicacionDAO = (InventarioConteoUbicacionDAO)object;

            row = sheet.createRow(rowNo);
            colNo = 0;
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.clave);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.noparte);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.descripcion);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.ubicacion);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoUbicacionDAO.usuario);

            rowNo++;
        }

        colNo = 0;
        for(String header : headers) {
            sheet.autoSizeColumn(colNo++);
        }
    }

    public void makeSheetConteos(XSSFWorkbook wb, XSSFCellStyle headerStyle,
            String flinventario, int conteo) throws Exception {

        XSSFSheet sheet = wb.createSheet("diferencias");
        sheet.setSelected(true);

        XSSFRow row;
        XSSFCell cell;

        row = sheet.createRow(0);
        String[] headers = {
            "CODIGO", "NOPARTE", "DESCRIPCION", "PU", "TEORICO", "CONTEO", "DIFF", "PT",
            "---",
            "1ER", "DIFF 1ER", "2DO", "DIFF 2DO", "3ER", "DIFF 3ER"
        };

        int colNo = 0;
        for(String header : headers) {
            cell = row.createCell(colNo++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(0, (header.length()*1 * 450));
        }

        int rowNo = 1;

        XSSFCellStyle monedaStyle = wb.createCellStyle();
        XSSFDataFormat dfMoneda = wb.createDataFormat();
        monedaStyle.setDataFormat(dfMoneda.getFormat("$#,##0.00;[Red]$-#,##0.00"));

        XSSFCellStyle numeroStyle = wb.createCellStyle();
        XSSFDataFormat dfNumero = wb.createDataFormat();
        numeroStyle.setDataFormat(dfMoneda.getFormat("#,##0.00;[Red]-#,##0.00"));

        ArrayList productos = ds.select(new InventarioConteoDAO(), "flinventario = "+flinventario, "descripcion");

        for(Object object : productos) {
            InventarioConteoDAO inventarioConteoDAO = (InventarioConteoDAO)object;

            double existencia = 0.0;
            double conteoExistencia = 0.0;
            double diferenciaExistencia = 0.0;
            double costo = 0.0;
            switch(conteo) {
                case 1:
                    existencia = inventarioConteoDAO.existencia;
                    conteoExistencia = inventarioConteoDAO.existencia1;
                    diferenciaExistencia = conteoExistencia - existencia;
                    costo = diferenciaExistencia * inventarioConteoDAO.precio;
                    break;
                case 2:
                    existencia = inventarioConteoDAO.existencia;
                    conteoExistencia = inventarioConteoDAO.existencia2;
                    diferenciaExistencia = conteoExistencia - existencia;
                    costo = diferenciaExistencia * inventarioConteoDAO.precio;
                    break;
                case 3:
                    existencia = inventarioConteoDAO.existencia;
                    conteoExistencia = inventarioConteoDAO.existencia3;
                    diferenciaExistencia = conteoExistencia - existencia;
                    costo = diferenciaExistencia * inventarioConteoDAO.precio;
                    break;
            }

            row = sheet.createRow(rowNo);
            colNo = 0;
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.clave);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.noparte);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.descripcion);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.precio);
            cell.setCellStyle(monedaStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(conteoExistencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(diferenciaExistencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(Numero.redondea(costo));
            cell.setCellStyle(monedaStyle);

            cell = row.createCell(colNo++);

            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia1);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia1 - inventarioConteoDAO.existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia2);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia2 - inventarioConteoDAO.existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia3);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.existencia3 - inventarioConteoDAO.existencia);
            cell.setCellStyle(numeroStyle);

            rowNo++;
        }

        colNo = 0;
        for(String header : headers) {
            sheet.autoSizeColumn(colNo++);
        }

        sheet.setColumnWidth(7, (10 * 250));
    }

    public void exportaValuacionInventarioConteo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"ValuacionInventarioConteo.xlsx\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String flinventario = request.getParameter("flinventario");
        int conteo = Numero.getIntFromString(request.getParameter("conteo"));

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("valuacion");
        sheet.setSelected(true);

        XSSFRow row;
        XSSFCell cell;

        XSSFCellStyle headerStyle = wb.createCellStyle();
        XSSFFont fontHeader = wb.createFont();
        fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        fontHeader.setColor(new XSSFColor(Color.white));
        headerStyle.setFont(fontHeader);
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(Color.lightGray));
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        row = sheet.createRow(0);
        String[] headers = {
            "CODIGO", "NOPARTE", "DESCRIPCION", "CONTEO", "PU", "PT"
        };

        int colNo = 0;
        for(String header : headers) {
            cell = row.createCell(colNo++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(0, (header.length()*1 * 450));
        }

        int rowNo = 1;

        XSSFCellStyle monedaStyle = wb.createCellStyle();
        XSSFDataFormat dfMoneda = wb.createDataFormat();
        monedaStyle.setDataFormat(dfMoneda.getFormat("$#,##0.00"));

        XSSFCellStyle numeroStyle = wb.createCellStyle();
        XSSFDataFormat dfNumero = wb.createDataFormat();
        numeroStyle.setDataFormat(dfNumero.getFormat("#,##0.00"));

        ArrayList productos = ds.select(new InventarioConteoDAO(), "flinventario = "+flinventario, "descripcion");

        for(Object object : productos) {
            InventarioConteoDAO inventarioConteoDAO = (InventarioConteoDAO)object;

            double existencia = 0.0;
            double costo = 0.0;
            switch(conteo) {
                case 1:
                    existencia = inventarioConteoDAO.existencia1;
                    costo = existencia * inventarioConteoDAO.precio;
                    break;
                case 2:
                    existencia = inventarioConteoDAO.existencia2;
                    costo = existencia * inventarioConteoDAO.precio;
                    break;
                case 3:
                    existencia = inventarioConteoDAO.existencia3;
                    costo = existencia * inventarioConteoDAO.precio;
                    break;
            }

            if (existencia==0.0)
                continue;

            row = sheet.createRow(rowNo);
            colNo = 0;
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.clave);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.noparte);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.descripcion);
            cell = row.createCell(colNo++);
            cell.setCellValue(existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioConteoDAO.precio);
            cell.setCellStyle(monedaStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(Numero.redondea(costo));
            cell.setCellStyle(monedaStyle);

            rowNo++;
        }

        colNo = 0;
        for(String header : headers) {
            sheet.autoSizeColumn(colNo++);
        }

        try (ServletOutputStream out = response.getOutputStream()) {
            wb.write(out);
        }
    }

    public void exportaValuacionInventario(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"ValuacionInventario.xlsx\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String flinventario = request.getParameter("flinventario");

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("valuacion");
        sheet.setSelected(true);

        XSSFRow row;
        XSSFCell cell;

        XSSFCellStyle headerStyle = wb.createCellStyle();
        XSSFFont fontHeader = wb.createFont();
        fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        fontHeader.setColor(new XSSFColor(Color.white));
        headerStyle.setFont(fontHeader);
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(Color.lightGray));
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        row = sheet.createRow(0);
        String[] headers = {
            "CODIGO", "NOPARTE", "DESCRIPCION", "UBICACION", "EXISTENCIA", "COSTO", "TOTAL"
        };

        int colNo = 0;
        for(String header : headers) {
            cell = row.createCell(colNo++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(0, (header.length()*1 * 450));
        }

        int rowNo = 1;

        XSSFCellStyle monedaStyle = wb.createCellStyle();
        XSSFDataFormat dfMoneda = wb.createDataFormat();
        monedaStyle.setDataFormat(dfMoneda.getFormat("$#,##0.00"));

        XSSFCellStyle numeroStyle = wb.createCellStyle();
        XSSFDataFormat dfNumero = wb.createDataFormat();
        numeroStyle.setDataFormat(dfNumero.getFormat("#,##0.00"));

        ArrayList productos = ds.select(new InventarioDetalleUbicacionDAO(), "flinventario = "+flinventario, "descripcion");

        for(Object object : productos) {
            InventarioDetalleUbicacionDAO inventarioDetalleUbicacionDAO = (InventarioDetalleUbicacionDAO)object;

            row = sheet.createRow(rowNo);
            colNo = 0;
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.clave);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.noparte);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.descripcion);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.ubicacion);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.existencia);
            cell.setCellStyle(numeroStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(inventarioDetalleUbicacionDAO.precio);
            cell.setCellStyle(monedaStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(Numero.redondea(inventarioDetalleUbicacionDAO.precio * inventarioDetalleUbicacionDAO.existencia));
            cell.setCellStyle(monedaStyle);

            rowNo++;
        }

        colNo = 0;
        for(String header : headers) {
            sheet.autoSizeColumn(colNo++);
        }

        try (ServletOutputStream out = response.getOutputStream()) {
            wb.write(out);
        }
    }

    public void exportaArchivoInventario(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"inventario.csv\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String flinventario = request.getParameter("flinventario");

        //
        InventarioDAO inventarioDAO = new InventarioDAO();
        inventarioDAO.flinventario = Integer.parseInt(flinventario);
        if (!ds.exists(inventarioDAO))
            throw new WebException("No existe este inventario ["+inventarioDAO+"].");

        if (inventarioDAO.fase==null)
            throw new WebException("No se ha realizado la Carga del Inventario ["+Constantes.ESTADO_INVENTARIO_CARGAINICIAL+"].");
        if (!(inventarioDAO.fase.compareTo(Constantes.ESTADO_INVENTARIO_AFECTADO)==0))
            throw new WebException("Solo se puede Finalizar el Inventario con el estado ["+Constantes.ESTADO_INVENTARIO_AFECTADO+"].");
        //

        CompaniaDAO companiaDAO = new CompaniaDAO(inventarioDAO.compania);
        if (!ds.exists(companiaDAO))
            throw new WebException("No existe esta compania ["+companiaDAO+"].");


        //
        //
        //

        if (companiaDAO.porubicacion.compareTo("1")==0) {

            // Obtengo los registros de inventario por ubicacion
            ArrayList<InventarioConteoUbicacionDAO> array = ds.select(new InventarioConteoUbicacionDAO(),
                    "flinventario = "+flinventario, "clave, ubicacion");

            try (ServletOutputStream out = response.getOutputStream()) {
                out.println("Codigo,Descripcion,Costo,Col_vacia,Cant_fisica,Cant_teorica,Domicilio_origen");

                for(InventarioConteoUbicacionDAO inventarioConteoUbicacionDAO : array) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(inventarioConteoUbicacionDAO.clave).append(",");
                    String descripcion = inventarioConteoUbicacionDAO.noparte;
                    String comillas = descripcion.contains(",") ? "\"" : "";
                    sb.append(comillas).append(descripcion).append(comillas).append(",");
                    sb.append(inventarioConteoUbicacionDAO.precio).append(",");
                    sb.append("").append(",");
                    sb.append(Numero.redondea(inventarioConteoUbicacionDAO.existencia)).append(",");
                    sb.append(Numero.redondea(inventarioConteoUbicacionDAO.existenciainicial)).append(",");
                    sb.append(inventarioConteoUbicacionDAO.ubicacion);

                    out.println(sb.toString());
                }
            }

        } else {

            // Obtengo los registros de inventario, solo mando los que tienen diferencia
            ArrayList<InventarioConteoDAO> array = ds.select(new InventarioConteoDAO(),
                    "flinventario = "+flinventario
                    +" AND existencia3 != existencia", "clave");

            try (ServletOutputStream out = response.getOutputStream()) {
                out.println("Codigo,Descripcion,Costo,Col_vacia,Cant_fisica,Cant_teorica,Domicilio_origen");

                for(InventarioConteoDAO inventarioConteoDAO : array) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(inventarioConteoDAO.clave).append(",");
                    String descripcion = inventarioConteoDAO.noparte;
                    String comillas = descripcion.contains(",") ? "\"" : "";
                    sb.append(comillas).append(descripcion).append(comillas).append(",");
                    sb.append(inventarioConteoDAO.precio).append(",");
                    sb.append("").append(",");
                    sb.append(Numero.redondea(inventarioConteoDAO.existencia3)).append(",");
                    sb.append(Numero.redondea(inventarioConteoDAO.existencia)).append(",");
                    sb.append("00-00-000-0");

                    out.println(sb.toString());
                }
            }
        }
    }

    public void exportaAuditoriaInventarios(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment;filename=\"AuditoriaInventarios.xlsx\"");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        String flinventario = request.getParameter("flinventario");

        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFSheet sheet = wb.createSheet("valuacion");
        sheet.setSelected(true);

        XSSFRow row;
        XSSFCell cell;

        XSSFCellStyle headerStyle = wb.createCellStyle();
        XSSFFont fontHeader = wb.createFont();
        fontHeader.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        fontHeader.setColor(new XSSFColor(Color.white));
        headerStyle.setFont(fontHeader);
        headerStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(new XSSFColor(Color.lightGray));
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        row = sheet.createRow(0);
        String[] headers = {
            "FOLIO", "FASE", "FECHA", "COMPANIA", "FLINVENTARIO", "TERMINAL", "USUARIO",
            "CREACION", "MODIFICACION", "TERMINO", "UBICACION", "PRODUCTOS", "CANTIDAD"
        };

        int colNo = 0;
        for(String header : headers) {
            cell = row.createCell(colNo++);
            cell.setCellValue(header);
            cell.setCellStyle(headerStyle);

            sheet.setColumnWidth(0, (header.length()*1 * 450));
        }

        int rowNo = 1;

        XSSFDataFormat df = wb.createDataFormat();

        XSSFCellStyle numeroStyle = wb.createCellStyle();
        numeroStyle.setDataFormat(df.getFormat("#,##0.00"));

        XSSFCellStyle numeroIntStyle = wb.createCellStyle();
        numeroIntStyle.setDataFormat(df.getFormat("#,##0"));

        XSSFCellStyle datetimeStyle = wb.createCellStyle();
        datetimeStyle.setDataFormat(df.getFormat("d/m/yy h:mm:ss"));

        AuditoriaInventariosCollection auditoriaInventariosCollection1 = new AuditoriaInventariosCollection();
        ArrayList inventarios = ds.collection(new AuditoriaInventariosCollection(),
                auditoriaInventariosCollection1.getSQL("ic.flinventario = "+flinventario));

        for(Object object : inventarios) {
            AuditoriaInventariosCollection auditoriaInventariosCollection = (AuditoriaInventariosCollection)object;

            row = sheet.createRow(rowNo);
            colNo = 0;
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.folio);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.fase);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.fechatransmision);
            cell.setCellStyle(datetimeStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.compania);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.flinventario);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.terminal);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.usuario);
            cell = row.createCell(colNo++);
            if (auditoriaInventariosCollection.fecreacion!=null) {
                cell.setCellValue(auditoriaInventariosCollection.fecreacion);
                cell.setCellStyle(datetimeStyle);
            }
            cell = row.createCell(colNo++);
            if (auditoriaInventariosCollection.femodificacion!=null) {
                cell.setCellValue(auditoriaInventariosCollection.femodificacion);
                cell.setCellStyle(datetimeStyle);
            }
            cell = row.createCell(colNo++);
            if (auditoriaInventariosCollection.fetermino!=null) {
                cell.setCellValue(auditoriaInventariosCollection.fetermino);
                cell.setCellStyle(datetimeStyle);
            }
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.ubicacion);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.lineas);
            cell.setCellStyle(numeroIntStyle);
            cell = row.createCell(colNo++);
            cell.setCellValue(auditoriaInventariosCollection.cantidad);
            cell.setCellStyle(numeroStyle);

            rowNo++;
        }

        colNo = 0;
        for(String header : headers) {
            sheet.autoSizeColumn(colNo++);
        }

        try (ServletOutputStream out = response.getOutputStream()) {
            wb.write(out);
        }
    }
}
