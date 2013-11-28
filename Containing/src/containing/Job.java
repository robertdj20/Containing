package containing;

import containing.Vehicle.ExternVehicle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class Job 
{
    private Date date;
    private float departureTime;
    private Container.TransportType vehicleType;
    private List<Container> Containers;
    private ExternVehicle TargetVehicle;
    
    public Job(Date date, float departureTime, Container.TransportType vehicleType) 
    {
        this.date = date;
        this.departureTime = departureTime;
        this.vehicleType = vehicleType;
        this.Containers = new ArrayList<>();
    }
    
    public void addContainer(Container container)
    {
        Containers.add(container);
    }

    public Date getDate() 
    {
        return date;
    }

    public float getDepartureTime() 
    {
        return departureTime;
    }

    public Container.TransportType getVehicleType() 
    {
        return vehicleType;
    }

    public List<Container> getContainers() 
    {
        return Containers;
    }

    public ExternVehicle getTargetVehicle() 
    {
        return TargetVehicle;
    }

    public void setTargetVehicle(ExternVehicle TargetVehicle) 
    {
        this.TargetVehicle = TargetVehicle;
    }
    
    public static Stack<Job> sortOutGoingJobs(List<Job> Jobs)
    {
        Stack JobStack = new Stack<>();
        
        if (Jobs.size() > 0)
        {
            Job LatestJob = Jobs.get(0);
        
            while (Jobs.size() > 0)
            {
                for (Job j : Jobs)
                {
                    if (j.date.after(LatestJob.date) && j.departureTime > LatestJob.getDepartureTime())
                    {
                        LatestJob = j;
                    }
                }
                
                JobStack.push(LatestJob);
                Jobs.remove(LatestJob);
            }
        }
        
        return JobStack;
    }
}