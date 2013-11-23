/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package containing.Vehicle;

import containing.Container;
import containing.Vector3f;
import java.util.Date;

/**
 *
 * @author Miriam
 */
public abstract class ExternVehicle extends Vehicle {
    
    private Date arrivalDate;
    protected enum Status{LEAVING, ENTER, WAITING};
    protected Status status;
    Container[][][] grid;
    public ExternVehicle(int capicity, Date arrivalDate, Container container){
        super(capicity);
        this.arrivalDate = arrivalDate;
        status = Status.WAITING;
        this.load(container);
    }
    ///[0,0,0] add grid container position
    public void load(Container container){
        //check out of capicity, check dubbel
        if (this.cargo.isEmpty()){this.isLoaded = true;} 
        Vector3f coordinates = container.getArrivalPosition();
        if (grid[coordinates.x][coordinates.y][coordinates.z]!=null){}//dubbel
        if(grid[coordinates.x][coordinates.y][coordinates.z]==null){
        grid[coordinates.x][coordinates.y][coordinates.z] = container;
        super.load(container);
        }
    }
    
    public Container unload(){
        if (cargo.isEmpty()) return null;
        Container container = cargo.get(0);
        cargo.remove(container);
        return container;
    }
    
    public Date getArrivalDate(){return arrivalDate;}
    public void leave(){this.status = Status.LEAVING;}
    public void enter(){this.status = Status.ENTER;}
    
    

    //public void leave(ExternVehicle vehicle){}
}
