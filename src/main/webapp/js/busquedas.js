
function busquedaInventarios(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.InventarioDAO",
        where: "compania = '"+usuario.compania+"'",
        order: "flinventario DESC",
        height: 370,
        title: "Inventarios",
        datafields: [
            {name: "flinventario", type: "number"},
            {name: "compania", type: "string"},
            {name: "almacen", type: "string"},
            {name: "descripcion", type: "string"},
            {name: "feinicio", type: "string"},
            {name: "fetermino", type: "string"},
            {name: "status", type: "string"},
            {name: "fase", type: "string"},
            {name: "fefase", type: "string"},
            {name: "feprimerconteo", type: "string"},
            {name: "fesegundoconteo", type: "string"},
            {name: "fetercerconteo", type: "string"}
        ],
        columns: [
            {text: "Folio", datafield: "flinventario", width: "10%"},
            {text: "Compania", datafield: "compania", width: "10%"},
            {text: "Inicio", datafield: "feinicio", width: "30%"},
            {text: "Termino", datafield: "fetermino", width: "30%"},
            {text: "Estatus", datafield: "status", width: "10%"},
            {text: "Fase", datafield: "fase", width: "10%"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function busquedaPerfiles(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.PerfilDAO",
        where: "descripcion LIKE '%"+texto+"%'",
        order: "descripcion",
        height: 370,
        title: "Perfiles",
        datafields: [
            {name: "perfil", type: "string"},
            {name: "descripcion", type: "string"}
        ],
        columns: [
            {text: "Perfil", datafield: "perfil", width: "40%"},
            {text: "Descripcion", datafield: "descripcion", width: "60%"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function busquedaUsuarios(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.UsuarioDAO",
        where: "nombre LIKE '%"+texto+"%'",
        order: "nombre",
        height: 370,
        title: "Usuarios",
        datafields: [
            {name: "usuario", type: "string"},
            {name: "password", type: "string"},
            {name: "nombre", type: "string"},
            {name: "email", type: "string"},
            {name: "perfil", type: "int"}
        ],
        columns: [
            {text: "Usuario", datafield: "usuario", width: "30%"},
            {text: "Nombre", datafield: "nombre", width: "60%"},
            {text: "Perfil", datafield: "perfil", width: "10%"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function busquedaProductos(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.ProductoDAO",
        where: "compania = '"+usuario.compania+"' AND descripcion LIKE '%"+texto+"%'",
        order: "descripcion",
        height: 370,
        title: "Productos",
        datafields: [
            {name: "clave", type: "string"},
            {name: "descripcion", type: "string"},
            {name: "precio", type: "float"},
            {name: "linea", type: "string"},
            {name: "unidadmedida", type: "string"}
        ],
        columns: [
            {text: "Clave", datafield: "clave", width: "20%"},
            {text: "Descripcion", datafield: "descripcion", width: "60%"},
            {text: "Precio", datafield: "precio", width: "20%", cellsformat: "c2", cellsalign: "right"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function busquedaUbicaciones(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.UbicacionDAO",
        where: "compania = '"+usuario.compania+"' AND (ubicacion LIKE '%"+texto+"%' OR descripcion LIKE '%"+texto+"%')",
        order: "descripcion",
        height: 370,
        title: "Ubicaciones",
        datafields: [
            {name: "compania", type: "string"},
            {name: "ubicacion", type: "string"},
            {name: "descripcion", type: "string"}
        ],
        columns: [
            {text: "Ubicaci&oacute;n", datafield: "ubicacion", width: "20%"},
            {text: "Descripci&oacute;n", datafield: "descripcion", width: "80%"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function busquedaCompanias(texto, onComplete, onError) {
    var parametros = {
        registro: "mx.com.rolcar.inventarios.dao.entity.CompaniaDAO",
        where: "razonsocial LIKE '%"+texto+"%'",
        order: "razonsocial",
        height: 370,
        title: "Companias",
        datafields: [
            {name: "compania", type: "string"},
            {name: "razonsocial", type: "string"}
        ],
        columns: [
            {text: "Compa&ntilde;ia", datafield: "compania", width: "20%"},
            {text: "Raz&oacute;n social", datafield: "razonsocial", width: "80%"}
        ]
    };

    busquedas(parametros, onComplete, onError);
}

function buscarCompania(onComplete, onAceptar) {
    var onAceptarII = function() {
        if (onAceptar)
            onAceptar();
    };
    var onFailII = function(err) {
        var mensaje = "Error al buscar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onErrorII = function(response) {
        if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, onAceptarII);
            notify_warning(response.mensaje);
        } else {
            error(response.exception, onAceptarII);
            notify_error(response.exception);
        }
    };
    var onCompleteII = function(response) {
        if (onComplete)
            onComplete(response);
    };
    notify_info("Buscando el registro ...");
    registro("mx.com.rolcar.inventarios.dao.entity.CompaniaDAO", usuario.compania,
        onCompleteII, onErrorII, onFailII);
}

function busquedas(parametros, onComplete, onError, onCancelar) {
    var onFailII = function(err) {
        error("Error al obtener la lista de registros ["+parametros.registro+";"+parametros.where+"].<br><br><b>("+
            err.status+") "+err.statusText+"</b>");
    };
    var onErrorII = function(response) {
        if (onError) {
            onError(response);
        } else {
            notify_error(response.mensaje);
        }
    };
    var onCompleteII = function(response) {
        if (response.length===1) {
            var selectedRowData = response[0];

            if (onComplete)
                onComplete(selectedRowData);

            return;
        }

        var selectedRowData;

        var aceptar = function() {
            var $grid = $("#grid_search");
            var selectedrowindex = $grid.jqxGrid("getselectedrowindex");
            if (selectedrowindex===-1) {
                notify_error("Debe de seleccionar un registro.", $notify);
                return;
            }

            selectedRowData = $grid.jqxGrid("getrowdata", selectedrowindex);
            $grid.jqxGrid("clearselection");
            $grid.jqxGrid({handlekeyboardnavigation: null});

            $modalDialogBusquedas.modal("hide");
        };

        var source = {
            localdata: response,
            datafields: parametros.datafields,
            datatype: "array"
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
        var $grid = $("<div id=\"grid_search\"></div>");
        $grid.jqxGrid({
            width: "100%",
            height: parametros.height - 120,
            altrows: true,
            sortable: true,
            source: dataAdapter,
            columns: parametros.columns,
            ready: function() {
                $grid.jqxGrid("clearselection");
                $grid.jqxGrid("focus");
            },
            handlekeyboardnavigation: function(event) {
                var key = event.charCode ? event.charCode :
                    event.keyCode ? event.keyCode : 0;
                if (key===13) {
                    aceptar();
                    return true;
                }
                return false;
            }
        });

        var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">Aceptar</button>");
        $buttonAceptar.click(function() {
            aceptar();
        });
        var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">Cancelar</button>");
        $buttonCancelar.click(function() {
            notify_info("Listo.");
            selectedRowData = null;
            $modalDialogBusquedas.modal("hide");
        });

        var $content = $("<hr><div class=\"space\"></div><div id=\"busquedas-notificacion\">Listo.</div>");

        $("#modalDialogBusquedasLabel").html(parametros.title);
        $("#modalDialogBusquedasDocument").addClass("modal-lg");
        $("#modalDialogBusquedasHeader").removeClass();
        $("#modalDialogBusquedasHeader").addClass("modal-header bg-info text-white");
        $("#modalDialogBusquedasBody").empty();
        $("#modalDialogBusquedasBody").append($grid);
        $("#modalDialogBusquedasBody").append($content);
        $("#modalDialogBusquedasFooter").empty();
        $("#modalDialogBusquedasFooter").append($buttonCancelar);
        $("#modalDialogBusquedasFooter").append($buttonAceptar);

        var $notify = $("#busquedas-notificacion");
        notify_info("Listo.", $notify);

        if (response.length===0)
            notify_warning("No se encontro ning&uacute;n registro.", $notify);

        $modalDialogBusquedas.unbind("shown.bs.modal");
        $modalDialogBusquedas.on("shown.bs.modal", function() {
            $grid.focus();
        });
        $modalDialogBusquedas.unbind("hidden.bs.modal");
        $modalDialogBusquedas.on("hidden.bs.modal", function() {
            if (selectedRowData) {
                if (onComplete)
                    onComplete(selectedRowData);
            } else {
                if (onCancelar)
                    onCancelar();
            }
        });

        $modalDialogBusquedas.modal({
            backdrop: "static"
        });
    };

    notify_secondary("Obteniendo registros ...");
    if (parametros.coleccion)
        coleccion(parametros.coleccion, parametros.where, onCompleteII, onErrorII, onFailII);
    else
        lista(parametros.registro, parametros.where, parametros.order, onCompleteII, onErrorII, onFailII);
}

function seleccionaTerminal(onSelected, onCanceled) {

    var onComplete = function(response) {
        var $content = $(response);

        var registro;
        var aceptar = function () {
            var $form = $("#buscar-terminal-datos");
            var pass = validaDatosForma($form);
            if (!pass) {
                notify_error("Hay errores con los datos, favor de corregirlos.", $notify);
                return;
            }

            registro = getValoresForma("#buscar-terminal-datos");
            $modalDialog3.modal("hide");
        };

        var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">Aceptar</button>");
        $buttonAceptar.click(function() {
            aceptar();
        });
        var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">Cancelar</button>");
        $buttonCancelar.click(function() {
            $modalDialog3.modal("hide");
        });

        $("#modalDialog3Label").html("Selecci&oacute;n de Terminal");
        $("#modalDialog3Header").removeClass();
        $("#modalDialog3Header").addClass("modal-header bg-info text-white");
        $("#modalDialog3Body").empty();
        $("#modalDialog3Body").append($content);
        $("#modalDialog3Footer").empty();
        $("#modalDialog3Footer").append($buttonCancelar);
        $("#modalDialog3Footer").append($buttonAceptar);

        var $input = $("#buscar-terminal-datos input[name=terminal]");
        var $notify = $("#buscar-terminal-notificacion");

        $input.keypress(function(e) {
            if (e.keyCode===13) {
                aceptar();
                return false;
            }
        });

        notify_info("Listo.", $notify);

        $modalDialog3.unbind("shown.bs.modal");
        $modalDialog3.on("shown.bs.modal", function() {
            $input.focus();
        });
        $modalDialog3.unbind("hidden.bs.modal");
        $modalDialog3.on("hidden.bs.modal", function() {
            if (registro) {
                if (onSelected)
                    onSelected(registro);
            } else {
                if (onCanceled)
                    onCanceled();
            }
        });

        $modalDialog3.modal({
            backdrop: "static"
        });
    };

    loadPage("/inventarios/view/almacen/terminal-seleccion.html", onComplete);
}
