package com.feilib.tool;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: pack_tool_src
 * @Package: com.bigun.packtool.test
 * @ClassName: MyGson
 * @Description: 处理Gson解析异常，返回默认值
 * @Author: murphy
 * @CreateDate: 2021/7/13 1:53 下午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/7/13 1:53 下午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class GsonUtil {
    private static Gson gson;
    public static String className;

    private Gson getGson(){
        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapterFactory(new GsonAdapter())
                    .create();
        }
        return gson;
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        className = classOfT.getName();
        return getGson().fromJson(json, classOfT);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return getGson().fromJson(json, typeOfT);
    }

    public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        className = classOfT.getName();
        return getGson().fromJson(json, classOfT);
    }

    public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        return getGson().fromJson(json, typeOfT);
    }


    public static class GsonAdapter implements TypeAdapterFactory {
        @Override
        public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return createCustomTypeAdapter(delegate);
        }

        private <T> TypeAdapter<T> createCustomTypeAdapter(TypeAdapter<T> delegate) {
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    try {
                        return delegate.read(in);
                    } catch (Exception e) {
                        System.out.println("出现解析异常,解析异常字段：" + in.getPath());

                        //通过反射设置默认值
                        T reflexResult = getReflexDefault(in);
                        if (reflexResult != null) {
                            return reflexResult;
                        }
                        System.out.println("解析异常，反射设置值失败");

                        //通过正常异常处理
                        return getDefault(in);
                    }
                }
            };
        }

        private <T> T getReflexDefault(JsonReader in) {
            try {
                if(className == null || "".equals(className)){
                    return null;
                }

                Class<?> ParamsModel = Class.forName(className);
                Field reflectionField = ParamsModel.getField(in.getPath().replace("$.", ""));//获得私有方法
                Class<?> fieldClass = reflectionField.getType();
                System.out.println("className解析异常，字段" + in.getPath() + "返回默认值");

                T t = null;
                if (fieldClass.equals(String.class) ||
                        fieldClass.equals(char.class)) {
                    t = (T) "";
                } else if (fieldClass.equals(Integer.class) ||
                        fieldClass.equals(Double.class) ||
                        fieldClass.equals(float.class) ||
                        fieldClass.equals(long.class)) {
                    t = (T) (Integer) 0;
                } else if (fieldClass.equals(Map.class)) {
                    t = (T) new HashMap<>();
                } else if (fieldClass.equals(boolean.class)) {
                    t = (T) (Boolean) false;
                } else if (fieldClass.equals(List.class)) {
                    t = (T) new ArrayList<>();
                }

                if (t != null) {
                    in.skipValue();
                    return t;
                }

            } catch (Exception classNotFoundException) {
                classNotFoundException.printStackTrace();
            }
            return null;
        }

        private <T> T getDefault(JsonReader in) {
            System.out.println("解析异常，通过正常异常处理手段：");
            try {
                in.skipValue();

                switch (in.peek()) {
                    case STRING:    //字符串
                        return (T) "";
                    case BEGIN_ARRAY:   //数组
                        return (T) new ArrayList();
                    case BOOLEAN:   //bool
                        return (T) (Boolean) false;
                    case NUMBER:    //double
                        return (T) (Double) 0.00;
                    case BEGIN_OBJECT:
                        return (T) new HashMap<>();
                    default:
                        return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
