###jvm学习总结
####一、jvm入门级相关概念：
1. java从编码到执行的过程：java文件经过javac编译生成class字节码文件。  
编译器补充知识： 
    - 前端编译器：把.java转变为.class的过程。如Sun的Javac、IDE工具中的编译器。
    - JIT编译器：把字节码转变为机器码的过程，如HotSpot VM的C1、C2编译器。 
    - AOT编译器：静态提前编译器，直接将*.java文件编译本地机器代码的过程。
2. 字节码文件执行java命令，被类加载器加载到内存中（同时加载的还有一些java类库），然后会调用字节码
解释器或者JIT即时编译器，进行解释或编译，最后交给执行引擎执行（面对操作系统和硬件）。
3. JVM是一种规范，和任何语言无关，只和class文件有关，只有特定格式的class文件才能被jvm识别和执行。
4. 有多种jvm的实现，Hotspot/Jrokit/J9-IBM/Microsoft VM/TaoBao VM/azul zing...
####二、class文件格式
1. class文件就是一个二进制字节流,由虚拟机解释。
2. 数据类型有u1/u2/u4/u8/_info
3. 一些工具查看二进制、十六进制文件：BinEd、JClassLib..
4. 具体内容结构(按顺序从前往后)：
    - u4:魔数 CAFE BABE 代表文件类型的标识。
    - u4:版本号：minor+major
    - u2:常量池中常量个数：0010  常量从##1开始，##0是预留的一个常量编号。 
    - 长度为常量个数-1的表，存储各种常量
    - ...
####三、class文件加载过程：
1. 加载：通过类加载器，将class文件加载到JVM中（内存）,并且生成一个Class对象，该对象指向二进制文件。
2. 链接：
    - 验证：class文件格式是否符合jvm规范
    - 准备：创建类或接口中的静态变量，并且给它们赋初始值，
    - 解析：将常量池中的符号应用（类、方法、属性等），替换为指针、偏移量等内存地址的直接引用。
3. 初始化：真正去执行类的初始化代码逻辑，包括静态变量的赋值，以及静态代码块中的逻辑。
  
####四、双亲委派模型：
1. 简单定义：当类加载器试图去加载某个类型的时候，除非父加载器找不到相应类型，否则尽量将加载任务
    代理给当前加载器的父加载器去加载。
2. 使用原因：主要是为了安全，防止恶意的加载，另外避免类的重复加载，浪费资源。
####五、类加载器分类：
1. BootStrap Class-Loader: 启动类加载器，加载jre/lib文件夹下的jar包，String等。
2. Extension Class-Loader：扩展类加载器，加载jre/lib/ext/文件夹下的jar包。
3. App Class-Loader：应用类加载器（系统类加载器）：加载classpath里面的内容，我们自己写的类。
4. 自定义加载器
####六、几个API方法：
1. getClassLoader():获得调用该方法的类的加载器，也就是被哪个类加载进虚拟机的。
2. getParent():获得调用该方法的加载器的父加载器。  
注意: *父加载器并不是继承关系，是在当前加载器内有一个ClassLoader类型的变量，叫parent,指向父加载器*  
    *也不是加载当前加载器的加载器（加载器本身也是一个类，需要被加载*  
    *ext/app加载器都是被启动类加载器加载的，调用getClassLoader返回null*
       
####七、Launcher 
1. bootstrap/ext/app 这些加载器都是Launcher的静态内部类，在Launcher内定义了各自的访问范围。 

####八、自定义类加载器
1. 调用加载器的loadClass()方法，会把二进制文件加载到内存中，返回Class对象。
2. 什么时候需要使用自定义加载器：一般是框架内部，比如spring框架的动态代理，要生成一个新的对象时会使用
自定义的加载器。还有热部署场景的应用(tomcat)，源码修改了，怎么热部署最新的代码，也可以使用自定义加载器加载。
3. loadClass()执行过程：先在缓存中查找该类是否已被加载，若没有加载过，尝试调用父加载器去加载，
也是先查询父加载器是否加载，若没有，再调用父加载器的父加载器，类似递归。若最后都没有返回Class对象，
只能由父加载器调用findCLass()自己加载，在指定的范围内查找，若没有找到，返回null，子加载器调用自己的
findClass()加载，一直往下传递，直到加载完成。
4. 自定义加载器实现：
    - 继承ClassLoader
    - 实现findClass()方法（模板方法设计模式）
    - 使用defineClass()方法将二进制字节数组(Byte[] bytes)转换为Class对象
        
####九、懒加载
1. jvm是采用懒加载的机制，需要用到某个类的时候，才去加载。
2. 但jvm严格规定了什么时候去初始化
        。。。
####十、java执行模式  
* 默认是混合模式，解释执行+编译执行，可以指定具体的模式去执行。
* -Xmixed:默认混合模式
* -Xint:纯解释模式，启动快，执行稍慢。
* -Xcomp:纯编译模式，启动慢，执行快（在代码量多的情况下）

####十一、自定义加载器怎么指定parent
* 在构造方法中调用super(parent);

####十二、如何破坏双亲委派机制
* 重写loadClass()方法

