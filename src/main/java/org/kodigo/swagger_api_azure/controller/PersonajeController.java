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
import org.kodigo.swagger_api_azure.dto.CreatePersonajeDTO;
import org.kodigo.swagger_api_azure.dto.PersonajeDTO;
import org.kodigo.swagger_api_azure.service.PersonajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/personajes")
@Tag(name = "Personajes", description = "API para gestión de personajes")
@Validated
//@CrossOrigin(origins = "*")
public class PersonajeController {

    private final PersonajeService personajeService;

    @Autowired
    public PersonajeController(PersonajeService personajeService) {
        this.personajeService = personajeService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los personajes",
            description = "Retorna una lista de todos los personajes disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de personajes obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PersonajeDTO>> getAllPersonajes() {
        List<PersonajeDTO> personajes = personajeService.findAll();
        return ResponseEntity.ok(personajes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener personaje por ID",
            description = "Retorna un personaje específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personaje encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Personaje no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PersonajeDTO> getPersonajeById(
            @Parameter(description = "ID del personaje", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {

        Optional<PersonajeDTO> personaje = personajeService.findById(id);
        return personaje.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar personajes por nombre",
            description = "Busca personajes que contengan el nombre especificado (búsqueda parcial)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PersonajeDTO>> searchPersonajesByNombre(
            @Parameter(description = "Nombre o parte del nombre del personaje", required = true)
            @RequestParam @NotBlank(message = "El nombre no puede estar vacío") String nombre) {

        List<PersonajeDTO> personajes = personajeService.findByNombre(nombre);
        return ResponseEntity.ok(personajes);
    }

    @GetMapping("/serie/{serieId}")
    @Operation(summary = "Obtener personajes por serie",
            description = "Retorna todos los personajes de una serie específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personajes de la serie obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "400", description = "ID de serie inválido"),
            @ApiResponse(responseCode = "404", description = "Serie no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<PersonajeDTO>> getPersonajesBySerieId(
            @Parameter(description = "ID de la serie", required = true)
            @PathVariable @Min(value = 1, message = "El ID de la serie debe ser mayor a 0") Long serieId) {

        List<PersonajeDTO> personajes = personajeService.findBySerieId(serieId);
        return ResponseEntity.ok(personajes);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo personaje",
            description = "Crea un nuevo personaje con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personaje creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un personaje con ese nombre en la serie"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PersonajeDTO> createPersonaje(
            @Parameter(description = "Datos del personaje a crear", required = true)
            @Valid @RequestBody PersonajeDTO personajeDto) {

        try {
            PersonajeDTO createdPersonaje = personajeService.create(personajeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/serie/{serieId}")
    @Operation(summary = "Crear personaje en una serie específica",
            description = "Crea un nuevo personaje directamente en una serie específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Personaje creado exitosamente en la serie",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Serie no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un personaje con ese nombre en la serie"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PersonajeDTO> createPersonajeInSerie(
            @Parameter(description = "ID de la serie", required = true)
            @PathVariable @Min(value = 1, message = "El ID de la serie debe ser mayor a 0") Long serieId,
            @Parameter(description = "Datos del personaje a crear", required = true)
            @Valid @RequestBody CreatePersonajeDTO createPersonajeDto) {

        try {
            PersonajeDTO createdPersonaje = personajeService.createInSerie(serieId, createPersonajeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPersonaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un personaje",
            description = "Actualiza completamente los datos de un personaje existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Personaje actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PersonajeDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Personaje no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto - Ya existe un personaje con ese nombre en la serie"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PersonajeDTO> updatePersonaje(
            @Parameter(description = "ID del personaje a actualizar", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id,
            @Parameter(description = "Nuevos datos del personaje", required = true)
            @Valid @RequestBody PersonajeDTO personajeDto) {

        try {
            PersonajeDTO updatedPersonaje = personajeService.update(id, personajeDto);
            return ResponseEntity.ok(updatedPersonaje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            String message = e.getMessage();
            if (message != null && message.contains("no encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un personaje",
            description = "Elimina un personaje específico basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Personaje eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Personaje no encontrado"),
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deletePersonaje(
            @Parameter(description = "ID del personaje a eliminar", required = true)
            @PathVariable @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {

        try {
            personajeService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}