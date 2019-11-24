package com.fedorizvekov.service;

import static java.lang.System.arraycopy;
import static java.util.Arrays.stream;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Function;
import java.util.stream.Collectors;
import jdk.internal.reflect.ReflectionFactory;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DynamicCreationEnum {

    private static final ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();


    /**
     * Adds a new enum instance to the specified enum class.
     *
     * @param <T>      the type of the enum (implicit)
     * @param enumType the class of the enum to be modified.
     * @param enumName the name of the new enum instance to be added to the class.
     * @throws RuntimeException if an exception occurs during the operation, including reflection-related issues.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> void addEnum(Class<T> enumType, String enumName) {

        try {
            var enumValuesField = stream(enumType.getDeclaredFields())
                    .filter(field -> field.getName().contains("$VALUES"))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchFieldException("$VALUES field not found"));

            AccessibleObject.setAccessible(new Field[]{enumValuesField}, true);
            var previousValues = (T[]) enumValuesField.get(enumType);
            var enumMap = stream(previousValues).collect(Collectors.toMap(Enum::name, Function.identity()));

            if (!enumMap.containsKey(enumName.toUpperCase())) {

                var newValue = (T) makeEnum(enumType, enumName.toUpperCase(), enumMap.size(), new Class<?>[]{}, new Object[]{});
                enumMap.put(newValue.name(), newValue);
                setFinalStaticField(enumValuesField, null, enumMap.values().toArray((T[]) Array.newInstance(enumType, 0)));
                cleanEnumCache(enumType);

            } else {
                log.warn("Variable '{}' is already defined in the scope", enumName);
            }

        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }


    private static <T> Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception {

        var types = new Class[additionalTypes.length + 2];
        types[0] = String.class;
        types[1] = int.class;
        arraycopy(additionalTypes, 0, types, 2, additionalTypes.length);

        var constructor = enumClass.getDeclaredConstructor(types);
        constructor.setAccessible(true);

        var params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;
        arraycopy(additionalValues, 0, params, 2, additionalValues.length);

        var constructorAccessor = reflectionFactory.newConstructorAccessor(constructor);
        return enumClass.cast(constructorAccessor.newInstance(params));
    }


    @SneakyThrows
    private static void setFinalStaticField(Field field, Object target, Object value) {

        field.setAccessible(true);

        var getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
        getDeclaredFields0.setAccessible(true);
        var fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);

        var modifiersField = stream(fields)
                .filter(declaredField -> "modifiers".equals(declaredField.getName()))
                .findFirst()
                .orElse(null);

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, modifiersField.getInt(field) & ~Modifier.FINAL);

        var fieldAccessor = reflectionFactory.newFieldAccessor(field, true);
        fieldAccessor.set(target, value);
    }


    private static void cleanEnumCache(Class<?> enumClass) {
        stream(Class.class.getDeclaredFields())
                .filter(field -> field.getName().contains("enumConstantDirectory") || field.getName().contains("enumConstants"))
                .forEach(field -> {
                    field.setAccessible(true);
                    setFinalStaticField(field, enumClass, null);
                });
    }

}
