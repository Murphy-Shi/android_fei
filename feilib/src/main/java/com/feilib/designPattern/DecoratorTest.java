package com.feilib.designPattern;

import android.util.Log;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/19 4:10 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/19 4:10 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 * 1.装饰者模式：动态地给一个对象添加一些额外的职责。就增加功能来说，装饰模式相比生成子类更为灵活。
 *
 * 2.装饰者模式属于结构型模式。
 *   装饰者模式在生活中应用实际上也非常广泛，一如一间房，放上厨具，它就是厨房;放上床，就是卧室。
 *   通常我们扩展类的功能是通过继承的方式来实现，但是装饰者模式是通过组合的方式来实现，这是继承的替代方案之一。
 *
 * 3.Component（抽象组件）：接口或者抽象类，被装饰的最原始的对象。具体组件与抽象装饰角色的父类。
 * ConcreteComponent（具体组件）：实现抽象组件的接口。
 * Decorator（抽象装饰角色）：一般是抽象类，抽象组件的子类，同时持有一个被装饰者的引用，用来调用被装饰者的方法;同时可以给被装饰者增加新的职责。
 * ConcreteDecorator（具体装饰类）：抽象装饰角色的具体实现。
 */
public class DecoratorTest {
    private String TAG = "Decorator";

    DecoratorTest() {
        Room room = new NewRoom();
        RoomDecorator bedroom = new Bedroom(room);
        bedroom.fitment();  //装修成卧室
        RoomDecorator kitchen = new Kitchen(room);
        kitchen.fitment(); //装修成厨房
    }

    //1. 抽象房子，定义一个装修方法
    public abstract static class Room {
        public abstract void fitment();
    }

    //2. 创建具体组件，电
    public class NewRoom extends Room {
        @Override
        public void fitment() {
            Log.i(TAG, "fitment: 装电");
        }
    }

    //3. 定义抽象的房间装饰类
    public abstract static class RoomDecorator extends Room {
        //持有者被装饰者的引用，这里是需要装修的房间
        private final Room mRoom;

        public RoomDecorator(Room room) {
            this.mRoom = room;
        }

        @Override
        public void fitment() {
            mRoom.fitment();
        }
    }

    //4. 创建具体装饰类
    public class Bedroom extends RoomDecorator {

        public Bedroom(Room room) {
            super(room);
        }

        @Override
        public void fitment() {
            super.fitment();
            addBedding();
        }

        private void addBedding() {
            Log.i(TAG, "addNedding: 装饰成卧室，添加卧具");
        }
    }

    // 4.1 创建厨房
    public class Kitchen extends RoomDecorator {

        public Kitchen(Room room) {
            super(room);
        }

        @Override
        public void fitment() {
            super.fitment();
            addKitchenware();
        }

        private void addKitchenware() {
            Log.i(TAG, "addKitchenware: 装饰成厨房");
        }
    }
}
