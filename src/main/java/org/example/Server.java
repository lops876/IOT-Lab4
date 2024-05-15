package org.example;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    static int MAX_SIZE = 200;
    static String[] names = {"A", "B", "C"};
    static ObjectMapper mapper = new ObjectMapper();
    static ObjectNode root = mapper.createObjectNode();
    static ArrayNode[] locationNode = new ArrayNode[3];
    static ArrayNode latestLocationNode = mapper.createArrayNode();
    static ArrayNode latestDistanceNode = mapper.createArrayNode();
    static Coordinate[] latestLocation = new Coordinate[3];
    static double[] latestDistance = {0,0,0};

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        for(int i=0;i<3;i++) {
            locationNode[i] = mapper.createArrayNode();
            root.put(names[i], locationNode[i]);
            latestLocation[i] = new Coordinate();
        }
        root.put("latest_location",latestLocationNode);
        root.put("latest_distance",latestDistanceNode);

        DatagramSocket socket = new DatagramSocket(8080);
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        for(int i=0;i<MAX_SIZE;i++) {
            socket.receive(packet);

            ByteArrayInputStream byteArrayStram = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(byteArrayStram);
            HashMap<Integer, Coordinate> loc = (HashMap<Integer, Coordinate>) objectStream.readObject();
            Map.Entry<Integer, Coordinate> firstEntry = loc.entrySet().iterator().next();

            int index = firstEntry.getKey();
            Coordinate current = firstEntry.getValue();
            latestLocation[index] = current;
            double distance = current.distance();
            latestDistance[index] = distance;
            String status = names[index]+":"+ current +" distance:"+distance;
            System.out.println(status);

            locationNode[index].add(mapper.createArrayNode().add(current.longitude).add(current.latitude));
            BufferedWriter out = new BufferedWriter(new FileWriter("data.js"));
            out.write("data = " + root.toPrettyString());

            out.close();
            objectStream.close();
            byteArrayStram.close();
        }
        for(int i=0;i<3;i++) {
            latestLocationNode.add(latestLocation[i].toString());
            latestDistanceNode.add(latestDistance[i]);
            if (latestDistance[i] > 10000)
                CustomRobotGroupMessage.alert(names[i] + " is getting too further!" +
                        "\nLatest location:" + latestLocation[i] + " distance:" + latestDistance[i]);
        }

        BufferedWriter out = new BufferedWriter(new FileWriter("data.js"));
        out.write("data = " + root.toPrettyString()+"\n");
        out.close();
    }
}