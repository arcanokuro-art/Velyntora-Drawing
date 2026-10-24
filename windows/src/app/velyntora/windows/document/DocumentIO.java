package app.velyntora.windows.document;
import art.velyntora.core.DrawingView;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.util.*;
import java.util.prefs.Preferences;
public final class DocumentIO {
 private static final Preferences prefs=Preferences.userRoot().node("app/velyntora/drawing/"+(app.velyntora.windows.app.DesktopPlatform.linux()?"linux":"windows"));
 public static java.util.List<String> recent(){String all=prefs.get("recent","");return Arrays.stream(all.split("\\n")).filter(s->!s.isBlank()&&Files.exists(Path.of(s))).toList();}
 public static void remember(Path path){java.util.List<String> paths=new ArrayList<>(recent());paths.remove(path.toAbsolutePath().toString());paths.add(0,path.toAbsolutePath().toString());prefs.put("recent",String.join("\n",paths.subList(0,Math.min(paths.size(),8))));}
 public static void open(Path path)throws Exception{
  if(path.toString().toLowerCase().endsWith(".vlycore")){if(!DrawingView.nativeProjectPath(path.toAbsolutePath().toString(),false))throw new java.io.IOException("Proyecto no compatible o dañado");}
  else if(art.velyntora.core.DesktopFormats.extension(path).equals("ora")){art.velyntora.core.DesktopFormats.openOra(path);}
  else{BufferedImage image=art.velyntora.core.DesktopFormats.readInteractive(null,path);if(image==null)throw new java.io.IOException("Formato de imagen no compatible en este piloto");if(image.getWidth()>6000||image.getHeight()>6000)throw new java.io.IOException("Máximo: 6000 × 6000 px");int w=image.getWidth(),h=image.getHeight();if(!DrawingView.nativeLoadBitmap(w,h,image.getRGB(0,0,w,h,null,0,w)))throw new java.io.IOException("No se pudo importar la imagen");}
  if(!path.toAbsolutePath().equals(DocumentRecovery.file().toAbsolutePath()))remember(path);
 }
 public static void saveProject(Path path)throws Exception{
  Path target=path.toAbsolutePath(),temp=Files.createTempFile(target.getParent(),"velyntora-",".tmp");try{if(!DrawingView.nativeProjectPath(temp.toString(),true))throw new java.io.IOException("No se pudo guardar el proyecto");try{Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException e){Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING);}remember(target);}finally{Files.deleteIfExists(temp);}
 }
 public static BufferedImage composite(){int w=DrawingView.nativeWidth(),h=DrawingView.nativeHeight();BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);for(int y=0;y<h;y+=128){int rows=Math.min(128,h-y);int[] pixels=DrawingView.nativeRegionPixels(0,y,w,rows);if(pixels==null)throw new IllegalStateException("Error de renderizado");image.setRGB(0,y,w,rows,pixels,0,w);}return image;}
 public static void export(Path path)throws Exception{String ext=art.velyntora.core.DesktopFormats.extension(path);Path target=path.toAbsolutePath(),temp=Files.createTempFile(target.getParent(),"velyntora-export-","."+ext);try{exportTo(temp);try{Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException error){Files.move(temp,target,StandardCopyOption.REPLACE_EXISTING);}}finally{Files.deleteIfExists(temp);}}
 private static void exportTo(Path path)throws Exception{String ext=art.velyntora.core.DesktopFormats.extension(path);BufferedImage image=composite();if(ext.equals("jpg")||ext.equals("jpeg")||ext.equals("bmp")){BufferedImage rgb=new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_RGB);Graphics2D g=rgb.createGraphics();g.setColor(Color.WHITE);g.fillRect(0,0,rgb.getWidth(),rgb.getHeight());g.drawImage(image,0,0,null);g.dispose();image=rgb;}art.velyntora.core.DesktopFormats.write(path,image);}
}
