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
     * Verifica si existe un personaje con el nombre dado en una serie
     * @param nombre Nombre del personaje
     * @param serieId ID de la serie
     * @return true si existe, false si no
     */
    boolean existsByNombreIgnoreCaseAndSerieId(String nombre, Long serieId);
}
