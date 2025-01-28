//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {
        Student student1 = new Student("МГУ", "Росиия", "Петров Петр Петрович", "119991, Российская Федерация, Москва, Ленинские горы, д.1", 18, 3,4,3,5,5,5);
        TestRunner.setObject(student1);
        TestRunner.runTests(student1.getClass());
      }
}