# Guardar y exportar

La flecha de guardado de la barra superior abre `SaveMenu`: proyecto .vlycore, OpenRaster, PNG, JPEG, WebP, BMP, TGA, TIFF, GIF fijo, ICO, PPM, PDF e Imprimir. Archivo conserva creación, apertura e inserción; no duplica guardados ni impresión. Ctrl+S sigue guardando el proyecto; Ctrl+P sigue imprimiendo.

`PdfExport` usa ACTION_CREATE_DOCUMENT con application/pdf y su propio request code. Al elegir destino, bloquea la edición mientras obtiene el compuesto de capas visibles, prepara un PDF completo en caché y lo copia al URI elegido. Cerrar el selector sin elegir destino no inicia exportación. No marca el proyecto como guardado ni reemplaza su archivo editable; confirma curvas pendientes del mismo modo que la exportación raster. El cierre de la Activity cancela el trabajo y elimina los temporales.

`ArtworkPdfWriter` produce una página con la proporción y resolución raster del dibujo: un píxel corresponde a un punto PDF, sin márgenes de impresora, sin recorte, con transparencia sobre blanco. Las capas quedan combinadas. No necesita servicio de impresión y no captura la interfaz. Reutiliza PrintSnapshot y el renderizado por franjas de PrintPdfWriter para evitar un bitmap del lienzo completo. Imprimir usa el panel del sistema y sus ajustes de papel y márgenes, conservando el icono original.
