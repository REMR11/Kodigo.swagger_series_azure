/**
 * Entidad Serie - Representa una serie de televisión
 * Aplica principios SOLID:
 * - SRP: Solo responsable de representar los datos de una serie
 * - OCP: Extensible sin modificar el código existente
 */
package org.kodigo.swagger_api_azure.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "serie")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Serie {

    public Serie(String titulo, String genero, LocalDate fechaEstreno) {
        this.titulo = titulo;
        this.genero = genero;
        this.fechaEstreno = fechaEstreno;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String titulo;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String genero;

    @NotNull
    @Column(name = "fecha_estreno", nullable = false)
    private LocalDate fechaEstreno;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Personaje> personajes = new ArrayList<>();

}
