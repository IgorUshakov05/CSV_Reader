import java.io.*;
import java.util.*;
import java.util.stream.*;

public class SalesApp {

    // Класс Sale для хранения информации о продаже
    static class Sale {
        int saleId;
        String dateTime;
        String customerCode;
        String productCode;
        double saleAmount;

        public Sale(int saleId, String dateTime, String customerCode, String productCode, double saleAmount) {
            this.saleId = saleId;
            this.dateTime = dateTime;
            this.customerCode = customerCode;
            this.productCode = productCode;
            this.saleAmount = saleAmount;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getCustomerCode() {
            return customerCode;
        }

        public double getSaleAmount() {
            return saleAmount;
        }

        public String getDateTime() {
            return dateTime;
        }
    }

    // Класс Product для хранения информации о товаре
    static class Product {
        String productCode;
        String productName;
        double price;

        public Product(String productCode, String productName, double price) {
            this.productCode = productCode;
            this.productName = productName;
            this.price = price;
        }

        public String getProductCode() {
            return productCode;
        }

        public String getProductName() {
            return productName;
        }
    }

    // Класс Customer для хранения информации о покупателе
    static class Customer {
        String customerCode;
        String name;
        String contactInfo;

        public Customer(String customerCode, String name, String contactInfo) {
            this.customerCode = customerCode;
            this.name = name;
            this.contactInfo = contactInfo;
        }
    }

    // Класс для чтения CSV файлов
    static class CSVReader {

        public List<Sale> readSales(String filePath) {
            List<Sale> sales = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Пропускаем первую строку (заголовки)
                br.readLine();
                
                // Читаем остальные строки
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    
                    // Преобразуем строковые данные в соответствующие типы
                    int saleId = Integer.parseInt(values[0]);
                    String dateTime = values[1];
                    String customerCode = values[2];
                    String productCode = values[3];
                    double saleAmount = Double.parseDouble(values[4]);
                    
                    // Создаем объект Sale и добавляем его в список
                    Sale sale = new Sale(saleId, dateTime, customerCode, productCode, saleAmount);
                    sales.add(sale);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return sales;
        }

        public List<Product> readProducts(String filePath) {
            List<Product> products = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Пропускаем первую строку (заголовки)
                br.readLine();
                
                // Читаем остальные строки
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    
                    // Преобразуем строковые данные в соответствующие типы
                    String productCode = values[0];
                    String productName = values[1];
                    double price = Double.parseDouble(values[2]);
                    
                    // Создаем объект Product и добавляем его в список
                    Product product = new Product(productCode, productName, price);
                    products.add(product);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return products;
        }

        public List<Customer> readCustomers(String filePath) {
            List<Customer> customers = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                // Пропускаем первую строку (заголовки)
                br.readLine();
                
                // Читаем остальные строки
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    
                    // Преобразуем строковые данные в соответствующие типы
                    String customerCode = values[0];
                    String name = values[1];
                    String contactInfo = values[2];
                    
                    // Создаем объект Customer и добавляем его в список
                    Customer customer = new Customer(customerCode, name, contactInfo);
                    customers.add(customer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return customers;
        }
    }

