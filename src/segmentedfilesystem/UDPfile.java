package segmentedfilesystem;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.net.DatagramPacket;
import java.util.*;

public class UDPfile {
    public boolean header;
    public boolean end;
    public int length;
    public DatagramPacket headerPacket;
    public ArrayList<DatagramPacket> packet = new ArrayList<>();

    public void add(DatagramPacket packet, boolean header, boolean end,int numPackets){
        if(header){
            this.header = true;
            this.headerPacket = packet;
            return;
        }
        if(end){
            this.end = true;
            this.length = numPackets+1;
        }
        this.packet.add(packet);
    }
    // checks if the file is complete.
    public boolean complete(){
        if(this.header && this.end){
            return (packet.size() == this.length);
        }else{
            return false;
        }
    }
    public String fileName(DatagramPacket packet){
        byte[] data = packet.getData();
        byte[] fileName = new byte[packet.getLength()-2];

        for (int i = 2; i<packet.getLength();i++){
            fileName[i-2] = data[i];
        }
        return new String(fileName);
    }
    public int calSize(int big, int small){
        if(big<0){
            big = big+256;
        }
        if(small<0){
            small += 256;
        }
        return big*256 +small;
    }
    public void sort(){
        Collections.sort(packet, new sortFileID());
    }
    public  byte[] getData(DatagramPacket packet){
        byte[] packetData = packet.getData();
        byte[] data = new byte[packet.getLength()];

        for(int i = 4; i<packet.getLength();i++){
            data[i-4] = packetData[i];
        }
        return data;
    }
    //for sorting
    public class  sortFileID implements Comparator<DatagramPacket> {
        public int compare (DatagramPacket packet1, DatagramPacket packet2){
            byte[] ar1 = packet1.getData();
            byte[] ar2 = packet2.getData();
            int size1 = calSize(ar1[2],ar1[3]);
            int size2 = calSize(ar2[2], ar2[3]);

            return size1 - size2;

        }
    }


}
