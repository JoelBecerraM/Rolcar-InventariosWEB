package mx.com.rolcar.inventarios.command;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.atcloud.commerce.services.JSON;
import com.atcloud.dao.engine.DatabaseServices;
import com.atcloud.web.WebCommandInterface;
import com.atcloud.web.WebException;
import com.atcloud.to.ErrorTO;
import com.atcloud.util.Reflector;
import java.util.ArrayList;
import mx.com.rolcar.inventarios.collection.PermisoPerfilCollection;
import mx.com.rolcar.inventarios.dao.entity.CompaniaDAO;
import mx.com.rolcar.inventarios.dao.entity.UsuarioDAO;
import mx.com.rolcar.inventarios.to.PermisosTO;
import mx.com.rolcar.inventarios.to.UsuarioTO;

public class IniciaSesionCommand implements WebCommandInterface {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response, DatabaseServices ds) throws WebException {
        try {
            String usuario = request.getParameter("usuario");
            String password = request.getParameter("password");
            String compania = request.getParameter("compania");
            String cerrar = request.getParameter("cerrar");

            UsuarioTO usuarioTO = new UsuarioTO();

            if (cerrar!=null) {
                HttpSession session = request.getSession();
                session.invalidate();

            } else {
                if (usuario==null&&password==null) {
                    throw new WebException("Debe de especificar el usuario y la contrase&ntilde;a");
                } else {
                    UsuarioDAO usuarioDAO = (UsuarioDAO)ds.first(new UsuarioDAO(), "usuario = '"+usuario+"'");
                    if (usuarioDAO==null) {
                        throw new WebException("No existe el Usuario ["+usuario+"].");
                    } else {
                        if (usuarioDAO.password.compareTo(password)!=0) {
                            throw new WebException("La contrase&ntilde;a no es correcta.");
                        } else {
                            CompaniaDAO companiaDAO = new CompaniaDAO();
                            companiaDAO.compania = compania;
                            if (!ds.exists(companiaDAO))
                                throw new WebException("No existe esta Compania ["+companiaDAO+"].");

                            Reflector.copyAllFields(usuarioDAO, usuarioTO);

                            PermisoPerfilCollection permisoPerfilCollection = new PermisoPerfilCollection();
                            permisoPerfilCollection.perfil = usuarioTO.perfil;

                            ArrayList<PermisoPerfilCollection> arrayPermisos = ds.collection(permisoPerfilCollection,
                                    permisoPerfilCollection.getSQL("pp.perfil = '"+usuarioTO.perfil+"'"));
                            usuarioTO.permisos = new PermisosTO();
                            for(PermisoPerfilCollection permisoPerfilCollection1 : arrayPermisos)
                                usuarioTO.permisos.addPermiso(permisoPerfilCollection1.nombre);

                            usuarioTO.compania = companiaDAO.compania;
                            usuarioTO.razonsocial = companiaDAO.razonsocial;

                            response.setHeader("Pragma-directive", "no-cache");
                            response.setHeader("Cache-directive", "no-cache");
                            response.setHeader("Cache-control", "no-cache");
                            response.setHeader("Pragma", "no-cache");
                            response.setHeader("Expires", "0");

                            HttpSession sesion = request.getSession();
                            sesion.setMaxInactiveInterval(45 * 60);
                            sesion.setAttribute("usuario", usuarioTO);
                        }
                    }
                }
            }

            PrintWriter out = response.getWriter();
            JSON.writeObject(out, usuarioTO);
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