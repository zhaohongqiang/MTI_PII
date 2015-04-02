package com.mednovo.tools;

//import java.util.EnumMap;

public class CommandsFound {

    public final static int SET_FACTORYDATA = 0;//���ó�������
    public final static int READ_FACTORYDATA = 1; //��ȡ��������
    public final static int SET_SYSSETDATA = 2;//����ϵͳ��������
    public final static int READ_SYSSETDATA = 3;//��ȡϵͳ��������

    public final static int SET_BASALDATA = 4;//���û���������
    public final static int READ_BASALDATA = 5;//��ȡ����������

    public final static int READ_RUNNINGDATA = 6;//��ȡ����״̬����

    public final static int READ_MEALBOLUSRECORD = 7;//��ȡ��ʱ�������¼
    public final static int READ_TIMEBOLUSRECORD = 8;//��ȡ��ʱ�������¼

    public final static int READ_SUSPENDEDPUMPRECORD = 9;//��ȡ��ͣ�ü�¼
    public final static int READ_BASALRECORD = 10;//��ȡ�����ʼ�¼
    public final static int READ_TEMPORARYRECORD = 11;//��ȡ��ʱ�ʼ�¼

    public final static int READ_DAILYTOTALRECORD = 12;//��ȡ��������¼
    public final static int READ_EVENTRECORD = 13;//��ȡ�¼���¼
    public final static int READ_EXHAUSTRECORD = 14;//��ȡ������¼
}//���峣��



/*
public enum CommandsEnum {//�������� ö��

    SET_FACTORYDATA,//���ó�������
    READ_FACTORYDATA, //��ȡ��������
    SET_SYSSETDATA,//����ϵͳ�������� 
    READ_SYSSETDATA,//��ȡϵͳ�������� 

    SET_BASALDATA,//���û��������� 
    READ_BASALDATA,//��ȡ���������� 

    READ_RUNNINGDATA,//��ȡ����״̬���� 

    READ_MEALBOLUSRECORD,//��ȡ��ʱ�������¼ 
    READ_TIMEBOLUSRECORD,//��ȡ��ʱ�������¼ 

    READ_SUSPENDEDPUMPRECORD,//��ȡ��ͣ�ü�¼ 
    READ_BASALRECORD,//��ȡ�����ʼ�¼ 
    READ_TEMPORARYRECORD,//��ȡ��ʱ�ʼ�¼ 

    READ_DAILYTOTALRECORD,//��ȡ��������¼
    READ_EVENTRECORD,//��ȡ�¼���¼
    READ_EXHAUSTRECORD//��ȡ������¼
}//15��
*/
/*public enum CommandsEnum {

    // ���ù��캯������
    SET_FACTORYDATA(0),//���ó�������
    READ_FACTORYDATA(1), //��ȡ��������
    SET_SYSSETDATA(2),//����ϵͳ��������
    READ_SYSSETDATA(3),//��ȡϵͳ��������

    SET_BASALDATA(4),//���û���������
    READ_BASALDATA(5),//��ȡ����������

    READ_RUNNINGDATA(6),//��ȡ����״̬����

    READ_MEALBOLUSRECORD(7),//��ȡ��ʱ�������¼
    READ_TIMEBOLUSRECORD(8),//��ȡ��ʱ�������¼

    READ_SUSPENDEDPUMPRECORD(9),//��ȡ��ͣ�ü�¼
    READ_BASALRECORD(10),//��ȡ�����ʼ�¼
    READ_TEMPORARYRECORD(11),//��ȡ��ʱ�ʼ�¼

    READ_DAILYTOTALRECORD(12),//��ȡ��������¼
    READ_EVENTRECORD(13),//��ȡ�¼���¼
    READ_EXHAUSTRECORD(14);//��ȡ������¼

    // ����˽�б���
    private int nCode;

    // ���캯����ö������ֻ��Ϊ˽��
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

//=======================����˵��==================================================
//����ֻ�ܹ���ʾ����ơ��̵ƺͻƵƣ����Ǿ����ֵ����û�취��ʾ������
// �𼱣���Ȼö�������ṩ�˹��캯�������ǿ���ͨ�����캯���͸�дtoString������ʵ�֡�
// ���ȸ�Lightö���������ӹ��췽����Ȼ��ÿ��ö�����͵�ֵͨ�����캯�������Ӧ�Ĳ�����
// ͬʱ��дtoString�������ڸ÷����з��شӹ��캯���д���Ĳ����������Ĵ������£�


/*public enum Light {

    // ���ù��캯������
    RED(1), GREEN(3), YELLOW(2);

    // ����˽�б���
    private int nCode;

    // ���캯����ö������ֻ��Ϊ˽��
    private Light(int _nCode) {

        this.nCode = _nCode;

    }

    @Override
    public String toString() {

        return String.valueOf(this.nCode);

    }

}*/


