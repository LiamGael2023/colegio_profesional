<?php $pageTitle = 'Módulo de Caja'; ?>
<?php require_once VIEWS_PATH . '/layout/header.php'; ?>

<div class="row">
    <div class="col-md-12">
        <div class="card mb-4">
            <div class="card-header bg-success text-white">
                <h5><i class="fas fa-cash-register"></i> Módulo de Caja - Procesamiento de Pagos</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <label>DNI de la Persona:</label>
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="dni" placeholder="Ingrese DNI">
                            <button class="btn btn-primary" id="btnBuscarPersona">
                                <i class="fas fa-search"></i> Buscar
                            </button>
                        </div>
                    </div>
                </div>

                <div id="personaInfo" style="display:none;">
                    <div class="alert alert-info">
                        <strong>Persona:</strong> <span id="nombrePersona"></span><br>
                        <strong>DNI:</strong> <span id="dniPersona"></span><br>
                        <strong>Tipo:</strong> <span id="tipoPersona"></span>
                    </div>

                    <h5>Aportaciones Pendientes</h5>
                    <div class="table-responsive">
                        <table class="table table-bordered" id="tablaAportaciones">
                            <thead class="table-light">
                                <tr>
                                    <th width="50">
                                        <input type="checkbox" id="selectAll">
                                    </th>
                                    <th>Mes/Año</th>
                                    <th>Concepto</th>
                                    <th>Monto</th>
                                    <th>Vencimiento</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody></tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="3" class="text-end"><strong>Total Seleccionado:</strong></td>
                                    <td colspan="3"><strong id="totalSeleccionado">S/ 0.00</strong></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>

                    <div class="row mt-4">
                        <div class="col-md-6">
                            <label>Método de Pago:</label>
                            <select class="form-select" id="metodoPago">
                                <option value="EFECTIVO">Efectivo</option>
                                <option value="TARJETA">Tarjeta</option>
                                <option value="TRANSFERENCIA">Transferencia</option>
                                <option value="YAPE">Yape</option>
                                <option value="PLIN">Plin</option>
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label>Número de Comprobante (opcional):</label>
                            <input type="text" class="form-control" id="numeroComprobante">
                        </div>
                    </div>

                    <div class="mt-3">
                        <button class="btn btn-success btn-lg" id="btnProcesarPago">
                            <i class="fas fa-check-circle"></i> Procesar Pago
                        </button>
                        <button class="btn btn-secondary" id="btnLimpiar">
                            <i class="fas fa-times"></i> Limpiar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
let personaActual = null;
let aportacionesSeleccionadas = [];

$(document).ready(function() {
    $('#btnBuscarPersona').click(buscarPersona);
    $('#selectAll').change(seleccionarTodos);
    $('#btnProcesarPago').click(procesarPago);
    $('#btnLimpiar').click(limpiar);

    $(document).on('change', '.aportacion-checkbox', calcularTotal);
});

function buscarPersona() {
    const dni = $('#dni').val().trim();

    if (!dni) {
        alert('Ingrese un DNI');
        return;
    }

    $.ajax({
        url: '/api/buscar-persona.php?dni=' + dni,
        method: 'GET',
        success: function(response) {
            if (response.success) {
                personaActual = response.data;
                mostrarPersona();
                cargarAportacionesPendientes();
            } else {
                alert('Persona no encontrada');
            }
        },
        error: function() {
            alert('Error al buscar persona');
        }
    });
}

function mostrarPersona() {
    $('#nombrePersona').text(personaActual.nombres + ' ' + personaActual.apellidoPaterno);
    $('#dniPersona').text(personaActual.dni);
    $('#tipoPersona').html('<span class="badge bg-' + (personaActual.esColegiado ? 'success' : 'secondary') + '">' +
                           (personaActual.esColegiado ? 'COLEGIADO' : 'PÚBLICO') + '</span>');
    $('#personaInfo').show();
}

function cargarAportacionesPendientes() {
    $.ajax({
        url: '/api/aportaciones-pendientes.php?personaId=' + personaActual.id,
        method: 'GET',
        success: function(response) {
            if (response.success) {
                mostrarAportaciones(response.data);
            }
        }
    });
}

function mostrarAportaciones(aportaciones) {
    const tbody = $('#tablaAportaciones tbody');
    tbody.empty();

    if (aportaciones.length === 0) {
        tbody.append('<tr><td colspan="6" class="text-center">No hay aportaciones pendientes</td></tr>');
        return;
    }

    aportaciones.forEach(function(aportacion) {
        const row = `
            <tr>
                <td>
                    <input type="checkbox" class="aportacion-checkbox" value="${aportacion.id}"
                           data-monto="${aportacion.monto}">
                </td>
                <td>${aportacion.mes}/${aportacion.anio}</td>
                <td>${aportacion.concepto}</td>
                <td>S/ ${parseFloat(aportacion.monto).toFixed(2)}</td>
                <td>${aportacion.fechaVencimiento || 'N/A'}</td>
                <td><span class="badge bg-warning">PENDIENTE</span></td>
            </tr>
        `;
        tbody.append(row);
    });
}

function seleccionarTodos() {
    $('.aportacion-checkbox').prop('checked', $(this).prop('checked'));
    calcularTotal();
}

function calcularTotal() {
    let total = 0;
    aportacionesSeleccionadas = [];

    $('.aportacion-checkbox:checked').each(function() {
        const monto = parseFloat($(this).data('monto'));
        total += monto;
        aportacionesSeleccionadas.push(parseInt($(this).val()));
    });

    $('#totalSeleccionado').text('S/ ' + total.toFixed(2));
}

function procesarPago() {
    if (aportacionesSeleccionadas.length === 0) {
        alert('Seleccione al menos una aportación');
        return;
    }

    const data = {
        personaId: personaActual.id,
        aportacionesIds: aportacionesSeleccionadas,
        metodoPago: $('#metodoPago').val(),
        numeroComprobante: $('#numeroComprobante').val() || null
    };

    $.ajax({
        url: '/api/procesar-pago.php',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function(response) {
            if (response.success) {
                alert('Pago procesado exitosamente');
                limpiar();
            } else {
                alert('Error: ' + response.error);
            }
        },
        error: function() {
            alert('Error al procesar pago');
        }
    });
}

function limpiar() {
    personaActual = null;
    aportacionesSeleccionadas = [];
    $('#dni').val('');
    $('#personaInfo').hide();
    $('#numeroComprobante').val('');
    $('#selectAll').prop('checked', false);
}
</script>

<?php require_once VIEWS_PATH . '/layout/footer.php'; ?>
