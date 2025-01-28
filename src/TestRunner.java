import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class TestRunner {
    // Массивы функций с разными параметрами:
    private static ArrayList<String> parString = new ArrayList<>(Arrays.asList("setUnivercityName", "setCountryName", "setName", "setAddressReg"));
    private static ArrayList<String> parInt = new ArrayList<>(Arrays.asList("setAge"));
    private static ArrayList<String> parIntArr = new ArrayList<>(Arrays.asList("setEstimation"));
    // Переменные для работы с методами:
    private static Map<MethodStruct, Integer> testMap = new HashMap<>();
    private static Map<MethodStruct, Integer> suiteMap = new HashMap<>();
    private static Map<MethodStruct, Integer> beforeTestMap = new HashMap<>();
    private static Map<MethodStruct, Integer> afterTestMap = new HashMap<>();
    private static Map<MethodStruct, Integer> csvSourceMap = new HashMap<>();
    private static int countBeforeSuite = 0;
    private static int countAfterSuite = 0;
    private static Object object = null;

    public static void setObject(Object object) {
        TestRunner.object = object;
    }

    private static MethodStruct getCsvSourceMethod(Method method, Annotation annotation) {
        MethodStruct metodStruct = new MethodStruct(method);
        metodStruct.annotationName = "CsvSource";
        metodStruct.annotation = annotation;
        csvSourceMap.put(metodStruct, 0);
        return metodStruct;
    }

    private static void runCsvSourceMethod(MethodStruct metodStruct, Annotation annotation) {
        if ( object != null ) {
            if ( Modifier.isPrivate(metodStruct.method.getModifiers()) ) metodStruct.method.setAccessible(true);
            try {
                Class<?>[] parameters = metodStruct.method.getParameterTypes();
                String[] parvalues = ((CsvSource)annotation).value().split(",");
                Object[]  parObjArr = new Object[parvalues.length];
                int i = 0;
                for (Class<?> parameter : parameters) {
                    if (parameter.getName()  == "java.lang.String") {
                        parObjArr[i] = parvalues[i];
                    } else if (parameter.getName() == "int") {
                        parObjArr[i] = Integer.parseInt(parvalues[i]);
                    } else if (parameter.getName() == "double") {
                        parObjArr[i] = Double.parseDouble(parvalues[i]);
                    } else if (parameter.getName() == "boolean") {
                        parObjArr[i] = Boolean.parseBoolean(parvalues[i]);
                    }
                    i++;
                }
                metodStruct.method.invoke(object, parObjArr);
                metodStruct.methodRuned = true;
            } catch (Exception e) {
                metodStruct.methodRuned = false;
            }
        }
    }

    private static MethodStruct getBeforeTestMethod(Method method) {
        MethodStruct metodStruct = new MethodStruct(method);
        metodStruct.annotationName = "BeforeTest";
        beforeTestMap.put(metodStruct, 0);
        return metodStruct;
    }

    private static void runBeforeAfterTestMethod(MethodStruct metodStruct, MethodStruct testMethodStruct) {
        if (object != null) {
            if (Modifier.isPrivate(metodStruct.method.getModifiers())) metodStruct.method.setAccessible(true);
            try {
                metodStruct.method.invoke(object, testMethodStruct);
                metodStruct.methodRuned = true;
            } catch (Exception e) {
                metodStruct.methodRuned = false;
            }
        }
    }

    private static MethodStruct getAfterTestMethod(Method method) {
        MethodStruct metodStruct = new MethodStruct(method);
        metodStruct.annotationName = "AfterTest";
        afterTestMap.put(metodStruct, 0);
        return metodStruct;
    }

    private static MethodStruct getTestMetod(Method method, Annotation annotation, String errorMessage) throws TestException {
        MethodStruct metodStruct = new MethodStruct(method);
        Test testAnnotation = (Test) annotation;
        if (testAnnotation.priority() > 0 && testAnnotation.priority() < 11) {
            metodStruct.annotationName = "Test";
            testMap.put(metodStruct, testAnnotation.priority());
            return metodStruct;
        } else throw new TestException(errorMessage);
    }

    private static void runTestMetod(MethodStruct metodStruct) throws IllegalAccessException, InvocationTargetException {
        if (object != null) {
            if (Modifier.isPrivate(metodStruct.method.getModifiers())) metodStruct.method.setAccessible(true);
            try {
                if (parString.contains(metodStruct.method.getName())) {
                    metodStruct.method.invoke(object, "Тест " + metodStruct.method.getName());
                } else if (parInt.contains(metodStruct.method.getName())) {
                    metodStruct.method.invoke(object, 20);
                } else if (parIntArr.contains(metodStruct.method.getName())) {
                    int[] myArray = {2, 2, 3, 3, 4, 4, 5, 5};
                    metodStruct.method.invoke(object, myArray);
                } else {
                    metodStruct.method.invoke(object);
                }
                metodStruct.methodRuned = true;
            } catch (Exception e) {
                metodStruct.methodRuned = false;
            }
        }
    }

    private static MethodStruct getStaticMethod(Method method, Annotation annotation, String firstEroorMess, String secondErrorMess) throws TestException {
        if (Modifier.isStatic(method.getModifiers())) {
            MethodStruct metodStruct = new MethodStruct(method);
            if (annotation instanceof BeforeSuite) {
                countBeforeSuite++;
                if (countBeforeSuite > 1) throw new TestException(firstEroorMess);
                metodStruct.annotationName = "BeforeSuite";
            } else if (annotation instanceof AfterSuite) {
                countAfterSuite++;
                if (countAfterSuite > 1) throw new TestException(firstEroorMess);
                metodStruct.annotationName = "AfterSuite";
            }
            suiteMap.put(metodStruct, 0);
            return metodStruct;
        } else throw new TestException(secondErrorMess);
    }

    private static void runStaticMethod(MethodStruct metodStruct) throws IllegalAccessException, InvocationTargetException {
        if (object != null) {
            if (Modifier.isPrivate(metodStruct.method.getModifiers())) metodStruct.method.setAccessible(true);
            try {
                if (metodStruct.method.getName() == "getAverageEstRnd")
                    metodStruct.method.invoke(object, ((Student) object).getEstimation());
                else metodStruct.method.invoke(object);
                metodStruct.methodRuned = true;
            } catch (Exception e) {
                metodStruct.methodRuned = false;
            }
        }
    }

    public static void runTests(Class c) throws TestException, IllegalAccessException, InvocationTargetException
    {
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof Test) {
                    MethodStruct metodStruct = getTestMetod(method, annotation, "Приоритет метода с аннотацией @Test не попадает в требуемый диапазон [1, 10]");
                } else if (annotation instanceof BeforeTest) {
                    getBeforeTestMethod(method);
                } else if (annotation instanceof AfterTest) {
                    getAfterTestMethod(method);
                } else if (annotation instanceof BeforeSuite) {
                    MethodStruct metodStruct = getStaticMethod(method, annotation, "Количество методов, помеченных аннотацией @BeforeSuite > 1", "Метод, помеченный аннотацией @BeforeSuite не static");
                } else if (annotation instanceof AfterSuite) {
                    MethodStruct metodStruct = getStaticMethod(method, annotation, "Количество методов, помеченных аннотацией @AfterSuite > 1", "Метод, помеченный аннотацией @AfterSuite не static");
                } else if (annotation instanceof CsvSource) {
                    getCsvSourceMethod(method, annotation);
                }
            }
        }
        // Создаем List-ы для запуска методов
        List<Map.Entry<MethodStruct, Integer>> listSuite = new ArrayList<>(suiteMap.entrySet());
        //Сортируем методы помеченные @Test в порядке убывания приоритета
        List<Map.Entry<MethodStruct, Integer>> litsTest = new ArrayList<>(testMap.entrySet().stream().sorted(Map.Entry.<MethodStruct, Integer>comparingByValue().reversed()).toList());
        List<Map.Entry<MethodStruct, Integer>> listBeforeTest = new ArrayList<>(beforeTestMap.entrySet());
        List<Map.Entry<MethodStruct, Integer>> listAfterTest = new ArrayList<>(afterTestMap.entrySet());
        List<Map.Entry<MethodStruct, Integer>> listcsvSource = new ArrayList<>(csvSourceMap.entrySet());

        System.out.println("-------------------------------");
        listSuite.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));
        System.out.println("-------------------------------");
        litsTest.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));

        // Запускаем методы помеченные аннотацией BeforeSuite
        for (Map.Entry<MethodStruct, Integer> methodStruct : listSuite) {
            if (methodStruct.getKey().annotationName == "BeforeSuite")
                runStaticMethod(methodStruct.getKey());
        }
        System.out.println("-------------------------------");
        listSuite.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));

        // Запускаем методы помеченные аннотацией Test
        for (Map.Entry<MethodStruct, Integer> methodStruct : litsTest) {
            if (methodStruct.getKey().annotationName == "Test") {
                listBeforeTest.forEach((metods) -> runBeforeAfterTestMethod(metods.getKey(), methodStruct.getKey()));
                runTestMetod(methodStruct.getKey());
                listAfterTest.forEach((metods) -> runBeforeAfterTestMethod(metods.getKey(), methodStruct.getKey()));
            }
        }

        System.out.println("-------------------------------");
        litsTest.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));
        // Запускаем методы помеченные аннотацией AfterSuite
        for (Map.Entry<MethodStruct, Integer> methodStruct : listSuite) {
            if (methodStruct.getKey().annotationName == "AfterSuite")
                runTestMetod(methodStruct.getKey());
        }

        System.out.println("-------------------------------");
        listSuite.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));
        System.out.println("-------------------------------");
        System.out.println("-------------------------------");
        listBeforeTest.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));
        listAfterTest.forEach((metods) -> System.out.println(metods.getKey().method.getName() + " " + metods.getKey().annotationName + " " + metods.getKey().methodRuned + " " + metods.getKey().tested + " -> " + metods.getValue()));

        for (Map.Entry<MethodStruct, Integer> methodStruct : listcsvSource) {
            if (methodStruct.getKey().annotationName == "CsvSource")
                runCsvSourceMethod(methodStruct.getKey(), methodStruct.getKey().annotation);
        }
    }
}


