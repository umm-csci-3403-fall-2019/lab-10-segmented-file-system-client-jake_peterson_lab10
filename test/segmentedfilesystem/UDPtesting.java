package segmentedfilesystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static segmentedfilesystem.Main.startConnection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class  UDPtesting{
    DatagramSocket socket;
    ArrayList<Byte> IDs;
    HashMap<Byte, UDPstructure> map;

    // setup for other tests
    @Before
    public void testStart() throws IOException{
        final InetAddress address;
        this.map = new HashMap<Byte, UDPstructure>();
        this.IDs = new ArrayList<Byte>();

        this.socket = new DatagramSocket();
        address = InetAddress.getByName("");

        //start a connection for testing
        startConnection(address, socket);
    }

    @Test
    public void testDone() throws IOException {
        assertEquals(Main.done(IDs, map), false);
        Main.receive(socket, map, IDs);
        assertEquals(Main.done(IDs, map), true);
    }
    @Test
    public void testGetFile_() {
        byte addThis = 0;
        IDs.add(addThis);
        map.put(addThis, new UDPstructure());
        //test
        assertEquals(map.get(IDs.get(0)),Main.getFile(IDs, map,0));
    }
    @Test
    public void mapperTest() throws IOException{
        Main.receive(socket, map, IDs);
        assertEquals(map.size(), 3);
        assertEquals(IDs.size(), 3);
    }
    @Test
    public void testCalcSize(){
        assertEquals(Main.calcSize(0,0),0);
        assertEquals(Main.calcSize(2,0),512);
        assertEquals(Main.calcSize(0,1),1);
        assertEquals(Main.calcSize(-3,4),64772);
        assertEquals(Main.calcSize(4,-3),1277);
    }

}
