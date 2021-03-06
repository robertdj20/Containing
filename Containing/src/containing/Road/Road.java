package containing.Road;

import containing.Container.TransportType;
import containing.ParkingSpot.ParkingSpot;
import containing.Platform.Platform;
import static containing.Platform.Platform.Positie.LINKS;
import static containing.Platform.Platform.Positie.ONDER;
import static containing.Platform.Platform.Positie.RECHTS;
import containing.Platform.StoragePlatform;
import containing.Platform.StoragePlatformOrientation;
import containing.Vector3f;
import containing.Vehicle.AGV;
import containing.Vehicle.Crane;
import containing.Vehicle.ExternVehicle;
import containing.Vehicle.Vehicle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Road implements Serializable 
{
    float roadWidth = AGV.width + AGV.width * 0.25f;
    
    volatile List<Vector3f> track = new ArrayList<>();
     
    public Road(List<Vector3f> roadPoints){ //vaste punten voor main road = 4, voor platform road = 2
        for (Vector3f v : roadPoints){
            track.add(v);
        }
     
        //Collections.sort(track); //links onder links boven rechts boven rechtsonder
        //for (Vector3f v : entryPoints){this.createCorrespondingWaypoint(v);}
    } 
    
    

    private synchronized Vector3f createCorrespondingWaypoint(Vector3f point){
        System.out.println("NR WAYPOINT: " + this.track.size());
        System.out.println("CW_BEFORE: " + point);
        if (track.size() == 4){ //function only relevant for mainroad/rectangle road
            //linksboven
            if (point.x <= track.get(0).x){ System.out.println("CW_AFTER0: " + new Vector3f(track.get(0).x, point.y, point.z)); return new Vector3f(track.get(0).x, point.y, point.z); }
            //rechtsonder
            if (point.x >= track.get(2).x) {System.out.println("CW_AFTER1: " + new Vector3f(track.get(2).x, point.y, point.z)); return new Vector3f(track.get(2).x, point.y, point.z); }
            //linksonder
            if (point.z >= track.get(1).z){System.out.println("CW_AFTER2: " + new Vector3f(point.x, point.y, track.get(1).z)); return new Vector3f(point.x, point.y, track.get(1).z); } 
            //was1
            if (point.z <= track.get(0).z){System.out.println("CW_AFTER3: " + new Vector3f(point.x, point.y, track.get(0).z)); return new Vector3f(point.x, point.y, track.get(0).z);} 
            //if (point.z < track.get(1).z){System.out.println("CW_AFTER4: " + new Vector3f(point.x, point.y, track.get(1).z)); return new Vector3f(point.x, point.y, track.get(1).z);} 
            
            if (Math.abs(track.get(0).z - point.z) < Math.abs(track.get(1).z - point.z)){System.out.println("CW_AFTER4: return closest z: "+ new Vector3f(point.x,point.y, track.get(0).z)); return new Vector3f(point.x,point.y, track.get(0).z);} //0}
            System.out.println("CW_AFTER4: return closest z: "+ new Vector3f(point.x,point.y, track.get(1).z));
            return new Vector3f(point.x,point.y,  track.get(1).z); //0
        
        }
        System.out.println("TRACK1: " + track.get(0));
        System.out.println("TRACK2: " + track.get(1));
        if (track.get(0).x == track.get(1).x){return new Vector3f(track.get(0).x, point.y, point.z);}
        else return new Vector3f(point.x, point.y, track.get(0).z);

    
    }
    
   
    
    
     public static float getPathLength(List<Vector3f> weg){
         
        if (weg.size() > 1){
            if (weg.get(0).x != weg.get(1).x){ return Math.abs(weg.get(1).x - weg.get(0).x) + getPathLength(weg.subList(1, weg.size()));}
            return Math.abs(weg.get(1).z - weg.get(0).z) + getPathLength(weg.subList(1, weg.size()));
        }   
        else return 0;
    }
    
    //allinclusive
    public Route getPathAllIn(Vehicle vehicle, ParkingSpot source, ParkingSpot destinationParkingSpot, Platform destinationPlatform, Road mainroad)
    {
        //source.UnparkVehicle();
        System.out.println("In function getPathAllIn");
        Route deel1 = vehicle.getCurrentPlatform().getRoad().getPathFromParkingSpotToPlatform(vehicle, source, vehicle.getCurrentPlatform().getExitpoint());
        Route deel2 = mainroad.getPathFromExitPointPlatformToEntryPointPlatform(vehicle, vehicle.getCurrentPlatform().getExitpoint(), destinationPlatform, mainroad);
        Route deel3 = destinationPlatform.getRoad().getPathFromEntryPointPlatformToParkingSpot(vehicle, destinationParkingSpot);
        
        List<Vector3f> track2 = new ArrayList<>();
   
            
            for (Vector3f v : deel1.getWeg()) { track2.add(v); }
            for (Vector3f v : deel2.getWeg()) { track2.add(v); }
            for (Vector3f v : deel3.getWeg()) { track2.add(v); }
            
            if (vehicle.getID() == 201 || vehicle.getID() == 103)
            {
                System.out.println("----------------------");
                System.out.println("Route nr: " + vehicle.routecounter);
                for (Vector3f v : deel1.getWeg() ) {
                    System.out.println("Deel 1: " + v);}
                for (Vector3f v : deel2.getWeg() ) {
                    System.out.println("Deel 2: " + v);}
                for (Vector3f v : deel3.getWeg() ) {
                    System.out.println("Deel 3: " + v);}
                
                System.out.println("----------------------");
            }
        
        Route route = new Route(track2, getPathLength(track2));
    
        route.destinationPlatform = destinationPlatform;
        route.destinationParkingSpot = destinationParkingSpot;
        if (destinationParkingSpot == null){
            System.out.println("NULL");}
        return route;
    }
    
      public Route getPathAllInVector(Vehicle vehicle, ParkingSpot source, Vector3f destinationVector, Platform destinationPlatform, Road mainroad)
    {
        System.out.println("In function getPathAllInVector");
        Route deel1 = vehicle.getCurrentPlatform().getRoad().getPathFromParkingSpotToPlatform(vehicle, source, vehicle.getCurrentPlatform().getExitpoint());
        Route deel2 = mainroad.getPathFromExitPointPlatformToEntryPointPlatform(vehicle, vehicle.getCurrentPlatform().getExitpoint(), destinationPlatform, mainroad);
        Route deel3 = destinationPlatform.getRoad().getPathFromEntryPointPlatformToVector(vehicle, destinationVector);

        List<Vector3f> track2 = new ArrayList<>();
        for (Vector3f v : deel1.getWeg()) { track2.add(v);}
        for (Vector3f v : deel2.getWeg()) { track2.add(v);}
        for (Vector3f v : deel3.getWeg()) { track2.add(v);}
        
                   if (vehicle.getID() == 201 || vehicle.getID() == 103)
            {
                System.out.println("----------------------");
                System.out.println("Route nr: " + vehicle.routecounter);
                for (Vector3f v : deel1.getWeg() ) {
                    System.out.println("Deel 1: " + v);}
                for (Vector3f v : deel2.getWeg() ) {
                    System.out.println("Deel 2: " + v);}
                for (Vector3f v : deel3.getWeg() ) {
                    System.out.println("Deel 3: " + v);}
                
                System.out.println("----------------------");
            }
        
        
        
       
        
        
        return new Route(track2, getPathLength(track2));
    }
   
    //van parkeerplaats op platform naar einde platform
    public Route getPathFromParkingSpotToPlatform(Vehicle vehicle, ParkingSpot source, Vector3f exitwayPlatform)
    {
        System.out.println("In function getPathFromParkingSpotToPlatform");
        List<Vector3f> track2 = new ArrayList<Vector3f>();
        track2.add(vehicle.getPosition());
        track2.add(this.createCorrespondingWaypoint(vehicle.getPosition()));
        track2.add(this.createCorrespondingWaypoint(exitwayPlatform));
        track2.add(exitwayPlatform);
        
        try
        {
            if(vehicle.getCurrentPlatform() instanceof StoragePlatform || vehicle.getCurrentPlatform() instanceof StoragePlatformOrientation)
                source.UnparkVehicle(); 
        }
        catch(Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
        
        //source.UnparkVehicle();
        vehicle.setPosition(exitwayPlatform);
        Route route = new Route(track2, getPathLength(track2));
        route.destinationParkingSpot = null;
        route.destinationPlatform = null;
        return route;
    }
    
     public Route moveToContainer(ExternVehicle ev, int column, Crane crane)
    {
         System.out.println("In function moveToContainer");
         //System.out.println("column : " + column);
         List<Vector3f> route = new ArrayList<>();
         route.add(crane.getPosition());
         Vector3f container = ev.getGrid()[column][0][0].getArrivalPosition();
         
         //System.out.println("ev.getPosition = " + ev.getPosition().toString());
        
         switch (ev.getCurrentPlatform().getAxis())
         {
             case X:
                 Vector3f haha = new Vector3f(ev.getPosition().x - column*1.5f, crane.getPosition().y, crane.getPosition().z);
                 route.add(haha); //??
                 //System.out.println("route x: " + haha.toString());
                 break;
             case Z:
                 if(ev.getCurrentPlatform().getTransportType() == TransportType.Train){
                    // hardcoded voor de trein nu ;( wagon is 1.5f en trein zelf ook
                    Vector3f hihi = new Vector3f(crane.getPosition().x, crane.getPosition().y, ev.getPosition().z - column*1.5f - 1.5f + 0.70f);
                    route.add(hihi); //??
                    //System.out.println("route z: " + hihi.toString());
                 }
                 else if(ev.getCurrentPlatform().getTransportType() == TransportType.Barge){
                    Vector3f hihi = new Vector3f(crane.getPosition().x, crane.getPosition().y, ev.getPosition().z - column+1.5f +1f);
                    route.add(hihi); //??
                 }

                 break;
             //caseY?  
             
                 
         }
         return new Route(route, getPathLength(route));
    }
    
    //van uitgang platform naar ingang andere platform
     public Route getPathFromExitPointPlatformToEntryPointPlatform(Vehicle vehicle, Vector3f sourcePlatformExitPoint,Platform destination, Road mainroad)
     {
         System.out.println("In function getPathFromExitPointPlatformToEntryPointPlatform");
         List<Vector3f> track2 = new ArrayList<Vector3f>();//this.track
         track2.add(sourcePlatformExitPoint);
       
         //System.out.println("sourcePlatformExitPoint: " + sourcePlatformExitPoint);
         track2.add(this.createCorrespondingWaypoint(sourcePlatformExitPoint));
         //System.out.println("sourcePlatformExitPointCW: " + this.createCorrespondingWaypoint(sourcePlatformExitPoint));
         track2.add(this.createCorrespondingWaypoint(destination.getEntrypoint()));
         //System.out.println("destinationEntryPointCW: " + this.createCorrespondingWaypoint(destination.getEntrypoint()));
         track2.add(destination.getEntrypoint());
         //System.out.println("destinationEntryPoint: " + destination.getEntrypoint());
   
         if (vehicle.getID() == 201 && vehicle.routecounter == 3)
         {
             System.out.println("Before deel 2");
             for (Vector3f v : track2)
             {
                 System.out.println("Deel 2 V: " + v);
             }
         }
         
         track2 = this.setPathCorrectOrder(track2, vehicle.getCurrentPlatform(), destination, mainroad);
         
               if (vehicle.getID() == 201 && vehicle.routecounter == 3)
         {
             System.out.println("After deel 2");
             for (Vector3f v : track2)
             {
                 System.out.println("Deel 2 V: " + v);
             }
         }
         vehicle.setCurrentPlatform(destination);
         vehicle.setPosition(destination.getEntrypoint());
         Route route = new Route(track2, getPathLength(track2));
         route.destinationPlatform = destination;
         route.destinationParkingSpot = null;
         return route;
         
         
     }
     
     //van ingang platform naar parkeerplaats
      public Route getPathFromEntryPointPlatformToParkingSpot(Vehicle vehicle, ParkingSpot ps)
      {
          System.out.println("In function getPathFromEntryPointPlatformToParkingSpot");
          List<Vector3f> track2 = new ArrayList<Vector3f>();//this.track
          track2.add(vehicle.getPosition());
          track2.add(this.createCorrespondingWaypoint(vehicle.getPosition()));
          track2.add(this.createCorrespondingWaypoint(this.createCorrespondingWaypoint(ps.getPosition())));
          track2.add(ps.getPosition());
          /*
          try
          {
          ps.ParkVehicle(vehicle);
          vehicle.setPosition(ps.getPosition());
          }
          catch(Exception e){
              Settings.messageLog.AddMessage(e.getMessage());
          }
          */
          return new Route(track2, getPathLength(track2));
      }
      
      
      
      public Route getPathToParkingsSpot(Vehicle vehicle, ParkingSpot ps)
      {
          System.out.println("In function getPathToParkingsSpot");
          List<Vector3f> track2 = new ArrayList<Vector3f>();
          track2.add(vehicle.getPosition());
          track2.add(this.createCorrespondingWaypoint(vehicle.getPosition()));
          track2.add(this.createCorrespondingWaypoint(ps.getPosition()));
          //track2.add(ps.getPosition());
          Route route = new Route(track2, getPathLength(track2));
          route.destinationPlatform = null;
          route.destinationParkingSpot = ps;
          return route;
          
      }
      
       public Route getPathFromEntryPointPlatformToVector(Vehicle vehicle, Vector3f destinationVector)
      {
          System.out.println("In function getPathFromEntryPointPlatformToVector");
          List<Vector3f> track2 = new ArrayList<Vector3f>();//this.track
          track2.add(vehicle.getPosition());
          track2.add(this.createCorrespondingWaypoint(vehicle.getPosition()));
          track2.add(this.createCorrespondingWaypoint(this.createCorrespondingWaypoint(destinationVector)));
          track2.add(destinationVector);
          vehicle.setPosition(destinationVector);
        
          return new Route(track2, getPathLength(track2));
          
      }
      
       public Route getPathExternVehicleExit(ParkingSpot ps, Vector3f v )
       {
           System.out.println("In function getPathExternVehicleExit");
           List<Vector3f> track2 = new ArrayList<Vector3f>();
           track2.add(ps.getPosition());
           track2.add(v);
           float length = this.getPathLength(track2);
           Route route = new Route(track2, length);
           route.destinationPlatform = null;
           route.destinationParkingSpot = null;
           ps.UnparkVehicle();
           return route;
           
       }
       
    public Route getPathExternVehicleEntry(ExternVehicle ev, ParkingSpot ps )
    {
        System.out.println("In function getPathExternVehicleEntry");
        List<Vector3f> track2 = new ArrayList<Vector3f>();
        track2.add(ev.getPosition());
        track2.add(ps.getPosition());
        float length = this.getPathLength(track2);
        Route route = new Route(track2, length);
        route.destinationPlatform = null;
        route.destinationParkingSpot = ps;
        return route;
    }
    
    public Route getPathMoveContainer(ExternVehicle ev, int column, Crane crane) {
        System.out.println("In function getPathMoveContainer");
        List<Vector3f> track2 = new ArrayList<Vector3f>();
        track2.add(crane.getPosition());
        //track2.add(ev.getPosition());
        //System.out.println("column : " + column);
         
        Vector3f container = ev.getGrid()[column][0][0].getArrivalPosition();

        //System.out.println("ev.getPosition = " + ev.getPosition().toString());

        switch (crane.getCurrentPlatform().getAxis())
        {
            case X:
                Vector3f haha = new Vector3f(ev.getPosition().x - column*1.5f, crane.getPosition().y, crane.getPosition().z);
                track2.add(haha); //??
              
                break;
            case Z:
                // hardcoded voor de trein nu ;( wagon is 1.5f en trein zelf ook
                Vector3f hihi = new Vector3f(crane.getPosition().x, crane.getPosition().y, ev.getPosition().z - column*1.5f - 1.5f);
                track2.add(hihi); //??
              
                break;
            //caseY?  

        }
        float length = this.getPathLength(track2);
        Route route = new Route(track2, length);
        route.destinationPlatform = null;
        route.destinationParkingSpot = null;
        return route;
    }
    
   
     
    
    
    
     /*
     * Calculates distance from current platform of agv to desination platform.
     * There are two possible roads for agv, the function selects the road with minimum distance (=shortest).
     * The shortest way is converted into a Route.
     */
    
 /*
    public Route calculateShortestPath(Vehicle vehicle, Vector3f destination){ //only for mainroad
        List<Vector3f> outsidetrack = new ArrayList<Vector3f>(this.track);

        outsidetrack.add(destination);
        Vector3f positionvehicle = this.createCorrespondingWaypoint(vehicle.getCurrentPlatform().getExitpoint());
        outsidetrack.add(positionvehicle);
        Collections.sort(outsidetrack);
        List<Vector3f> insidetrack = new ArrayList<>(outsidetrack);
        Collections.reverse(outsidetrack);
        
        insidetrack = this.setPathCorrectOrder(insidetrack, positionvehicle, destination);
        outsidetrack = this.setPathCorrectOrder(outsidetrack,positionvehicle, destination);
        
        float length_insidetrack = getPathLength(insidetrack);
        float length_outsidetrack = getPathLength(outsidetrack);
        
        if (length_insidetrack < length_outsidetrack){return new Route(insidetrack, length_insidetrack);}
        else return new Route(outsidetrack, length_outsidetrack);
    }*/
    
    public synchronized List<Vector3f> setPathCorrectOrder(List<Vector3f> path, Platform source, Platform destination, Road mainroad){
        //path size altijd 4
       
        System.out.println("In function setPathCorrectOrder");
       
        List<Vector3f> correctPath = new ArrayList<>();
        correctPath.add(path.get(0)); //exitpoint
        correctPath.add(path.get(1)); //exitpoint op weg
        
     
        System.out.println("POSITIES: " + source.positie); //=rechts
        System.out.println("POSITIED: " + destination.positie); //=links
     
        //if (destination.positie == Platform.Positie.)
        
        boolean right = true;
        
        
        //bepalen aan hand van platform of rechts of links: nu altijd rechtsom
        
        if (source instanceof StoragePlatform)
        {
            //System.out.println("RIGHT IS THE GOOD SIDE");
            right = false; //
        }
        //if (typeOrInterface.isInstance(someObject);)
        
        //rechtsom
        if (right)
        {    

            
            
            switch(source.positie)
            {
                case RECHTS:
               
                    
                    if (destination.positie == Platform.Positie.LINKS) 
                    {  //rechtsonder linksonder
                       
                       
                        correctPath.add(mainroad.track.get(2)); 
                        correctPath.add(mainroad.track.get(1)); 
                        break;
                    }
                    if (destination.positie == Platform.Positie.ONDER)
                    {
                        correctPath.add(mainroad.track.get(2)); //rechtsonder
                    }
                    break;
                    
                case LINKS:
                    
                    if (destination.positie == Platform.Positie.RECHTS)
                    {
                        System.out.println("Not in else");
                        correctPath.add(mainroad.track.get(1)); //linksboven
                        System.out.println("0: "+ mainroad.track.get(1));
                        System.out.println("2: "+mainroad.track.get(2));
                        correctPath.add(mainroad.track.get(2)); //rechtsboven
                    }
                    if (destination.positie == Platform.Positie.ONDER)
                    {
                        correctPath.add(mainroad.track.get(0)); //linksboven
                        correctPath.add(mainroad.track.get(3)); //rechtsboven
                        correctPath.add(mainroad.track.get(2)); //rechtsonder
                    }
                    break;
                    
                case ONDER:
                    if (destination.positie == Platform.Positie.RECHTS)
                    {
                        //linksonder
                        //linksboven
                        //rechtsboven
                        correctPath.add(mainroad.track.get(1));
                        correctPath.add(mainroad.track.get(0));
                        correctPath.add(mainroad.track.get(3));
                    }
                    if (destination.positie == Platform.Positie.LINKS)
                    {
                        correctPath.add(mainroad.track.get(1)); //linksonder
                     
                    }
                    break;
                    
                
                    
            }
            
    
        }
        
        //linksom <= 
        
        else //linksom
        {//moet nog
            //correctPath.add(path.get(3));
            //correctPath.add(path.get(2));
            switch(source.positie)
            {
                case RECHTS:
                    
                    if (destination.positie == Platform.Positie.LINKS) 
                    { //rechtsboven
                        //linksboven
                        correctPath.add(mainroad.track.get(3));
                        correctPath.add(mainroad.track.get(0));
                        break;
                    }
                    if (destination.positie == Platform.Positie.ONDER)
                    {
                        correctPath.add(mainroad.track.get(3)); //rechtsboven
                        correctPath.add(mainroad.track.get(0)); //linksboven
                        correctPath.add(mainroad.track.get(1)); //linksonder
                        break;
                    }
                    break;
                    
                case LINKS:
                    
                    if (destination.positie == Platform.Positie.RECHTS)
                    {
                        System.out.println("In else");
                        //linksonder
                        //rechtsonder
                       System.out.println("1: " + mainroad.track.get(1));
                        System.out.println("2: " + mainroad.track.get(2));
                                
                        correctPath.add(mainroad.track.get(1)); 
                        correctPath.add(mainroad.track.get(2)); 
                    }
                    if (destination.positie == Platform.Positie.ONDER)
                    {
                        correctPath.add(mainroad.track.get(1)); 
                      
                    }
                    break;
                     
                case ONDER:
                    if (destination.positie == Platform.Positie.RECHTS)
                    {
                        correctPath.add(mainroad.track.get(2)); //rechtsonder
           
                    }
                    if (destination.positie == Platform.Positie.LINKS)
                    {
                        correctPath.add(mainroad.track.get(2)); //rechtsonder
                        correctPath.add(mainroad.track.get(3)); //rechtsboven
                        correctPath.add(mainroad.track.get(0)); //linksboven
                    }
                    break;
                    
                
                    
            }
            
        }
        
        correctPath.add(path.get(2));
        correctPath.add(path.get(3));
        for (Vector3f v : path)
        {
          //System.out.println("VectorPath: " + v);  
        }
        
        return correctPath;
    }
    
    
    

    
     
}
