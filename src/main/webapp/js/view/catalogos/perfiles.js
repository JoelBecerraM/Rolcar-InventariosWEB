
function initCatalogo() {
    var _catalogo;
    var catalogo = new catalogoABC();
    catalogo.registroCatalogo = "mx.com.rolcar.inventarios.dao.entity.PerfilDAO";

    catalogo.initialize = function() {
        var $perfil = $("#datos [name=perfil]");
        $perfil.keypress(function(e) {
            if (e.keyCode===13) {
                _catalogo.buscarRegistros();
            }
        });
        $perfil.focus();
        notify_info("Listo.");
    };

    catalogo.buscarRegistrosValores = function() {
        var $perfil = $("#datos [name=perfil]");
        var perfil = $perfil.val();

        return perfil;
    };

    catalogo.buscarRegistrosBusqueda = function(complete, error) {
        var $perfil = $("#datos [name=perfil]");

        busquedaPerfiles($perfil.val(), complete, error);
    };

    catalogo.buscarRegistrosOnAceptar = function() {
        var $perfil = $("#datos [name=perfil]");
        $perfil.select();
        $perfil.focus();
    };

    catalogo.guardarRegistroOnComplete = function(response) {
        notify_success("Registro guardado correctamente.");
    };

    catalogo.borrarRegistroConfirmadoOnComplete = function(response) {
        notify_success("Registro borrado correctamente.");

        _catalogo.limpiarRegistro();
    };

    catalogo.limpiarRegistroInit = function() {
        var $perfil = $("#datos [name=perfil]");
        $perfil.focus();
    };

    catalogo.initCaptura();

    _catalogo = catalogo;
}