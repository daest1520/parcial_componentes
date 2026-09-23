# parcial_componentes
[README.md](https://github.com/user-attachments/files/32543406/README.md)
# Casos Periodista – Parcial 1 (Programación por componentes 2026-3)

Aplicación Android en **Kotlin + Jetpack Compose + Room (SQLite)** para que un periodista
administre las entrevistas que realiza sobre casos criminales.

## Funcionalidades

| Requisito del parcial | Dónde está |
|---|---|
| Crear y guardar caso (título, descripción, fecha, estado) | `ui/formulario` + `CasoRepository.guardarCaso` |
| Editar y eliminar caso | Formulario en modo edición; ícono de papelera en el detalle (con confirmación) |
| Buscar y listar casos | `ui/lista` – búsqueda por título/descripción y filtro por estado |
| Registrar entrevistas, hallazgos y evidencias | `ui/detalle` – diálogo "Nueva entrevista" |
| Gestionar estado y cierre | Estados Abierto / En investigación / Cerrado; cerrar exige conclusión; se puede reabrir |
| Resumen general | `ui/resumen` – totales por estado y casos recientes |
| Pruebas unitarias | `app/src/test` – 22 pruebas de las reglas de negocio |

## Arquitectura (separación interfaz / lógica / persistencia)

```
com.periodista.casos
├── domain/          ← LÓGICA (Kotlin puro, sin Android)
│   ├── model/       Caso, Entrevista, EstadoCaso
│   └── rules/       CasoRules (validaciones, cierre, búsqueda), ResumenCasos
├── data/            ← PERSISTENCIA
│   ├── local/       Room: entidades, DAOs, AppDatabase, conversores, mappers
│   └── repository/  CasoRepository (interfaz) + CasoRepositoryRoom
├── ui/              ← INTERFAZ (MVVM con Compose)
│   ├── resumen/ lista/ formulario/ detalle/   pantalla + ViewModel cada una
│   ├── navigation/  rutas y barra inferior
│   └── components/  tarjetas y chips reutilizables
├── util/Fechas.kt
├── AppContainer.kt  inyección de dependencias manual
└── CasosApp.kt / MainActivity.kt
```

Flujo: **Pantalla → ViewModel → Repositorio → DAO (Room)**. La base de datos expone `Flow`,
así que cualquier cambio (crear, editar, cerrar, agregar entrevista) se refleja
automáticamente en todas las pantallas.

### Reglas de negocio implementadas
- Título obligatorio (mínimo 3 caracteres) y descripción obligatoria.
- La fecha no puede ser futura.
- Un caso solo se puede cerrar con una conclusión; un caso cerrado no se vuelve a cerrar.
- Al reabrir, el caso pasa a *En investigación* y conserva la conclusión anterior.
- Una entrevista necesita entrevistado y al menos un hallazgo (evidencias opcionales).
- Al eliminar un caso se eliminan sus entrevistas (llave foránea con `CASCADE`).

## Cómo ejecutar
1. Abrir la carpeta en Android Studio (Koala o superior) y esperar la sincronización de Gradle.
2. Ejecutar en un emulador o dispositivo con Android 8.0 (API 26) o superior.
3. Pruebas: clic derecho en `app/src/test` → *Run Tests*, o `./gradlew test`.
