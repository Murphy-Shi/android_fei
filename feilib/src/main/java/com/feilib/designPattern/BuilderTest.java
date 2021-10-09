package com.feilib.designPattern;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/19 3:30 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/19 3:30 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class BuilderTest {
    private String name;
    private String age;

    public String getName() {
        return name;
    }

    public BuilderTest setName(String name) {
        this.name = name;
        return this;
    }

    public String getAge() {
        return age;
    }

    public BuilderTest setAge(String age) {
        this.age = age;
        return this;
    }
}
