package org.kodigo.swagger_api_azure.service;

import jakarta.transaction.Transactional;
import org.kodigo.swagger_api_azure.dto.CreatePersonajeDTO;
import org.kodigo.swagger_api_azure.dto.PersonajeDTO;
import org.kodigo.swagger_api_azure.entity.Personaje;
import org.kodigo.swagger_api_azure.entity.Serie;
import org.kodigo.swagger_api_azure.repository.PersonajeRepository;
import org.kodigo.swagger_api_azure.repository.SerieRepository;
import org.kodigo.swagger_api_azure.service.mapper.PersonajeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PersonajeService {

    private final PersonajeRepository personajeRepository;
    private final SerieRepository serieRepository;
    private final PersonajeMapper personajeMapper;

    @Autowired
    public PersonajeService(PersonajeRepository personajeRepository,
                            SerieRepository serieRepository,
                            PersonajeMapper personajeMapper) {
        this.personajeRepository = personajeRepository;
        this.serieRepository = serieRepository;
        this.personajeMapper = personajeMapper;
    }

    /**
     * Obtiene todos los personajes
     * @return Lista de PersonajeDto
     */
    @Transactional
    public List<PersonajeDTO> findAll() {
        return personajeRepository.findAll()
                .stream()
                .map(personajeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un personaje por ID
     * @param id ID del personaje
     * @return PersonajeDto si existe
     */
    @Transactional
    public Optional<PersonajeDTO> findById(Long id) {
        return personajeRepository.findById(id)
                .map(personajeMapper::toDto);
    }

    /**
     * Obtiene personajes por serie ID
     * @param serieId ID de la serie
     * @return Lista de PersonajeDto de la serie
     */
    @Transactional
    public List<PersonajeDTO> findBySerieId(Long serieId) {
        return personajeRepository.findBySerieId(serieId)
                .stream()
                .map(personajeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo personaje
     * @param personajeDto Datos del personaje a crear
     * @return PersonajeDto creado
     * @throws RuntimeException si la serie no existe o si ya existe un personaje con el mismo nombre en la serie
     */
    public PersonajeDTO create(PersonajeDTO personajeDto) {
        validateSerieExists(personajeDto.getSerieId());
        validateUniquePersonajeInSerie(personajeDto.getNombre(), personajeDto.getSerieId());

        Serie serie = serieRepository.getReferenceById(personajeDto.getSerieId());
        Personaje personaje = personajeMapper.toEntity(personajeDto);
        personaje.setSerie(serie);

        Personaje savedPersonaje = personajeRepository.save(personaje);
        return personajeMapper.toDto(savedPersonaje);
    }

    /**
     * Crea un nuevo personaje en una serie específica
     * @param serieId ID de la serie
     * @param createPersonajeDto Datos del personaje a crear
     * @return PersonajeDto creado
     */
    public PersonajeDTO createInSerie(Long serieId, CreatePersonajeDTO createPersonajeDto) {
        validateSerieExists(serieId);
        validateUniquePersonajeInSerie(createPersonajeDto.getNombre(), serieId);

        Serie serie = serieRepository.getReferenceById(serieId);
        Personaje personaje = new Personaje(
                createPersonajeDto.getNombre(),
                createPersonajeDto.getDescripcion(),
                serie
        );

        Personaje savedPersonaje = personajeRepository.save(personaje);
        return personajeMapper.toDto(savedPersonaje);
    }

    /**
     * Actualiza un personaje existente
     * @param id ID del personaje a actualizar
     * @param personajeDto Nuevos datos del personaje
     * @return PersonajeDto actualizado
     * @throws RuntimeException si el personaje o la serie no existen
     */
    public PersonajeDTO update(Long id, PersonajeDTO personajeDto) {
        Personaje existingPersonaje = personajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado con ID: " + id));

        validateSerieExists(personajeDto.getSerieId());

        // Validar nombre único solo si ha cambiado el nombre o la serie
        if (!existingPersonaje.getNombre().equalsIgnoreCase(personajeDto.getNombre()) ||
                !existingPersonaje.getSerie().getId().equals(personajeDto.getSerieId())) {
            validateUniquePersonajeInSerie(personajeDto.getNombre(), personajeDto.getSerieId());
        }

        Serie serie = serieRepository.getReferenceById(personajeDto.getSerieId());
        existingPersonaje.setNombre(personajeDto.getNombre());
        existingPersonaje.setDescripcion(personajeDto.getDescripcion());
        existingPersonaje.setSerie(serie);

        Personaje updatedPersonaje = personajeRepository.save(existingPersonaje);
        return personajeMapper.toDto(updatedPersonaje);
    }

    /**
     * Elimina un personaje por ID
     * @param id ID del personaje a eliminar
     * @throws RuntimeException si el personaje no existe
     */
    public void deleteById(Long id) {
        if (!personajeRepository.existsById(id)) {
            throw new RuntimeException("Personaje no encontrado con ID: " + id);
        }
        personajeRepository.deleteById(id);
    }

    /**
     * Busca personajes por nombre (parcial)
     * @param nombre Nombre o parte del nombre
     * @return Lista de PersonajeDto que coinciden
     */
    @Transactional
    public List<PersonajeDTO> findByNombre(String nombre) {
        return personajeRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(personajeMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Valida que la serie exista
     * @param serieId ID de la serie
     * @throws RuntimeException si la serie no existe
     */
    private void validateSerieExists(Long serieId) {
        if (!serieRepository.existsById(serieId)) {
            throw new RuntimeException("Serie no encontrada con ID: " + serieId);
        }
    }

    /**
     * Valida que no exista un personaje con el mismo nombre en la serie
     * @param nombre Nombre del personaje
     * @param serieId ID de la serie
     * @throws IllegalArgumentException si ya existe
     */
    private void validateUniquePersonajeInSerie(String nombre, Long serieId) {
        if (personajeRepository.existsByNombreIgnoreCaseAndSerieId(nombre, serieId)) {
            throw new IllegalArgumentException("Ya existe un personaje con el nombre '" + nombre + "' en esta serie");
        }
    }
}
