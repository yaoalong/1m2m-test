package lab.mars.m2m.test.resourcetest;

import lab.mars.m2m.protocol.common.m2m_childResourceRef;
import lab.mars.m2m.protocol.resource.m2m_ContentInstance;
import lab.mars.m2m.protocol.resource.m2m_resource;
import lab.mars.m2m.reality.pojo.*;
import lab.mars.m2m.reflection.ResourceReflection;
import lab.mars.onem2m.pojo.NotificationUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static lab.mars.onem2m.pojo.NotificationUtils.cntMapMachine;

/**
 * Author:yaoalong.
 * Date:2016/5/28.
 * Email:yaoalong@foxmail.com
 */

/**
 * 真实场景模拟
 */
public class ResourceRealityTest extends PreCreateResource {
    /**
     * 光线传感器
     */
    private static final int light_current_value = 0;
    private static final int light_low_value = 10;
    private static final int light_lowest_value = 0;
    private static final int light_high_value = 100;
    private static final int light_highest_value = 200;
    private static final int light_period = 2;
    private static final int light_increment_num = 1;
    /**
     * 温度传感器
     */
    private static final int temperature_current_value = 0;
    private static final int temperature_lowest_value = -20;
    private static final int temperature_low_value = 0;
    private static final int temperature_high_value = 25;
    private static final int temperature_highest_value = 40;
    private static final int temperature_period = 1;
    private static final int temperature_increment_num = 1;

    /**
     * 防盗传感器
     */
    private static final boolean antitheft_sensor_value = false;
    private static final int antitheft_sensor_period = 1;

    /**
     * 楼层
     */
    private static final int garageFloors = 2;
    /**
     * 停车场
     */
    private static final int number_parking_number = 1000;
    /**
     * 传感器
     */
    private static final boolean laser_sensor_value = false;
    private static final int laser_sensor_period = 1;

    //0代表的是每个传感器从初始值开始变化
    //1代表的是传感器速度随机、频率随机

    //光线传感器0
    //空调传感器1
    //灯2
    //空调3
    private static int mode = 0;
    private static Random random = new Random();

    public static void main(String args[]) throws Exception {
        ResourceRealityTest resourceRealityTest = new ResourceRealityTest();
        resourceRealityTest.setUp();
        resourceRealityTest.test();
    }

    @Override
    public void handleBefore() {
        isReality = true;
    }

    @Override
    public void handleNotify(m2m_childResourceRef ref) {
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
//        executorService.submit(() -> {
//            m2m_resource response = null;
//            try {
//                response = testRetrieve(ref.v, null, OK, SYNC);
//            } catch (Exception e1) {
//                e1.printStackTrace();
//            }
//            if (response instanceof m2m_ContentInstance) {
//                m2m_ContentInstance cin = (m2m_ContentInstance) response;
//                Object object = ResourceReflection.deserializeKryo(cin.con);
//                if (object instanceof LightSensor) {
//                    LightSensor lightSensor = (LightSensor) object;
//                    System.out.println("cost time:" + (System.currentTimeMillis() - NotificationUtils.zxidMapStartTime.get(lightSensor.getId())));
//                    cntMapMachine.get(lightSensor.getMachineUri()).create(lightSensor.getValue());
//                } else if (object instanceof AntiTheftSensor) {
//                    AntiTheftSensor antiTheftSensor = (AntiTheftSensor) object;
//                    System.out.println("cost time:" + (System.currentTimeMillis() - NotificationUtils.zxidMapStartTime.get(antiTheftSensor.getId())));
//                    cntMapMachine.get(antiTheftSensor.getMachineUri()).create(antiTheftSensor.getValue());
//                } else if (object instanceof TemperatureSensor) {
//                    TemperatureSensor temperatureSensor = (TemperatureSensor) object;
//                    System.out.println("cost time:" + (System.currentTimeMillis() - NotificationUtils.zxidMapStartTime.get(temperatureSensor.getId())));
//                    cntMapMachine.get(temperatureSensor.getMachineUri()).create(temperatureSensor.getValue());
//                }
//            } else {
//                System.out.println("classs:" + response.getClass());
//            }
//        });
    }

