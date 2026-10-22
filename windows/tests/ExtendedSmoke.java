import art.velyntora.core.*;
import app.velyntora.windows.document.DocumentIO;
import java.nio.file.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
public final class ExtendedSmoke {
 static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
 static BufferedImage sample(){BufferedImage p=new BufferedImage(32,24,2);for(int y=0;y<24;y++)for(int x=0;x<32;x++)p.setRGB(x,y,0xff000000|((x*8)<<16)|((y*10)<<8)|64);return p;}
 public static void run(Path folder)throws Exception{
  DrawingView.nativeImageStorage(folder.toString());BufferedImage image=sample();check(DrawingView.nativeLoadBitmap(32,24,image.getRGB(0,0,32,24,null,0,32)),"format fixture");
  for(String ext:new String[]{"png","jpg","bmp","tga","ppm","ico","tiff","gif","webp"}){Path file=folder.resolve("format."+ext);DocumentIO.export(file);BufferedImage restored=DesktopFormats.read(file);check(restored!=null&&restored.getWidth()==32&&restored.getHeight()==24,"roundtrip "+ext);}
  Path ora=folder.resolve("layers.ora");check(DrawingView.nativeAddLayer(),"ORA added layer");DocumentIO.export(ora);check(DrawingView.nativeCreate(8,8),"ORA reset");DocumentIO.open(ora);check(DrawingView.nativeWidth()==32&&DrawingView.nativeLayerCount()==2,"ORA preserves layers");
  Path svg=folder.resolve("vector.svg");Files.writeString(svg,"<svg xmlns='http://www.w3.org/2000/svg' width='32' height='24'><rect width='32' height='24' fill='red'/></svg>");check(DesktopFormats.read(svg).getRGB(3,3)==0xffff0000,"SVG import");
  ByteArrayOutputStream encoded=new ByteArrayOutputStream();javax.imageio.ImageIO.write(image,"png",encoded);byte[] source=encoded.toByteArray();
  check(DrawingView.nativeBeginPlacedImage("Texto",1),"object staging");int[] style={16,0xff204080,0,1,32};float[] transform={0,0,32,24,16,12,32,24,0};byte[] content="Texto 日本".getBytes(java.nio.charset.StandardCharsets.UTF_8);check(DrawingView.nativeStageObject(source,transform,content,style),"retained text/image");check(DrawingView.nativePlacedImageTile(0,0,32,24,image.getRGB(0,0,32,24,null,0,32)),"object raster preview");check(DrawingView.nativeCommitPlacedImage(),"object commit");int index=DrawingView.nativeActiveLayer();check(Arrays.equals(DrawingView.nativeTextContent(index),content),"retained text bytes");
  Path project=folder.resolve("editable.vlycore");DocumentIO.saveProject(project);check(DrawingView.nativeCreate(8,8),"object reset");DocumentIO.open(project);index=DrawingView.nativeActiveLayer();check(Arrays.equals(source,DrawingView.nativeObjectSource(index))&&Arrays.equals(content,DrawingView.nativeTextContent(index)),"editable roundtrip");check(DrawingView.nativeBeginPlacedImageEdit(index,1),"reedit staging");DrawingView.nativeCancelPlacedImage();
  check(DrawingView.nativeLoadBitmap(32,24,image.getRGB(0,0,32,24,null,0,32)),"AI fixture");int[] original=DrawingView.nativeRemoveSource();byte[] mask=new byte[32*24];for(int y=8;y<16;y++)for(int x=12;x<20;x++)mask[y*32+x]=1;int[] result;try(OfflineRemove ai=new OfflineRemove()){result=ai.run(original,mask,32,24);}check(result.length==original.length,"real offline MI-GAN output");for(int i=0;i<result.length;i++)if(mask[i]==0)check(result[i]==original[i],"AI preserves unmasked pixels");long revision=DrawingView.nativeRevision();check(!DrawingView.nativeRemoveCommit(result,mask,32,24,0,revision+1),"stale AI result rejected");check(DrawingView.nativeRemoveCommit(result,mask,32,24,0,revision),"AI transaction");
  check(DrawingView.nativePointerPressure()>0,"Windows Ink pressure API");System.out.println("Extended Windows passed: real offline MI-GAN, nine raster formats, SVG, layered ORA, retained text/images, Unicode and pressure API.");
 }
}
