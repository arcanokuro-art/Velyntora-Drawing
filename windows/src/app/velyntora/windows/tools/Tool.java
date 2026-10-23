package app.velyntora.windows.tools;
public enum Tool {
 REMOVE_AI("Remove AI"),CIRCLE("Círculo"),FREEFORM("Forma libre"),MOVE_PIXELS("Mover píxeles"),BRUSH("Pincel"),PENCIL("Lápiz"),ERASER("Goma"),FILL("Cubo"),PICKER("Selector de color"),PAN("Desplazamiento"),ZOOM("Zoom"),LINE("Línea/Curva"),RECT("Rectángulo"),ELLIPSE("Elipse"),TRIANGLE("Triángulo"),ROUNDRECT("Rectángulo redondeado"),SELECT("Selección rectangular"),ELLIPSE_SELECT("Selección elíptica"),LASSO("Lazo"),WAND("Varita"),GRADIENT("Degradado"),TEXT("Texto"),CLONE("Clonar"),RECOLOR("Recoloración"),MOVE("Mover selección");
 public static final Tool[] SIDEBAR={MOVE_PIXELS,FILL,MOVE,GRADIENT,ZOOM,PICKER,PAN,TEXT,SELECT,LINE,ELLIPSE_SELECT,RECT,LASSO,WAND,BRUSH,FREEFORM,PENCIL,CLONE,ERASER,RECOLOR,REMOVE_AI};
 public boolean figure(){return this==RECT||this==ROUNDRECT||this==ELLIPSE||this==TRIANGLE||this==CIRCLE;}
 public final String label;Tool(String label){this.label=label;}public boolean usesWidth(){return switch(this){case BRUSH,PENCIL,ERASER,LINE,RECT,ELLIPSE,CIRCLE,TRIANGLE,ROUNDRECT,FREEFORM,CLONE,RECOLOR,REMOVE_AI->true;default->false;};}public String toString(){return label;}
}
