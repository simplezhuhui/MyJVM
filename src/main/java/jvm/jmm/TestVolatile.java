package jvm.jmm;

public class TestVolatile {

    void m(){
        synchronized(this){
            System.out.println("aaaaaa");
        }
    }
    public static void main(String[] args) {

    }
}