    // Основной метод для запуска приложения
    public static void main(String[] args) {
        CSVReader reader = new CSVReader();
        
        // Чтение данных из CSV
        List<Sale> sales = reader.readSales("sales.csv");
        List<Product> products = reader.readProducts("products.csv");
        List<Customer> customers = reader.readCustomers("customers.csv");

        // 1. Определение текущей суммы всех продаж
        double totalSales = sales.stream().mapToDouble(Sale::getSaleAmount).sum();
        System.out.println("Текущая сумма всех продаж: " + totalSales);

        // 2. Определение пяти самых популярных товаров
        Map<String, Long> productFrequency = sales.stream()
                .collect(Collectors.groupingBy(Sale::getProductCode, Collectors.counting()));

        List<Product> topProducts = products.stream()
                .filter(p -> productFrequency.containsKey(p.getProductCode()))
                .sorted((p1, p2) -> Long.compare(productFrequency.getOrDefault(p2.getProductCode(), 0L),
                                                  productFrequency.getOrDefault(p1.getProductCode(), 0L)))
                .limit(5)
                .collect(Collectors.toList());
        
        System.out.println("Пять самых популярных товаров:");
        topProducts.forEach(p -> System.out.println(p.getProductName()));

        // 3. Определение пяти самых непопулярных товаров
        List<Product> bottomProducts = products.stream()
                .filter(p -> productFrequency.containsKey(p.getProductCode()))
                .sorted((p1, p2) -> Long.compare(productFrequency.getOrDefault(p1.getProductCode(), 0L),
                                                  productFrequency.getOrDefault(p2.getProductCode(), 0L)))
                .limit(5)
                .collect(Collectors.toList());
        
        System.out.println("Пять самых непопулярных товаров:");
        bottomProducts.forEach(p -> System.out.println(p.getProductName()));

        // 4. Определение тенденции продаж товара по введенному коду
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите код товара для анализа тенденции продаж: ");
        String productCodeForTrend = scanner.nextLine();
        
        List<Sale> productSales = sales.stream()
                .filter(sale -> sale.getProductCode().equals(productCodeForTrend))
                .collect(Collectors.toList());
        
        if (!productSales.isEmpty()) {
            System.out.println("Тенденция продаж товара " + productCodeForTrend + ":");
            productSales.forEach(sale -> System.out.println(sale.getDateTime() + " - " + sale.getSaleAmount()));
        } else {
            System.out.println("Продаж для товара с кодом " + productCodeForTrend + " не найдено.");
        }

        // 5. Запрос суммы и вывод покупателей с общей суммой покупок больше введенной
        System.out.print("Введите минимальную сумму покупок для вывода покупателей: ");
        double minAmount = scanner.nextDouble();
        
        Map<String, Double> customerTotals = sales.stream()
                .collect(Collectors.groupingBy(Sale::getCustomerCode,
                                               Collectors.summingDouble(Sale::getSaleAmount)));
        
        System.out.println("Покупатели с общей суммой покупок больше " + minAmount + ":");
        customers.stream()
                .filter(customer -> customerTotals.getOrDefault(customer.customerCode, 0.0) > minAmount)
                .forEach(customer -> System.out.println(customer.name + " - " + customerTotals.get(customer.customerCode)));
        
        // 6. Генерация отчета в текстовый файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("sales_report.txt"))) {
            writer.write("Отчет по продажам:\n");
            writer.write("1) Общая сумма продаж: " + totalSales + "\n");
            
            writer.write("2) Пять самых популярных товаров:\n");
            topProducts.forEach(p -> {
                try {
                    writer.write(p.getProductName() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            writer.write("2.1) Пять самых непопулярных товаров:\n");
            bottomProducts.forEach(p -> {
                try {
                    writer.write(p.getProductName() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.write("3) Покупатели с суммой покупок больше " + minAmount + ":\n");
            customers.stream()
                    .filter(customer -> customerTotals.getOrDefault(customer.customerCode, 0.0) > minAmount)
                    .forEach(customer -> {
                        try {
                            writer.write(customer.name + " - " + customerTotals.get(customer.customerCode) + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            writer.write("4) Тенденция продаж товара по коду: " + productCodeForTrend + "\n");
            productSales.forEach(sale -> {
                try {
                    writer.write(sale.getDateTime() + " - " + sale.getSaleAmount() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            
            
        } catch (IOException e) {
            try (BufferedWriter error = new BufferedWriter(new FileWriter("error_report.txt", true))) {
                error.write("Ошибка: " + "\n" + e + "\n");
            } catch (IOException writeException) {
                System.out.println("Ошибка при записи в файл error_report.txt: " + writeException.getMessage());
            }
            e.printStackTrace();
        }
        
    }
}
