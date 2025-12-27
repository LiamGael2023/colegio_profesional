<?php $pageTitle = 'Convertir a Colegiado'; ?>
<?php require_once VIEWS_PATH . '/layout/header.php'; ?>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header bg-info text-white">
                <h5><i class="fas fa-user-plus"></i> Convertir Persona a Colegiado</h5>
            </div>
            <div class="card-body">
                <!-- Paso 1: Buscar Persona -->
                <div id="paso1">
                    <h5>Paso 1: Buscar Persona</h5>
                    <div class="row">
                        <div class="col-md-6">
                            <div class="input-group mb-3">
                                <input type="text" class="form-control" id="dni" placeholder="Ingrese DNI" maxlength="8">
                                <button class="btn btn-primary" type="button" id="btnBuscar">
                                    <i class="fas fa-search"></i> Buscar
                                </button>
                            </div>
                        </div>
                    </div>

                    <div id="personaInfo" style="display:none;">
                        <div class="alert alert-info">
                            <strong>Persona encontrada:</strong><br>
                            <strong>Nombre:</strong> <span id="nombrePersona"></span><br>
                            <strong>DNI:</strong> <span id="dniPersona"></span>
                        </div>
                        <button class="btn btn-success" id="btnSiguiente">Continuar <i class="fas fa-arrow-right"></i></button>
                    </div>
                </div>

                <!-- Paso 2: Datos del Colegiado -->
                <div id="paso2" style="display:none;">
                    <h5>Paso 2: Datos de Colegiación</h5>

                    <form id="formColegiado">
                        <input type="hidden" id="personaId">

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label">Número de Colegiatura *</label>
                                    <input type="text" class="form-control" id="numeroColegiatura" required>
                                    <small class="text-muted">Ejemplo: COL-2025-001</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label">Especialidad</label>
                                    <input type="text" class="form-control" id="especialidad">
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label">Universidad</label>
                                    <input type="text" class="form-control" id="universidad">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label">Fecha de Colegiatura</label>
                                    <input type="date" class="form-control" id="fechaColegiatura" value="<?= date('Y-m-d') ?>">
                                </div>
                            </div>
                        </div>

                        <div class="mt-3">
                            <button type="button" class="btn btn-secondary" id="btnVolver">
                                <i class="fas fa-arrow-left"></i> Volver
                            </button>
                            <button type="submit" class="btn btn-success">
                                <i class="fas fa-check"></i> Convertir a Colegiado
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
let personaActual = null;

$(document).ready(function() {
    $('#btnBuscar').click(buscarPersona);
    $('#btnSiguiente').click(mostrarPaso2);
    $('#btnVolver').click(mostrarPaso1);
    $('#formColegiado').submit(convertirAColegiado);
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

                if (personaActual.esColegiado) {
                    alert('Esta persona ya es un colegiado');
                    return;
                }

                $('#nombrePersona').text(personaActual.nombres + ' ' + personaActual.apellidoPaterno);
                $('#dniPersona').text(personaActual.dni);
                $('#personaId').val(personaActual.id);
                $('#personaInfo').show();
            } else {
                alert('Persona no encontrada');
            }
        },
        error: function() {
            alert('Error al buscar persona');
        }
    });
}

function mostrarPaso2() {
    $('#paso1').hide();
    $('#paso2').show();
}

function mostrarPaso1() {
    $('#paso2').hide();
    $('#paso1').show();
}

function convertirAColegiado(e) {
    e.preventDefault();

    const data = {
        personaId: parseInt($('#personaId').val()),
        numeroColegiatura: $('#numeroColegiatura').val(),
        especialidad: $('#especialidad').val() || null,
        universidad: $('#universidad').val() || null,
        fechaColegiatura: $('#fechaColegiatura').val()
    };

    $.ajax({
        url: 'http://localhost:8080/api/colegiados/convertir',
        method: 'POST',
        contentType: 'application/json',
        headers: {
            'Authorization': 'Bearer ' + '<?= $_SESSION['auth_token'] ?? '' ?>'
        },
        data: JSON.stringify(data),
        success: function(response) {
            alert('¡Persona convertida a colegiado exitosamente!');
            window.location.href = 'colegiados.php';
        },
        error: function(xhr) {
            const error = xhr.responseJSON?.message || 'Error al convertir a colegiado';
            alert(error);
        }
    });
}
</script>

<?php require_once VIEWS_PATH . '/layout/footer.php'; ?>
