import com.jeefix.limitlessled.device.LightDevice;
import com.jeefix.limitlessled.utils.DeviceFactory;

public class SampleUsage {

    public static void main(String[] args) throws Exception {

        DeviceFactory deviceFactory = new DeviceFactory("192.168.1.66");
        LightDevice ledStripDevice = deviceFactory.getDevice(LightDevice.class, 0);

        ledStripDevice
                .on() //turn lights on
                .kelvin(2700)
                .brightness(50) //set brightness to 100%
                .whiteOn() //turn on white led
                .off();
    }
}
