package com.fedorizvekov.service;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

public class DynamicCreationEnum {

    private static final Logger log = LoggerFactory.getLogger(DynamicCreationEnum.class);
    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();


    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumType, String enumName) {

        try {

            Field enumValuesField = null;
            Field[] fields = enumType.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().contains("$VALUES")) {
                    enumValuesField = field;
                    break;
                }
            }

            AccessibleObject.setAccessible(new Field[]{enumValuesField}, true);
            T[] previousValues = (T[]) enumValuesField.get(enumType);

            Map<String, T> values = new HashMap<>(previousValues.length + 1);

            for (T enm : previousValues) {
                values.put(enm.name(), enm);
            }

            if (!values.containsKey(enumName.toUpperCase())) {

                T newValue = (T) makeEnum(enumType, enumName.toUpperCase(), values.size(), new Class<?>[]{}, new Object[]{});
                values.put(newValue.name(), newValue);
                setFinalStaticField(enumValuesField, null, values.values().toArray((T[]) Array.newInstance(enumType, 0)));
                cleanEnumCache(enumType);

            } else {
                log.warn("Variable '{}' is already defined in the scope", enumName);
            }

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }


    private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException {

        Class<?>[] types = new Class[additionalTypes.length + 2];
        types[0] = String.class;
        types[1] = int.class;
        System.arraycopy(additionalTypes, 0, types, 2, additionalTypes.length);

        Constructor<?> constructor = enumClass.getDeclaredConstructor(types);
        constructor.setAccessible(true);

        Object[] params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);

        ConstructorAccessor constructorAccessor = reflectionFactory.newConstructorAccessor(constructor);
        return enumClass.cast(constructorAccessor.newInstance(params));
    }


    private static void setFinalStaticField(Field field, Object target, Object value) throws IllegalAccessException, NoSuchFieldException {

        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);

        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);

        FieldAccessor fieldAccessor = reflectionFactory.newFieldAccessor(field, false);
        fieldAccessor.set(target, value);
    }


    private static void cleanEnumCache(Class<?> enumClass) throws IllegalAccessException, NoSuchFieldException {

        for (Field field : Class.class.getDeclaredFields()) {

            String fieldName = field.getName();

            if (fieldName.contains("enumConstantDirectory") || fieldName.contains("enumConstants")) {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setFinalStaticField(field, enumClass, null);
            }
        }
    }

}
