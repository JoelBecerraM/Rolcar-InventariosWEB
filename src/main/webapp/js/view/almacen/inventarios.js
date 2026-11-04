
function initInventario() {
    deshabilitaTodo();
    buscaInventario();

    notify_info("Listo.");
}

function deshabilitaTodo() {
    $("#inicia :input").attr("disabled", true);
    $("#inventario :input").attr("disabled", true);
    $("#productos :input").attr("disabled", true);
    $("#1er-conteo :input").attr("disabled", true);
    $("#2do-conteo :input").attr("disabled", true);
    $("#3er-conteo :input").attr("disabled", true);
    $("#termina :input").attr("disabled", true);
}

function habilitaFase(response) {
    if (!response.fase) {
        $("#productos :input").removeAttr("disabled");
    } else if (response.fase==="CI") {
        $("#productos :input").removeAttr("disabled");
    } else if (response.fase==="1ER") {
        $("#1er-conteo :input").removeAttr("disabled");
    } else if (response.fase==="2DO") {
        $("#2do-conteo :input").removeAttr("disabled");
    } else if (response.fase==="3ER") {
        $("#3er-conteo :input").removeAttr("disabled");
    } else if (response.status==="PE" && response.fase==="AF") {
        $("#termina :input").removeAttr("disabled");
    } else if (response.status==="FI") {
        $("#inicia :input").removeAttr("disabled");
    }
}

function verificaInventario(response) {
    pasaValoresForma("#form-inventario", response);
    deshabilitaTodo();
    habilitaFase(response);
    notify_success("Listo.");
}

function buscaInventario() {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        compania: usuario.compania
    };

    var onAceptar = function() {
        $flinventario.select();
        $flinventario.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(msg);
    };
    var onError = function(response) {
        if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, onAceptar);
            notify_warning(response.mensaje);
            if (response.mensaje==="No existe un Inventario [PE] en esta compania.") {
                $("#inicia :input").removeAttr("disabled");
            }
        } else {
            error(response.exception, onAceptar);
            notify_error(response.exception);
        }
    };
    var onComplete = function(response) {
        verificaInventario(response);
    };
    notify_info("Buscando el registro de Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "busca", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function iniciaInventario() {
    var aceptar = function() {
        iniciaInventarioAlmacen();
    };
    pregunta("Este proceso <b>Inicia la toma de Inventario.</b>&iquest; Realmente desea continuar?", aceptar);
}

function iniciaInventarioAlmacen() {
    var $btniniciainventario = $("#btn-inicia-inventario");

    var valores = {
        compania: usuario.compania,
        almacen: 0
    };

    var onAceptar = function() {
        $btniniciainventario.select();
        $btniniciainventario.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
    };
    notify_info("Buscando el registro de Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "crea", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function cargaProductos() {
    var aceptar = function() {
        cargaProductosSeleccion();
    };
    pregunta("Este proceso <b>Carga los Productos a la toma de Inventario.</b><br>&iquest;Realmente desea continuar?", aceptar);
}

function validaDatosCargaProductos() {
    var $form = $("#form-carga");
    var pass = validaDatosForma($form);
    if (!pass)
        notify_error("Hay errores con los datos, favor de corregirlos.");
    return pass;
}

function cargaProductosSeleccion() {
    var onComplete = function(response) {
        var $content = $(response);

        var aceptar = function() {
            if (!validaDatosCargaProductos())
                return;

            var $flinventario = $("#form-inventario [name=flinventario]");
            var flinventario = $flinventario.val();
            $("#cargaflinventario").val(flinventario);

            cargaProductosValidado();
        };

        var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">Aceptar</button>");
        $buttonAceptar.click(function() {
            aceptar();
        });
        var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">Cancelar</button>");
        $buttonCancelar.click(function() {
            $modalDialog3.modal("hide");
        });

        $("#modalDialog3Label").html("Selecci&oacute;n");
        $("#modalDialog3Document").addClass("modal-lg");
        $("#modalDialog3Header").removeClass();
        $("#modalDialog3Header").addClass("modal-header bg-info text-white");
        $("#modalDialog3Body").empty();
        $("#modalDialog3Body").append($content);
        $("#modalDialog3Footer").empty();
        $("#modalDialog3Footer").append($buttonCancelar);
        $("#modalDialog3Footer").append($buttonAceptar);
        $modalDialog3.modal();
    };

    loadPage("/inventarios/view/almacen/productos-carga.html", onComplete);
}

