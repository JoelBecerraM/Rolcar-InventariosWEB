
function maestroDetalleABC() {
}

maestroDetalleABC.prototype.initialize = function() {
};

maestroDetalleABC.prototype.initCaptura = function() {
    var _this = this;

    $("#btn-primero").click(function() {
        _this.primeroMaestro();
    });
    $("#btn-anterior").click(function() {
        _this.anteriorMaestro();
    });
    $("#btn-siguiente").click(function() {
        _this.siguienteMaestro();
    });
    $("#btn-ultimo").click(function() {
        _this.ultimoMaestro();
    });

    $("#btn-agregar").click(function() {
        _this.agregarMaestro();
    });
    $("#btn-guardar").click(function() {
        _this.guardarMaestro();
    });
    $("#btn-borrar").click(function() {
        _this.borrarMaestro();
    });
    $("#btn-finalizar").click(function() {
        _this.finalizarMaestro();
    });
    $("#btn-imprimir").click(function() {
        _this.imprimirMaestro();
    });

    $("#btn-detail-limpiar").click(function() {
        _this.limpiarDetalle();
    });
    $("#btn-detail-guardar").click(function() {
        _this.guardarDetalle();
    });
    $("#btn-detail-borrar").click(function() {
        _this.borrarDetalle();
    });

    _this.initGrid();
    _this.cargaMaestro();
    _this.initialize();
};

maestroDetalleABC.prototype.initGrid = function() {
};

maestroDetalleABC.prototype.cargaMaestro = function() {
};

maestroDetalleABC.prototype.permiteCaptura = function(response) {
};

maestroDetalleABC.prototype.pasaValoresMaestro = function(response) {
    pasaValoresForma("#form-master", response);
};

maestroDetalleABC.prototype.primeroMaestroValores = function() {
};

