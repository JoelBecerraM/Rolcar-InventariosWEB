
function initCatalogo() {
    var _catalogo;
    var catalogo = new catalogoABC();
    catalogo.registroCatalogo = "mx.com.rolcar.inventarios.dao.entity.UsuarioDAO";

    catalogo.initialize = function() {
        var $perfil = $("#datos [name=perfil]");
        comboRegistros("mx.com.rolcar.inventarios.dao.entity.PerfilDAO", "1 = 1", "descripcion",
            $perfil, "perfil", "descripcion");

        var $usuario = $("#datos [name=usuario]");
        $usuario.keypress(function(e) {
            if (e.keyCode===13) {
                _catalogo.buscarRegistros();
            }
        });
        $usuario.focus();
        notify_info("Listo.");
    };

    catalogo.buscarRegistrosValores = function() {
        var $usuario = $("#datos [name=usuario]");
        var usuario = $usuario.val();

        return usuario;
    };

    catalogo.buscarRegistrosBusqueda = function(complete, error) {
        var $usuario = $("#datos [name=usuario]");

        busquedaUsuarios($usuario.val(), complete, error);
    };

    catalogo.buscarRegistrosOnAceptar = function() {
        var $usuario = $("#datos [name=usuario]");
        $usuario.select();
        $usuario.focus();
    };

    catalogo.guardarRegistroOnComplete = function(response) {
        notify_success("Registro guardado correctamente.");
    };

    catalogo.borrarRegistroConfirmadoOnComplete = function(response) {
        notify_success("Registro borrado correctamente.");

        _catalogo.limpiarRegistro();
    };

    catalogo.limpiarRegistroInit = function() {
        var $usuario = $("#datos [name=usuario]");
        $usuario.focus();
    };

    catalogo.initCaptura();

    _catalogo = catalogo;
}
