package org.radrso.plugins;

/**
 * Created by raomengnan on 16-12-2.
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Launcher {

    private static Log logger = LogFactory.getLog(Launcher.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("=======================");
        System.out.println("        Core包启动          ");
        SystemDetails.outputDetails();
        System.out.println("=======================");

        getLocalip();
        // 初始化spring
        logger.info("开始初始化core服务");
        BeanFactoryUtils.init();
        BeanFactoryUtils.getContext().start();

        try{
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得本机ip地址 注意：Spring RmiServiceExporter取得本机ip的方法：InetAddress.getLocalHost()
     */
    private static void getLocalip() {
        try {
            System.out.println("服务暴露的ip: "
                    + java.net.InetAddress.getLocalHost().getHostAddress());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}