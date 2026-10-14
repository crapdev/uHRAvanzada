-- V3: seed masivo del catalogo
--   15 lugares en 9 ciudades, 8 categorias, 201 eventos activos + 10 inactivos (soft delete)

INSERT INTO venues (name, address, city, capacity)
VALUES ('Movistar Arena', 'Diagonal 61C # 26-36', 'Bogotá', 14000),
       ('Teatro Colón', 'Calle 10 # 5-32', 'Bogotá', 900),
       ('Corferias', 'Carrera 37 # 24-67', 'Bogotá', 20000),
       ('Plaza Mayor', 'Calle 41 # 55-80', 'Medellín', 12000),
       ('Teatro Pablo Tobón Uribe', 'Carrera 40 # 51-24', 'Medellín', 850),
       ('Estadio Atanasio Girardot', 'Carrera 74 # 48-10', 'Medellín', 45000),
       ('Teatro Municipal Enrique Buenaventura', 'Calle 7 # 6-56', 'Cali', 1200),
       ('Centro de Eventos Valle del Pacífico', 'Calle 37 # 8-120', 'Cali', 15000),
       ('Centro de Convenciones Puerta de Oro', 'Vía 40 # 79B-06', 'Barranquilla', 5000),
       ('Centro de Convenciones Cartagena de Indias', 'Calle 24 # 8A-344', 'Cartagena', 4000),
       ('Teatro Santander', 'Calle 33 # 18-49', 'Bucaramanga', 800),
       ('Expofuturo', 'Vía Cerritos km 4', 'Pereira', 6000),
       ('Teatro Fundadores', 'Carrera 22 # 33-35', 'Manizales', 1200),
       ('Parque del Agua', 'Carrera 7 # 10-50', 'Santa Marta', 300),
       ('Auditorio Riwi', 'Carrera 43A # 1-50', 'Medellín', 250);

INSERT INTO categories (name, description)
VALUES ('Conciertos', 'Presentaciones de música en vivo'),
       ('Talleres', 'Sesiones prácticas para aprender una habilidad'),
       ('Conferencias', 'Charlas y paneles de expertos'),
       ('Deportes', 'Torneos y competencias deportivas'),
       ('Gastronomía', 'Ferias y experiencias culinarias'),
       ('Festivales', 'Eventos culturales de varios días'),
       ('Teatro', 'Obras y artes escénicas'),
       ('Rock', 'Todo lo relacionado con el rock');

-- Evento de referencia para el escenario 6 (busqueda parcial e insensible a mayusculas)
INSERT INTO events (name, date, description, active, venue_id)
SELECT 'Concierto de ROCK', DATE '2026-11-21', 'Gran noche de rock en la capital', TRUE, id
FROM venues
WHERE name = 'Movistar Arena';

-- 200 eventos generados: 7 tipos x 5 temas, repartidos en 15 lugares y ~3 años
INSERT INTO events (name, date, description, active, venue_id)
SELECT t.prefix || ' ' || t.topic || ' #' || g,
       DATE '2025-01-01' + ((g * 37) % 1095),
       t.prefix || ' ' || t.topic || ' en ' || v.city,
       TRUE,
       v.id
FROM generate_series(1, 200) AS g
         CROSS JOIN LATERAL (
    SELECT (ARRAY ['Concierto de', 'Taller de', 'Conferencia de', 'Torneo de',
                   'Feria de', 'Festival de', 'Obra de teatro:'])[(g % 7) + 1] AS prefix,
           (ARRAY [
               ['rock', 'jazz', 'salsa', 'vallenato', 'música clásica'],
               ['Spring Boot', 'fotografía', 'cerámica', 'cocina italiana', 'escritura creativa'],
               ['inteligencia artificial', 'ciberseguridad', 'emprendimiento', 'cambio climático', 'desarrollo web'],
               ['fútbol', 'baloncesto', 'ajedrez', 'tenis', 'voleibol'],
               ['café', 'comida callejera', 'cerveza artesanal', 'postres', 'cocina del Pacífico'],
               ['rock al parque', 'cine', 'luces', 'verano', 'música electrónica'],
               ['Hamlet', 'La casa de Bernarda Alba', 'Romeo y Julieta', 'El avaro', 'Esperando a Godot']
               ])[(g % 7) + 1][((g / 7) % 5) + 1]                                        AS topic
    ) AS t
         JOIN (SELECT id, city, ROW_NUMBER() OVER (ORDER BY id) AS rn FROM venues) AS v
              ON v.rn = (g % 15) + 1;

-- 10 eventos "eliminados" (soft delete): existen en la tabla pero la app nunca los muestra
INSERT INTO events (name, date, description, active, venue_id)
SELECT 'Concierto cancelado #' || g,
       DATE '2026-06-01' + g,
       'Evento dado de baja (borrado logico)',
       FALSE,
       (SELECT id FROM venues WHERE name = 'Movistar Arena')
FROM generate_series(1, 10) AS g;

-- Vinculos evento <-> categoria segun el nombre del evento
INSERT INTO events_categories (event_id, category_id)
SELECT DISTINCT e.id, c.id
FROM events e
         JOIN (VALUES ('Concierto%', 'Conciertos'),
                      ('Taller%', 'Talleres'),
                      ('Conferencia%', 'Conferencias'),
                      ('Torneo%', 'Deportes'),
                      ('Feria%', 'Gastronomía'),
                      ('Feria%', 'Festivales'),
                      ('Festival%', 'Festivales'),
                      ('Festival de música%', 'Conciertos'),
                      ('Festival de rock%', 'Conciertos'),
                      ('Obra%', 'Teatro'),
                      ('%cocina%', 'Gastronomía'),
                      ('%rock%', 'Rock')) AS m(pattern, category)
              ON e.name ILIKE m.pattern
         JOIN categories c ON c.name = m.category;
