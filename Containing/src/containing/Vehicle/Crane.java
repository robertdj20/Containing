/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package containing.Vehicle;

import containing.Command;
import containing.CommandHandler;
import containing.Container;
import containing.Exceptions.CargoOutOfBoundsException;
import containing.Exceptions.ContainerNotFoundException;
import containing.Exceptions.VehicleOverflowException;
import containing.Platform.Platform;
import containing.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Robert
 */
public abstract class Crane extends InternVehicle {
    
    protected int timeCounter = 0;
    protected final static int CAPICITY = 1;
    protected int counter;
    protected final float SECURETIME = 0.5f;
    
    //testvariables -> for every crane and container different
    private final int liftTimeMin = 0;
    private final float liftTimeMax = 3.5f * 60;
    private final float dropTimeMin = 0;
    private final float dropTimeMax = 3.5f * 60;
    private final float moveContainerSpeed = 5;
    protected final int maxSpeedUnloaded = 4;
    private final int resetTime = 50;
    protected final int metersToNextAgvSpot = 10;
    
    public float width;
    public float length;
    
    public Crane(Vector3f startPosition, Platform platform, Type type, float width, float length){
        super(CAPICITY, startPosition, platform, type);
        this.width = width;
        this.length = length;
    }
    
    public void unload(AGV agv) throws VehicleOverflowException, ContainerNotFoundException, CargoOutOfBoundsException{
        try{
        agv.load(super.unload());
        }
        catch(Exception e){throw e;}
        //platform moet agv volgende route geven
    }

    
    public void load(Container container) throws VehicleOverflowException, CargoOutOfBoundsException{ //container from extern verhicle
        try{
        super.load(container);
        }
        catch(Exception e){throw e;}
        //update: while (this.timeCounter < starttime + liftTimeMax + moveContainerSpeed * 2){} //aan het laden
        //roep evt nieuwe agv aan (op zelfde parkeerplaats of op parkeerplaats opzij [moet platform doen]
    }
    
    public void move(int direction) //-1 is left 1 is right
    {
        //override this method for truckcrane
        List<Vector3f> route = new ArrayList<>();
        route.add(this.position);
        if (direction == -1)
        {
            route.add(new Vector3f(this.position.x - Container.depth, this.position.y, this.position.z));
        }
        
        else 
        {
            route.add(new Vector3f(this.position.x + Container.depth, this.position.y, this.position.z));
        }
        
        this.currentSpeed = (this.isLoaded) ? Vehicle.maxSpeedLoaded : Vehicle.maxSpeedUnloaded;
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", this.getID());
        map.put("vehicleType", this.getVehicleType());
        map.put("motionPath", route); 
        map.put("speed", currentSpeed);
            
        CommandHandler.addCommand(new Command("moveCrane", map));
    }
    
    public void reset(){
        /*
        int starttime = this.timeCounter;
        while (this.timeCounter < starttime + resetTime){} //kraan gaat t
        //evt ga opzij [moet platform doen]
        this.isAvailable = true;
        this.timeCounter = 0;
        */
    }
    

    
    
    
    //methode opzij in subclasses waarvoor geldig is
}
