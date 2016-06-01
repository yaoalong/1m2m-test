package lab.mars.m2m.reality.pojo;

/**
 * Author:yaoalong.
 * Date:2016/4/21.
 * Email:yaoalong@foxmail.com
 */


import lab.mars.m2m.test.resourcetest.ResourceTestBase;

/**
 * 防盗传感器
 */
public class AntiTheftSensor extends AbstractSensor {
    private static final long serialVersionUID = -3092906479136585494L;
    private boolean value;
    private int period;

    public AntiTheftSensor(boolean value, String machineUri) {
        this.value = value;
        this.machineUri = machineUri;
    }

    public AntiTheftSensor(boolean value, int period, ResourceTestBase resourceTestBase, String cntUri, String machineUri) {
        this.value = value;
        this.period = period;
        this.resourceTestBase = resourceTestBase;
        this.cntUri = cntUri;
        this.machineUri = machineUri;
    }

    @Override
    public void run() {
        value = !value;
        request(new AntiTheftSensor(value, machineUri));
       // System.out.println("红外线传感器" + machineUri + " 状态为:" + (value ? "空" : "被占用"));
    }

    @Override
    public int getValue() {
        return value ? 1 : 0;
    }

}
