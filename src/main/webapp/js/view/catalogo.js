
function catalogoABC() {
}

catalogoABC.prototype.initialize = function() {
};

catalogoABC.prototype.initCaptura = function() {
    var _this = this;

    $("#btn-buscar").click(function() {
        _this.buscarRegistros();
    });
    $("#btn-limpiar").click(function() {
        _this.limpiarRegistro();
    });
    $("#btn-guardar").click(function() {
        _this.guardarRegistro();
    });
    $("#btn-borrar").click(function() {
        _this.borrarRegistro();
    });
    $("#btn-reportes").click(function() {
        _this.reportesRegistro();
    });

    _this.initialize();
};

catalogoABC.prototype.validaDatosRegistro = function() {
    var $form = $("#datos");
    var pass = validaDatosForma($form);
    if (!pass)
        notify_error("Hay errores con los datos, favor de corregirlos.");
    return pass;
};

catalogoABC.prototype.getValoresRegistro = function() {
    var valores = getValoresForma("#datos");
    return JSON.stringify(valores);
};

catalogoABC.prototype.guardarRegistroOnComplete = function(response) {
};

catalogoABC.prototype.guardarRegistro = function() {
    var _this = this;

    if (!_this.validaDatosRegistro())
        return;

    var valores = _this.getValoresRegistro();

    var onFail = function(err) {
        var mensaje = "Error al guardar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
        notify_error(response.mensaje);
    };
    notify_secondary("Guardando el registro ...");
    accion(_this.registroCatalogo, "save", valores,
        _this.guardarRegistroOnComplete, onError, onFail);
};

catalogoABC.prototype.limpiarRegistroInit = function() {
};

catalogoABC.prototype.limpiarRegistro = function() {
    var _this = this;

    var $form = $("#datos");
    limpiaValoresForma($form);

    _this.limpiarRegistroInit();

    $(window).scrollTop($("#titulo").offset().top);

    notify_info("Listo.");
};

catalogoABC.prototype.borrarRegistro = function() {
    var _this = this;

    var onAceptar = function() {
        _this.borrarRegistroConfirmado();
    };
    pregunta("&iquest;Esta seguro de borrar el registro?", onAceptar);
};

catalogoABC.prototype.borrarRegistroConfirmadoOnComplete = function(response) {
};

catalogoABC.prototype.borrarRegistroConfirmado = function() {
    var _this = this;

    var valores = _this.getValoresRegistro();

    var onFail = function(err) {
        var mensaje = "Error al borrar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
        notify_error(response.mensaje);
    };
    notify_info("Borrando el registro ...");
    accion(_this.registroCatalogo, "delete", valores,
        _this.borrarRegistroConfirmadoOnComplete, onError, onFail);
};

catalogoABC.prototype.buscarRegistrosOnAceptar = function() {
};

catalogoABC.prototype.buscarRegistrosValores = function() {
};

catalogoABC.prototype.buscarRegistrosBusqueda = function(complete, error) {
};

catalogoABC.prototype.buscarRegistrosOnComplete = function(response) {
    pasaValoresForma("#datos", response);
    notify_info("Listo.");
};

catalogoABC.prototype.buscarRegistros = function() {
    var _this = this;

    var onFail = function(err) {
        var mensaje = "Error al buscar el registro.<br><br><b>("+err.status+") "+err.statusText+"</b>";
        notify_error(mensaje);
    };
    var onError = function(response) {
        if (response.exception.indexOf("No existe este registro")!==-1) {
            _this.buscarRegistrosBusqueda(_this.buscarRegistrosOnComplete, onError);
        } else if (response.exception.indexOf("WebException")!==-1) {
            precaucion(response.mensaje, _this.buscarRegistrosOnAceptar);
            notify_warning(response.mensaje);
        } else {
            error(response.exception, _this.buscarRegistrosOnAceptar);
            notify_error(response.exception);
        }
    };
    notify_secondary("Buscando el registro ...");
    registro(_this.registroCatalogo, _this.buscarRegistrosValores(),
        _this.buscarRegistrosOnComplete, onError, onFail);
};

catalogoABC.prototype.reportesRegistro = function() {
};

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

function direccionABC() {
}

direccionABC.prototype.modificaDireccion = function() {
    var $direccion = $("#datos [name=direccion]");
    var direccion = $direccion.val();
    if (direccion==="")
        return;

    catalogoDirecciones(direccion);
};

direccionABC.prototype.buscarDireccion = function() {
    var _this = this;

    var $direccion = $("#datos [name=direccion]");
    var direccion = $direccion.val();
    if (direccion==="")
        return;

    var onAceptar = function() {
        $direccion.select();
        $direccion.focus();
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
        _this.buscarColonia(response);
        notify_info("Listo.");
    };
    notify_secondary("Buscando el registro ...");
    registro("com.atcloud.erp.dao.entity.DireccionDAO", usuario.compania+"|"+direccion,
        onComplete, onError, onFail);
};

direccionABC.prototype.buscarColonia = function(direccion) {
    var _this = this;

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
    var onComplete = function(colonias) {
        _this.pasaValoresDireccion(direccion, colonias[0]);
        notify_info("Listo.");
    };
    notify_secondary("Buscando el registro ...");
    coleccion("com.atcloud.erp.collection.ColoniasCollection", "c.compania = '"+direccion.compania+"' AND c.colonia = '"+direccion.colonia+"'",
        onComplete, onError, onFail);
};

direccionABC.prototype.pasaValoresDireccion = function(direccion, colonia) {
    var $descripcion_direccion = $("#datos [name=descripcion_direccion]");
    //$descripcion_direccion.val(JSON.stringify(response));
    var direccion_descripcion = direccion.calle
        +(direccion.noexterior==="" ? "" : " No. Ext. "+direccion.noexterior)
        +(direccion.nointerior==="" ? "" : " No. Int. "+direccion.nointerior);
    if (colonia) {
        direccion_descripcion = direccion_descripcion
            +"\n"+colonia.dscolonia+", "+colonia.dspoblacion
            +"\n"+colonia.dsentidadfederativa+", "+colonia.dspais;
    }
    direccion_descripcion = direccion_descripcion+"\nC.P.: "+direccion.codigopostal;


    $descripcion_direccion.val(direccion_descripcion);
};
