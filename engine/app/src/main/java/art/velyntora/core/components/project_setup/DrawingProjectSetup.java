package art.velyntora.core;

import android.app.AlertDialog;
import android.text.*;
import android.view.View;
import android.widget.*;

/** Creates illustrations; resizing existing content remains in document/dimensions. */
final class DrawingProjectSetup {
  private final MainActivity host;
  private final EditText width,height;
  private final CheckBox linked;
  private final Spinner presets;
  private final DrawingPresets.Preset[] catalog=DrawingPresets.all();
  private int ratioWidth,ratioHeight,lastEdited;
  private boolean updating;
  DrawingProjectSetup(MainActivity host){
    this.host=host;
    width=field("Ancho en píxeles",host.readDrawing().documentWidth());
    height=field("Alto en píxeles",host.readDrawing().documentHeight());
    ratioWidth=host.readDrawing().documentWidth();ratioHeight=host.readDrawing().documentHeight();
    linked=new CheckBox(host);linked.setText("Mantener proporciones");
    presets=new Spinner(host);presets.setContentDescription("Presets de dibujo");
  }
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
    form.addView(host.text("Tamaño del lienzo"));
    form.addView(host.text("Ancho (px)"));form.addView(width);
    form.addView(host.text("Alto (px)"));form.addView(height);
    form.addView(linked);
    form.addView(host.text("Presets de dibujo"));
    String[] labels=new String[catalog.length+1];labels[0]="Personalizado";
    for(int i=0;i<catalog.length;i++)labels[i+1]=catalog[i].toString();
    ArrayAdapter<String> adapter=new ArrayAdapter<>(host,android.R.layout.simple_spinner_item,labels);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);presets.setAdapter(adapter);
    form.addView(presets);
    form.addView(host.text("Límite: 6000 px por lado y 36 millones de píxeles. Los formatos mayores requieren reducción proporcional."));
    form.addView(host.text("Se creará una ilustración nueva. Guarda primero el dibujo actual."));
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
    AlertDialog dialog=new AlertDialog.Builder(host).setTitle("Crear ilustración")
      .setView(host.scrollForm(form)).setNegativeButton("Cancelar",null)
      .setPositiveButton("Crear ilustración",null).create();
    dialog.setOnShowListener(ignored->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view->{
      if(host.readProjectProgress()!=null){host.message("Espera a que termine la operación actual");return;}
      try{
        DocumentDimensions size=dimensions();
        if(!host.readDrawing().newDocument(size.width,size.height)){host.message("No se pudo crear la ilustración con la memoria disponible");return;}
        host.markDocumentClean("Sin título");dialog.dismiss();
      }catch(IllegalArgumentException error){
        String message=error instanceof NumberFormatException?"Introduce ancho y alto válidos en píxeles":error.getMessage();
        (lastEdited==0?width:height).setError(message);host.message(message);
      }
    }));
    dialog.show();return dialog;
  }
}
