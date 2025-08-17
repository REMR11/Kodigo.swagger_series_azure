/**
 * Repositorio para la entidad Serie
 * Aplica principios SOLID:
 * - SRP: Solo responsable del acceso a datos de Serie
 * - DIP: Depende de la abstracción JpaRepository
 * - ISP: Interface específica para operaciones de Serie
 */
package org.kodigo.swagger_api_azure.repository;

import org.kodigo.swagger_api_azure.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    /**
     * Busca series por género
     * @param genero Género de la serie
     * @return Lista de series del género especificado
     */
    List<Serie> findByGeneroIgnoreCase(String genero);

    /**
     * Busca series por título (parcial, ignorando mayúsculas/minúsculas)
     * @param titulo Título o parte del título
     * @return Lista de series que coinciden
     */
    List<Serie> findByTituloContainingIgnoreCase(String titulo);

    /**
     * Busca series por rango de fechas de estreno
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de series estrenadas en el rango
     */
    List<Serie> findByFechaEstrenoBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Busca serie por título exacto
     * @param titulo Título exacto de la serie
     * @return Optional con la serie si existe
     */
    Optional<Serie> findByTituloIgnoreCase(String titulo);

    /**
     * Verifica si existe una serie con el título dado
     * @param titulo Título de la serie
     * @return true si existe, false si no
     */
    boolean existsByTituloIgnoreCase(String titulo);

    /**
     * Query personalizada para obtener series con sus personajes
     * @return Lista de series con personajes cargados
     */
    @Query("SELECT s FROM Serie s LEFT JOIN FETCH s.personajes")
    List<Serie> findAllWithPersonajes();

    /**
     * Query para obtener series por año de estreno
     * @param year Año de estreno
     * @return Lista de series del año especificado
     */
    @Query("SELECT s FROM Serie s WHERE YEAR(s.fechaEstreno) = :year")
    List<Serie> findByYear(@Param("year") int year);
}