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

## Auditoría adicional de controles Android → Windows

La referencia es el código Android incluido en `engine/app/src/main/java` y las herramientas de `engine/tools`. La rama Windows comparte el motor C++ de ese árbol. Las interfaces Android y Swing son implementaciones distintas.

| Control o comportamiento de Android | Ubicación Windows | Comprobación |
|---|---|---|
| HSV, RGB, alfa, hexadecimal RGBA, rueda y cuadrado | `color/ColorPicker.java` | Conversión hexadecimal, cambios de alfa e independencia del secundario |
| 22 colores predeterminados y colores guardados | `color/PalettePanel.java` | Misma paleta, persistencia, duplicados y eliminación |
| Elegir principal/secundario y eliminar color | Menús contextuales de las muestras | Acciones implementadas en cada muestra |
| Iconos locales y figuras rellenas | `tools/DesktopToolIcon.java`, generación desde los originales | Rasterización de todos los iconos; clases generadas desde la fuente Android |
| Orden de herramientas y figuras agrupadas | `tools/Tool.java`, `EditorWindow.figureMenu` | Catálogo explícito, nueve opciones de figura |
| Selección de pincel por categoría | `brushes/BrushSelector.java` | Mismo catálogo de 34 presets y recursos originales |
| Punta cuadrada, presión, opacidad, dureza | `ToolSettings`, `CanvasPanel`, configuración del pincel | Valores por herramienta y persistencia; conexión a los renderizadores nativos |
| Capas con miniatura, nombre, visibilidad, opacidad y mezcla | `layers/LayersPanel.java` | Miniatura del motor real y pruebas nativas de persistencia |
| Coordenadas, giro, dimensiones, zoom y documento | `EditorWindow.status`, `CanvasPanel.cursorStatus` | Actualización por eventos y cambio de documento |
| Crear con presets; escala proporcional/porcentaje; remuestreo y anclaje | `document/DimensionsDialog.java` | Valores de remuestreo/anclaje y llamada al mismo motor de redimensionado |
| Cola de trabajo, progreso e historial | `tasks/WorkQueue.java` | Orden, devolución de resultados y propagación de fallos; EDT disponible durante ejecución |
| Mover contorno sin mover píxeles | `CanvasPanel.moveSelectionMask` | Máscara desplazada, píxeles y revisión intactos |
| Deshacer/rehacer borrador y curva confirmada | `CanvasPanel` | Gestos de línea, revisión nativa, confirmar, deshacer y rehacer |
| Acciones rápidas de color con valores originales | `DesktopColorPresets` generado | Etiquetas y argumentos extraídos del menú Android; métodos JNI comprobados al generar |
| Guardar como y exportadores con selector | `EditorWindow`, `DocumentIO` | Exportación fallida conserva el archivo anterior |
| Cambiar de documento descarta borradores y máscaras anteriores | `CanvasPanel.resetDocumentState` | Máscaras y selección reiniciadas |

Las suites automáticas comprueban casos concretos, no una ausencia universal de fallos. No se ha realizado aquí una evaluación física de todas las tabletas ni una comparación visual píxel a píxel de cada control entre ambos sistemas.
