/**
 * DTO para Personaje - Aplica principio de Responsabilidad Única (SRP)
 * Solo responsable de transportar datos de Personaje entre capas
 */
package org.kodigo.swagger_api_azure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PersonajeDTO {
    public PersonajeDTO(String nombre, String descripcion, Long serieId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.serieId = serieId;
    }

    public PersonajeDTO(Long id, String nombre, String descripcion, Long serieId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.serieId = serieId;
    }

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 1, max = 255, message = "El nombre debe tener entre 1 y 255 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El ID de la serie es obligatorio")
    private Long serieId;

    private String serieTitulo;
}
