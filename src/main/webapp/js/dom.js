
function startsWith(value, str) {
    return value.indexOf(str)===0;
}

function replaceAll(value, find, replace) {
    return value.replace(new RegExp(find, "g"), replace);
}

function replaceAllIgnoreCase(value, find, replace) {
    return value.replace(new RegExp(find, "ig"), replace);
}

function matchExp(value, str) {
    return (new String(value)).match(str);
}

function getDate() {
    var today = new Date();
    var localoffset = -(today.getTimezoneOffset() / 60);
    return new Date(new Date().getTime() + localoffset * 3600 * 1000);
}

function getISODateTime() {
    return replaceAll(getDate().toJSON().substring(0,19), "T", " ");
}

function getDaysSince(target) {
    var now = new Date();
    var diff = now.getTime() - target.getTime();

    var out = [];
    var years = Math.floor(diff / (1000*60*60*24*365));

    if(years > 0)
        out.push(years+" año"+(years === 1 ? "" : "s"));
    return out.join(" ");
}

function parseDate(date) {
    var tokens = date.split("-");
    return new Date(parseInt(tokens[0]), parseInt(tokens[1]), parseInt(tokens[2]));
}

function parseDateTime(date) {
    var date1 = date.substring(0,10);
    var time1 = date.substring(11);
    var tokensd = date1.split("-");
    var tokenst = time1.split(":");
    return new Date(parseInt(tokensd[0]), parseInt(tokensd[1]), parseInt(tokensd[2]),
        parseInt(tokenst[0]), parseInt(tokenst[1]), parseInt(tokenst[2]), 0);
}

function formatNumberDecimal(value) {
    var format = formatMoney(value);
    format = format.substring(1);
    return format;
}

function formatNumber(value) {
    var format = formatMoney(value);
    format = format.substring(1);
    format = format.substring(0, format.length - 3);
    return format;
}

function formatMoney(value) {
    return "$"+parseFloat(value, 10).toFixed(2).replace(/(\d)(?=(\d{3})+\.)/g, "$1,").toString();
}

function formatPercentage(value) {
    return redondea(parseFloat(value) * 100)+"%";
}

function redondea(value, decimals) {
    if (!decimals)
        decimals = 2;
    return Number(parseFloat(value)).toFixed(decimals);
}

function XMLtoString(elem) {
    var serialized;
    try {
        serializer = new XMLSerializer();
        serialized = serializer.serializeToString(elem);
    }
    catch (e) {
        serialized = elem.xml;
    }
    return serialized;
}

function validateEmptyField(field, notify) {
    if (field.val()==="") {
        var aceptar = function() {
            field.focus();
        };
        if (!notify)
            notify = mensaje;
        notify("Este campo no puede estar vac&iacute;o.", aceptar);
        return false;
    }
    return true;
}

function getKeys(obj) {
    var keys = [];
    for (var key in obj)
        keys.push(key);
    return keys;
}

function getProperties(obj) {
    var properties = [];
    //var methods = [];
    // Determination functions and properties of the target by a parent object
    Object.getOwnPropertyNames(obj).forEach((name) => {
        if (typeof obj[name]==='function') {
            //methods.push(name);
        } else if (obj.hasOwnProperty(name) && properties.indexOf(name)===-1) {
            properties.push(name);
        }
    });
    return properties;
}

