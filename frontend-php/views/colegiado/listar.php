<?php $pageTitle = 'Listado de Colegiados'; ?>
<?php require_once VIEWS_PATH . '/layout/header.php'; ?>

<div class="row">
    <div class="col-md-12">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5><i class="fas fa-users"></i> Listado de Colegiados</h5>
            </div>
            <div class="card-body">
                <div class="mb-3">
                    <a href="convertir-colegiado.php" class="btn btn-success">
                        <i class="fas fa-plus"></i> Convertir Nuevo Colegiado
                    </a>
                </div>

                <div class="table-responsive">
                    <table class="table table-striped table-hover">
                        <thead class="table-dark">
                            <tr>
                                <th>Nº Colegiatura</th>
                                <th>Nombre Completo</th>
                                <th>DNI</th>
                                <th>Especialidad</th>
                                <th>Estado</th>
                                <th>Meses Impagos</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php if (empty($colegiados)): ?>
                                <tr>
                                    <td colspan="7" class="text-center">No hay colegiados registrados</td>
                                </tr>
                            <?php else: ?>
                                <?php foreach ($colegiados as $colegiado): ?>
                                    <tr>
                                        <td><?= htmlspecialchars($colegiado['numeroColegiatura']) ?></td>
                                        <td>
                                            <?php
                                            $persona = $colegiado['persona'];
                                            echo htmlspecialchars($persona['nombres'] . ' ' . $persona['apellidoPaterno']);
                                            ?>
                                        </td>
                                        <td><?= htmlspecialchars($persona['dni']) ?></td>
                                        <td><?= htmlspecialchars($colegiado['especialidad'] ?? 'N/A') ?></td>
                                        <td>
                                            <?php
                                            $estado = $colegiado['estadoHabilitacion'];
                                            $badge = $estado === 'HABILITADO' ? 'success' :
                                                    ($estado === 'INHABILITADO' ? 'danger' : 'warning');
                                            ?>
                                            <span class="badge bg-<?= $badge ?>"><?= $estado ?></span>
                                        </td>
                                        <td>
                                            <span class="badge bg-<?= $colegiado['mesesImpagosConsecutivos'] > 0 ? 'danger' : 'success' ?>">
                                                <?= $colegiado['mesesImpagosConsecutivos'] ?> meses
                                            </span>
                                        </td>
                                        <td>
                                            <a href="caja.php?personaId=<?= $persona['id'] ?>" class="btn btn-sm btn-success">
                                                <i class="fas fa-cash-register"></i> Caja
                                            </a>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            <?php endif; ?>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<?php require_once VIEWS_PATH . '/layout/footer.php'; ?>
