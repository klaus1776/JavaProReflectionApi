import java.util.Arrays;
import java.util.stream.IntStream;

public class Student {
    // Наименование университета
    private static String univercityName;
    // Наименование страны
    private static String countryName;
    // Количество студентов
    private static int studCount = 0;
    // ФИО студента
    private String name;
    // Адрес регистрации студента
    private String addressReg;
    // Возраст студента
    private int age;
    // Оценки студента
    private int[] estimation;

    public Student(String univercityName, String countryName, String name, String addressReg, int age, int... estimation) {
        Student.univercityName = univercityName;
        Student.countryName = countryName;
        this.name = name;
        this.addressReg = addressReg;
        this.age = age;
        this.estimation = estimation;

        Student.studCount ++;
    }

    public Student(String name, String addressReg, int age, int... estimation) {
        this.name = name;
        this.addressReg = addressReg;
        this.age = age;
        this.estimation = estimation;

        Student.studCount ++;
    }

    @Test
    @CsvSource(value = "Василий Иванов, 20, 4.34, false")
    public static void printStudentInfo(String name, int age, double averageScore, boolean expelled) {
        System.out.println( "Студент{" +
                            "Имя = '" + name + '\'' +
                            "; возраст = '" + age +
                            "; средний балл = " + averageScore +
                            "; отчислен = " + expelled +
                            '}' );
    }

    @BeforeTest
    public static void beforeTest() {
        System.out.println("BeforeTest");
    }

    @AfterTest
    public static void afterTest() {
        System.out.println("AfterTest");
    }

    //@Test(priority = 9)
    public static void setUnivercityName(String univercityName) {
        Student.univercityName = univercityName;
    }
    //@Test(priority = 10)
    public static void setCountryName(String countryName) {
        Student.countryName = countryName;
    }
    //@Test(priority = 2)
    public void setName(String name) {
        this.name = name;
    }
    //@Test(priority = 3)
    public void setAddressReg(String addressReg) {
        this.addressReg = addressReg;
    }
    //@Test(priority = 1)
    public void setAge(int age) {
        this.age = age;
    }
    //@Test(priority = 4)
    public void setEstimation(int... estimation) {
        this.estimation = Arrays.copyOf(estimation, estimation.length);
    }

    @Test
    public static String getUnivercityName() {
        System.out.println("Test #5");
        return univercityName;
    }
    @Test(priority = 3)
    public static String getCountryName() {
        System.out.println("Test #3");
        return countryName;
    }

    @Test(priority = 4)
    public String getName() {
        System.out.println("Test #4");
        return name;
    }
    @Test(priority = 6)
    public String getAddressReg() {
        System.out.println("Test #6");
        return addressReg;
    }
    @Test(priority = 8)
    public int getAge() {
        System.out.println("Test #8");
        return age;
    }

    @AfterSuite
    public static int getStudCount() {
        System.out.println("AfterSuite");
        return studCount;
    }

    public int[] getEstimation() {
        return Arrays.copyOf(estimation, estimation.length);
    }

    // Вычисление средней оценки для заданного набора оценок
    public static long getAverageEstRnd(int[] estimations) {
        return Math.round( Arrays.stream(estimations).average().getAsDouble() );
    }
    @BeforeSuite
    public static void runBeforeSuite() {
        System.out.println("BeforeSuite");
    }

    // Вычисление среднего значения оценок студента
    public double getAverageEst(){
        return Arrays.stream(estimation).average().getAsDouble();
    }

    @Override
    public String toString() {
        return "Student{" +
                "univercityName='" + univercityName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", name='" + name + '\'' +
                ", addressReg='" + addressReg + '\'' +
                ", age=" + age +
                ", estimation=" + Arrays.toString(estimation) +
                '}';
    }
}
