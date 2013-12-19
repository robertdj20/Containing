/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package containing.Platform;

import containing.Platform.Platform.Positie;
import containing.Road.Road;
import containing.Vector3f;

public class StoragePlatformOrientation {
    
    private final Positie positie;
    private final Road road;
    private final Vector3f entrypoint;
    private final Vector3f exitpoint;
    
    public StoragePlatformOrientation(Positie positie, Road road, Vector3f entrypoint, Vector3f exitpoint) {
        this.positie = positie;
        this.road = road;
        this.entrypoint = entrypoint;
        this.exitpoint = exitpoint;
    }
    
    public Positie getPositie() {
        return positie;
    }
    
    public Road getRoad() {
        return road;
    }
    
    public Vector3f getEntrypoint() {
        return entrypoint;
    }
    
    public Vector3f getExitpoint() {
        return exitpoint;
    }
    
}
