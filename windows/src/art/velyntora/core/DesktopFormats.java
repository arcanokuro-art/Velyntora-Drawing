package art.velyntora.core;
import java.io.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.PNGTranscoder;
public final class DesktopFormats {
 private static BufferedImage image(int w,int h,int[] pixels){BufferedImage out=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);out.setRGB(0,0,w,h,pixels,0,w);return out;}
 public static BufferedImage read(Path path)throws Exception{String ext=extension(path);try(InputStream in=Files.newInputStream(path)){switch(ext){
 case "bmp":{var v=BmpReader.read(in);return image(v.width,v.height,v.pixels);}
 case "tga":{var v=TgaReader.read(in);return image(v.width,v.height,v.pixels);}
 case "ppm":case "pgm":case "pbm":case "pnm":{var v=PpmCodec.read(in);return image(v.width,v.height,v.pixels);}
 case "pcx":{var v=PcxReader.read(in);return image(v.width,v.height,v.pixels);}
 case "xbm":case "xpm":{var v=ext.equals("xbm")?XRasterReader.xbm(in):XRasterReader.xpm(in);return image(v.width,v.height,v.pixels);}
 case "ico":case "cur":{var v=IcoCodec.read(in,bytes->{BufferedImage p=ImageIO.read(new ByteArrayInputStream(bytes));if(p==null)throw new IOException("PNG inválido");return new IcoCodec.Image(p.getWidth(),p.getHeight(),p.getRGB(0,0,p.getWidth(),p.getHeight(),null,0,p.getWidth()));});return image(v.width,v.height,v.pixels);}
 case "icns":case "ani":case "qtif":case "qti":{var v=IconContainers.read(in,bytes->{BufferedImage p=ImageIO.read(new ByteArrayInputStream(bytes));if(p==null)throw new IOException("Imagen inválida");return new IconContainers.Image(p.getWidth(),p.getHeight(),p.getRGB(0,0,p.getWidth(),p.getHeight(),null,0,p.getWidth()));});return image(v.width,v.height,v.pixels);}
 case "svg":{PNGTranscoder transcoder=new PNGTranscoder();transcoder.addTranscodingHint(PNGTranscoder.KEY_MAX_WIDTH,6000f);transcoder.addTranscodingHint(PNGTranscoder.KEY_MAX_HEIGHT,6000f);transcoder.addTranscodingHint(org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES,Boolean.FALSE);transcoder.addTranscodingHint(org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_EXECUTE_ONLOAD,Boolean.FALSE);ByteArrayOutputStream bytes=new ByteArrayOutputStream();transcoder.transcode(new TranscoderInput(in),new TranscoderOutput(bytes));return ImageIO.read(new ByteArrayInputStream(bytes.toByteArray()));}
 default:{BufferedImage p=ImageIO.read(in);if(p==null)throw new IOException("Formato no compatible: "+ext);return p;}
 }}}
 public static String extension(Path path){String name=path.getFileName().toString().toLowerCase(java.util.Locale.ROOT);return name.substring(name.lastIndexOf('.')+1);}
 public static void openOra(Path path)throws Exception{Path project=Files.createTempFile("velyntora-ora-",".vlycore");try{try(OutputStream out=Files.newOutputStream(project)){OpenRasterArchive.read(path.toFile(),out,(in,w,h,x,y)->{BufferedImage p=ImageIO.read(in);if(p==null)throw new IOException("PNG de capa inválido");int[] result=new int[w*h];for(int row=0;row<p.getHeight();row++)for(int col=0;col<p.getWidth();col++)if(x+col>=0&&x+col<w&&y+row>=0&&y+row<h)result[(y+row)*w+x+col]=p.getRGB(col,row);return result;});}if(!DrawingView.nativeProjectPath(project.toString(),false))throw new IOException("No se pudo abrir ORA");}finally{Files.deleteIfExists(project);}}
 public static void write(Path path,BufferedImage image)throws Exception{String ext=extension(path);int w=image.getWidth(),h=image.getHeight();if(ext.equals("ora")){Path project=Files.createTempFile("velyntora-ora-",".vlycore");try{if(!DrawingView.nativeProjectPath(project.toString(),true))throw new IOException("No se puede leer el proyecto");try(InputStream in=Files.newInputStream(project);OutputStream out=Files.newOutputStream(path)){OpenRasterArchive.write(in,out,(width,height,pixels,stream)->ImageIO.write(image(width,height,pixels),"png",stream));}}finally{Files.deleteIfExists(project);}return;}
 try(OutputStream out=Files.newOutputStream(path)){RasterFileWriter.RowSource rows=(y,pixels)->image.getRGB(0,y,w,1,pixels,0,w);switch(ext){case "tga":case "bmp":RasterFileWriter.write(out,w,h,ext.equals("tga"),rows);return;case "ppm":case "pnm":PpmCodec.write(out,w,h,rows);return;case "ico":IcoCodec.write(out,w,h,rows);return;default:if(!ImageIO.write(image,ext.equals("jpg")?"jpeg":ext,out))throw new IOException("Exportador no disponible: "+ext);}}
 }
}
