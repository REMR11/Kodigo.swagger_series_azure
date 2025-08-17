package org.kodigo.swagger_api_azure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SerieDTO {

    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 255, message = "El título debe tener entre 1 y 255 caracteres")
    private String titulo;

    @NotBlank(message = "El género es obligatorio")
    @Size(min = 1, max = 100, message = "El género debe tener entre 1 y 100 caracteres")
    private String genero;

    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate fechaEstreno;

    private List<PersonajeDTO> personajes;

    public SerieDTO(String titulo, String genero, LocalDate fechaEstreno) {
        this.titulo = titulo;
        this.genero = genero;
        this.fechaEstreno = fechaEstreno;
    }

    public SerieDTO(Long id, String titulo, String genero, LocalDate fechaEstreno) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.fechaEstreno = fechaEstreno;
    }
}
