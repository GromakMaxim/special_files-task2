import java.util.List;

@FunctionalInterface
interface XMLReadable {
    List<Employee> xmlRead(String xmlTitle);
}