import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        // >_< продолжаю практиковать лямбды
        CSVReadable csvParser = (tableHeader, csvTitle) -> {
            try (CSVReader csvReader = new CSVReader(new FileReader(csvTitle))) {
                ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();//определяем "стратегию"
                strategy.setType(Employee.class);//данные из csv привяязаны к классу employee
                strategy.setColumnMapping(tableHeader);//обозначаем "шапку"
                CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                        .withMappingStrategy(strategy)//взаимодействие документа и выбранной стратегии
                        .build();
                return csv.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        };

        //записываем результат в файл
        JSONWritable jsonWriter = (resultPath, employee) -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(resultPath), employee);
        };

        //читаем XMLфайл
        XMLReadable xmlParser = (xmlTitle)->{
            List<Employee> employeeArrXML = new ArrayList<>();
            try {
                DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();// Создается построитель документа
                Document document = documentBuilder.parse(xmlTitle);// Создается дерево DOM документа из файла
                Node root = document.getDocumentElement();// Получаем корневой элемент

                NodeList staff = root.getChildNodes();
                for (int i = 0; i < staff.getLength(); i++) {// Просматриваем все подэлементы корневого
                    Node employee = staff.item(i);
                    if (employee.getNodeType() != Node.TEXT_NODE) {// Если нода не текст, то это employee - заходим внутрь
                        NodeList employeeData = employee.getChildNodes();
                        Employee em = new Employee();
                        for (int j = 0; j < employeeData.getLength(); j++) {
                            Node employeeProperty = employeeData.item(j);
                            if (employeeProperty.getNodeType() != Node.TEXT_NODE) {// Если нода не текст, то это один из параметров employee
                                switch (employeeProperty.getNodeName()) {
                                    case "id" -> em.setId(Long.parseLong(employeeProperty.getChildNodes().item(0).getTextContent()));
                                    case "firstName" -> em.setFirstName(employeeProperty.getChildNodes().item(0).getTextContent());
                                    case "lastName" -> em.setLastName(employeeProperty.getChildNodes().item(0).getTextContent());
                                    case "country" -> em.setCountry(employeeProperty.getChildNodes().item(0).getTextContent());
                                    case "age" -> em.setAge(Integer.parseInt(employeeProperty.getChildNodes().item(0).getTextContent()));
                                }
                            }
                        }
                        employeeArrXML.add(em);
                    }
                }
                return employeeArrXML;
            } catch (ParserConfigurationException | IOException | SAXException ex) {
                ex.printStackTrace(System.out);
            }
            return employeeArrXML;
        };

        List<Employee> employeeArr = csvParser.csvRead(new String[]{"id", "firstName", "lastName", "country", "age"}, "example.csv");
        employeeArr.forEach(System.out::println);//выводим в консольку содержимое csv

        jsonWriter.createJSON("newJSONfile.json", employeeArr);//создаём json
        jsonWriter.createJSON("fromXMLtoJSON.json", xmlParser.xmlRead("data.xml")); //создаём json из xml
    }
}