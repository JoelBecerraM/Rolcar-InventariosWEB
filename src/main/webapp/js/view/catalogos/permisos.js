
function initCatalogo() {
    creaGridPermisos();
    creaGridRoles();
    obtenPermisos();

    notify_info("Listo.");
}

function obtenPermisos() {
    var onAceptar = function() {
    };
    var onFail = function(err) {
        var msg = "Error al buscar los registros.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(msg);
    };
    var onError = function(response) {
        if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, onAceptar);
            notify_warning(response.mensaje);
        } else {
            error(response.exception, onAceptar);
            notify_error(response.exception);
        }
        var array = {};
        muestraPermisos(array);
    };
    var onComplete = function(response) {
        muestraPermisos(response);
        notify_info("Listo.");
    };

    var where = "1 = 1";

    notify_secondary("Buscando los permisos ...");
    lista("mx.com.rolcar.inventarios.dao.entity.PermisoDAO", where, "permiso",
        onComplete, onError, onFail);
}

function muestraPermisos(response) {
    sourceGrid.localdata = response;

    var $grid = $("#grid_permisos");
    $grid.jqxGrid("updatebounddata", "cells");

    if (response.length>0)
        $grid.jqxGrid("clearselection");
}

function obtenRolesPermiso(rowdata) {
    var onAceptar = function() {
    };
    var onFail = function(err) {
        var msg = "Error al buscar los registros.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(msg);
    };
    var onError = function(response) {
        if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, onAceptar);
            notify_warning(response.mensaje);
        } else {
            error(response.exception, onAceptar);
            notify_error(response.exception);
        }
        var array = {};
        muestraRoles(array);
    };
    var onComplete = function(response) {
        muestraRoles(response);
        notify_info("Listo.");
    };

    var where = "pp.permiso = "+rowdata.permiso;

    notify_secondary("Buscando los roles ...");
    coleccion("mx.com.rolcar.inventarios.collection.PermisoPerfilCollection", where,
        onComplete, onError, onFail);
}

function muestraRoles(response) {
    sourceGrid2.localdata = response;

    var $grid = $("#grid_roles");
    $grid.jqxGrid("clearselection");
    $grid.jqxGrid("updatebounddata", "cells");
}

