package mx.com.rolcar.inventarios.servlets;

import com.atcloud.commerce.services.JSON;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.to.ErrorTO;
import com.atcloud.util.Numero;
import com.atcloud.web.WebException;
import mx.com.rolcar.inventarios.business.InventarioBussines;
import mx.com.rolcar.inventarios.dao.entity.InventarioDAO;
import mx.com.rolcar.inventarios.util.Constantes;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class CargaInventarioServlet extends HttpServlet {
    static Logger log = Logger.getLogger(CargaInventarioServlet.class.getName());

    public Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource)ic.lookup("java:comp/env/jdbc/inventarios");
        return ds.getConnection();
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        response.setHeader("Cache-Control", "no-cache");
        
        Connection connection = getConnection();
        DatabaseServices ds = new DatabaseServices(connection);
            
        try {
            log.debug("CargaInventario processRequest() ...");
            for(String parametro : request.getParameterMap().keySet())
                log.debug("parametro: "+parametro);
                        
            ds.beginTransaction();
            
            InventarioDAO inventarioDAO = new InventarioDAO();            
            byte[] archivo = null;
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) {
            } else {

                // Create a factory for disk-based file items
                FileItemFactory factory = new DiskFileItemFactory();
                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                // Parse the request
                List items = upload.parseRequest(request);

                // Process the uploaded items
                Iterator iterator = items.iterator();
                while (iterator.hasNext()) {
                    FileItem item = (FileItem)iterator.next();

                    if (item.isFormField()) {
                        String name = item.getFieldName();
                        String value = item.getString();

                        log.debug("parameter ["+name+"] = "+value);
                        
                        if (name.compareTo("cargaflinventario")==0)
                            inventarioDAO.flinventario = Numero.getIntFromString(value);

                    } else {
                        String fieldName = item.getFieldName();
                        String fileName = item.getName();
                        String contentType = item.getContentType();
                        //boolean isInMemory = item.isInMemory();
                        long sizeInBytes = item.getSize();

                        log.debug("file ["+fieldName+"] = "+fileName+" "+contentType+" len "+sizeInBytes);

                        InputStream uploadedStream = item.getInputStream();
                        byte[] data = new byte[(int)sizeInBytes];
                        uploadedStream.read(data);
                        uploadedStream.close();

                        if(fieldName.compareTo("archivo")==0)
                            archivo = data;
                    }
                }
            }

            if (!ds.exists(inventarioDAO))
                throw new WebException("Este Inventario ["+inventarioDAO+"] no existe.");
            
            InventarioBussines inventarioBussines = new InventarioBussines();
            inventarioBussines.cargaInventario(ds, inventarioDAO, archivo);

            // Cambia el status del inventario
            inventarioDAO.fase = Constantes.ESTADO_INVENTARIO_CARGAINICIAL;
            inventarioDAO.fefase = new Date();

            ds.update(inventarioDAO, new String[]{"fase", "fefase"});
            
            PrintWriter out = response.getWriter();
            JSON.writeObject(out, inventarioDAO);
            out.close();
            
            ds.commit();
            
        } catch(WebException e) {
            ErrorTO errorTO = new ErrorTO();
            errorTO.fromException(e);
            
            try {
                ds.rollback();
            } catch(SQLException SQLe) {
            }
            
            try {                
                PrintWriter out = response.getWriter();
                JSON.writeObject(out, errorTO);
                out.close();
                
            } catch(Exception ex) {
                throw new WebException(ex.getMessage());
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            
            try {
                ds.rollback();
            } catch(SQLException SQLe) {
            }
            
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
        
        connection.close();
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }        
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch(Exception e) {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
        }        
    }
}