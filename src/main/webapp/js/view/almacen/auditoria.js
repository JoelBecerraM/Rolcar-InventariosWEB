
function initAuditoriaInventarios() {
    var complete = function() {
        var $btn = $("#btn-buscar-flinventario");
        $("#btn-buscar-flinventario").click(function() {
            buscarFolioInventario();
        });

        notify_info("Listo.");

        buscaInventario();
    };
    creaGridInventarios(complete);
}

function reporteAuditoriaInventarios(conteo) {
    var $flinventario = $("#form-inventario [name=flinventario]");
    var flinventario = $flinventario.val();

    download("/inventarios/ExportadorExcel?export=AuditoriaInventarios&flinventario="+flinventario);
}

function buscarFolioInventario() {
    var complete = function(response) {
        pasaValoresForma("#form-inventario", response);
        obtenDatos(response);
        notify_info("Listo.");
    };
    busquedaInventarios(usuario.compania, complete);
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
        pasaValoresForma("#form-inventario", response);
        obtenDatos(response);
    };
    notify_info("Buscando el registro de Inventario ...");
    accion("mx.com.rolcar.inventarios.dao.entity.InventarioDAO", "busca", JSON.stringify(valores),
        onComplete, onError, onFail);
}

function actualizaDatos() {
    var datos = getValoresForma("#form-inventario");
    obtenDatos(datos);
}

function obtenDatos(response) {
    if (response.fase!=="1ER"&&response.fase!=="2DO"&&response.fase!=="3ER"
            &&response.fase!=="AF") {
        var msg = "La fase del Inventario es incorrecta ["+response.fase+"].";
        precaucion(msg);
        notify_warning(msg);
        return;
    }

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
        muestraInventarios(array);
    };
    var onComplete = function(response) {
        muestraInventarios(response);
        notify_info("Listo.");
    };

    var where = "ic.flinventario = "+response.flinventario;

    notify_secondary("Buscando los registros de Inventario ...");
    coleccion("mx.com.rolcar.inventarios.collection.AuditoriaInventariosCollection", where,
        onComplete, onError, onFail);
}

function muestraInventarios(response) {
    sourceGrid.localdata = response;

    var analizando = 0;
    for(var indx=0; indx<response.length; indx++) {
        if (response[indx].status==="AN")
            analizando++;
    }

    $("#analisis-totales input[name=tanalizando]").val(analizando);

    var $grid = $("#grid_inventarios");
    $grid.jqxGrid("updatebounddata", "cells");

    $grid.jqxGrid("clearselection");
}

var sourceGrid;

function creaGridInventarios(onComplete) {
    sourceGrid = {
        localdata: [],
        datafields: [
            {name: "flinventario", type: "int"},
            {name: "folio", type: "int"},
            {name: "fase", type: "string"},
            {name: "fechatransmision", type: "string"},
            {name: "compania", type: "string"},
            {name: "terminal", type: "string"},
            {name: "usuario", type: "string"},
            {name: "fecreacion", type: "string"},
            {name: "femodificacion", type: "string"},
            {name: "fetermino", type: "string"},
            {name: "ubicacion", type: "string"},
            {name: "lineas", type: "number"},
            {name: "cantidad", type: "number"}
        ],
        datatype: "array"
    };
    var cellclass = function (row, columnfield, value) {
        var status = sourceGrid.localdata[row].status;
        //if (status==="AN")
        //    return "row-green";
        //else if (status==="TE")
        //    return "row-blue";
        return "";
    };
    var dataAdapter = new $.jqx.dataAdapter(sourceGrid);
    var $grid = $("<div id=\"grid_inventarios\"></div>");
    $grid.jqxGrid({
        width: "100%",
        height: "400px",
        altrows: true,
        sortable: true,
        columnsresize: true,
        selectionmode: "multiplerows",
        showstatusbar: true,
        statusbarheight: 50,
        showaggregates: true,
        filterable: true,
        source: dataAdapter,
        columns: [
            {text: "Folio", datafield: "folio", width: "10%", cellclassname: cellclass,
                aggregates: ["count"],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key === "count" ? "Cuenta:" : "Total:";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; margin: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b><br>'+formatNumber(value)+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            },
            {text: "Fase", datafield: "fase", width: "10%", cellclassname: cellclass},
            {text: "Fecha", datafield: "fechatransmision", width: "20%", cellclassname: cellclass},
            {text: "Compania", datafield: "compania", width: "10%", cellclassname: cellclass},
            {text: "FlInventario", datafield: "flinventario", width: "10%", cellclassname: cellclass},
            {text: "Terminal", datafield: "terminal", width: "10%", cellclassname: cellclass},
            {text: "Usuario", datafield: "usuario", width: "10%", cellclassname: cellclass},
            {text: "Creacion", datafield: "fecreacion", width: "15%", cellclassname: cellclass},
            {text: "Modificacion", datafield: "femodificacion", width: "15%", cellclassname: cellclass},
            {text: "Termino", datafield: "fetermino", width: "15%", cellclassname: cellclass},
            {text: "Ubicacion", datafield: "ubicacion", width: "10%", cellclassname: cellclass},
            {text: "Productos", datafield: "lineas", cellsalign: "right", cellsformat: "f0", width: "10%", cellclassname: cellclass,
                aggregates: [
                    {'T':
                        function (aggregatedValue, currentValue) {
                            return aggregatedValue+currentValue;
                        }
                    },
                    {'CE':
                        function (aggregatedValue, currentValue) {
                            if (currentValue!==0) {
                                return aggregatedValue+1;
                            }
                            return aggregatedValue;
                        }
                    }
                ],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key === "T" ? "&Sigma;" : key+":";
                        var margin = key === "T" ? "margin-top" : "margin-bottom";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; '+margin+': 6px; margin-left: 6px; margin-right: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b>&nbsp;'+value+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            },
            {text: "Cantidad", datafield: "cantidad", cellsalign: "right", cellsformat: "f2", width: "10%", cellclassname: cellclass,
                aggregates: [
                    {'T':
                        function (aggregatedValue, currentValue) {
                            return aggregatedValue+currentValue;
                        }
                    },
                    {'CE':
                        function (aggregatedValue, currentValue) {
                            if (currentValue!==0) {
                                return aggregatedValue+1;
                            }
                            return aggregatedValue;
                        }
                    }
                ],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key === "T" ? "&Sigma;" : key+":";
                        var margin = key === "T" ? "margin-top" : "margin-bottom";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; '+margin+': 6px; margin-left: 6px; margin-right: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b>&nbsp;'+value+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            }
        ],
        ready: function() {
            $grid.jqxGrid("clearselection");
            $grid.jqxGrid("focus");

            if (onComplete)
                onComplete();
        }
    });

    $("#productos").append($grid);
}
