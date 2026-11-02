# Impresión Android

Entrada: **☰ → Archivo → Imprimir…**. Atajo opcional con teclado: **Ctrl+P**.

- `DocumentPrinting`: prepara una copia temporal del documento y abre PrintManager.
- `PrintSnapshot`: compone las capas visibles a PNG a resolución original, por bloques y con comprobación de revisión; limpia archivos incompletos al cancelar o fallar.
- `PrintPdfWriter`: genera una página PDF ajustada y centrada en el área imprimible, sin recortar ni deformar. Respeta márgenes, orientación y modo monocromático; la transparencia queda sobre blanco. No imprime barras, selección ni cuadrícula de transparencia.
- `ArtworkPrintAdapter`: atiende disposición, escritura, rangos, cancelación y limpieza final del sistema de impresión.
- `PrintLayout`: geometría independiente del sistema.
- `PrintIcon` y `assets/printing/impresora.webp`: icono exacto proporcionado por el usuario, sin modificaciones. SHA-256: `092a0f409cc7d7ae956db3cb9598788703ac2e52f4c07c8bb973c2919f26061f`.

La elección de impresora, papel, copias y Guardar como PDF se hace en el panel del sistema Android. La disponibilidad de una impresora depende de su servicio/controlador Android. Imprimir no guarda ni reemplaza el proyecto y no borra su historial. Una Línea/Curva pendiente se confirma de la misma forma que al exportar.

La copia se elimina al terminar el adaptador. Si el proceso se interrumpe antes de la limpieza, el archivo queda únicamente en la caché privada de Android; no es accesible como documento compartido.
