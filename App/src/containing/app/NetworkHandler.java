/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package containing.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Robert
 */
public class NetworkHandler implements Runnable {

    private String ip;
    private int port;
    private Socket client;
    private long lastPing;
    
    private PrintWriter writer;
    private BufferedReader reader;

    public NetworkHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Date date = new Date();
        this.lastPing = (date.getTime() / 1000);
    }

    @Override
    public void run() {
    	try{
    		client = new Socket(ip, port);
    		client.setSoTimeout(100);
    	}
    	catch(Exception e){
    		MainActivity.showDialog("Error while connecting to server", e);
    		MainActivity.setIsConnected(false);
    		return;
    	}
        try{
            writer = new PrintWriter(client.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        }
        catch(IOException e){
    		MainActivity.showDialog("Error while connecting to server", e);
    		MainActivity.setIsConnected(false);
    		return;
        }
        
        System.out.println("Connected to " +ip +":" +port);
        MainActivity.setIsConnected(true);
        
        while(true){
        	if(sendPing()){
        		System.out.println("Sending: PING");
                writer.println("PING");
            }
        	try{
                if(!reader.ready()){
                	if(CommandHandler.newCommandsAvailable()){  
                		List<String> commands = CommandHandler.getCommands();
                		
                		System.out.println(commands);
                		for(String cmd : commands){
                        	System.out.println("Sending: " +cmd);
                            writer.println(cmd);
                        }
                		CommandHandler.clearCommands();
                	}
                    
                }
                else{
                	System.out.println("now reading");
                	try{
                    String inputLine = reader.readLine();
                    System.out.println("Received: " +inputLine);
                    CommandHandler.handle(inputLine);
                	}
                	catch(IOException e){
                		
                	}
                    
                }
        	}
        	catch(IOException e){
        		MainActivity.showDialog("Error while getting information from server", e);
        		MainActivity.setIsConnected(false);
        		return;
        	}
        }
    }
    
    
    private boolean sendPing(){
        Date date = new Date();
        long currentTime = (date.getTime() / 1000);
        if(currentTime - this.lastPing > 60){
            this.lastPing = currentTime;
            return true;
        }
        return false;
    }
}