####十三、JMM（java内存模型）  
1. 存储器层次结构：
    - L0 寄存器
    - L1 高速缓存
    - L2 高速缓存
    ***
    **以上在CPU内部，以下是CPU共享部分：**
    - L3 高速缓存
    - L4 主存（内存）
    - L5 磁盘
    - L6 远程文件存储  
    **由于多核CPU在操作内存时，可能会出现数据不一致的情况，所以硬件层使用以下方式保证数据一致**  
    - MESI缓存一致性协议（intel使用）
    - 总线锁  
    缓存行的概念：读取缓存以缓存行为单位，目前为64个字节  
    位于同一缓存行的两个不同的数据被两个不同的CPU锁定，产生相互影响的伪共享问题。  
    使用缓存行对齐能提高效率。
2. 乱序问题：
    - cpu在执行一条指令时，为了提高指令执行效率，会同时去执行另一条指令（前提是两条指令之间没有依赖关系）
3. 如何保证不乱序
    - 硬件级别：使用cpu内存屏障  
    sfence：在sfence指令之前的写操作，必须在sfence指令之后的写操作前完成  
    lfence：在lfence指令之前的读操作，必须在lfence指令之后的读操作之前完成  
    mfence：在mfence指令之前的读写操作，必须在mfence指令之后的读写操作之前完成
    - JVM级别规范：四条指令屏障  
    - LoadLoad屏障：对于语句：Load1;LoadLoad;Load2;  
    在Load2及后续读取操作要读取的数据被访问前，保证Load1要读取的数据被读取完毕。
    - StoreStore屏障：对于这样的语句：Store1;StroeStore;Store2;  
    在Store及后续写入操作执行，保证Store1的写入操作对其它处理器可见。
    - LoadStore屏障：对于语句：Load1;LoadStore;Store2；  
    在Store2及后续写入操作被刷出前，保证Load1要读取的数据被读取完毕。
    - StoreLoad屏障：对于语句：Store1;StoreLoad;Load2；  
    在Load2及后续所有读取操作执行前，保证Store1的写入对所有处理器可见
4. volatile底层如何实现？
    - 源码层面：volatile修饰
    - 字节码层面：仅添加Access flag:volatile
    - jvm层面：volatile内存区的读写都加屏障
    - os/硬件层面：windows lock指令、 MESI缓存一致性协议、总线锁
5. synchronized底层实现？
    - 源码层面：synchronized代码块
    - 字节码层面：Acc_synchronized、monitorenter-monitorexit指令
    - jvm层面：C++调用了操作系统提供的同步机制
    -os/硬件层面：intelx86使用lock指令
    
####十四、对象在内存中的分布几个问题
1. 对象的创建过程？
    - 类加载的几个步骤：加载、链接(验证、准备(静态变量赋默认值)、解析)、初始化(静态变量赋初始值，执行静态语句块)
    - 申请对象内存空间
    - 成员变量赋默认值，如0，null等
    - 调用构造方法  
    1.成员变量顺序赋初始值  
    2.执行构造方法语句（先调用父类构造方法，super()）
2. 对象在内存中的存储布局？  
普通对象：
    - 对象头（markword）8字节
    - classPointer指针：XX:+UseCompressedClassPointers 默认打开指针压缩为4字节 不开启为8字节
    - 实例数据（成员变量）：-XX:+UseCompressedOops普通对象指针，默认开启为4字节 不开启为8字节（在64为机器上）） 
    - padding对齐，8的倍数。（根据对象大小，补充到8的倍数）  
数组对象：  
    - 对象头：同上 8
    - classpointer指针：同上 
    - 数组长度：4字节
    - 数据内容：
    - padding对齐 8的倍数
3. new Object()、new int[]{}在内存中占用多少字节？  
new Object()：对象头8，类型指针4，对齐4,共16字节  
new int[]{}:对象头8，指针4，数组长度：4字节，数组内容暂无，共16字节  
```java
private static class P {
                           //8 markword
                           //4 class指针
           int id;         //4
           String name;    //4 oops 
           int age;        //4
           byte b1;        //1
           byte b2;        //1
           Object o;       //4
           byte b3;        //1
       }//new P():32个字节
```
4. 对象头具体包括什么内容（1.8）  
    - 锁定标志位：代表对象有没有被synchronized锁定  
    无锁、偏向锁为01，但还有一位区分是否是偏向锁。0为无锁，1为偏向锁。  
    轻量级锁（自旋锁）：00  
    重量级锁：10  
    - GC标志位：代表GC的分代年龄（4位，最大为15）--无锁和偏向锁上有GC分代年龄  
    在无锁状态下：如果没有重写过hashCode()方法，会有31位保存在对象头上，称为identityHashCode;
    重写过后hashCode不会存在这里
5. 对象怎么定位？T t=new T()//t如何定位到对象
    - 句柄池：t先指向一个句柄池，句柄池中有指针指向对象，还有指针指向Class对象。效率低，在GC时效率高。
    - 直接引用（java使用）：直接指向对象，对象里有指针指向Class对象，效率较高。
6. 对象的分配过程？
    - ...
 
 
 
 
 
 
 
 
 
 
 
 
 
       