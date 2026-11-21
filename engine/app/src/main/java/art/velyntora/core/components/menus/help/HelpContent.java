package art.velyntora.core;

/** Offline help articles shown by the Drawing editor. */
final class HelpContent {
  private HelpContent() {}

  static final String PHONE = """
      Barra superior
      Abre el menú para gestionar archivos o consultar Ayuda. Toca Guardar para abrir las opciones de proyecto, exportación e impresión.

      Barra inferior
      Pincel y Borrador cambian la herramienta activa. Color y Capas abren sus paneles. Más muestra el resto de las herramientas. Deshacer y Rehacer permiten corregir un paso y recuperarlo.

      Ajustes de la herramienta
      Busca los controles de la herramienta activa junto al lienzo. Por ejemplo, al elegir Pincel puedes ajustar la anchura y la opacidad.

      Navegar por el lienzo
      Usa dos dedos para ampliar o desplazar la vista. Si pierdes de vista el dibujo, abre Todas las opciones → Ver → Ajustar al lienzo.
      """;

  static final String WIDE = """
      Barra superior
      Encontrarás el menú, los controles del documento, la flecha de guardado y el historial. Los ajustes cambian según la herramienta activa.

      Herramientas y paneles
      Elige herramientas en la barra izquierda. Usa la paleta inferior para cambiar el color y el panel derecho para trabajar con capas.

      Vista del dibujo
      Usa Zoom para acercarte a los detalles y Desplazamiento para recorrer el lienzo. En Ver → Ajustar al lienzo puedes recuperar la vista completa.

      Ventanas reducidas
      En una ventana estrecha, Drawing puede activar el diseño de celular. Busca las herramientas en Más y los paneles en Color y Capas.
      """;

  static final String SHORTCUTS = """
      Ctrl+N · Nuevo
      Ctrl+O · Abrir
      Ctrl+S · Guardar proyecto
      Ctrl+P · Imprimir

      Ctrl+Z · Deshacer
      Ctrl+Y o Ctrl+Mayús+Z · Rehacer

      Ctrl+A · Seleccionar todo
      Ctrl+D · Deseleccionar
      Ctrl+C · Copiar
      Ctrl+X · Cortar
      Ctrl+V · Pegar
      """;

  static final String PAINT = """
      En celular, abre Más para ver las herramientas que no aparecen en la barra inferior. En tablet y modo PC están en la barra izquierda.

      Pincel · Dibuja trazos con la punta seleccionada. Ajusta la anchura para pasar de un detalle fino a un trazo ancho y usa la opacidad para suavizar el color.

      Lápiz · Traza líneas precisas. Pruébalo para un boceto o para corregir un detalle con el lienzo ampliado.

      Borrador · Borra de la capa activa. Comprueba qué capa está seleccionada antes de borrar una parte del dibujo.

      Cubeta · Aplica el color activo a una zona con un toque. Para practicar, dibuja un contorno cerrado en la capa activa y toca su interior.

      Degradado · Arrastra para indicar el inicio y el final de la transición. Mantén pulsado el icono de la herramienta para abrir su configuración.

      Cuentagotas · Toca un punto del dibujo para tomar su color y reutilizarlo con el pincel.

      Tampón de clonar · El primer toque fija el origen. Dibuja después en el destino para copiar textura de esa zona. Vuelve a seleccionar la herramienta para cambiar el origen.

      Recoloración · Sustituye el color inicial por el color activo en la zona que recorres con la herramienta. Ajusta la tolerancia para controlar qué tan parecidos deben ser los colores.

      Remove AI · Pinta encima del área que quieres retirar y pulsa Eliminar. Revisa el resultado en el lienzo y usa Deshacer si necesitas repetirlo.
      """;

  static final String SELECTION = """
      Selección rectangular o elíptica · Arrastra para delimitar una zona regular. Por ejemplo, usa la elíptica para aislar un elemento redondo.

      Selección libre (contorno) · Rodea un elemento siguiendo su silueta para trabajar sobre una zona irregular.

      Varita mágica · Toca un área para seleccionar píxeles de color parecido. Ajusta Tolerancia para ampliar o reducir los colores incluidos. Usa los controles de confirmación o cancelación de la selección.

      Mover contorno · Cambia la posición del área seleccionada. Úsalo para alinear el contorno con otro detalle antes de editarlo.

      Mover píxeles · Arrastra el contenido seleccionado a otra posición. Por ejemplo, desplaza un elemento que quedó demasiado cerca del borde.

      Zoom y Desplazamiento · Úsalos para inspeccionar y recorrer la vista del dibujo. Para volver al encuadre completo, elige Ver → Ajustar al lienzo.
      """;

