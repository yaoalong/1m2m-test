package lab.mars.m2m.reality.pojo;

/**
 * Author:yaoalong.
 * Date:2016/4/29.
 * Email:yaoalong@foxmail.com
 */


import lab.mars.m2m.test.resourcetest.ResourceTestBase;

/**
 * 空调设备
 */
public class AirConditioning extends Machine {
    private int low;//低于这个值开启
    private int high;//高于这个值开启

    public AirConditioning(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public AirConditioning(int low, int high, boolean isClosed, ResourceTestBase resourceTestBase, String cntUri) {
        this.low = low;
        this.high = high;
        this.isClosed = isClosed;
        this.resourceTestBase = resourceTestBase;
        this.cntUri = cntUri;
    }

    @Override
    public void create(int value) {
        if (value < low && isClosed == true) {
            System.out.println("温度传感器感应到温度过低，打开空调");
            isClosed = false;
            request(new AirConditioning(isClosed));
        } else if (value > high && isClosed == true) {
            System.out.println("温度传感器感应到温度过高，打开空调");
            isClosed = false;
            request(new AirConditioning(isClosed));
        } else if (value >= low && value <= high && isClosed == false) {
            System.out.println("温度传感器感应到温度适中，关闭空调");
            isClosed = true;
            request(new AirConditioning(isClosed));
        } else {
            System.out.println("温度"+":" + value + "空调+"+cntUri+"的状态为：" + (isClosed ? "关闭" : "开启"));
        }
    }
}
