import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodStruct {
    public Method method;
    public String annotationName = null;
    public Annotation annotation = null;
    public boolean methodRuned = false;
    public boolean tested = false;

    public MethodStruct(Method method) {
        this.method = method;
    }
}
