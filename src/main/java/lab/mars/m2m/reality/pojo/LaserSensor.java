package lab.mars.m2m.reality.pojo;

import lab.mars.m2m.test.resourcetest.ResourceTestBase;

/**
 * Author:yaoalong.
 * Date:2016/4/29.
 * Email:yaoalong@foxmail.com
 */
public class LaserSensor extends AbstractSensor {
    private static final long serialVersionUID = 6021429766370514584L;
    private boolean value;
    private int period;

    public LaserSensor(boolean value, String machineUri) {
        this.value = value;
        this.machineUri = machineUri;
    }

    public LaserSensor(boolean value, int period, ResourceTestBase resourceTestBase, String cntUri, String machineUri) {
        this.value = value;
        this.period = period;
        this.resourceTestBase = resourceTestBase;
        this.cntUri = cntUri;
        this.machineUri = machineUri;
    }

    @Override
    public void run() {
        value = !value;
      //  request(new LaserSensor(value, machineUri));
     //   System.out.println("传感器" + machineUri + " 状态为:" + (value ? "空" : "被占用"));
    }

    @Override
    public int getValue() {
        return value ? 1 : 0;
    }

}
