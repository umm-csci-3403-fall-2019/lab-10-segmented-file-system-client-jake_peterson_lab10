package segmentedfilesystem;

import java.io.FileOutputStream;
import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static DatagramSocket socket;
    private static InetAddress address;
    private static int port = 6014;

    public static void main(String[] args) throws IOException {

        DatagramSocket socket;
        boolean running;
        byte buf[] = new byte[256];
        HashMap<Byte, UDPstructure> fileMap = new HashMap<Byte, UDPstructure>();
        ArrayList<Byte> fileIDs = new ArrayList<Byte>();

        socket = new DatagramSocket(6014);
        address = InetAddress.getByName(args[0]);

        startConnection(address, socket);
        receive(socket, fileMap, fileIDs);
        sortPackets(fileMap, fileIDs);
        writeToDisk(fileIDs, fileMap);

        socket.close();
    }
    //start the connection with the server
    public static void startConnection(InetAddress address, DatagramSocket socket)throws IOException {
        byte[] buf = new byte[0];
        DatagramPacket packet = new DatagramPacket(buf, buf.length,address,6014);

        socket.send(packet);
    }
    //write data to the disk into the correct files
    public static void writeToDisk(ArrayList<Byte> fileIDs, HashMap<Byte, UDPstructure> fileMap)throws IOException{
        for(int i =0; i<fileIDs.size();i++){
            DatagramPacket header = getFile(fileIDs, fileMap,i).headerPacket;
            FileOutputStream out = new FileOutputStream(fileMap.get(fileIDs.get(i)).fileName(header));
            for(int j = 0; j< fileMap.get(fileIDs.get(i)).packet.size(); j++){

            }
        }
    }
    //sort the packets in the files
    public static void sortPackets(HashMap<Byte, UDPstructure> fileMap, ArrayList<Byte> fileIDs){
        for(int i=0; i<fileIDs.size(); i++){
            fileMap.get(fileIDs.get(i)).sort();
        }
    }

    public static UDPstructure getFile(ArrayList<Byte> fileIDs, HashMap<Byte, UDPstructure> fileMap, int mapIndex){
        return fileMap.get(fileIDs.get(mapIndex));
    }
    public static int calcSize(int big, int small) {
        if (big < 0) {
            big = big + 256;
        }
        if (small < 0) {
            small = small + 256;
        }
        return big * 256 + small;
    }
    public static boolean done(ArrayList<Byte> fileIDs, HashMap<Byte, UDPstructure> fileMap){
        for(int i = 0; i<fileIDs.size();i++){
            if(!(fileMap.get(fileIDs.get(i)).complete())){
                return false;
            }
        }
        return fileIDs.size() == 3;
    }

    //read packets from server
    public static void receive(DatagramSocket socket, HashMap<Byte, UDPstructure> fileMap, ArrayList<Byte> fileIDs )throws IOException{
        while (!done(fileIDs,fileMap)) {

            byte[] buf = new byte[1028];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);

            if (buf[0] % 2 == 0) {
                if (fileMap.get(buf[1]) == null) {
                    fileIDs.add(buf[1]);
                    fileMap.put(buf[1], new UDPstructure());
                }
                fileMap.get(buf[1]).add(packet, true, false, 0);
            }
            if (buf[0] % 2 != 0) {
                if (buf[0] == (3 % 4)) {
                    if (fileMap.get(buf[1]) == null) {
                        fileIDs.add(buf[1]);
                        fileMap.put(buf[1], new UDPstructure());
                    }
                    fileMap.get(buf[1]).add(packet, false, true, calcSize(buf[2], buf[3]));
                } else {
                    if (fileMap.get(buf[1]) == null) {
                        fileIDs.add(buf[1]);
                        fileMap.put(buf[1], new UDPstructure());
                    }
                    fileMap.get(buf[1]).add(packet, false, false, 0);

                }
            }
        }
    }
}
