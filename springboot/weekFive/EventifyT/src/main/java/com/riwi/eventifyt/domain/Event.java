package com.riwi.eventifyt.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
// Filtro global: Hibernate agrega "WHERE active = true" a TODAS las consultas sobre Event
// → los eventos "borrados" (inactivos) nunca vuelven a aparecer
@SQLRestriction("active = true")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id  // clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // la BD asigna el id (1, 2, 3...)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    // Soft delete: en vez de borrar la fila, la marcamos como inactiva
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // Muchos eventos → un lugar. Obligatorio (optional = false + nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    // Muchos eventos ↔ muchas categorías, con tabla intermedia explícita
    // Cascade PERSIST/MERGE: al guardar el evento se guardan sus categorías y los vínculos
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "events_categories",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // "Eliminar" = desactivar. Hibernate detecta el cambio y lanza un UPDATE
    public void softDelete() {
        this.active = false;
    }
}
