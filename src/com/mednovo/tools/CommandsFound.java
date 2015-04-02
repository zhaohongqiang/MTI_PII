package com.mednovo.tools;

//import java.util.EnumMap;

public class CommandsFound {

    public final static int SET_FACTORYDATA = 0;//设置出厂数据
    public final static int READ_FACTORYDATA = 1; //读取出厂数据
    public final static int SET_SYSSETDATA = 2;//设置系统设置数据
    public final static int READ_SYSSETDATA = 3;//读取系统设置数据

    public final static int SET_BASALDATA = 4;//设置基础率数据
    public final static int READ_BASALDATA = 5;//读取基础率数据

    public final static int READ_RUNNINGDATA = 6;//读取运行状态数据

    public final static int READ_MEALBOLUSRECORD = 7;//读取即时大剂量记录
    public final static int READ_TIMEBOLUSRECORD = 8;//读取定时大剂量记录

    public final static int READ_SUSPENDEDPUMPRECORD = 9;//读取暂停泵记录
    public final static int READ_BASALRECORD = 10;//读取基础率记录
    public final static int READ_TEMPORARYRECORD = 11;//读取暂时率记录

    public final static int READ_DAILYTOTALRECORD = 12;//读取日总量记录
    public final static int READ_EVENTRECORD = 13;//读取事件记录
    public final static int READ_EXHAUSTRECORD = 14;//读取排气记录
}//定义常量



/*
public enum CommandsEnum {//常用命令 枚举

    SET_FACTORYDATA,//设置出厂数据
    READ_FACTORYDATA, //读取出厂数据
    SET_SYSSETDATA,//设置系统设置数据 
    READ_SYSSETDATA,//读取系统设置数据 

    SET_BASALDATA,//设置基础率数据 
    READ_BASALDATA,//读取基础率数据 

    READ_RUNNINGDATA,//读取运行状态数据 

    READ_MEALBOLUSRECORD,//读取即时大剂量记录 
    READ_TIMEBOLUSRECORD,//读取定时大剂量记录 

    READ_SUSPENDEDPUMPRECORD,//读取暂停泵记录 
    READ_BASALRECORD,//读取基础率记录 
    READ_TEMPORARYRECORD,//读取暂时率记录 

    READ_DAILYTOTALRECORD,//读取日总量记录
    READ_EVENTRECORD,//读取事件记录
    READ_EXHAUSTRECORD//读取排气记录
}//15个
*/
/*public enum CommandsEnum {

    // 利用构造函数传参
    SET_FACTORYDATA(0),//设置出厂数据
    READ_FACTORYDATA(1), //读取出厂数据
    SET_SYSSETDATA(2),//设置系统设置数据
    READ_SYSSETDATA(3),//读取系统设置数据

    SET_BASALDATA(4),//设置基础率数据
    READ_BASALDATA(5),//读取基础率数据

    READ_RUNNINGDATA(6),//读取运行状态数据

    READ_MEALBOLUSRECORD(7),//读取即时大剂量记录
    READ_TIMEBOLUSRECORD(8),//读取定时大剂量记录

    READ_SUSPENDEDPUMPRECORD(9),//读取暂停泵记录
    READ_BASALRECORD(10),//读取基础率记录
    READ_TEMPORARYRECORD(11),//读取暂时率记录

    READ_DAILYTOTALRECORD(12),//读取日总量记录
    READ_EVENTRECORD(13),//读取事件记录
    READ_EXHAUSTRECORD(14);//读取排气记录

    // 定义私有变量
    private int nCode;

    // 构造函数，枚举类型只能为私有
    private CommandsEnum(int _nCode) {

        this.nCode = _nCode;

    }

    @Override
    public String toString() {

        return String.valueOf(this.nCode);


    }

    public static CommandsEnum valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
}*/

//=======================举例说明==================================================
//我们只能够表示出红灯、绿灯和黄灯，但是具体的值我们没办法表示出来。
// 别急，既然枚举类型提供了构造函数，我们可以通过构造函数和覆写toString方法来实现。
// 首先给Light枚举类型增加构造方法，然后每个枚举类型的值通过构造函数传入对应的参数，
// 同时覆写toString方法，在该方法中返回从构造函数中传入的参数，改造后的代码如下：