//ö�����͵�������ʾ�������£�


/*public class LightTest {

    // 1.����ö������

    public enum Light {

        // ���ù��캯������

        RED(1), GREEN(3), YELLOW(2);

        // ����˽�б���

        private int nCode;

        // ���캯����ö������ֻ��Ϊ˽��

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

        // 1.����ö������

        System.out.println("��ʾö�����͵ı��� ......");

        testTraversalEnum();

        // 2.��ʾEnumMap�����ʹ��

        System.out.println("��ʾEnmuMap�����ʹ�úͱ���.....");

        testEnumMap();

        // 3.��ʾEnmuSet��ʹ��

        System.out.println("��ʾEnmuSet�����ʹ�úͱ���.....");

        testEnumSet();

    }

    *//**
     *
     * ��ʾö�����͵ı���
     *//*

    private static void testTraversalEnum() {

        Light[] allLight = Light.values();

        for (Light aLight : allLight) {

            System.out.println("��ǰ��name��" + aLight.name());

            System.out.println("��ǰ��ordinal��" + aLight.ordinal());

            System.out.println("��ǰ�ƣ�" + aLight);

        }

    }

    *//**
     *
     * ��ʾEnumMap��ʹ�ã�EnumMap��HashMap��ʹ�ò�ֻ࣬����keyҪ��ö������
     *//*

    private static void testEnumMap() {


        // 1.��ʾ����EnumMap����EnumMap����Ĺ��캯����Ҫ��������,Ĭ����key���������

        EnumMap<Light, String> currEnumMap = new EnumMap<Light, String>(

                Light.class);

        currEnumMap.put(Light.RED, "���");

        currEnumMap.put(Light.GREEN, "�̵�");

        currEnumMap.put(Light.YELLOW, "�Ƶ�");

        // 2.��������

        for (Light aLight : Light.values()) {

            System.out.println("[key=" + aLight.name() + ",value="

                    + currEnumMap.get(aLight) + "]");

        }

    }

    *//**
     *
     * ��ʾEnumSet���ʹ�ã�EnumSet��һ�������࣬��ȡһ�����͵�ö����������<BR/>
     *
     * ����ʹ��allOf����
     *//*

    private static void testEnumSet() {

        EnumSet<Light> currEnumSet = EnumSet.allOf(Light.class);

        for (Light aLightSetElement : currEnumSet) {

            System.out.println("��ǰEnumSet������Ϊ��" + aLightSetElement);

        }

    }

}*/
/*        ִ�н�����£�

        ��ʾö�����͵ı��� ......

        ��ǰ��name��RED

        ��ǰ��ordinal��0

        ��ǰ�ƣ�1

        ��ǰ��name��GREEN

        ��ǰ��ordinal��1

        ��ǰ�ƣ�3

        ��ǰ��name��YELLOW

        ��ǰ��ordinal��2

        ��ǰ�ƣ�2

        ��ʾEnmuMap�����ʹ�úͱ���.....

        [key=RED,value=���]

        [key=GREEN,value=�̵�]

        [key=YELLOW,value=�Ƶ�]

        ��ʾEnmuSet�����ʹ�úͱ���.....

        ��ǰEnumSet������Ϊ��1

        ��ǰEnumSet������Ϊ��3

        ��ǰEnumSet������Ϊ��2
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

//Enum���ṩ��һ��ordinal()��������������ö�ٶ����������
// ���籾����SPRING, SUMMER, AUTUMN, WINTER�������ͷֱ�Ϊ0, 1, 2, 3��





