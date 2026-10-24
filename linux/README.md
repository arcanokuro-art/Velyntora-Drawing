# Velyntora Drawing — Linux x64

Rama `linux`. `main` (Android) y `windows` conservan su trabajo independiente.

## Ejecutar sin instalar

Extrae **todo** `Velyntora-Drawing-Linux-x64.tar.gz` y abre `V-Drawing/V-Drawing.sh`, o ejecuta desde terminal:

```sh
./V-Drawing/V-Drawing.sh
```

Incluye Java 17, motor C++, modelo MI-GAN y recursos. No requiere Java instalado, emuladores ni conexión para dibujar o ejecutar Remove AI. Debe conservarse toda la carpeta. No se ejecuta como administrador.

Linux x86-64 con glibc 2.35 o posterior (Ubuntu 22.04 y posteriores). Necesita un escritorio X11; una sesión Wayland debe disponer de XWayland. No es una compilación ARM ni para Alpine/musl. Si el escritorio no permite abrir scripts con doble clic, usa la orden anterior. La interfaz funciona con ratón y teclado; la tableta es opcional.

También se genera `velyntora-drawing_0.4.0_amd64.deb` para Debian/Ubuntu. Instálalo con `sudo apt install ./velyntora-drawing_0.4.0_amd64.deb`; aparecerá en el menú de aplicaciones.

## Funciones

Se compilan todos los módulos de escritorio de la rama de origen: 25 herramientas, figuras con/sin relleno, 34 presets en nueve categorías, HSV/RGBA, rueda/cuadrado y hexadecimal, paleta y colores guardados, capas y mezclas con miniaturas, selección/máscaras, Línea/Curva editable, texto e imágenes editables, ajustes, efectos, formatos, proyectos y recuperación. El mismo motor `engine/core` conserva historial, capas, persistencia y Remove AI con MI-GAN sin conexión.

Rueda: zoom; Alt+rueda: rotación; botón central: desplazamiento. Ctrl+S/O/N/Z/Y/X/C/V; Enter confirma la curva; Escape cancela. Clonar: primer clic fija origen, Alt+clic lo cambia. Clic derecho abre opciones de colores y puntos de curva.

Los módulos Java se comparten desde `windows/src`; no se mantiene una copia divergente de las herramientas. `linux/` contiene la compilación, integración nativa, distribución y pruebas de Linux. Los nombres internos de paquetes Java se conservan para compartir código y proyectos.

## Integración Linux

- Recuperación: `$XDG_STATE_HOME/velyntora/drawing`, o `~/.local/state/velyntora/drawing`.
- Caché e historial de sesión: `$XDG_CACHE_HOME/velyntora/drawing`, o `~/.cache/velyntora/drawing`.
- Preferencias: Java Preferences, en el perfil del usuario.
- Archivos Unicode mediante descriptores POSIX y UTF-8, incluyendo caracteres fuera de BMP.
- Presión: XInput2 en dispositivos con eje `Abs Pressure`. Se vuelve a consultar el catálogo al conectar dispositivos. Sin muestras de lápiz se conserva presión uniforme. La respuesta de cada tableta requiere una prueba física; XWayland puede limitar los datos del controlador.

## Compilar y comprobar

JDK 17 completo, Python 3.12 con Pillow 12, Maven, CMake/Ninja, GCC, cabeceras X11/XInput2, xvfb/xauth, fakeroot y dpkg. `linux/scripts/build.sh` prepara dependencias/modelo verificado, compila el motor y todos los módulos Java, ejecuta las 23 suites C++ originales con aserciones activadas y las suites Java/regresiones, crea el runtime portable y comprueba ese ejecutable antes de generar TAR y DEB. `--dependencies-only` prepara solo dependencias y recursos.

CI compila en Ubuntu 22.04 y vuelve a ejecutar el paquete extraído en Ubuntu 24.04. La publicación sucede únicamente después de ambas comprobaciones. Esto no equivale a una prueba física de cada GPU, escritorio o tableta.

## Diseño del editor

La primera adaptación visual usa el mismo estilo de la vista previa de Windows: tema oscuro propio independiente de GTK, menú ☰ con todas las operaciones anteriores, accesos superiores, deslizador de ancho, herramientas con desplazamiento vertical, panel de capas más ancho y paleta compacta. El código de escritorio reutilizado permanece en `windows/src`; Linux conserva su backend POSIX, rutas XDG y captura XInput2.

Esta etapa no constituye todavía equivalencia visual completa con Android. Las pruebas de Linux se ejecutan con el tema instalado y comprueban apertura, funciones del motor y paquetes; la apariencia a distintas escalas y las tabletas requieren comprobación interactiva.

El espacio de trabajo conserva ahora la disposición de Android: herramientas sin recuadros, controles con valores, colores grandes y paleta inferior, seis acciones de capas y visibilidad por fila. Propiedades: doble clic, menú contextual o Capas. F11 activa pantalla completa. Se verifican capturas a 1366 × 768 y 1536 × 960.
