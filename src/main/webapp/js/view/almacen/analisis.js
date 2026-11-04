
function initAnalisisInventario() {
    var complete = function() {
        buscaInventario();

        notify_info("Listo.");
    };
    creaGridProductos(complete);
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
    if (response.fase!=="1ER"&&response.fase!=="2DO"&&response.fase!=="3ER") {
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
        muestraProductos(array);
    };
    var onComplete = function(response) {
        muestraProductos(response);
        notify_info("Listo.");
    };

    var where = "flinventario = "+response.flinventario
        +" AND existencia <> 0 AND existencia != existenciac ";

    notify_secondary("Buscando los registros de Inventario ...");
    coleccion("mx.com.rolcar.inventarios.collection.AnalisisInventarioCollection", where,
        onComplete, onError, onFail);
}

function muestraProductos(response) {
    sourceGrid.localdata = response;

    var analizando = 0;
    for(var indx=0; indx<response.length; indx++) {
        if (response[indx].status==="AN")
            analizando++;
    }

    $("#analisis-totales input[name=tanalizando]").val(analizando);

    var $grid = $("#grid_productos");
    $grid.jqxGrid("updatebounddata", "cells");

    $grid.jqxGrid("clearselection");
}

var sourceGrid;

function creaGridProductos(onComplete) {
    sourceGrid = {
        localdata: [],
        datafields: [
            {name: "fldinventario", type: "int"},
            {name: "flinventario", type: "int"},
            {name: "clave", type: "string"},
            {name: "descripcion", type: "string"},
            {name: "precio", type: "number"},
            {name: "existencia", type: "number"},
            {name: "existenciac", type: "number"},
            {name: "diferencia", type: "number"},
            {name: "valor", type: "number"},
            {name: "status", type: "string"},
            {name: "terminal", type: "string"}
        ],
        datatype: "array"
    };
    var cellclass = function (row, columnfield, value) {
        var status = sourceGrid.localdata[row].status;
        if (status==="AN")
            return "row-green";
        //else if (status==="TE")
        //    return "row-blue";
        return "";
    };
    var dataAdapter = new $.jqx.dataAdapter(sourceGrid);
    var $grid = $("<div id=\"grid_productos\"></div>");
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
            {text: "Clave", datafield: "clave", width: "10%", cellclassname: cellclass,
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
            {text: "Descripcion", datafield: "descripcion", width: "40%", cellclassname: cellclass},
            {text: "Precio", datafield: "precio",  cellsalign: "right", cellsformat: "f0", width: "10%", cellclassname: cellclass},
            {text: "Existencia", datafield: "existencia", cellsalign: "right", cellsformat: "f2", width: "9%", cellclassname: cellclass,
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
            {text: "Conteo", datafield: "existenciac", cellsalign: "right", cellsformat: "f2", width: "9%", cellclassname: cellclass,
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
            {text: "Diferencia", datafield: "diferencia", cellsalign: "right", cellsformat: "f2", width: "9%", cellclassname: cellclass,
                aggregates: ["sum"],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key === "sum" ? "Total:" : "Promedio:";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; margin: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b><br>'+value+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            },
            {text: "Valor", datafield: "valor", cellsalign: "right", cellsformat: "c2", width: "13%", cellclassname: cellclass,
                aggregates: ["sum"],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key === "sum" ? "Total:" : "Promedio:";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; margin: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b><br>'+value+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            },
            {text: "Estado", datafield: "status", cellsalign: "middle", width: "9%", cellclassname: cellclass,
                aggregates: [
                    {'PE':
                        function (aggregatedValue, currentValue) {
                            if (currentValue==="PE") {
                                return aggregatedValue+1;
                            }
                            return aggregatedValue;
                        }
                    },
                    {'AN':
                        function (aggregatedValue, currentValue) {
                            if (currentValue==="AN") {
                                return aggregatedValue+1;
                            }
                            return aggregatedValue;
                        }
                    }
                ],
                aggregatesrenderer: function (aggregates, column, element, summaryData) {
                    var renderstring = "<div class='jqx-widget-content style='float: left; width: 100%; height: 100%;'>";
                    $.each(aggregates, function (key, value) {
                        var name = key + ":";
                        var margin = key === "PE" ? "margin-top" : "margin-bottom";
                        var color = "green";
                        renderstring += '<div style="color: '+color+'; position: relative; '+margin+': 6px; margin-left: 6px; margin-right: 6px; text-align: right; overflow: hidden;"><b>'+name+'</b>&nbsp;'+formatNumber(value)+'</div>';
                    });
                    renderstring += "</div>";
                    return renderstring;
                }
            },
            {text: "Terminal", datafield: "terminal", cellsalign: "left", width: "9%", cellclassname: cellclass}
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

function mandaAnalizar() {
    var $grid = $("#grid_productos");
    var rowindexes = $grid.jqxGrid("getselectedrowindexes");
    if (rowindexes.length===0) {
        var msg = "No ha seleccionado ningun rengl&oacute;n.";
        precaucion(msg);
        notify_warning(msg);
        return;
    }

    /*
     *
    for(var indx=0; indx<rowindexes.length; indx++) {
        var selectedrowindex = rowindexes[indx];
        var rowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
        if (rowdata) {
            if (rowdata.existenciac===0) {
                var msg = "El producto ["+rowdata.clave+"] no se puede analizar, no tiene conteo de inventario.";
                precaucion(msg);
                notify_warning(msg);
                return;
            }
        }
    }
     *
     */

    var onCancel = function() {
    };
    var onSelected = function(response) {
        enviaAnalizar(response);
    };
    seleccionaTerminal(onSelected, onCancel);
}

function enviaAnalizar(response) {
    var datos = getValoresForma("#form-inventario");

    var data = {
        id: "ValidaTerminalInventario",
        compania: usuario.compania,
        folio: datos.flinventario,
        terminal: response.terminal
    };

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
    };
    var onComplete = function(response) {
        enviaAnalizarConfirmado(data);
        notify_info("Listo.");
    };

    mvc(data, onComplete, onFail, onError);
}

function enviaAnalizarConfirmado(data) {
    var $grid = $("#grid_productos");
    var rowindexes = $grid.jqxGrid("getselectedrowindexes");
    if (rowindexes.length===0) {
        var msg = "No ha seleccionado ningun rengl&oacute;n.";
        precaucion(msg);
        notify_warning(msg);
        return;
    }

    var indices = "";
    var detalles = "";
    var claves = "";
    for(var indx=0; indx<rowindexes.length; indx++) {
        var selectedrowindex = rowindexes[indx];
        var rowdata = $grid.jqxGrid("getrowdata", selectedrowindex);
        if (rowdata) {
            indices = indices+selectedrowindex+";";
            detalles = detalles+rowdata.fldinventario+";";
            claves = claves+rowdata.clave+";";
        }
    }

    data["id"] = "AnalizaProductosInventario";
    data["indices"] = indices;
    data["detalles"] = detalles;
    data["claves"] = claves;

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
    };
    var onComplete = function(response) {
        actualizaDatos();
        notify_info("Listo.");
    };

    mvc(data, onComplete, onFail, onError);
}



