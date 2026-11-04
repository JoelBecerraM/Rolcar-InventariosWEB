
function login() {
    function onComplete() {
        function onCompleteII() {
            initLogin();
        };
        loadScript("/inventarios/js/view/login.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/login.html", onComplete);
}

function main() {
    var state = {
        func: "_main"
    };
    pushState(state);

    _main();
}

function _main() {
    function onComplete() {
        function onCompleteII() {
            initMain();
        };
        loadScript("/inventarios/js/view/main.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/main.html", onComplete);
}

function catalogoCompanias() {
    var state = {
        func: "_catalogoCompanias"
    };
    pushState(state);

    _catalogoCompanias();
}

function _catalogoCompanias() {
    if (!validaPermiso("catalogoCompanias"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initCatalogo();
        };
        loadScript("/inventarios/js/view/catalogos/companias.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/catalogos/companias.html", onComplete);
}

function catalogoPermisos() {
    var state = {
        func: "_catalogoPermisos"
    };
    pushState(state);

    _catalogoPermisos();
}

function _catalogoPermisos() {
    if (!validaPermiso("catalogoPermisos"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initCatalogo();
        };
        loadScript("/inventarios/js/view/catalogos/permisos.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/catalogos/permisos.html", onComplete);
}

function catalogoPerfiles() {
    var state = {
        func: "_catalogoPerfiles"
    };
    pushState(state);

    _catalogoPerfiles();
}

function _catalogoPerfiles() {
    if (!validaPermiso("catalogoPerfiles"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initCatalogo();
        };
        loadScript("/inventarios/js/view/catalogos/perfiles.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/catalogos/perfiles.html", onComplete);
}

function catalogoUsuarios() {
    var state = {
        func: "_catalogoUsuarios"
    };
    pushState(state);

    _catalogoUsuarios();
}

function _catalogoUsuarios() {
    if (!validaPermiso("catalogoUsuarios"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initCatalogo();
        };
        loadScript("/inventarios/js/view/catalogos/usuarios.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/catalogos/usuarios.html", onComplete);
}

function inventarios() {
    var state = {
        func: "_catalogoUsuarios"
    };
    pushState(state);

    _inventarios();
}

function _inventarios() {
    if (!validaPermiso("inventarios"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initInventario();
        };
        loadScript("/inventarios/js/view/almacen/inventarios.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/almacen/inventarios.html", onComplete);
}

function analisisInventarios() {
    var state = {
        func: "_analisisInventarios"
    };
    pushState(state);

    _analisisInventarios();
}

function _analisisInventarios() {
    if (!validaPermiso("analisisInventarios"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initAnalisisInventario();
        };
        loadScript("/inventarios/js/view/almacen/analisis.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/almacen/analisis.html", onComplete);
}

function auditoriaInventarios() {
    var state = {
        func: "_auditoriaInventarios"
    };
    pushState(state);

    _auditoriaInventarios();
}

function _auditoriaInventarios() {
    if (!validaPermiso("auditoriaInventarios"))
        return false;

    function onComplete() {
        function onCompleteII() {
            initAuditoriaInventarios();
        };
        loadScript("/inventarios/js/view/almacen/auditoria.js", onCompleteII);
    };

    loadMainPage("/inventarios/view/almacen/auditoria.html", onComplete);
}

//
//
//

function validaPermiso(permiso) {
    $("#navbarTogglerHeader").collapse("hide");

    if (!usuario.permisos.includes(permiso)) {
        error("Usted no tiene permiso para esta opci&oacute;n \"<b>"+permiso+"</b>\".");
        return false;
    }

    return true;
}

function cerrarSesion() {
    var aceptar = function() {
        var data = {
            id: "IniciaSesion",
            cerrar: "OK"
        };
        var done = function(response) {
            top.location = "/inventarios";
        };
        var fail = function(err) {
            error("Error al Cerrar la Sesi&oacute;n.<br><br><b>("+err.status+") "+err.statusText+"</b>");
        };
        mvc(data, done, fail);
    };
    pregunta("&iquest;Realmente desea cerrar la sesi&oacute;n?", aceptar);
}

//
//
//

function pushState(state) {
    //console.log("pushState");
    //console.log(state);
    window.history.pushState(state, null, "");
}

function renderState(state) {
    //console.log("renderState");
    //console.log(state);
    if (!state)
        return;
    if (!state.func)
        return;
    if (!state.data) {
        var fn = window[state.func];
        if (typeof fn==="function")
            fn.apply(null, null);
    }
}