    public void test() throws Exception {
        Map<String, String> containerURI = new HashMap<>();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
        //栋数
        int banNum = 10;
        //层数
        int floor = 20;
        //户数
        int houseHold = 4;
        //房间
        int roomsNums = 5;
        //  String aeUri = createSyncAEResource();
        for (int i = 0; i < banNum; i++) {
            for (int j = 0; j < floor; j++) {
                String aeUri = createSyncAEResource();//现在是每层楼是一个AE
                for (int z = 0; z < houseHold; z++) {
                    for (int y = 0; y < roomsNums; y++) {
                        for (int w = 0; w < 4; w++) {
                            String cntUri = createSyncContainer(aeUri);//创建一个container
                            containerURI.put(i + "/" + j + "/" + z + "/" + y + "/" + w, cntUri);
                            if (w == 2) {
                                Light light = new Light(light_low_value, light_high_value, false, this, cntUri);//空调
                                cntMapMachine.put(cntUri, light);
                            }
                            if (w == 3) {
                                AirConditioning airConditioning = new AirConditioning(temperature_low_value, temperature_high_value, false, this, cntUri);//灯
                                cntMapMachine.put(cntUri, airConditioning);
                            }
                        }


                    }
                    //防盗传感器
                    String cntUri = createSyncContainer(aeUri);//创建一个container
                    containerURI.put(i + "/" + j + "/" + z + "/" + 0 + "/", cntUri);
                    //防盗报警器
                    cntUri = createSyncContainer(aeUri);//创建一个container
                    containerURI.put(i + "/" + j + "/" + z + "/" + 1 + "/", cntUri);
                    AntitheftAlarm antitheftAlarm = new AntitheftAlarm(false, this, cntUri);
                    cntMapMachine.put(cntUri, antitheftAlarm);

                }
            }
        }
        for (int i = 0; i < banNum; i++) {
            for (int j = 0; j < floor; j++) {
                for (int z = 0; z < houseHold; z++) {
                    String containerURL = containerURI.get(i + "/" + j + "/" + z + "/" + 0 + "/");
                    createAsyncSubScriptions(containerURL);
                    for (int y = 0; y < roomsNums; y++) {
                        /**
                         * 创建不同的subscription
                         */
                        for (int w = 0; w < 2; w++) {
                            containerURL = containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + w);
                            createAsyncSubScriptions(containerURL);
                        }


                        if (mode == 1) {
                            int increment = random.nextInt(light_high_value);
                            int period = getNextRandom();
                            executorService.scheduleAtFixedRate(new LightSensor(light_current_value, increment, light_lowest_value, light_highest_value, period, this, containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 0), containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 2)), 1
                                    , period, TimeUnit.SECONDS);
                            increment = random.nextInt(temperature_increment_num);
                            period = getNextRandom();
                            executorService.scheduleAtFixedRate(new TemperatureSensor(temperature_current_value, increment, temperature_lowest_value, temperature_highest_value, period, this, containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 1), containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 3)), 1
                                    , period, TimeUnit.SECONDS);
                        } else {
                            executorService.scheduleAtFixedRate(new LightSensor(light_current_value, light_increment_num, light_lowest_value, light_highest_value, light_period, this, containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 0), containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 2)), 0
                                    , light_period, TimeUnit.SECONDS);
                            executorService.scheduleAtFixedRate(new TemperatureSensor(temperature_current_value, temperature_increment_num, temperature_lowest_value, temperature_highest_value, temperature_period, this, containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 1), containerURI.get(i + "/" + j + "/" + z + "/" + y + "/" + 3)), 1
                                    , light_period, TimeUnit.SECONDS);
                        }

                    }
                    if (mode == 1) {
                        int period = getNextRandom();
                        executorService.scheduleAtFixedRate(new AntiTheftSensor(antitheft_sensor_value, period, this, containerURI.get(i + "/" + j + "/" + z + "/" + 0 + "/"), containerURI.get(i + "/" + j + "/" + z + "/" + 1 + "/")), 1
                                , period, TimeUnit.SECONDS);
                    } else {
                        executorService.scheduleAtFixedRate(new AntiTheftSensor(antitheft_sensor_value, antitheft_sensor_period, this, containerURI.get(i + "/" + j + "/" + z + "/" + 0 + "/"), containerURI.get(i + "/" + j + "/" + z + "/" + 1 + "/")), 1
                                , antitheft_sensor_period, TimeUnit.SECONDS);
                    }

                }
            }
        }
//        String aeUri = createSyncAEResource();
//        for (int i = 0; i < garageFloors; i++) {
//            for (int j = 0; j < number_parking_number; j++) {
//                String cntUri = createSyncContainer(aeUri);//创建一个container
//                containerURI.put(i + "/" + j, cntUri);
//            }
//        }
//        for (int i = 0; i < garageFloors; i++) {
//            for (int j = 0; j < number_parking_number; j++) {
//                if (mode == 1) {
//                    int period = getNextRandom();
//                    executorService.scheduleAtFixedRate(new LaserSensor(laser_sensor_value, period, this, containerURI.get(i + "/" + j), null), 1, period, TimeUnit.SECONDS);
//                } else {
//                    executorService.scheduleAtFixedRate(new LaserSensor(laser_sensor_value, laser_sensor_period, this, containerURI.get(i + "/" + j), null), 1, laser_sensor_period, TimeUnit.SECONDS);
//                }
//
//            }
//        }
        System.in.read();
    }

    public int getNextRandom() {
        int nextInt = random.nextInt();
        return nextInt <= 0 ? 5 : nextInt;
    }
}
