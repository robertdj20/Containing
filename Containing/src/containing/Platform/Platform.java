package containing.Platform;

import containing.Container;
import containing.Container.TransportType;
import containing.Controller;
import containing.Dimension2f;
import containing.Exceptions.AgvQueueSpaceOutOfBounds;
import containing.Exceptions.AgvSpotOutOfBounds;
import containing.Exceptions.CargoOutOfBoundsException;
import containing.Exceptions.ContainerNotFoundException;
import containing.Exceptions.NoJobException;
import containing.Exceptions.VehicleOverflowException;
import containing.Job;
import containing.ParkingSpot.AgvSpot;
import containing.ParkingSpot.ParkingSpot;
import containing.Settings;
import containing.Vector3f;
import containing.Vehicle.AGV;
import containing.Vehicle.Crane;
import containing.Vehicle.ExternVehicle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Platform implements Serializable {
    
    public enum State { FREE, LOAD, UNLOAD }
    protected enum DynamicAxis { X, Z }
    
    protected final float AGVSPOT_OFFSET = 0f;
    
    private static int idCounter = 1;
    
    private final int id;
    private final Vector3f position;
    private Dimension2f dimension;
    protected State state;
    protected DynamicAxis axis;
    private Vector3f entrypoint = null;
    private Vector3f exitpoint = null;
    private TransportType transportType = null;
    
    protected List<AgvSpot> agvSpots;
    protected List<Crane> cranes;
    protected List<ParkingSpot> extVehicleSpots;
    
    protected Queue<Job> jobs = null;
    protected Queue<AGV> agvQueue = null; //is de bedoeling dat er een wachtrij van AGV's ontstaat
    protected int maxAgvQueue = 1;
    
    protected int time = 0;
    
    public Platform(Vector3f position) 
    {
        id = idCounter++;
        this.position = position;
        state = State.FREE;
        /* initialize arraylists */
        agvSpots = new ArrayList<>();
        cranes = new ArrayList<>();
        extVehicleSpots = new ArrayList<>();
        jobs = new LinkedList<>();
        agvQueue = new LinkedList<>();
    }
    
    protected boolean hasExtVehicle()
    {
        for(ParkingSpot vs : extVehicleSpots)
            if(!vs.isEmpty())
                return true;
        return false;
    }
    
    public void load(final Platform instance)
    {
        /* platform -> requestNextContainer -> agv from storagePlatform -> crane -> externVehicle */
        Crane craneTemp = null;
        for(Crane c : cranes)
        {
            if(c.getIsAvailable())
                craneTemp = c;
        }
        
        final Crane crane = craneTemp;
        new Thread() {
            
            @Override
            public void run()
            {
                int index = 0;
                boolean hasContainer = false;
                for(int i = 0; i < jobs.peek().getContainers().size(); i++)
                {
                    if(Controller.RequestNextContainer(jobs.peek().getContainers().get(index), instance))
                    {
                        hasContainer = true;
                        break;
                    }
                    index++;
                }
                
                if(hasContainer)
                {
                    Container container = jobs.peek().getContainers().remove(index);
                    try
                    {
                        crane.load(container);
                        extVehicleSpots.get(0).getParkedVehicle().load(crane.unload());
                    }
                    catch(ContainerNotFoundException | CargoOutOfBoundsException | VehicleOverflowException e)
                    {
                        System.out.println(e.getMessage());
                        this.interrupt();
                    }
                    
                }
            }
     
        }.start();
        
    }
    
    public void unload()
    {   
        /* determine rows on vehicle */
        int rows = 0;
        for(Container c : extVehicleSpots.get(0).getParkedVehicle().getCargo()) 
        {
            rows = (int)c.getArrivalPosition().x + 1;
        }
        int rowsPerCrane = rows / cranes.size();
        
        /* get available crane */
        Crane craneTemp = null;
        int craneId = 0; 
        for(Crane c : cranes)
        {
            if(c.getIsAvailable())
                craneTemp = c;
            craneId++;
        }
        
        /* get row with highest priority */
        int startIndex = craneId + (rowsPerCrane - 1);
        long highestPriority = 0L;
        int rowToGive = 0;
        for(int i = startIndex; i < startIndex + rowsPerCrane; i++) 
        {
            for(Container c : extVehicleSpots.get(0).getParkedVehicle().getCargo()) 
            {
                if(c.getArrivalPosition().x == i) 
                {
                    long time = Settings.getTimeStamp(c.getDepartureDate(), c.getDepartureTimeFrom());
                    if(highestPriority == 0L)
                    {
                        highestPriority = time;
                    }
                    else
                    {
                        if(time < highestPriority) {
                            rowToGive = (int)c.getArrivalPosition().x;
                            highestPriority = time;
                        }
                    }      
                }
            }
        }
        
        /* give available crane job */
        final Crane crane = craneTemp;
        final int row = rowToGive;
        if(crane != null)
        {
            final AGV agv = agvQueue.poll();
            if(agv != null)
            {
                new Thread() {

                    @Override
                    public void run()
                    {
                        try
                        {
                            crane.load(((ExternVehicle)(extVehicleSpots.get(0).getParkedVehicle())), row);
                        } 
                        catch(CargoOutOfBoundsException | ContainerNotFoundException | VehicleOverflowException e) 
                        {
                            System.out.println(e.getMessage());
                            this.interrupt();
                        } catch (Exception ex) 
                        {
                            Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }.start();
            }
        }
    }
    
    protected void unloadAGV(Container container)
    {
        //todo
    }
    
    protected void requestNextContainer()
    {
        //Controller.RequestNextContainer(null, this);
    }
    
    protected void requestNextJob()
    {
        try 
        {
            jobs.add(Controller.RequestNewJob(this));
        } 
        catch(NoJobException e){/* ignore */}
    }
    
    private void agvToQueue(AGV agv) throws AgvQueueSpaceOutOfBounds
    {
        if(agvQueue.size() < maxAgvQueue)
            agvQueue.add(agv);
        else
            throw new AgvQueueSpaceOutOfBounds("No space available for AGV in agvQueue");
    }
    
    protected void addAgvToQueue(AGV agv)
    {
        try
        {
            agvToQueue(agv);
        }
        catch(AgvQueueSpaceOutOfBounds e){/* ignore */}
    }
    
    protected void createAgvSpots(Vector3f baseposition)
    {   
        float x = baseposition.x;
        float z = baseposition.z;
        
        for(int i = 0; i < getAgvSpotAmount(); i++)
        {
            Vector3f spotPosition;
            float currentWidth = AgvSpot.width*i+AGVSPOT_OFFSET;
            spotPosition = axis.equals(DynamicAxis.X) ? new Vector3f(currentWidth,0,z) : new Vector3f(x,0,currentWidth);
            agvSpots.add(new AgvSpot(spotPosition));
        }
    }
    
    protected int getAgvSpotAmount()
    {
        return (int)((float)(axis.equals(DynamicAxis.X) ? dimension.width : dimension.length) / AgvSpot.width);
    }
    
    protected AgvSpot getAgvSpot(int spot) throws AgvSpotOutOfBounds
    {
        if(agvSpots.size() > spot)
            return agvSpots.get(spot);
        throw new AgvSpotOutOfBounds("The requested AGV Spot does not exist");
    }
    
    public boolean hasFreeParkingSpot()
    {
        for(AgvSpot spot : agvSpots)
            if(spot.isEmpty())
                return true;
        return false;
    }
    
    public Vector3f getPosition()
    {
        return position;
    }
    
    public Dimension2f getDimension() 
    {
        return dimension;
    }
    
    protected void setDimension(Dimension2f dimension)
    {
        this.dimension = dimension;
    }
    
    public DynamicAxis getAxis()
    {
        return axis;
    }
    
    protected void setAxis(DynamicAxis axis) 
    {
        this.axis = axis;
    }
    
    public Vector3f getEntrypoint()
    {
        return entrypoint;
    }
    
    protected void setEntrypoint(Vector3f entrypoint)
    {
        this.entrypoint = entrypoint;
    }
    
    public Vector3f getExitpoint() {
        return exitpoint;
    }
    
    protected void setExitpoint(Vector3f exitpoint)
    {
        this.exitpoint = exitpoint;
    }
    
    public TransportType getTransportType() {
        return transportType;
    }
    
    protected void setTransportType(TransportType transportType)
    {
        this.transportType = transportType;
    }
    
    protected void setMaxAgvQueue(int max)
    {
        //calculate how much AGV's fit in queue
        maxAgvQueue = max;
    }
    
    public int getId()
    {
        return id;
    }
    
    protected abstract void createCranes();
    protected abstract void createExtVehicleSpots();
    
    public abstract void update();
    
    @Override
    public String toString() {
        return String.format("[%d, width=%.1f, length=%.1f]", id, dimension.width, dimension.length);
    }
    
    protected void log(String msg)
    {
        Settings.messageLog.AddMessage(msg);
    }
    
}