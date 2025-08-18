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
import org.kodigo.swagger_api_azure.dto.SerieDTO;
import org.kodigo.swagger_api_azure.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/series")
@Tag(name = "Series", description = "API para gestión de series")
@Validated
@CrossOrigin(origins = "*")
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
        List<SerieDTO> series = serieService.findAll();
        return ResponseEntity.ok(series);
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

        Optional<SerieDTO> serie = serieService.findById(id);
        return serie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

        List<SerieDTO> series = serieService.findByTitulo(titulo);
        return ResponseEntity.ok(series);
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

        List<SerieDTO> series = serieService.findByGenero(genero);
        return ResponseEntity.ok(series);
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

        try {
            SerieDTO createdSerie = serieService.create(serieDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSerie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

        try {
            SerieDTO updatedSerie = serieService.update(id, serieDto);
            return ResponseEntity.ok(updatedSerie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
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

        try {
            serieService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            }
            // Si hay una restricción de integridad referencial (personajes asociados)
            if (message != null && (message.contains("constraint") || message.contains("referential"))) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint adicional para obtener estadísticas básicas
    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de series",
            description = "Retorna estadísticas básicas sobre las series")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<SerieStatsResponse> getSerieStats() {
        List<SerieDTO> allSeries = serieService.findAll();

        long totalSeries = allSeries.size();
        long uniqueGenres = allSeries.stream()
                .map(SerieDTO::getGenero)
                .distinct()
                .count();

        SerieStatsResponse stats = new SerieStatsResponse(totalSeries, uniqueGenres);
        return ResponseEntity.ok(stats);
    }

    // Clase interna para la respuesta de estadísticas
    public static class SerieStatsResponse {
        private final long totalSeries;
        private final long uniqueGenres;

        public SerieStatsResponse(long totalSeries, long uniqueGenres) {
            this.totalSeries = totalSeries;
            this.uniqueGenres = uniqueGenres;
        }

        public long getTotalSeries() {
            return totalSeries;
        }

        public long getUniqueGenres() {
            return uniqueGenres;
        }
    }
}