function quitaPermiso() {
    var $grid = $("#grid_roles");

    var selectedrowindex = $grid.jqxGrid("selectedrowindex");
    if (selectedrowindex===-1) {
        notify_warning("Por favor seleccione un Rol de Usuario primero.");
        return;
    }

    var rowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
    if (!rowdata) {
        notify_warning("Por favor seleccione un Rol de Usuario primero.");
        return;
    }

    var valores = {
        permiso: rowdata.permiso,
        perfil: rowdata.perfil
    };

    var onFail = function(err) {
        var mensaje = "Error al borrar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
        notify_error(response.mensaje);
    };
    var onComplete = function(response) {
        notify_success("Rol eliminado.");
        obtenRolesPermiso(rowdata);
    };
    notify_secondary("Quitando ...");
    accion("mx.com.rolcar.inventarios.dao.entity.PermisoPerfilDAO", "delete", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function agregaPermiso() {
    var $grid = $("#grid_permisos");

    var selectedrowindex = $grid.jqxGrid("selectedrowindex");
    if (selectedrowindex===-1) {
        notify_warning("Por favor seleccione un Permiso primero.");
        return;
    }

    var rowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
    if (!rowdata) {
        notify_warning("Por favor seleccione un Permiso primero.");
        return;
    }

    var onAceptar = function() {
    };
    var onFail = function(err) {
        var mensaje = "Error al buscar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, onAceptar);
            notify_warning(response.mensaje);
        } else {
            error(response.exception, onAceptar);
            notify_error(response.exception);
        }
    };
    var onComplete = function(response) {
        agregaPermisoPerfil(rowdata, response);
        notify_info("Listo.");
    };
    var perfil = "";
    notify_info("Buscando ...");
    busquedaPerfiles(perfil, onComplete, onError);
}

function agregaPermisoPerfil(rowdata, response) {
    var valores = {
        permiso: rowdata.permiso,
        perfil: response.perfil
    };

    var onFail = function(err) {
        var mensaje = "Error al borrar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
        notify_error(response.mensaje);
    };
    var onComplete = function(response) {
        notify_success("Rol agregando.");
        obtenRolesPermiso(rowdata);
    };
    notify_secondary("Agregando ...");
    accion("mx.com.rolcar.inventarios.dao.entity.PermisoPerfilDAO", "save", JSON.stringify(valores),
        onComplete, onError, onFail);
}

var sourceGrid;

function creaGridPermisos() {
    sourceGrid = {
        localdata: [],
        datafields: [
            {name: "permiso", type: "int"},
            {name: "nombre", type: "string"}
        ],
        datatype: "array"
    };
    var cellclass = function (row, columnfield, value) {
        return "bold";
    };
    var dataAdapter = new $.jqx.dataAdapter(sourceGrid);
    var $grid = $("<div id=\"grid_permisos\"></div>");
    $grid.jqxGrid({
        width: "100%",
        height: "400px",
        altrows: true,
        source: dataAdapter,
        columns: [
            {text: "Permiso", datafield: "nombre", width: "100%", cellclassname: cellclass}
        ],
        ready: function() {
            $grid.jqxGrid("clearselection");
            $grid.jqxGrid("focus");
        }
    });
    $grid.on("rowselect", function (event) {
        var selectedrowindex = event.args.rowindex;
        if (selectedrowindex===-1)
            return;

        var rowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
        if (rowdata) {
            obtenRolesPermiso(rowdata);
        }
    });

    $("#permisos").append($grid);
}

var sourceGrid2;

function creaGridRoles() {
    sourceGrid2 = {
        localdata: [],
        datafields: [
            {name: "permiso", type: "int"},
            {name: "perfil", type: "string"},
            {name: "descripcion", type: "string"}
        ],
        datatype: "array"
    };
    var dataAdapter = new $.jqx.dataAdapter(sourceGrid2);
    var $grid = $("<div id=\"grid_roles\"></div>");
    $grid.jqxGrid({
        width: "100%",
        height: "400px",
        altrows: true,
        source: dataAdapter,
        columns: [
            {text: "Rol", datafield: "descripcion", width: "100%"}
        ],
        ready: function() {
            $grid.jqxGrid("clearselection");
            $grid.jqxGrid("focus");
        }
    });

    $("#roles").append($grid);
}

function capturaPermiso(onComplete, onCancelar) {

    var onCompleteII = function(response) {
        var $content = $(response);

        var registro;

        var aceptar = function () {
            var $form = $("#captura-permiso-datos");
            var pass = validaDatosForma($form);
            if (!pass) {
                notify_error("Hay errores con los datos, favor de corregirlos.", $notify);
                return;
            }

            registro = getValoresForma("#captura-permiso-datos");

            $modalDialog3.modal("hide");
        };

        var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">Aceptar</button>");
        $buttonAceptar.click(function() {
            aceptar();
        });
        var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">Cancelar</button>");
        $buttonCancelar.click(function() {
            registro = undefined;
            $modalDialog3.modal("hide");
        });

        $("#modalDialog3Label").html("Permiso");
        $("#modalDialog3Document").addClass("modal-lg");
        $("#modalDialog3Header").removeClass();
        $("#modalDialog3Header").addClass("modal-header bg-info text-white");
        $("#modalDialog3Body").empty();
        $("#modalDialog3Body").append($content);
        $("#modalDialog3Footer").empty();
        $("#modalDialog3Footer").append($buttonCancelar);
        $("#modalDialog3Footer").append($buttonAceptar);

        var $input = $("#captura-permiso-datos input[name=nombre]");
        var $notify = $("#captura-permiso-notificacion");

        $input.keypress(function(e) {
            if (e.keyCode===13) {
                aceptar();
                return false;
            }
        });

        notify_info("Listo.", $notify);

        $modalDialog3.unbind("shown.bs.modal");
        $modalDialog3.on("shown.bs.modal", function () {
            $input.select();
            $input.focus();
        });
        $modalDialog3.unbind("hidden.bs.modal");
        $modalDialog3.on("hidden.bs.modal", function() {
            if (registro) {
                if (onComplete)
                    onComplete(registro);
            } else {
                if (onCancelar)
                    onCancelar();
            }
        });

        $modalDialog3.modal({
            keyboard: false,
            backdrop: "static"
        });
    };

    loadPage("/inventarios/view/catalogos/permiso-captura.html", onCompleteII);
}

function agregaRegistroPermiso() {
    var onCancel = function() {
    };
    var onSelected = function(response) {
        if (!response)
            return;
        var onAceptar = function() {
            agregaRegistroPermisoConfirmado(response);
        };
        pregunta("&iquest;Esta seguro de agregar el PERMISO <b>"+response.nombre+"<b>?", onAceptar);
    };
    capturaPermiso(onSelected, onCancel);
}

function agregaRegistroPermisoConfirmado(response) {
    var valores = {
        nombre: response.nombre
    };

    var onFail = function(err) {
        var mensaje = "Error al guardar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
        notify_error(response.mensaje);
    };
    var onComplete = function(response) {
        notify_success("Permiso agregado.");
        obtenPermisos();
    };
    notify_secondary("Agregando ...");
    accion("mx.com.rolcar.inventarios.dao.entity.PermisoDAO", "add", JSON.stringify(valores),
        onComplete, onError, onFail);
}