function loadScript(script, onComplete) {
    $.getScript(script)
    .done(function(script, textStatus) {
        if (onComplete)
            onComplete();
    }).fail(function(err) {
        error("Error al obtener el script ["+script+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function loadMainPage(page, onComplete) {
    $.ajax({
        url: page,
        beforeSend: function() {
            wm();
        }
    }).done(function(response) {
        cwm();
        $("#mainContent").html(response);
        if (onComplete)
            onComplete();
    }).fail(function(err) {
        cwm();
        error("Error al obtener la pagina ["+page+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function loadPage(page, onComplete) {
    $.ajax({
        url: page,
        beforeSend: function() {
            wm();
        }
    }).done(function(response) {
        cwm();
        if (onComplete)
            onComplete(response);
    }).fail(function(err) {
        cwm();
        error("Error al obtener la pagina ["+page+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function percentageFormatter(value, columnproperties) {
    var percentage = formatPercentage(value);
    return "<span style=\"margin: 4px; float: "+columnproperties.cellsalign+";\">"+percentage+"</span>";
}

function selectedRowIndex($grid) {
    var selectedrowindex = $grid.jqxGrid("getselectedrowindex");
    return selectedrowindex;
}

function selectedRowData($grid) {
    var selectedrowindex = $grid.jqxGrid("getselectedrowindex");
    if (selectedrowindex!==-1) {
        var selectedrowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
        return selectedrowdata;
    }
    return null;
}

var $modalDialog = $("#modalDialog");
var $modalDialog2 = $("#modalDialog2");
var $modalDialog3 = $("#modalDialog3");
var $modalDialogBusquedas = $("#modalDialogBusquedas");

function pregunta(msg, aceptar, cancelar, etiquetas, aceptardefault) {
    if (!etiquetas)
        etiquetas = ["Si", "No"];

    var _call;
    var $buttonAceptar = $("<button type=\"button\" class=\"btn btn-outline-success\">"+etiquetas[0]+"</button>");
    $buttonAceptar.click(function() {
        $modalDialog2.modal("hide");
        _call = aceptar;
    });
    var $buttonCancelar = $("<button type=\"button\" class=\"btn btn-outline-secondary\">"+etiquetas[1]+"</button>");
    $buttonCancelar.click(function() {
        $modalDialog2.modal("hide");
        _call = cancelar;
    });

    $("#modalDialogLabel2").html("Pregunta");
    $("#modalDialogDocument2").removeClass("modal-lg");
    $("#modalDialogHeader2").removeClass();
    $("#modalDialogHeader2").addClass("modal-header bg-info text-white");
    $("#modalDialogBody2").html("<p>"+msg+"<p>");
    $("#modalDialogFooter2").empty();
    $("#modalDialogFooter2").append($buttonCancelar);
    $("#modalDialogFooter2").append($buttonAceptar);
    $modalDialog2.unbind("shown.bs.modal");
    $modalDialog2.on("shown.bs.modal", function() {
        if (aceptardefault)
            $buttonAceptar.focus();
        else
            $buttonCancelar.focus();
    });
    $modalDialog2.unbind("hidden.bs.modal");
    $modalDialog2.on("hidden.bs.modal", function() {
        if (_call)
            _call();
    });
    $modalDialog2.modal({
        keyboard: false,
        backdrop: "static"
    });
}

function precaucion(msg, aceptar) {
    var _call;
    var $button = $("<button type=\"button\" class=\"btn btn-outline-warning\">Aceptar</button>");
    $button.click(function() {
        $modalDialog.modal("hide");
        _call = aceptar;
    });

    $("#modalDialogLabel").html("Alerta");
    $("#modalDialogDocument").removeClass("modal-lg");
    $("#modalDialogHeader").removeClass();
    $("#modalDialogHeader").addClass("modal-header bg-warning text-dark");
    $("#modalDialogBody").html("<p>"+msg+"<p>");
    $("#modalDialogFooter").empty();
    $("#modalDialogFooter").append($button);
    $modalDialog.unbind("shown.bs.modal");
    $modalDialog.on("shown.bs.modal", function() {
        $button.focus();
    });
    $modalDialog.unbind("hidden.bs.modal");
    $modalDialog.on("hidden.bs.modal", function() {
        if (_call)
            _call();
    });
    $modalDialog.modal({
        keyboard: false,
        backdrop: "static"
    });
}

function error(msg, aceptar) {
    var _call;
    var $button = $("<button type=\"button\" class=\"btn btn-outline-danger\">Aceptar</button>");
    $button.click(function() {
        $modalDialog.modal("hide");
        _call = aceptar;
    });

    $("#modalDialogLabel").html("Error");
    $("#modalDialogDocument").removeClass("modal-lg");
    $("#modalDialogHeader").removeClass();
    $("#modalDialogHeader").addClass("modal-header bg-danger text-white");
    $("#modalDialogBody").html("<p>"+msg+"<p>");
    $("#modalDialogFooter").empty();
    $("#modalDialogFooter").append($button);
    $modalDialog.unbind("shown.bs.modal");
    $modalDialog.on("shown.bs.modal", function() {
        $button.focus();
    });
    $modalDialog.unbind("hidden.bs.modal");
    $modalDialog.on("hidden.bs.modal", function() {
        if (_call)
            _call();
    });
    $modalDialog.modal({
        keyboard: false,
        backdrop: "static"
    });
}

function mensaje(msg, aceptar) {
    var _call;
    var $button = $("<button type=\"button\" class=\"btn btn-outline-primary\">Aceptar</button>");
    $button.click(function() {
        $modalDialog.modal("hide");
        _call = aceptar;
    });

    $("#modalDialogLabel").html("Mensaje");
    $("#modalDialogDocument").removeClass("modal-lg");
    $("#modalDialogHeader").removeClass();
    $("#modalDialogHeader").addClass("modal-header bg-primary text-white");
    $("#modalDialogBody").html("<p>"+msg+"<p>");
    $("#modalDialogFooter").empty();
    $("#modalDialogFooter").append($button);
    $modalDialog.unbind("shown.bs.modal");
    $modalDialog.on("shown.bs.modal", function() {
        $button.focus();
    });
    $modalDialog.unbind("hidden.bs.modal");
    $modalDialog.on("hidden.bs.modal", function() {
        if (_call)
            _call();
    });
    $modalDialog.modal({
        keyboard: false,
        backdrop: "static"
    });
}

function comboRegistros(registro, where, order, $campo, valor, texto, valorinicial) {
    var onFail = function(err) {
        var mensaje = "Error al obtener la lista de registros ["+registro+";"+where+"] .<br><br><b>("+err.status+") "+err.statusText+"</b>";
        error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
    };
    var onComplete = function(response) {
        //$campo.empty();
        for (var i=0; i<response.length; i++) {
            $campo.append($("<option></option>").attr("value",
                response[i][valor]).text(response[i][texto]));
        }
        if (valorinicial)
            $campo.val(valorinicial);
    };
    lista(registro, where, order, onComplete, onError, onFail);
}

function lista(registro, where, order, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Lista",
            where: where,
            order: order,
            registro: registro
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al obtener la lista de registros ["+registro+";"+where+"] .<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function coleccion(registro, where, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Coleccion",
            registro: registro,
            where: where
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al obtener la coleccion de los registros.<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function array(registro, criteria, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Array",
            registro: registro,
            criteria: criteria
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al obtener los registros.<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function maestroDetalle(registro, accion, valores, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "MaestroDetalle",
            registro: registro,
            accion: accion,
            valores: valores
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al ejecutar el maestro detalle sobre el registro ["+registro+";"+accion+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function catalogos(registro, accion, valores, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Catalogos",
            registro: registro,
            accion: accion,
            valores: valores
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al ejecutar el catalogo sobre el registro ["+registro+";"+accion+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function accion(registro, accion, valores, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Accion",
            registro: registro,
            accion: accion,
            valores: valores
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al ejecutar la accion en el registro ["+registro+";"+accion+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function registro(record, valores, onComplete, onError, onFail) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: {
            id: "Registro",
            registro: record,
            valores: valores
        }
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (onError)
                onError(response);
        } else {
            if (onComplete)
                onComplete(response);
        }
    }).fail(function(err) {
        cwm();
        if (onFail)
            onFail(err);
        else
            error("Error al obtener el registro ["+record+";"+valores+"].<br><br><b>("+err.status+") "+err.statusText+"</b>");
    });
}

function mvc_wm(data, done, fail) {
    $.ajax({
        url: "/inventarios/MVC",
        data: data
    }).done(function(response, textStatus, jqXHR) {
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            error(response.mensaje);
            return;
        }
        if (done)
            done(response);
    }).fail(function(err) {
        if (fail)
            fail(err);
    });
}

function mvc(data, done, fail, message) {
    $.ajax({
        url: "/inventarios/MVC",
        beforeSend: function() {
            wm();
        },
        data: data
    }).done(function(response, textStatus, jqXHR) {
        cwm();
        if (jqXHR.status===205) {
            var complete = function() {
                top.location = index;
            };
            mensaje("La sesion no existe o ha expirado, para continuar debe de iniciar su sesión nuevamente.", complete);
            return;
        }
        if (response.error) {
            if (message) {
                message(response);
                return;
            }
            error(response.mensaje);
            return;
        }
        if (done)
            done(response);
    }).fail(function(err) {
        cwm();
        if (fail)
            fail(err);
    });
}

function download(url, complete) {
    wm();

    $.fileDownload(url, {
        successCallback: function (url) {
            cwm();
            if (complete)
                complete();
        },
        failCallback: function (responseHtml, url, err) {
            cwm();
            error(JSON.stringify(err));
        }
    });
}

function notify_info(msg, $field) {
    notify_class(msg, "alert alert-info", $field);
}

function notify_warning(msg, $field) {
    notify_class(msg, "alert alert-warning", $field);
}

function notify_error(msg, $field) {
    notify_class(msg, "alert alert-danger", $field);
}

function notify_success(msg, $field) {
    notify_class(msg, "alert alert-success", $field);
}

function notify_secondary(msg, $field) {
    notify_class(msg, "alert alert-secondary", $field);
}

function notify_class(msg, classes, $field) {
    if (!$field)
        $field = $("#notificacion");
    if ($field) {
        $field.removeClass();
        $field.addClass(classes);
        $field.html(msg);
    }
}

function a(msg) {
    mensaje(msg);
}

function gi(id) {
    return document.getElementById(id);
}

function ce(id) {
    return document.createElement(id);
}

var $pleaseWaitDiv = $(
    '<div class="modal" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-hidden="true" style="padding-top:15%; overflow-y:visible;">' +
    '  <div class="modal-dialog">' +
    '    <div class="modal-content">' +
    '      <div class="modal-header"><h3>Por favor espere ...</h3></div>' +
    '      <div class="modal-body">' +
    '        <div class="progress progress-bar progress-bar-striped progress-bar-animated" style="width: 100%"></div>' +
    '      </div>' +
    '    </div>' +
    '  </div>' +
    '</div>'
    );

function wm() {
    $pleaseWaitDiv.modal();
    document.body.style.cursor = "wait";
}

function cwm() {
    $pleaseWaitDiv.modal("hide");
    document.body.style.cursor = "default";
}

function kc(e) {
    var a = window.event ? event.keyCode : e.keyCode;
    return a;
}

function scv(name, value, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = name + "=" + value + "; " + expires;
}

function gcv(name) {
    var s = document.cookie;
    var pos = s.indexOf(name + "=");
    if (pos===-1)
        return null;
    var start = pos + name.length + 1;
    var end = s.indexOf(";", start);
    if (end===-1)
        end = s.length;
    return s.substring(start, end);
}

function rcv(name) {
    scv(name, "", -1);
}

function popup(href, windowname) {
    var width = screen.width - 200;
    var height = screen.height - 200;
    var left = (screen.width / 2) - (width / 2);
    var top = (screen.height / 2) - (height / 2);
    window.open(href, windowname,
        "toolbar=0,scrollbars=1,location=0,statusbar=0,menubar=0,resizable=1,width="+width+",height="+height+",top="+top+",left="+left);
}

function openTab(url) {
  var win = window.open(url, '_blank');
  win.focus();
}

function pasaValoresForma(form, response) {
    var properties = getProperties(response);
    properties.forEach(function(element) {
        var $input = $(form+" [name="+element+"]");
        if ($input[0]) {
            var type = $input[0].type;
            if (type==="checkbox") {
                $input.prop("checked", response[element]==="1");
            } else {
                $input.val(response[element]);
            }
        }
    });
}

function validaDatosForma($form) {
    var valid = $form[0].checkValidity();
    $form[0].classList.add("was-validated");
    return valid;
}

function getValoresParametros(response) {
    var ret = "";
    var properties = getProperties(response);
    properties.forEach(function(element) {
        var value = response[element];
        if (value&&value!=="")
            ret += "&"+element+"="+response[element];
    });
    return ret;
}

function getValoresForma(forma) {
    var values = {};
    $.each($("input, select, textarea", forma), function (k) {
        var name = $(this).attr("name");
        var type = $(this)[0].type;
        if (type==="checkbox") {
            values[name] = $(this).is(":checked") ? "1" : "0";
        } else if (type==="radio") {
            if ($(this).is(":checked"))
                values[name] = $(this).val();
        } else {
            var value = $(this).val();
            if ($(this).hasClass("text-uppercase"))
                value = value.toUpperCase();
            values[name] = value;
        }
    });
    return values;
}

function resetForma($form) {
    $form.removeClass("was-validated");
    $form[0].reset();
}

function limpiaValoresForma(forma) {
    forma.removeClass("was-validated");
    $.each($("input, select, textarea", forma), function (k) {
        //var name = $(this).attr("name");
        var disabled = $(this)[0].disabled;
        if (disabled)
            return;
        var type = $(this)[0].type;
        if (type==="checkbox") {
            $(this).attr("checked", false);
        } else if (type==="select-one") {
            $(this).prop("selectedIndex", 0);
        } else {
            $(this).val("");
        }
    });
}

function autocomplete(min, inp, data, result, render, onclick) {
    var arr = [];
    /*the autocomplete function takes two arguments,
    the text field element and an array of possible autocompleted values:*/
    var currentFocus;

    /*execute a function when someone writes in the text field:*/
    inp.addEventListener("input", function(e) {
        var val = this.value;
        //close any already open lists of autocompleted values
        closeAllLists();
        if (!val) {
            arr = [];
            return;
        }
        //starts only whit min characters
        if (arr.length===0&&val.length<min) {
            return;
        }
        seekRecords(val);
    });

    /*execute a function presses a key on the keyboard:*/
    inp.addEventListener("keydown", function(e) {
        var list = gi(this.id+"-autocomplete-list");
        if (!list)
            return;
        var x = list.getElementsByTagName("div");
        if (e.keyCode===40) {
        /*If the arrow DOWN key is pressed,
         increase the currentFocus variable:*/
            currentFocus++;
            /*and and make the current item more visible:*/
            addActive(x);
            /*scroll div*/
            list.scrollTop = x[currentFocus].offsetTop;
            return false;
        } else if (e.keyCode===38) { //up
            /*If the arrow UP key is pressed,
            decrease the currentFocus variable:*/
            currentFocus--;
            /*and and make the current item more visible:*/
            addActive(x, list);
            /*scroll div*/
            list.scrollTop = x[currentFocus].offsetTop;
            return false;
        } else if (e.keyCode===13) {
            /*If the ENTER key is pressed, prevent the form from being submitted,*/
            e.preventDefault();
            if (currentFocus > - 1) {
                /*and simulate a click on the "active" item:*/
                if (x)
                    x[currentFocus].click();
                return false;
            }
        }
    });

    /*execute a function presses a key on the keyboard:*/
    inp.addEventListener("keyup", function(e) {
        var k = kc(e);
        /*close on ESC*/
        if (k===27) {
            arr = [];
            closeAllLists();
            e.preventDefault();
            return false;
        }
        /*seek on ENTER*/
        if (k===13) {
            if (arr.length===0) {
                var val = inp.value;
                if (val==="")
                    return;
                arr = [];
                closeAllLists();
                seekRecords(val);
            }
            e.preventDefault();
            return false;
        }
    });

    function seekRecords(val) {
        currentFocus = - 1;
        /*fill the array*/
        if (arr.length===0) {
            var ondata = function(array) {
                arr = array;
                if (arr.length===1) {
                    closeAllLists();
                    var record = arr[0];
                    arr = [];
                    onclick(inp, record);
                    return;
                }
                if (arr.length===0) {
                    inp.select();
                    inp.focus();
                    return;
                }
                selectedRecords(val);
            };
            data(val, ondata);
            return;
        } else {
            /*search records*/
            selectedRecords(val);
        }
    };

    function selectedRecords(val) {
        val = val.toUpperCase();
        var a;
        /*create a div element that will contain the items (values):*/
        a = ce("div");
        a.setAttribute("id", inp.id+"-autocomplete-list");
        a.setAttribute("class", "autocomplete-items");
        /*append the div element as a child of the autocomplete container:*/
        inp.parentNode.appendChild(a);
        /*for each item in the array...*/
        for (var i=0; i<arr.length; i++) {
            /*check if the item starts with the same letters as the text field value:*/
            if (result(arr[i], val)) {
            //if (arr[i].substr(0, val.length).toUpperCase()===val.toUpperCase()) {
                /*create a DIV element for each matching element:*/
                var b = ce("div");
                /*make the matching letters bold:*/
                b.innerHTML = render(arr[i], val);
                /*insert a input field that will hold the current array item's value:*/
                b.innerHTML += "<input type='hidden' value='"+JSON.stringify(arr[i])+"'>";
                /*execute a function when someone clicks on the item value (DIV element):*/
                b.addEventListener("click", function(e) {
                    /*get the value for the autocomplete text field:*/
                    var value = this.getElementsByTagName("input")[0].value;
                    /*close the list of autocompleted values,
                    (or any other open lists of autocompleted values:*/
                    closeAllLists();
                    /*empty array*/
                    arr = [];
                    /*callback*/
                    onclick(inp, JSON.parse(value));
                });
                a.appendChild(b);
            }
        }
        inp.focus();
    }

    function addActive(x, list) {
        /*a function to classify an item as "active":*/
        if (!x)
            return false;
        /*start by removing the "active" class on all items:*/
        removeActive(x);
        if (currentFocus >= x.length)
            currentFocus = 0;
        if (currentFocus < 0)
            currentFocus = (x.length - 1);
        /*add class "autocomplete-active":*/
        x[currentFocus].classList.add("autocomplete-active");
    }

    function removeActive(x) {
        /*a function to remove the "active" class from all autocomplete items:*/
        for (var i=0; i<x.length; i++) {
            x[i].classList.remove("autocomplete-active");
        }
    }

    function closeAllLists(elmnt) {
        /*close all autocomplete lists in the document,
        except the one passed as an argument:*/
        var x = document.getElementsByClassName("autocomplete-items");
        for (var i=0; i<x.length; i++) {
            if (elmnt!==x[i] && elmnt!==inp) {
                x[i].parentNode.removeChild(x[i]);
            }
        }
    }

    /*execute a function when someone clicks in the document:*/
    //document.addEventListener("click", function (e) {
    //    closeAllLists(e.target);
    //});
}

var device = {
    options: [],
    header: [navigator.platform, navigator.userAgent, navigator.appVersion, navigator.vendor, window.opera],
    dataos: [
        {name: 'Windows Phone', value: 'Windows Phone', version: 'OS'},
        {name: 'Windows', value: 'Win', version: 'NT'},
        {name: 'iPhone', value: 'iPhone', version: 'OS'},
        {name: 'iPad', value: 'iPad', version: 'OS'},
        {name: 'Kindle', value: 'Silk', version: 'Silk'},
        {name: 'Android', value: 'Android', version: 'Android'},
        {name: 'PlayBook', value: 'PlayBook', version: 'OS'},
        {name: 'BlackBerry', value: 'BlackBerry', version: '/'},
        {name: 'Macintosh', value: 'Mac', version: 'OS X'},
        {name: 'Linux', value: 'Linux', version: 'rv'},
        {name: 'Palm', value: 'Palm', version: 'PalmOS'}
    ],
    databrowser: [
        {name: 'Chrome', value: 'Chrome', version: 'Chrome'},
        {name: 'Firefox', value: 'Firefox', version: 'Firefox'},
        {name: 'Safari', value: 'Safari', version: 'Version'},
        {name: 'Internet Explorer', value: 'MSIE', version: 'MSIE'},
        {name: 'Opera', value: 'Opera', version: 'Opera'},
        {name: 'BlackBerry', value: 'CLDC', version: 'CLDC'},
        {name: 'Mozilla', value: 'Mozilla', version: 'Mozilla'}
    ],
    init: function () {
        var agent = this.header.join(' '),
                os = this.matchItem(agent, this.dataos),
                browser = this.matchItem(agent, this.databrowser);

        return {os: os, browser: browser};
    },
    matchItem: function (string, data) {
        var i = 0,
                j = 0,
                html = '',
                regex,
                regexv,
                match,
                matches,
                version;

        for (i = 0; i < data.length; i += 1) {
            regex = new RegExp(data[i].value, 'i');
            match = regex.test(string);
            if (match) {
                regexv = new RegExp(data[i].version+'[- /:;]([\\d._]+)', 'i');
                matches = string.match(regexv);
                version = '';
                if (matches) {
                    if (matches[1]) {
                        matches = matches[1];
                    }
                }
                if (matches) {
                    matches = matches.split(/[._]+/);
                    for (j = 0; j < matches.length; j += 1) {
                        if (j === 0) {
                            version += matches[j]+'.';
                        } else {
                            version += matches[j];
                        }
                    }
                } else {
                    version = '0';
                }
                return {
                    name: data[i].name,
                    version: parseFloat(version)
                };
            }
        }
        return {name: 'unknown', version: 0};
    }
};
