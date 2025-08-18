/**
 * Entidad Personaje - Representa un personaje de una serie
 * Aplica principios SOLID:
 * - SRP: Solo responsable de representar los datos de un personaje
 * - DIP: Depende de abstracciones (JPA annotations) no de concreciones
 */
package org.kodigo.swagger_api_azure.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "personaje")
@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = "serie")
@EqualsAndHashCode(exclude = "serie")
public class Personaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Serie serie;


    // Constructores
    public Personaje() {}

    public Personaje(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Personaje(String nombre, String descripcion, Serie serie) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.serie = serie;
    }
}
