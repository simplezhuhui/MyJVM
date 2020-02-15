package jvm.classloader;

public class T004_ParentAndChild {
    public static void main(String[] args) {
        System.out.println(T004_ParentAndChild.class.getClassLoader());//app
        System.out.println(T004_ParentAndChild.class.getClassLoader().getClass().getClassLoader());//null
        System.out.println(T004_ParentAndChild.class.getClassLoader().getParent());//ext
        System.out.println(T004_ParentAndChild.class.getClassLoader().getParent().getParent());//null
        System.out.println(T004_ParentAndChild.class.getClassLoader().getParent().getClass().getClassLoader());//null
        //System.out.println(T004_ParentAndChild.class.getClassLoader().getParent().getParent().getParent());

    }
}
