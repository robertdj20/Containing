package containing;

import containing.Platform.BargePlatform;
import containing.Platform.Platform;
import containing.Platform.SeashipPlatform;
import containing.Platform.StoragePlatform;
import containing.Platform.TrainPlatform;
import containing.Platform.TruckPlatform;
import containing.Vehicle.AGV;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Port implements Serializable
{
    private int ID = 0;
    private List<Platform> Platforms;
    //private List<AGV> aGVs;
    private StoragePlatform storagePlatform;
    

    public Port() 
    {
        this.Platforms = new ArrayList<>();
        Settings.messageLog.AddMessage("Created Harbor Object with id: " + ID);

        Platforms.add(new BargePlatform(new Vector3f(3, 0, 0)));
        Platforms.add(new SeashipPlatform(new Vector3f(0, 0, 0)));
        Platforms.add(new TrainPlatform(new Vector3f(-42.57f, 0, -82.15f)));
        Platforms.add(new TruckPlatform(new Vector3f(0, 0, 0)));
        
        storagePlatform = new StoragePlatform(new Vector3f(0,0,0));
        //aGVs = storagePlatform.getAllCreatedAgvs();
        Platforms.add(storagePlatform);
        
        
    }

    public void update() 
    {
        for (Platform P : Platforms) 
        {
            P.update();
        }
    }

    public int getID() 
    {
        return ID;
    }

    public StoragePlatform getStoragePlatform() 
    {
        return storagePlatform;
    }

    public List<Platform> getPlatforms() 
    {
        return Platforms;
    }

    @Override
    public String toString() {
        return "Port{" + "ID=" + ID + ", Platforms=" + Platforms + ", storagePlatform=" + storagePlatform + '}';
    }
}
