package org.kodigo.swagger_api_azure.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Personaje")
@Getter
@Setter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
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
    @JoinColumn(name = "serie_id", nullable = false)
    @JsonBackReference
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
