package jvm.classloader;

public class T002_ClassLoaderLevel {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());//null 表示到了最顶级加载器，也就是bootstrap启动类加载器。
        System.out.println(sun.awt.HKSCS.class.getClassLoader());//null
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());//ext扩展类加载器。
        System.out.println(T002_ClassLoaderLevel.class.getClassLoader());//app应用类加载器

        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader().getClass().getClassLoader());
        System.out.println(T002_ClassLoaderLevel.class.getClassLoader().getClass().getClassLoader());

        System.out.println(new T006_MSBClassLoader().getParent());
        System.out.println(new T006_MSBClassLoader().getClass().getClassLoader());
        System.out.println(ClassLoader.getSystemClassLoader());
    }
}