/*public enum Light {

    // 利用构造函数传参
    RED(1), GREEN(3), YELLOW(2);

    // 定义私有变量
    private int nCode;

    // 构造函数，枚举类型只能为私有
    private Light(int _nCode) {

        this.nCode = _nCode;

    }

    @Override
    public String toString() {

        return String.valueOf(this.nCode);

    }

}*/


//枚举类型的完整演示代码如下：


/*public class LightTest {

    // 1.定义枚举类型

    public enum Light {

        // 利用构造函数传参

        RED(1), GREEN(3), YELLOW(2);

        // 定义私有变量

        private int nCode;

        // 构造函数，枚举类型只能为私有

        private Light(int _nCode) {

            this.nCode = _nCode;

        }

        @Override
        public String toString() {

            return String.valueOf(this.nCode);

        }

    }

    *//**
     *
     * @param args
     *//*

    public static void main(String[] args) {

        // 1.遍历枚举类型

        System.out.println("演示枚举类型的遍历 ......");

        testTraversalEnum();

        // 2.演示EnumMap对象的使用

        System.out.println("演示EnmuMap对象的使用和遍历.....");

        testEnumMap();

        // 3.演示EnmuSet的使用

        System.out.println("演示EnmuSet对象的使用和遍历.....");

        testEnumSet();

    }

    *//**
     *
     * 演示枚举类型的遍历
     *//*

    private static void testTraversalEnum() {

        Light[] allLight = Light.values();

        for (Light aLight : allLight) {

            System.out.println("当前灯name：" + aLight.name());

            System.out.println("当前灯ordinal：" + aLight.ordinal());

            System.out.println("当前灯：" + aLight);

        }

    }

    *//**
     *
     * 演示EnumMap的使用，EnumMap跟HashMap的使用差不多，只不过key要是枚举类型
     *//*

    private static void testEnumMap() {


        // 1.演示定义EnumMap对象，EnumMap对象的构造函数需要参数传入,默认是key的类的类型

        EnumMap<Light, String> currEnumMap = new EnumMap<Light, String>(

                Light.class);

        currEnumMap.put(Light.RED, "红灯");

        currEnumMap.put(Light.GREEN, "绿灯");

        currEnumMap.put(Light.YELLOW, "黄灯");

        // 2.遍历对象

        for (Light aLight : Light.values()) {

            System.out.println("[key=" + aLight.name() + ",value="

                    + currEnumMap.get(aLight) + "]");

        }

    }

    *//**
     *
     * 演示EnumSet如何使用，EnumSet是一个抽象类，获取一个类型的枚举类型内容<BR/>
     *
     * 可以使用allOf方法
     *//*

    private static void testEnumSet() {

        EnumSet<Light> currEnumSet = EnumSet.allOf(Light.class);

        for (Light aLightSetElement : currEnumSet) {

            System.out.println("当前EnumSet中数据为：" + aLightSetElement);

        }

    }

}*/
/*        执行结果如下：

        演示枚举类型的遍历 ......

        当前灯name：RED

        当前灯ordinal：0

        当前灯：1

        当前灯name：GREEN

        当前灯ordinal：1

        当前灯：3

        当前灯name：YELLOW

        当前灯ordinal：2

        当前灯：2

        演示EnmuMap对象的使用和遍历.....

        [key=RED,value=红灯]

        [key=GREEN,value=绿灯]

        [key=YELLOW,value=黄灯]

        演示EnmuSet对象的使用和遍历.....

        当前EnumSet中数据为：1

        当前EnumSet中数据为：3

        当前EnumSet中数据为：2
*/


/*public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER;
    public static Season valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
}*/

//Enum类提供了一个ordinal()方法，用来返回枚举对象的序数，
// 比如本例中SPRING, SUMMER, AUTUMN, WINTER的序数就分别为0, 1, 2, 3。





