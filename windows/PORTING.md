# Correspondencia con Velyntora Core

Esta tabla describe el código implementado en Windows. No representa una promesa de ausencia de fallos ni una certificación de todos los dispositivos.

| Área de Core | Implementación Windows | Validación |
|---|---|---|
| Motor, capas, historial y persistencia C++ | Misma biblioteca `engine/core` y JNI originales; adaptación de archivos temporales Windows | Compilación nativa, dibujo, deshacer/rehacer, capas, Unicode y guardado/reapertura |
| Pinceles | 34 presets originales y dos renderizadores clásicos, selector con vistas previas, presión, aerógrafo sostenido y parámetros por herramienta | Configuración y trazos con los 34 presets; pruebas de gestos de ratón |
| Herramientas | Pincel, lápiz, goma, cubo, selector, zoom, desplazamiento, clonar, recoloración, figuras, selecciones, texto, mover píxeles y selección | Construcción del editor y pruebas de dibujo/selección |
| Línea/Curva | `CurveDraft` original; inserción, movimiento, tensión, múltiples líneas, Enter y recuperación tras Deshacer | Geometría que pasa por los nodos originales y motor nativo |
| Degradado | `GradientRaster` original; cinco geometrías, dos colores o transparencia y máscara | Generación con los cinco modos |
| Editar | Copiar, cortar, pegar, duplicar, seleccionar todo, invertir, expandir, contraer, borrar, mover y transformar | Gestos reales, inversión y morfología de selección |
| Imagen y capas | Dimensiones, recorte a selección, recorte de capa, vueltas y rotaciones, capas, propiedades, duplicado, combinación y mezcla | Motor nativo, persistencia de capas y editor |
| Ajustes y efectos | Familias básicas, desenfoques, distorsiones, arte, generadores, utilidades, objetos, curvas, niveles y extras de color | Mismos puntos de entrada y algoritmos C++; controles compilados |
| Remove AI | Backend MI-GAN original adaptado a escritorio; modelo incluido y verificado; máscara, cancelación y revisión comprobada al confirmar | Inferencia real sin red; píxeles fuera de la máscara intactos y resultado obsoleto rechazado |
| Texto e imágenes retenidos | Fuente, contenido UTF-8, estilo, recorte, posición, escala y giro conservados; edición y rasterización explícita | Guardado/reapertura de fuentes y texto; cancelación de edición |
| Formatos | Códecs Java originales y proveedores de escritorio para PNG/JPEG/BMP/TGA/PPM/ICO/GIF/TIFF/WebP, PCX, XBM/XPM, ANI/ICNS/QuickTime, SVG y ORA | 19 suites originales de Core más nueve ciclos de imagen, SVG y ORA con capas; dimensiones, orientación EXIF y confirmación de reducción proporcional |
| Recuperación | Revisión comprobada, temporal y sustitución atómica; recuperación desde el inicio | Rechazo de revisión obsoleta y escritura válida de proyecto |
| Entrada de lápiz | Windows Ink por ventana y presión entregada al pincel | Compilación y API; respuesta de una tableta física pendiente de prueba |
| Inicio y distribución | Imagen, logo transparente, crédito Zaskar Startup, recientes, runtime incluido y ejecutable V-Drawing | Inicio/editor renderizados y pruebas repetidas desde el ejecutable empaquetado |

No se modifica ni se mezcla `main`: este trabajo pertenece a `windows`.
