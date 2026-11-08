package app.velyntora.editor;

import android.app.Activity;
import android.net.Uri;
import art.velyntora.core.DrawingProjectSetup;
import art.velyntora.core.EditorEntry;

/** Connects the home actions to the editor once the user chooses a canvas or file. */
public final class EditorNavigator {
    private EditorNavigator(){}
    public static void create(Activity host){
        DrawingProjectSetup.showFromStart(host,(width,height)->host.startActivity(EditorEntry.create(host,width,height)));
    }
    public static void open(Activity host,Uri image){host.startActivity(EditorEntry.open(host,image));}
}
