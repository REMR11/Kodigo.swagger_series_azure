package org.kodigo.swagger_api_azure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kodigo.swagger_api_azure.dto.SerieDTO;
import org.kodigo.swagger_api_azure.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/v1/series")
@Tag(name = "Series", description = "API para gestión de series")
@Validated
@CrossOrigin(origins = "*")
@Slf4j
public class SerieController {

    private final SerieService serieService;

    @Autowired
    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las series",
            description = "Retorna una lista de todas las series disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de series obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SerieDTO>> getAllSeries() {
        return executeWithExceptionHandling(
                () -> {
                    List<SerieDTO> series = serieService.findAll();
                    return ResponseEntity.ok(series);
                },
                "Error al obtener todas las series"
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener serie por ID",
            description = "Retorna una serie específica basada en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serie encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "404", description = "Serie no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SerieDTO> getSerieById(
            @Parameter(description = "ID de la serie", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {

        return executeWithExceptionHandling(
                () -> {
                    Optional<SerieDTO> serie = serieService.findById(id);
                    return serie.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                },
                String.format("Error al obtener serie por ID: %d", id)
        );
    }

    @GetMapping("/search/titulo")
    @Operation(summary = "Buscar series por título",
            description = "Busca series que contengan el título especificado (búsqueda parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda por título completada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SerieDTO>> searchSeriesByTitulo(
            @Parameter(description = "Título o parte del título de la serie", required = true)
            @RequestParam @NotBlank(message = "El título no puede estar vacío") String titulo) {

        return executeWithExceptionHandling(
                () -> {
                    List<SerieDTO> series = serieService.findByTitulo(titulo);
                    return ResponseEntity.ok(series);
                },
                String.format("Error al buscar series por título: %s", titulo)
        );
    }

    @GetMapping("/search/genero")
    @Operation(summary = "Buscar series por género",
            description = "Busca series del género especificado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda por género completada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<SerieDTO>> searchSeriesByGenero(
            @Parameter(description = "Género de la serie", required = true)
            @RequestParam @NotBlank(message = "El género no puede estar vacío") String genero) {

        return executeWithExceptionHandling(
                () -> {
                    List<SerieDTO> series = serieService.findByGenero(genero);
                    return ResponseEntity.ok(series);
                },
                String.format("Error al buscar series por género: %s", genero)
        );
    }

    @PostMapping
    @Operation(summary = "Crear una nueva serie",
            description = "Crea una nueva serie con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serie creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe una serie con ese título"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SerieDTO> createSerie(
            @Parameter(description = "Datos de la serie a crear", required = true)
            @Valid @RequestBody SerieDTO serieDto) {

        return executeCreateOperation(serieDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una serie",
            description = "Actualiza completamente los datos de una serie existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serie actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SerieDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Serie no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe una serie con ese título"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SerieDTO> updateSerie(
            @Parameter(description = "ID de la serie a actualizar", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id,
            @Parameter(description = "Nuevos datos de la serie", required = true)
            @Valid @RequestBody SerieDTO serieDto) {

        return executeUpdateOperation(id, serieDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una serie",
            description = "Elimina una serie específica basada en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serie eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Serie no encontrada"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "409", description = "Conflicto - No se puede eliminar la serie porque tiene personajes asociados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteSerie(
            @Parameter(description = "ID de la serie a eliminar", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {

        return executeDeleteOperation(id);
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de series",
            description = "Retorna estadísticas básicas sobre las series")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SerieStatsResponse> getSerieStats() {
        return executeWithExceptionHandling(
                () -> {
                    List<SerieDTO> allSeries = serieService.findAll();
                    SerieStatsResponse stats = buildSerieStats(allSeries);
                    return ResponseEntity.ok(stats);
                },
                "Error al obtener estadísticas de series"
        );
    }

    // Métodos privados para operaciones específicas

    private ResponseEntity<SerieDTO> executeCreateOperation(SerieDTO serieDto) {
        try {
            log.info("Intentando crear serie: {}", serieDto.getTitulo());
            log.info("ID del DTO recibido: {}", serieDto.getId());

            preprocessSerieForCreation(serieDto);
            SerieDTO createdSerie = serieService.create(serieDto);

            log.info("Serie creada exitosamente con ID: {}", createdSerie.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSerie);

        } catch (IllegalArgumentException e) {
            return handleConflictException(e, "Conflicto al crear serie");
        } catch (InvalidDataAccessApiUsageException e) {
            return handleDataAccessException(e);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingException(e);
        } catch (DataIntegrityViolationException e) {
            return handleDataIntegrityException(e);
        } catch (RuntimeException e) {
            return handleRuntimeException(e, "Error de runtime al crear serie");
        } catch (Exception e) {
            log.error("Error inesperado al crear serie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<SerieDTO> executeUpdateOperation(Long id, SerieDTO serieDto) {
        try {
            log.info("Intentando actualizar serie con ID: {}", id);
            SerieDTO updatedSerie = serieService.update(id, serieDto);
            return ResponseEntity.ok(updatedSerie);

        } catch (IllegalArgumentException e) {
            return handleConflictException(e, "Conflicto al actualizar serie");
        } catch (RuntimeException e) {
            return handleRuntimeException(e, String.format("Error al actualizar serie con ID: %d", id));
        } catch (Exception e) {
            log.error("Error inesperado al actualizar serie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Void> executeDeleteOperation(Long id) {
        try {
            log.info("Intentando eliminar serie con ID: {}", id);
            serieService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            log.error("Error al eliminar serie con ID: {}", id, e);
            String message = e.getMessage();

            if (isNotFoundException(message)) {
                return ResponseEntity.notFound().build();
            }
            if (isConstraintViolation(message)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("Error inesperado al eliminar serie", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Métodos utilitarios

    private void preprocessSerieForCreation(SerieDTO serieDto) {
        if (serieDto.getId() != null && serieDto.getId() <= 0) {
            serieDto.setId(null);
            log.info("ID reseteado a null para nueva serie");
        }
    }

    private SerieStatsResponse buildSerieStats(List<SerieDTO> allSeries) {
        long totalSeries = allSeries.size();
        long uniqueGenres = allSeries.stream()
                .map(SerieDTO::getGenero)
                .distinct()
                .count();

        return new SerieStatsResponse(totalSeries, uniqueGenres);
    }

    // Métodos para manejo de excepciones específicas

    private ResponseEntity<SerieDTO> handleConflictException(IllegalArgumentException e, String logMessage) {
        log.warn("{} - Ya existe: {}", logMessage, e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    private ResponseEntity<SerieDTO> handleDataAccessException(InvalidDataAccessApiUsageException e) {
        log.error("Error de acceso a datos inválido al crear serie: {}", e.getMessage());
        if (e.getMessage() != null && e.getMessage().contains("detached entity passed to persist")) {
            log.error("Problema: Intentando persistir entidades relacionadas con ID. Verifica que los personajes no tengan ID para crear una serie nueva.");
        }
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<SerieDTO> handleOptimisticLockingException(ObjectOptimisticLockingFailureException e) {
        log.error("Error de concurrencia optimista al crear serie: {}", e.getMessage());
        return ResponseEntity.badRequest().build();
    }

    private ResponseEntity<SerieDTO> handleDataIntegrityException(DataIntegrityViolationException e) {
        log.error("Violación de integridad de datos: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    private ResponseEntity<SerieDTO> handleRuntimeException(RuntimeException e, String logMessage) {
        log.error(logMessage, e);
        String message = e.getMessage();
        if (isNotFoundException(message)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }

    // Métodos de utilidad para validación de mensajes

    private boolean isNotFoundException(String message) {
        return message != null && message.toLowerCase().contains("no encontrada");
    }

    private boolean isConstraintViolation(String message) {
        return message != null && (message.contains("constraint") || message.contains("referential"));
    }

    // Método genérico para manejo de excepciones en operaciones simples

    private <T> ResponseEntity<T> executeWithExceptionHandling(
            Supplier<ResponseEntity<T>> operation,
            String errorMessage) {
        try {
            return operation.get();
        } catch (Exception e) {
            log.error(errorMessage, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Getter
    public static class SerieStatsResponse {
        private final long totalSeries;
        private final long uniqueGenres;

        public SerieStatsResponse(long totalSeries, long uniqueGenres) {
            this.totalSeries = totalSeries;
            this.uniqueGenres = uniqueGenres;
        }
    }
}