maestroDetalleABC.prototype.primeroMaestro = function() {
    var _this = this;

    var onAceptar = function() {
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
        _this.pasaValoresMaestro(response);
        _this.permiteCaptura(response);
        _this.capturarDetalles(response);
    };
    notify_info("Buscando los registros ...");
    catalogos(_this.registroMaestro, "first", _this.primeroMaestroValores(),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.anteriorMaestroValores = function() {
};

maestroDetalleABC.prototype.anteriorMaestro = function() {
    var _this = this;

    var onAceptar = function() {
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
        _this.pasaValoresMaestro(response);
        _this.permiteCaptura(response);
        _this.capturarDetalles(response);
    };
    notify_info("Buscando los registros ...");
    catalogos(_this.registroMaestro, "prev", _this.anteriorMaestroValores(),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.siguienteMaestroValores = function() {
};

maestroDetalleABC.prototype.siguienteMaestro = function() {
    var _this = this;

    var onAceptar = function() {
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
        _this.pasaValoresMaestro(response);
        _this.permiteCaptura(response);
        _this.capturarDetalles(response);
    };
    notify_info("Buscando los registros ...");
    catalogos(_this.registroMaestro, "next", _this.siguienteMaestroValores(),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.ultimoMaestroValores = function() {
};

maestroDetalleABC.prototype.ultimoMaestro = function() {
    var _this = this;

    var onAceptar = function() {
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
        _this.pasaValoresMaestro(response);
        _this.permiteCaptura(response);
        _this.capturarDetalles(response);
    };
    notify_info("Buscando los registros ...");
    catalogos(_this.registroMaestro, "last", _this.ultimoMaestroValores(),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.agregarMaestroInit = function() {
};

maestroDetalleABC.prototype.agregarMaestroValidar = function($notify) {
    var $form = $("#form-agregar");

    var pass = validaDatosForma($form);
    if (!pass) {
        notify_error("Hay errores con los datos, favor de corregirlos.", $notify);
        return false;
    }

    return true;
};

maestroDetalleABC.prototype.agregarMaestroValores = function(valores) {
};

maestroDetalleABC.prototype.agregarMaestroCancelar = function(_this) {
};

maestroDetalleABC.prototype.agregarMaestroAceptar = function(_this) {
    var valores = getValoresForma("#form-agregar");

    _this.agregarMaestroValores(valores);

    _this.agregarMaestroConfirmado(valores);
};

maestroDetalleABC.prototype.agregarMaestro = function() {
    var _this = this;

    var _call;

    var onComplete = function(response) {
        var $content = $(response);

        var aceptar = function() {
            if (!_this.agregarMaestroValidar($notify))
                return;

            $modalDialog3.modal("hide");

            _call = _this.agregarMaestroAceptar;
        };

        var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">Aceptar</button>");
        $buttonAceptar.click(function() {
            aceptar();
        });
        var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">Cancelar</button>");
        $buttonCancelar.click(function() {
            $modalDialog3.modal("hide");

            _call = _this.agregarMaestroCancelar;
        });

        $("#modalDialog3Label").html("Agregar");
        $("#modalDialog3Document").addClass("modal-lg");
        $("#modalDialog3Header").removeClass();
        $("#modalDialog3Header").addClass("modal-header bg-info text-white");
        $("#modalDialog3Body").empty();
        $("#modalDialog3Body").append($content);
        $("#modalDialog3Footer").empty();
        $("#modalDialog3Footer").append($buttonCancelar);
        $("#modalDialog3Footer").append($buttonAceptar);

        var $notify = $("#agregar-notificacion");
        notify_info("Listo.", $notify);

        $modalDialog3.unbind("shown.bs.modal");
        $modalDialog3.on("shown.bs.modal", function () {
            _this.agregarMaestroInit();
        });
        $modalDialog3.unbind("hidden.bs.modal");
        $modalDialog3.on("hidden.bs.modal", function() {
            if (_call)
                _call(_this);
        });

        $modalDialog3.modal({
            keyboard: false,
            backdrop: "static"
        });
    };

    loadPage(_this.agregarMaestroHTML, onComplete);
};

maestroDetalleABC.prototype.agregarMaestroRegistro = function(valores) {
};

maestroDetalleABC.prototype.agregarMaestroConfirmado = function(valores) {
    var _this = this;

    var $btnagregar = $("#btn-agregar");

    var onAceptar = function() {
        $btnagregar.focus();
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
        _this.agregarMaestroRegistro(response);

        _this.pasaValoresMaestro(response);
        _this.permiteCaptura(response);

        _this.capturarDetalles(response);

        notify_info("Listo.");
    };
    notify_info("Creando el registro de Maestro ...");
    accion(_this.registroMaestro, "agrega", JSON.stringify(valores),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.obtenRegistroMaestro = function() {
};

maestroDetalleABC.prototype.capturarDetallesOnComplete = function(response) {
};

maestroDetalleABC.prototype.capturarDetallesWhere = function(rowdata) {
};

maestroDetalleABC.prototype.capturarDetallesOrder = function() {
};

maestroDetalleABC.prototype.capturarDetallesOnError = function(response) {
    var onAceptar = function() {
    };
    if (response.exception.indexOf("WebException")!==-1) {
        precaucion(response.mensaje, onAceptar);
        notify_warning(response.mensaje);
    } else {
        error(response.exception, onAceptar);
        notify_error(response.exception);
    }
};

maestroDetalleABC.prototype.capturarDetalles = function(rowdata) {
    var _this = this;

    if (!rowdata) {
        rowdata = _this.obtenRegistroMaestro();
        if (!rowdata)
            return;
    }

    var $grid = $("#grid-detail");
    $grid.jqxGrid("clear");
    $grid.jqxGrid("clearselection");

    var onFail = function(err) {
        var msg = "Error al ejecutar la accion en el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(msg);
    };
    var onError = function(response) {
        _this.capturarDetallesOnError(response);
    };
    notify_secondary("Buscando los registros de Detalle ...");
    lista(_this.registroDetalle,
        _this.capturarDetallesWhere(rowdata), _this.capturarDetallesOrder(),
        _this.capturarDetallesOnComplete, onError, onFail);
};

maestroDetalleABC.prototype.limpiarDetalleInit = function() {
};

maestroDetalleABC.prototype.limpiarDetalle = function() {
    var _this = this;

    var $forma =$("#form-detail");
    limpiaValoresForma($forma);

    var $grid = $("#grid-detail");
    $grid.jqxGrid("clearselection");

    _this.limpiarDetalleInit();

    notify_info("Listo.");
};

maestroDetalleABC.prototype.validaDatosMaestro = function() {
    var $form = $("#form-master");
    var pass = validaDatosForma($form);
    if (!pass)
        notify_error("Hay errores con los datos, favor de corregirlos.");
    return pass;
};

maestroDetalleABC.prototype.guardarMaestroValores = function(valores) {
};

maestroDetalleABC.prototype.guardarMaestro = function() {
    var _this = this;

    if (!_this.validaDatosMaestro())
        return;

    var valores = getValoresForma("#form-master");

    _this.guardarMaestroValores(valores);

    var onAceptar = function() {
        _this.guardarMaestroConfirmado(valores);
    };
    pregunta("&iquest;Esta seguro de guardar el registro <b>"+valores.descripcion+"</b>?", onAceptar);
};

maestroDetalleABC.prototype.guardarMaestroConfirmadoOnComplete = function(response) {
};

maestroDetalleABC.prototype.guardarMaestroConfirmado = function(valores) {
    var _this = this;

    var $btnguardar = $("#btn-guardar");

    var onAceptar = function() {
        $btnguardar.focus();
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
    notify_info("Guardando el registro ...");
    accion(_this.registroMaestro, "guarda", JSON.stringify(valores),
        _this.guardarMaestroConfirmadoOnComplete, onError, onFail);
};

maestroDetalleABC.prototype.borrarMaestro = function() {
    var _this = this;

    if (!_this.validaDatosMaestro())
        return;

    var valores = getValoresForma("#form-master");

    _this.guardarMaestroValores(valores);

    var onAceptar = function() {
        _this.borrarMaestroConfirmado(valores);
    };
    pregunta("&iquest;Esta seguro de borrar el registro <b>"+valores.descripcion+"</b>?", onAceptar);
};

maestroDetalleABC.prototype.borrarMaestroConfirmadoOnComplete = function(response) {
};

maestroDetalleABC.prototype.borrarMaestroConfirmado = function(valores) {
    var _this = this;

    var $btnborrar = $("#btn-borrar");

    var onAceptar = function() {
        $btnborrar.focus();
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
    notify_info("Borrando el registro ...");
    accion(_this.registroMaestro, "borra", JSON.stringify(valores),
        _this.borrarMaestroConfirmadoOnComplete, onError, onFail);
};

maestroDetalleABC.prototype.finalizarMaestro = function() {
    var _this = this;

    if (!_this.validaDatosMaestro())
        return;

    var valores = getValoresForma("#form-master");

    _this.guardarMaestroValores(valores);

    var onAceptar = function() {
        _this.finalizarMaestroConfirmado(valores);
    };
    pregunta("&iquest;Esta seguro de finalizar el registro <b>"+valores.descripcion+"</b>?", onAceptar);
};

maestroDetalleABC.prototype.finalizarMaestroConfirmadoOnComplete = function(response) {
};

maestroDetalleABC.prototype.finalizarMaestroConfirmado = function(valores) {
    var _this = this;

    var $btnfinalizar = $("#btn-finalizar");

    var onAceptar = function() {
        $btnfinalizar.focus();
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
    notify_info("Finalizando el registro ...");
    accion(_this.registroMaestro, "finaliza", JSON.stringify(valores),
        _this.finalizarMaestroConfirmadoOnComplete, onError, onFail);
};

maestroDetalleABC.prototype.imprimirMaestro = function() {
};

maestroDetalleABC.prototype.validaDatosDetalle = function() {
    var $form = $("#form-detail");
    var pass = validaDatosForma($form);
    if (!pass)
        notify_error("Hay errores con los datos, favor de corregirlos.");
    return pass;
};

maestroDetalleABC.prototype.guardarDetalleValores = function(valores) {
};

maestroDetalleABC.prototype.guardarDetalleOnComplete = function(response) {
};

maestroDetalleABC.prototype.guardarDetalle = function() {
    var _this = this;

    if (!_this.validaDatosDetalle())
        return;

    var valores = getValoresForma("#form-detail");

    _this.guardarDetalleValores(valores);

    var $btn = $("#btn-detail-guardar");

    var onAceptar = function() {
        $btn.focus();
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
    notify_secondary("Agregando el registro de detalle ...");
    accion(_this.registroDetalle, "guarda", JSON.stringify(valores),
        this.guardarDetalleOnComplete, onError, onFail);
};

maestroDetalleABC.prototype.borrarDetalle = function() {
    var _this = this;

    var $grid = $("#grid-detail");
    var selectedrowindex = $grid.jqxGrid("selectedrowindex");
    if (selectedrowindex===-1) {
        notify_warning("Debe de seleccionar un rengl&oacute;n.");
        return;
    }

    var valores = $grid.jqxGrid("getrowdata", selectedrowindex);

    var onAceptar = function() {
        _this.borrarDetalleConfirmado(selectedrowindex, valores);
    };
    pregunta("&iquest;Esta seguro de borrar este detalle <b>"+valores.descripcion+"</b>?", onAceptar);
};

maestroDetalleABC.prototype.borrarDetalleConfirmado = function(selectedrowindex, valores) {
    var _this = this;

    var $btn = $("#btn-detail-borrar");

    var onAceptar = function() {
        $btn.focus();
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
        var $grid = $("#grid-detail");

        var rowid = $grid.jqxGrid("getrowid", selectedrowindex);
        var commit = $grid.jqxGrid("deleterow", rowid);
        $grid.jqxGrid("ensurerowvisible", 0);
        $grid.jqxGrid("clearselection");

        _this.limpiarDetalle();
    };
    notify_secondary("Borrando el registro de detalle ...");
    accion(_this.registroDetalle, "borra", JSON.stringify(valores),
        onComplete, onError, onFail);
};

maestroDetalleABC.prototype.percentageformatter = function (value, columnproperties) {
    return "<span style=\"margin: 4px; margin-top: 8px; float: "+columnproperties.cellsalign + ";\">"+formatPercentage(value)+"</span>";
};