  static final String SHAPES = """
      Línea/Curva · Traza una línea y arrastra sus tiradores para darle forma. Usa Esquinas para un cambio de dirección marcado. Confirma con ✓ y cancela con ✕ cuando estén visibles.

      Figuras · Abre el selector de figuras, elige una forma y arrastra en el lienzo para definir su tamaño. Usa una figura rellena para dibujar una silueta sólida, o una figura de contorno para dibujar un marco.

      Forma libre (contorno cerrado) · Dibuja una silueta irregular recorriendo su borde. Sirve para crear formas que no encajan en un rectángulo o una elipse.

      Texto · Añade palabras al lienzo. Ajusta el tamaño, el espaciado y el color según el diseño. Revisa la posición y confirma antes de guardar.
      """;

  static final String LAYERS = """
      Dibuja por capas
      Abre Capas, añade una capa y selecciónala antes de dibujar. Puedes separar el boceto, las líneas y el color para corregirlos por separado.

      Revisa la composición
      Cambia la visibilidad para comparar el dibujo con o sin una capa. Usa la opacidad de capa para aclarar el boceto de referencia. Los modos de fusión controlan cómo se combina la capa con las de abajo.

      Elige un color
      Abre Color en celular o usa la paleta inferior en tablet y modo PC. Ajusta el tono, la saturación y el valor con HSV. Usa HEX cuando quieras repetir un color exacto y las muestras para tener tus colores a mano.

      Conserva las capas
      Usa Guardar proyecto y vuelve a abrir el archivo .vlycore desde Inicio para seguir editándolo.
      """;

  static final String IMAGES = """
      Abrir o añadir
      Usa Abrir imagen en Inicio para editar un archivo. Dentro de un dibujo abierto, elige Archivo → Insertar imagen como capa… para añadir una imagen al proyecto.

      Colocar y ajustar
      Arrastra la imagen para moverla y usa los tiradores para cambiar su tamaño. Cuando se acerque al centro o a un borde del lienzo, las guías indican el ajuste. Mantén Proporciones activado para conservar su forma al redimensionar.

      Girar y voltear
      En la barra de transformación, abre Girar o voltear. Puedes girar 90° a la izquierda o derecha y voltear horizontal o verticalmente. Confirma cuando la imagen quede colocada o cancela para salir de la operación.

      Guardar
      Guarda el proyecto .vlycore para retomar la edición. Exporta una copia PNG para compartir el resultado visible.
      """;

  static final String SAVING = """
      Abre Guardar desde la barra superior. En la vista amplia, busca el botón de la flecha hacia abajo.

      Guardar proyecto · Crea un archivo .vlycore con capas. Úsalo cuando quieras editar el dibujo más tarde.

      Exportar PNG · Crea una imagen para compartir y conserva la transparencia disponible en el dibujo. Las capas quedan combinadas en la imagen.

      Exportar PDF · Crea un documento con el resultado visible para compartirlo o imprimirlo.

      Imprimir… · Abre las opciones de impresión de Android para elegir el destino y revisar la vista previa.

      Más formatos · Usa OpenRaster (.ora) para pasar capas a otros editores compatibles, JPEG para una imagen sin transparencia, o WebP si el destino admite ese formato. También están disponibles BMP, TGA, TIFF, GIF de imagen fija, ICO hasta 256 px y PPM.

      Si hay una imagen o texto en transformación, confirma o cancela la colocación antes de guardar.
      """;

  static final String TROUBLESHOOTING = """
      ¿Dónde están las demás herramientas en celular?
      Toca Más en la barra inferior. Pincel y Borrador tienen acceso directo, igual que los paneles Color y Capas.

      ¿Cómo vuelvo a ver todo mi dibujo?
      Abre Ver → Ajustar al lienzo. Si el dibujo se ve inclinado, usa Ver → Restablecer rotación.

      ¿Por qué no veo lo que estoy dibujando?
      Revisa que estés en la capa correcta, que esté visible y que tenga opacidad. Si hay una selección activa, revisa si estás dibujando dentro de ella.

      ¿Por qué no puedo guardar en este momento?
      Confirma o cancela la imagen o el texto en transformación y espera a que termine cualquier operación de guardado o exportación.

      ¿Por qué el dibujo que compartí ya no tiene capas?
      Abre el proyecto .vlycore original para seguir editando. PNG, JPEG y PDF contienen el resultado visible.

      ¿Qué hago si Android dice que la actualización entra en conflicto?
      Si vienes de una instalación antigua con otra firma, primero guarda y verifica tus proyectos .vlycore en una carpeta fuera de la app. Después puedes desinstalar esa copia e instalar el APK oficial. Conserva la misma fuente de descarga para futuras actualizaciones.
      """;
}
