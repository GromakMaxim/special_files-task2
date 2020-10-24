import java.io.IOException;
import java.util.List;

@FunctionalInterface
interface JSONWritable {
    void createJSON(String resultPath, List<Employee> employeeArr) throws IOException;
}