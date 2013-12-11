package containing.Platform;

import containing.Container.TransportType;
import containing.Dimension2f;
import containing.ParkingSpot.BargeSpot;
import containing.ParkingSpot.TrainSpot;
import containing.Settings;
import containing.Vector3f;
import containing.Vehicle.ExternVehicle;
import containing.Vehicle.TrainCrane;

/**
 * TrainPlatform.java
 * The dynamic axis for cranes is: Z
 * @author Minardus
 */
public class TrainPlatform extends Platform {
    
    private final float WIDTH          = 100f*Settings.METER;  // ???
    private final float LENGTH         = 1643f*Settings.METER; // ???
    private final int MAX_VEHICLES     = 1;
    private final int CRANES           = 4;
    
    private final float AGV_OFFSET     = 0f;
    private final float CRANE_OFFSET   = 25f*Settings.METER;   // ???
    private final float VEHICLE_OFFSET = 0f;
    
    public TrainPlatform(Vector3f position)
    {
        super(position);
        setDimension(new Dimension2f(WIDTH, LENGTH));
        setAxis(DynamicAxis.Z);
        setEntrypoint(new Vector3f(WIDTH,0,0));
        setExitpoint(new Vector3f(WIDTH,0,LENGTH));
        setRoad();
        setTransportType(TransportType.Train);
        setMaxAgvQueue(CRANES);
        createAgvSpots(new Vector3f(CRANE_OFFSET + TrainCrane.length + AGV_OFFSET, 0, 0));
        createExtVehicleSpots();
        createCranes();
        log("Created TrainPlatform object: " + toString());
    }

    @Override
    protected final void createCranes() 
    {
        float space = LENGTH / (float)CRANES;
        float offset = (space / 2f) - ( TrainCrane.width / 2f);
        for(int i = 0; i < CRANES; i++) 
        {
            Vector3f cranePosition = new Vector3f(CRANE_OFFSET, 0, space*i + offset);
            cranes.add(new TrainCrane(cranePosition, this));
        }
    }
    
    @Override
    protected final void createExtVehicleSpots() 
    {
        float space = LENGTH / (float)MAX_VEHICLES;
        float offset = (space / 2f) - (TrainSpot.length / 2f);
        for(int i = 0; i < MAX_VEHICLES; i++)
        {
            Vector3f spotPosition = new Vector3f(VEHICLE_OFFSET + getPosition().x,0,space*i + offset - getPosition().z);
            extVehicleSpots.add(new TrainSpot(spotPosition));
        }
    }
    
    @Override
    public void update()
    {
        super.update();
        
        /* if platform is free, request next job */
        if(state.equals(State.FREE))
            requestNextJob();
        
        if(hasExtVehicle()) {
            state = State.UNLOAD;
        } else if(jobs.size() > 0) {
            state = State.LOAD;
        } else {
            state = State.FREE;
        }
        
        /* UNLOAD EXTERNAL VEHICLE */
        if(state.equals(State.UNLOAD))
        {
            unload();
        }
        
        /* LOAD EXTERNAL VEHICLE */
        if(state.equals(State.LOAD))
        {
            load(this);
        }
    }
    
}