function cargaProductosValidado() {
    $modalDialog3.modal("hide");
    wm();

    $("#form-carga").submit(function (e) {
        var formObj = $(this);
        var formURL = formObj.attr("action");
        var formData = new FormData(this);
        $.ajax({
            url: formURL,
            type: "post",
            data: formData,
            mimeType: "multipart/form-data",
            contentType: false,
            cache: false,
            processData: false,
            success: function (data, textStatus, jqXHR) {
                cwm();

                var response = JSON.parse(data);
                if (response.error) {
                    notify_error(response.mensaje);
                } else {
                    verificaInventario(response);
                    mensaje("Carga de Productos terminada correctamente.");
                    notify_info("Listo.");
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                cwm();
                notify_error(errorThrown);
            }
        });
        e.preventDefault();
        //e.unbind();
    });
    $("#form-carga").submit();
}

function valuacionInventario() {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var flinventario = $flinventario.val();

    download("/inventarios/ExportadorExcel?export=ValuacionInventario&flinventario="+flinventario);
}

function valuacionInventarioConteo(conteo) {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var flinventario = $flinventario.val();

    download("/inventarios/ExportadorExcel?export=ValuacionInventarioConteo&flinventario="+flinventario+"&conteo="+conteo);
}

function diferenciasInventarioConteo(conteo) {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var flinventario = $flinventario.val();

    download("/inventarios/ExportadorExcel?export=DiferenciasInventarioConteo&flinventario="+flinventario+"&conteo="+conteo);
}

function analisisInventarioConteo(conteo) {
    analisisInventarios();
}

function activaPrimerConteo() {
    var aceptar = function() {
        activaPrimerConteoValidado();
    };
    pregunta("Este proceso <b>ACTIVA el PRIMER CONTEO</b> y marca todas las capturas de inventario como 1er Conteo.<br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function activaPrimerConteoValidado() {
    var $btnhabilita1erconteo = $("#btn-habilita-1er-conteo");
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        flinventario: parseInt($flinventario.val()),
        compania: usuario.compania
    };

    var onAceptar = function() {
        $btnhabilita1erconteo.select();
        $btnhabilita1erconteo.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
        mensaje("1er Conteo activado correctamente.");
    };
    notify_info("Cargando Productos al Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "activaPrimero", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function activaSegundoConteo() {
    var aceptar = function() {
        activaSegundoConteoValidado();
    };
    pregunta("Este proceso <b>ACTIVA el SEGUNDO CONTEO</b> y marca todas las capturas de inventario como 2do Conteo.<br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function activaSegundoConteoValidado() {
    var $btnhabilita2doconteo = $("#btn-habilita-2do-conteo");
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        flinventario: parseInt($flinventario.val()),
        compania: usuario.compania
    };

    var onAceptar = function() {
        $btnhabilita2doconteo.select();
        $btnhabilita2doconteo.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
        mensaje("2do Conteo activado correctamente.");
    };
    notify_info("Cargando Productos al Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "activaSegundo", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function activaTercerConteo() {
    var aceptar = function() {
        activaTercerConteoValidado();
    };
    pregunta("Este proceso <b>ACTIVA el TERCER CONTEO</b> y marca todas las capturas de inventario como 3er Conteo.<br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function activaTercerConteoValidado() {
    var $btnhabilita3erconteo = $("#btn-habilita-3er-conteo");
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        flinventario: parseInt($flinventario.val()),
        compania: usuario.compania
    };

    var onAceptar = function() {
        $btnhabilita3erconteo.select();
        $btnhabilita3erconteo.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
        mensaje("3er Conteo activado correctamente.");
    };
    notify_info("Cargando Productos al Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "activaTercero", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function afectaExistencias() {
    var aceptar = function() {
        afectaExistenciasValidado();
    };
    pregunta("Este proceso <b>AFECTA LAS EXISTENCIAS CON LA INFORMACI&Oacute;N DISPONIBLE DEL 3er CONTEO.</b><br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function afectaExistenciasValidado() {
    var $btnafectaexistencias = $("#btn-afecta-existencias");
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        flinventario: parseInt($flinventario.val()),
        compania: usuario.compania
    };

    var onAceptar = function() {
        $btnafectaexistencias.select();
        $btnafectaexistencias.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
        mensaje("Inventario Finalizado Correctamente.");
    };
    notify_info("Cargando Productos al Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "afecta", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function archivoInventario() {
    var aceptar = function() {
        archivoInventarioValidado();
    };
    pregunta("Este proceso <b>GENERA EL ARCHIVO DE EXCEL CON LA TOMA DE INVENTARIO.</b><br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function archivoInventarioValidado() {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var flinventario = $flinventario.val();

    download("/inventarios/ExportadorExcel?export=ArchivoInventario&flinventario="+flinventario);
}

function finalizaInventario() {
    var aceptar = function() {
        finalizaInventarioValidado();
    };
    pregunta("Este proceso <b>FINALIZA EL INVENTARIO.</b><br>"
            +"&iquest;Realmente desea continuar?", aceptar);
}

function finalizaInventarioValidado() {
    var $btnfinalizainventario = $("#btn-finaliza-inventario");
    var $flinventario = $("#form-inventario [name=flinventario]");
    var valores = {
        flinventario: parseInt($flinventario.val()),
        compania: usuario.compania
    };

    var onAceptar = function() {
        $btnfinalizainventario.select();
        $btnfinalizainventario.focus();
    };
    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
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
    };
    var onComplete = function(response) {
        verificaInventario(response);
        mensaje("Inventario Finalizado Correctamente.");
    };
    notify_info("Cargando Productos al Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "finaliza", JSON.stringify(valores),
        onComplete, onError, onFail);
}
