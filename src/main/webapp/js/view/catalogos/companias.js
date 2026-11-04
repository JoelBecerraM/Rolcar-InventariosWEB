
function initCatalogo() {
    var _catalogo;
    var catalogo = new catalogoABC();
    catalogo.registroCatalogo = "mx.com.rolcar.inventarios.dao.entity.CompaniaDAO";

    catalogo.initialize = function() {
        var $compania = $("#datos [name=compania]");
        $compania.keypress(function(e) {
            if (e.keyCode===13) {
                _catalogo.buscarRegistros();
            }
        });
        $compania.focus();
        notify_info("Listo.");
    };

    catalogo.buscarRegistrosValores = function() {
        var $compania = $("#datos [name=compania]");
        var compania = $compania.val();

        return compania;
    };

    catalogo.buscarRegistrosBusqueda = function(complete, error) {
        var $compania = $("#datos [name=compania]");

        busquedaCompanias($compania.val(), complete, error);
    };

    catalogo.buscarRegistrosOnAceptar = function() {
        var $compania = $("#datos [name=compania]");
        $compania.select();
        $compania.focus();
    };

    catalogo.guardarRegistroOnComplete = function(response) {
        notify_success("Registro guardado correctamente.");
    };

    catalogo.borrarRegistroConfirmadoOnComplete = function(response) {
        notify_success("Registro borrado correctamente.");

        _catalogo.limpiarRegistro();
    };

    catalogo.limpiarRegistroInit = function() {
        var $compania = $("#datos [name=compania]");
        $compania.focus();
    };

    catalogo.initCaptura();

    _catalogo = catalogo;
}