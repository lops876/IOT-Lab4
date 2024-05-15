package org.example;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sensor {
    static Coordinate[] currentLocations = new Coordinate[3];
    static String[] names = {"A", "B", "C"};
    static Date date;
    static void sentData(int index)throws Exception{
        InetAddress address = InetAddress.getByName("localhost");
        int port = 8080;
        HashMap<Integer, Coordinate>loc = new HashMap<>();
        loc.put(index, currentLocations[index]);

        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream);
        objectStream.writeObject(loc);
        byte[] data = byteArrayStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
        DatagramSocket socket = new DatagramSocket();

        System.out.println(date + ", "+ names[index] + ":" + currentLocations[index]);
        socket.send(packet);

        objectStream.close();
        byteArrayStream.close();
    }

    public static void main(String[] args){
        for(int i=0;i<3;i++)
            currentLocations[i] = new Coordinate();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            executorService.execute(() -> {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        date = new Date();
                        Random random = new Random();
                        currentLocations[finalI].move(BigDecimal.valueOf((random.nextDouble()-0.5)%0.01)
                                        .setScale(6, RoundingMode.UP).doubleValue()
                                ,BigDecimal.valueOf((random.nextDouble()-0.5)%0.01)
                                        .setScale(6, RoundingMode.UP).doubleValue());
                        try {
                            sentData(finalI);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000L,1000L);
            });
        }
        executorService.shutdown();
    }
}
