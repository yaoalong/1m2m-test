package lab.mars.m2m.reality.pojo;

/**
 * Author:yaoalong.
 * Date:2016/4/29.
 * Email:yaoalong@foxmail.com
 */


import lab.mars.m2m.test.resourcetest.ResourceTestBase;

/**
 * 灯设备
 */
public class Light extends Machine {

    private int low;
    private int high;

    public Light(boolean isClosed) {
        this.isClosed = isClosed;
    }

    public Light(int low, int high, boolean isClosed, ResourceTestBase resourceTestBase, String cntUri) {
        this.low = low;
        this.high = high;
        this.isClosed = isClosed;
        this.resourceTestBase = resourceTestBase;
        this.cntUri = cntUri;
    }

    @Override
    public void create(int value) {
        if (value < low && isClosed == true) {
            System.out.println("光线传感器感应到光强度过低，因此开启灯");
            isClosed = false;
            request(new AirConditioning(isClosed));
        } else if (value > high && isClosed == false) {
            System.out.println("光线传感器感应到光强度始终，关闭灯");
            isClosed = false;
            request(new AirConditioning(isClosed));
        } else {
            System.out.println("光线强度为:" + value + "灯"+cntUri+"的状态为：" + (isClosed ? "关闭" : "开启"));
        }

    }

}
