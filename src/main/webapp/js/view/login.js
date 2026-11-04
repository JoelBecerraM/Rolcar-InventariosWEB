
function initLogin() {
    $("#navTop").hide();
    $("#navBottom").hide();

    var login = gcv("login");
    var valores;
    if (login) {
        valores = JSON.parse(login);
        pasaValoresForma("#form-login", valores);
        $("#form-login [name=recordar]").prop("checked", valores.recordar==="1");
    }
    
    buscaCompanias();    
}

function buscaCompanias(valorinicial) {
    var $compania = $("#form-login [name=compania]");

    var onFail = function(err) {
        var mensaje = "Error al obtener la lista de registros ["+registro+";"+where+"] .<br><br><b>("+err.status+") "+err.statusText+"</b>";
        error(mensaje);
    };
    var onError = function(response) {
        error(response.mensaje);
    };
    var onComplete = function(response) {
        //$compania.empty();
        for (var i=0; i<response.length; i++) {
            $compania.append($("<option></option>").attr("value",
                response[i].compania).text(response[i].razonsocial+" ["+response[i].compania+"]"));
        }
        if (valorinicial)
            $compania.val(valorinicial);
    };

    var where = "1 = 1";
    var order = "razonsocial";
    lista("mx.com.rolcar.inventarios.dao.entity.CompaniaDAO", where, order,
        onComplete, onError, onFail);
}

function iniciaSesion() {
    var $form = $("#form-login");
    var pass = validaDatosForma($form);
    if (!pass)
        notify_error("Hay errores con los datos, favor de corregirlos.");

    var data = getValoresForma("#form-login");

    if (data.recordar==="1") {
        scv("login", JSON.stringify(data), 7);
    } else {
        rcv("login");
    }

    var location = top.location;

    data["id"] = "IniciaSesion";

    var done = function(response) {
        var inicio = function() {
            top.location = location;
        };

        mensaje("Bienvenido <b>"+response.nombre+"</b> a la pagina de Rolcar Inventarios.", inicio);
    };
    var fail = function(err) {
        error("Error al Iniciar la Sesi&oacute;n.<br><br><b>("+err.status+") "+err.statusText+"</b>");
    };
    mvc(data, done, fail);
}

