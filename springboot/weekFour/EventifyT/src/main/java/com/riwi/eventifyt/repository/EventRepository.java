package com.riwi.eventifyt.repository;

import com.riwi.eventifyt.DTO.EventSummaryDTO;
import com.riwi.eventifyt.domain.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// Recordatorio: @SQLRestriction("active = true") en Event agrega el filtro de soft delete
// a TODAS estas consultas automaticamente
public interface EventRepository extends JpaRepository<Event, Long> {

    // Consulta derivada: Spring genera el SQL a partir del nombre del metodo
    List<Event> findByNameContainingIgnoreCase(String name);

    // ---------- Detalle: entidad completa con sus relaciones ----------

    // @EntityGraph: trae evento + lugar + categorias en UNA sola consulta (LEFT JOIN) → sin N+1
    @EntityGraph(attributePaths = {"venue", "categories"})
    Optional<Event> findWithRelationsById(Long id);

    // ---------- Catalogo: proyeccion a record + Slice ----------

    /*
     * Motor de busqueda. Cada filtro es opcional (si llega null, esa condicion se ignora).
     *  - SELECT new ...: construye el record directamente, no se cargan entidades
     *  - JOIN e.venue: una sola sentencia SQL para traer lugar y ciudad
     *  - Categoria con EXISTS: filtra sin duplicar filas cuando un evento tiene varias categorias
     *  - LOWER + LIKE %x%: busqueda parcial e insensible a mayusculas ("bog" encuentra "Bogotá")
     *  - CAST(:param AS ...): con un parametro null PostgreSQL no sabe su tipo (lo trata como bytea
     *    y falla "lower(bytea) does not exist"); el CAST se lo indica explicitamente
     *  - Slice: Spring pide size + 1 filas para saber si hay pagina siguiente, sin SELECT COUNT
     */
    @Query("""
            SELECT new com.riwi.eventifyt.DTO.EventSummaryDTO(e.id, e.name, e.date, v.name, v.city)
            FROM Event e
            JOIN e.venue v
            WHERE (CAST(:name AS String) IS NULL
                     OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:name AS String), '%')))
              AND (CAST(:city AS String) IS NULL
                     OR LOWER(v.city) LIKE LOWER(CONCAT('%', CAST(:city AS String), '%')))
              AND (CAST(:category AS String) IS NULL OR EXISTS (
                     SELECT 1 FROM e.categories c
                     WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', CAST(:category AS String), '%'))))
              AND (CAST(:minCapacity AS Integer) IS NULL OR v.capacity >= :minCapacity)
              AND (CAST(:from AS LocalDate) IS NULL OR e.date >= :from)
              AND (CAST(:to AS LocalDate) IS NULL OR e.date <= :to)
            ORDER BY e.date DESC, e.id DESC
            """)
    Slice<EventSummaryDTO> search(@Param("name") String name,
                                  @Param("city") String city,
                                  @Param("category") String category,
                                  @Param("minCapacity") Integer minCapacity,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to,
                                  Pageable pageable);

    // Atajos por un solo criterio (reutilizan el mismo motor, siempre ordenados por fecha DESC)

    default Slice<EventSummaryDTO> findAllSummaries(Pageable pageable) {
        return search(null, null, null, null, null, null, pageable);
    }

    default Slice<EventSummaryDTO> findByCity(String city, Pageable pageable) {
        return search(null, city, null, null, null, null, pageable);
    }

    default Slice<EventSummaryDTO> findByCategory(String category, Pageable pageable) {
        return search(null, null, category, null, null, null, pageable);
    }

    default Slice<EventSummaryDTO> findByMinCapacity(Integer minCapacity, Pageable pageable) {
        return search(null, null, null, minCapacity, null, null, pageable);
    }

    default Slice<EventSummaryDTO> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable) {
        return search(null, null, null, null, from, to, pageable);
    }
}
