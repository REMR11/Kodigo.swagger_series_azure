/**
 * Repositorio para la entidad Personaje
 * Aplica principios SOLID:
 * - SRP: Solo responsable del acceso a datos de Personaje
 * - DIP: Depende de la abstracción JpaRepository
 * - OCP: Extensible mediante queries personalizadas
 */
package org.kodigo.swagger_api_azure.repository;

import org.kodigo.swagger_api_azure.entity.Personaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonajeRepository extends JpaRepository<Personaje, Long> {

    /**
     * Busca personajes por serie ID
     * @param serieId ID de la serie
     * @return Lista de personajes de la serie
     */
    List<Personaje> findBySerieId(Long serieId);

    /**
     * Busca personajes por nombre (parcial, ignorando mayúsculas/minúsculas)
     * @param nombre Nombre o parte del nombre
     * @return Lista de personajes que coinciden
     */
    List<Personaje> findByNombreContainingIgnoreCase(String nombre);

    /**
     * Busca personaje por nombre exacto en una serie específica
     * @param nombre Nombre del personaje
     * @param serieId ID de la serie
     * @return Optional con el personaje si existe
     */
    Optional<Personaje> findByNombreIgnoreCaseAndSerieId(String nombre, Long serieId);

    /**
     * Verifica si existe un personaje con el nombre dado en una serie
     * @param nombre Nombre del personaje
     * @param serieId ID de la serie
     * @return true si existe, false si no
     */
    boolean existsByNombreIgnoreCaseAndSerieId(String nombre, Long serieId);

    /**
     * Cuenta personajes por serie
     * @param serieId ID de la serie
     * @return Número de personajes en la serie
     */
    long countBySerieId(Long serieId);

    /**
     * Query para buscar personajes con información de la serie
     * @param serieId ID de la serie
     * @return Lista de personajes con serie cargada
     */
    @Query("SELECT p FROM Personaje p JOIN FETCH p.serie WHERE p.serie.id = :serieId")
    List<Personaje> findBySerieIdWithSerie(@Param("serieId") Long serieId);

    /**
     * Query para buscar personajes que contengan cierto texto en la descripción
     * @param descripcion Texto a buscar en la descripción
     * @return Lista de personajes que coinciden
     */
    @Query("SELECT p FROM Personaje p WHERE p.descripcion IS NOT NULL AND LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))")
    List<Personaje> findByDescripcionContaining(@Param("descripcion") String descripcion);

    /**
     * Elimina todos los personajes de una serie específica
     * @param serieId ID de la serie
     */
    void deleteBySerieId(Long serieId);
}
