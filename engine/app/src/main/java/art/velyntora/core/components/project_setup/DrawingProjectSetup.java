package art.velyntora.core;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.*;
import android.view.View;
import android.widget.*;

/** Creates illustrations; resizing existing content remains in document/dimensions. */
public final class DrawingProjectSetup {
  private final Activity host;
  private final MainActivity editor;
  private final Submit submit;
  private interface Submit { boolean create(int width,int height); }
  public interface OnCreate { void create(int width,int height); }

  public static AlertDialog showFromStart(Activity host,OnCreate onCreate){
    return new DrawingProjectSetup(host,null,800,800,(width,height)->{
      onCreate.create(width,height);return true;
    }).show();
  }
  private final EditText width,height;
  private final CheckBox linked;
  private final Spinner presets;
  private final DrawingPresets.Preset[] catalog=DrawingPresets.all();
  private int ratioWidth,ratioHeight,lastEdited;
  private boolean updating;
  DrawingProjectSetup(MainActivity host){
    this(host,host,host.readDrawing().documentWidth(),host.readDrawing().documentHeight(),(width,height)->{
      if(!host.readDrawing().newDocument(width,height))return false;
      host.markDocumentClean("Sin título");return true;
    });
  }
  private DrawingProjectSetup(Activity host,MainActivity editor,int initialWidth,int initialHeight,Submit submit){
    this.host=host;this.editor=editor;this.submit=submit;
    width=field("Ancho en píxeles",initialWidth);
    height=field("Alto en píxeles",initialHeight);
    ratioWidth=initialWidth;ratioHeight=initialHeight;
    linked=new CheckBox(host);linked.setText("Mantener proporciones");
    presets=new Spinner(host);presets.setContentDescription("Presets de dibujo");
  }
  private TextView text(String label){
    TextView view=new TextView(host);view.setText(label);view.setTextSize(12);return view;
  }
  private void message(String message){Toast.makeText(host,message,Toast.LENGTH_SHORT).show();}
  private EditText field(String description,int value){
    EditText field=new EditText(host);field.setInputType(InputType.TYPE_CLASS_NUMBER);field.setSingleLine(true);
    field.setContentDescription(description);field.setText(Integer.toString(value));return field;
  }
  private int number(EditText field){return Integer.parseInt(field.getText().toString());}
  private TextWatcher watch(int axis){return new TextWatcher(){
    public void beforeTextChanged(CharSequence text,int start,int count,int after){}
    public void onTextChanged(CharSequence text,int start,int before,int count){}
    public void afterTextChanged(Editable text){
      if(updating)return;
      lastEdited=axis;presets.setSelection(0);width.setError(null);height.setError(null);
      if(!linked.isChecked())return;
      updating=true;
      try{
        DocumentDimensions size=dimensions();
        if(axis==0)height.setText(Integer.toString(size.height));else width.setText(Integer.toString(size.width));
      }catch(IllegalArgumentException error){(axis==0?width:height).setError("Introduce un tamaño válido que conserve la proporción (hasta 6000 px)");}
      finally{updating=false;}
    }
  };}
  private DocumentDimensions dimensions(){
    if(!linked.isChecked())return new DocumentDimensions(number(width),number(height));
    return lastEdited==0?DocumentDimensions.fromWidth(ratioWidth,ratioHeight,number(width)):
        DocumentDimensions.fromHeight(ratioWidth,ratioHeight,number(height));
  }
  private void apply(DrawingPresets.Preset preset){
    int[] size=preset.compatibleSize();updating=true;
    try{width.setText(Integer.toString(size[0]));height.setText(Integer.toString(size[1]));
      width.setError(null);height.setError(null);ratioWidth=size[0];ratioHeight=size[1];lastEdited=0;
    }finally{updating=false;}
  }
  AlertDialog show(){
    LinearLayout form=new LinearLayout(host);form.setOrientation(LinearLayout.VERTICAL);
    form.addView(text("Tamaño del lienzo"));
    form.addView(text("Ancho (px)"));form.addView(width);
    form.addView(text("Alto (px)"));form.addView(height);
    form.addView(linked);
    form.addView(text("Presets de dibujo"));
    String[] labels=new String[catalog.length+1];labels[0]="Personalizado";
    for(int i=0;i<catalog.length;i++)labels[i+1]=catalog[i].toString();
    ArrayAdapter<String> adapter=new ArrayAdapter<>(host,android.R.layout.simple_spinner_item,labels);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);presets.setAdapter(adapter);
    form.addView(presets);
    form.addView(text("Límite: 6000 px por lado y 36 millones de píxeles. Los formatos mayores requieren reducción proporcional."));
    if(editor!=null)form.addView(text("Se creará una ilustración nueva. Guarda primero el dibujo actual."));
    width.addTextChangedListener(watch(0));height.addTextChangedListener(watch(1));
    linked.setOnCheckedChangeListener((button,checked)->{
      if(!checked)return;
      try{DocumentDimensions current=new DocumentDimensions(number(width),number(height));ratioWidth=current.width;ratioHeight=current.height;lastEdited=0;}
      catch(IllegalArgumentException error){linked.setChecked(false);width.setError("Introduce primero un ancho y alto válidos");}
    });
    presets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
      public void onNothingSelected(AdapterView<?> parent){}
      public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
        if(position==0||updating)return;
        DrawingPresets.Preset preset=catalog[position-1];
        if(preset.supported()){apply(preset);return;}
        int[] size=preset.compatibleSize();
        new AlertDialog.Builder(host).setTitle("Ajustar "+preset.name)
          .setMessage("El tamaño original es "+preset.width+" × "+preset.height+" px y supera el límite de 6000 px por lado. Puedes usar "+size[0]+" × "+size[1]+" px conservando la proporción.")
          .setPositiveButton("Usar tamaño compatible",(dialog,which)->apply(preset))
          .setNegativeButton("Cancelar",(dialog,which)->presets.setSelection(0))
          .setOnCancelListener(dialog->presets.setSelection(0)).show();
      }
    });
    ScrollView scroll=new ScrollView(host);scroll.addView(form,new ScrollView.LayoutParams(-1,-2));
    AlertDialog dialog=new AlertDialog.Builder(host).setTitle("Crear ilustración")
      .setView(scroll).setNegativeButton("Cancelar",null)
      .setPositiveButton("Crear ilustración",null).create();
    dialog.show();
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view->{
      if(editor!=null&&editor.readProjectProgress()!=null){message("Espera a que termine la operación actual");return;}
      try{
        DocumentDimensions size=dimensions();
        if(!submit.create(size.width,size.height)){message("No se pudo crear la ilustración con la memoria disponible");return;}
        dialog.dismiss();
      }catch(IllegalArgumentException error){
        String message=error instanceof NumberFormatException?"Introduce ancho y alto válidos en píxeles":error.getMessage();
        (lastEdited==0?width:height).setError(message);message(message);
      }
    });
    return dialog;
  }
}
