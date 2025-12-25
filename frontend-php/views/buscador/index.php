<?php $pageTitle = 'Buscar Persona'; ?>
<?php require_once VIEWS_PATH . '/layout/header.php'; ?>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5><i class="fas fa-search"></i> Buscador de Personas por DNI</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <div class="input-group mb-3">
                            <input type="text" class="form-control" id="dni"
                                   placeholder="Ingrese DNI" maxlength="8">
                            <button class="btn btn-primary" type="button" id="btnBuscar">
                                <i class="fas fa-search"></i> Buscar
                            </button>
                        </div>
                    </div>
                </div>

                <div id="resultado" class="mt-4" style="display:none;">
                    <h5>Información de la Persona</h5>
                    <div class="table-responsive">
                        <table class="table table-bordered">
                            <tr>
                                <th width="200">DNI:</th>
                                <td id="info-dni"></td>
                            </tr>
                            <tr>
                                <th>Nombre Completo:</th>
                                <td id="info-nombre"></td>
                            </tr>
                            <tr>
                                <th>Email:</th>
                                <td id="info-email"></td>
                            </tr>
                            <tr>
                                <th>Teléfono:</th>
                                <td id="info-telefono"></td>
                            </tr>
                            <tr>
                                <th>Tipo:</th>
                                <td id="info-tipo"></td>
                            </tr>
                        </table>
                    </div>

                    <div id="info-colegiado" style="display:none;">
                        <h5 class="mt-4">Información de Colegiado</h5>
                        <div class="table-responsive">
                            <table class="table table-bordered">
                                <tr>
                                    <th width="200">Número de Colegiatura:</th>
                                    <td id="col-numero"></td>
                                </tr>
                                <tr>
                                    <th>Especialidad:</th>
                                    <td id="col-especialidad"></td>
                                </tr>
                                <tr>
                                    <th>Universidad:</th>
                                    <td id="col-universidad"></td>
                                </tr>
                                <tr>
                                    <th>Estado de Habilitación:</th>
                                    <td id="col-estado"></td>
                                </tr>
                                <tr>
                                    <th>Meses Impagos:</th>
                                    <td id="col-impagos"></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <div class="mt-3">
                        <a href="#" class="btn btn-success" id="btnVerCaja">
                            <i class="fas fa-cash-register"></i> Ver en Caja
                        </a>
                    </div>
                </div>

                <div id="noEncontrado" class="alert alert-warning mt-4" style="display:none;">
                    <i class="fas fa-exclamation-triangle"></i> No se encontró ninguna persona con ese DNI.
                </div>
            </div>
        </div>
    </div>
</div>

<script>
$(document).ready(function() {
    $('#btnBuscar').click(buscarPersona);

    $('#dni').keypress(function(e) {
        if (e.which === 13) {
            buscarPersona();
        }
    });

    function buscarPersona() {
        const dni = $('#dni').val().trim();

        if (!dni) {
            alert('Por favor ingrese un DNI');
            return;
        }

        $.ajax({
            url: '/api/buscar-persona.php?dni=' + dni,
            method: 'GET',
            success: function(response) {
                if (response.success) {
                    mostrarPersona(response.data);
                } else {
                    $('#resultado').hide();
                    $('#noEncontrado').show();
                }
            },
            error: function() {
                alert('Error al buscar persona');
            }
        });
    }

    function mostrarPersona(persona) {
        $('#info-dni').text(persona.dni);
        $('#info-nombre').text(persona.nombres + ' ' + persona.apellidoPaterno + ' ' + (persona.apellidoMaterno || ''));
        $('#info-email').text(persona.email || 'No registrado');
        $('#info-telefono').text(persona.telefono || 'No registrado');
        $('#info-tipo').html('<span class="badge bg-' + (persona.esColegiado ? 'success' : 'secondary') + '">' +
                             (persona.esColegiado ? 'COLEGIADO' : 'PÚBLICO GENERAL') + '</span>');

        if (persona.esColegiado && persona.colegiado) {
            const col = persona.colegiado;
            $('#col-numero').text(col.numeroColegiatura);
            $('#col-especialidad').text(col.especialidad || 'No registrada');
            $('#col-universidad').text(col.universidad || 'No registrada');

            let estadoBadge = 'secondary';
            if (col.estadoHabilitacion === 'HABILITADO') estadoBadge = 'success';
            if (col.estadoHabilitacion === 'INHABILITADO') estadoBadge = 'danger';
            if (col.estadoHabilitacion === 'SUSPENDIDO') estadoBadge = 'warning';

            $('#col-estado').html('<span class="badge bg-' + estadoBadge + '">' + col.estadoHabilitacion + '</span>');
            $('#col-impagos').html('<span class="badge bg-' + (col.mesesImpagosConsecutivos > 0 ? 'danger' : 'success') + '">' +
                                   col.mesesImpagosConsecutivos + ' meses</span>');

            $('#info-colegiado').show();
        } else {
            $('#info-colegiado').hide();
        }

        $('#btnVerCaja').attr('href', '/caja.php?personaId=' + persona.id);

        $('#noEncontrado').hide();
        $('#resultado').show();
    }
});
</script>

<?php require_once VIEWS_PATH . '/layout/footer.php'; ?>
