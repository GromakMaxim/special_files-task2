import java.util.List;

@FunctionalInterface
interface CSVReadable {
    List<Employee> csvRead(String[] tableHeader, String csvTitle);
}