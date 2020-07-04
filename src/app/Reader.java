package app;

import collection.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Класс, отвечающий за забор ввода от пользователя из консоли
 */
public class Reader {


    /**
     * Базовый метод для забора ввода от пользователя без вывода сообщения в консоль
     * @return Ввод пользователя
     * @throws IOException
     */
    public static String request() throws IOException {
        String answer;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        answer = reader.readLine();
        if (answer.equals("")) {
            return null;
        }
        return answer.trim();
    }

    /**
     * Метод для забора данных от пользователя. Выводит приглашающее сообщение в консоль
     * @param message Приглашающее сообщение
     * @return Ввод пользователя
     * @throws IOException
     */
    public static String request(String message) throws IOException {
        System.out.println(message);
        String answer = request();
        return answer;
    }

    /**
     * Метод для забора данных от пользователя. Выводит приглашающее сообщение в консоль
     * @param message Приглашающее сообщение
     * @param nullable Может ли результат ввода быть равен null
     * @return Ввод пользователя
     * @throws IOException
     */
    public static String request(String message, boolean nullable) throws IOException {
        String answer = request(message);
        if (nullable) {
            return answer;
        }
        else if (answer == null){
            do {
                System.err.println("Значение не может быть null");
                answer = request(message);
            } while (answer == null);
        }
        return answer;
    }

    /**
     * Метод для забора числовых данных от пользователя. Выводит приглашающее сообщение в консоль
     * @param message Приглашающее сообщение
     * @param min Минимально допустимое значение
     * @param max Максимально допустимое значение
     * @return Ввод пользователя
     * @throws IOException
     */
    public static String request(String message, int min, int max) throws IOException {
        String answer;
        try {
            answer = request(message, false);
            if (!checkNumber(Double.parseDouble(answer), min, max)) {
                System.err.println("Некорректные границы числа, повторите ввод");
                return request(message, min, max);
            }
        } catch (NumberFormatException e) {
            System.err.println("Некорректное число, повторите ввод");
            return request(message, min, max);
        }
        return answer;
    }

    /**
     * Метод для запроса данных на ввод параметров квартиры
     * @return Сформированный экземпляр класса Flat
     * @throws IOException
     */
    public static Flat requestForFlat() throws IOException {
        String name = request("Введите название квартиры: ", false);
        Coordinates coordinates = requestForCoordinates();
        Date creationDate = new Date();
        Double area = Double.valueOf(request("Введите жилую площадь (число, большее 0): ", 1, -1));
        int numberOfRooms = Integer.parseInt(request("Введите количество комнат (целое число, большее 0): ", 1, -1));
        int kitchenArea = Integer.parseInt(request("Введите площадь кухни (целое число, большее 0): ", 1, -1));
        Double timeToMetroOnFoot = Double.valueOf(request("Введите время до метро пешком (число, большее 0): ", 1, -1));
        Furnish furnish = requestForFurnish();
        House house = requestForHouse();
        //int id = generateId(1000);
        return new Flat(name, coordinates, creationDate, area, numberOfRooms, kitchenArea, timeToMetroOnFoot, furnish, house);
    }

    /**
     * Метод для запроса ввода координат
     * @return Сформированный экземпляр класса Coordinates
     * @throws IOException
     */
    public static Coordinates requestForCoordinates() throws IOException {
        System.out.println("Введите координаты квартиры: ");
        double x;
        double y;
        try {
            x = Double.parseDouble(request("Введите расположение квартиры по X (число): ", false));
            y = Double.parseDouble(request("Введите расположение квартиры по Y (число): ", false));
        } catch (NumberFormatException e) {
            System.err.println("Некорректные координаты, повторите ввод");
            return requestForCoordinates();
        }
        return new Coordinates(x, y);
    }

    /**
     * Метод для запроса ввода состояния квартиры
     * @return значение furnish
     * @throws IOException
     */
    public static Furnish requestForFurnish() throws IOException {
        Furnish furnish;
        try {
            String answer = request("Введите состояние квартиры КАПСОМ (DESIGNER,NONE,FINE,BAD,LITTLE) : ", false);
            furnish = Furnish.valueOf(answer.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Введите один из доступных вариантов");
            return requestForFurnish();
        }
        return furnish;
    }

    /**
     * Метод для запроса ввода дома
     * @return Сформированный экземпляр класса House
     * @throws IOException
     */
    public static House requestForHouse() throws IOException {
        System.out.println("Введите данные о доме: ");
        String name = request("Введите называние дома: ", false);
        Integer year = Integer.valueOf(request("Введите возраст дома (целое число, большее 0): ", 1, -1));
        int numberOfFlatsOnFloor = Integer.parseInt(request("Введите количество квартир на этаже (целое число, большее 0): ", 1, -1));
        return new House(name, year, numberOfFlatsOnFloor);
    }

    public static void write(String msg) {

    }

    /**
     * Метод для проверки числа на вхождение в заданный диапазон
     * @param s Число
     * @param min Минимально допустимое значение
     * @param max Максимально допустимое значение (-1, если не имеет значение)
     * @return
     */
    public static boolean checkNumber(double s, int min, int max) {
        return ((min < 0 || s >= min) && (max < 0 || s <= max));
    }

    /**
     * Метод для генерации id для новой квартиры
     * @param max Максимальное значение id
     * @return
     */
    public static int generateId(int max) {
        final int min = 1;
        int generatedId;
        do {
            max -= min;
            generatedId = (int) (Math.random() * ++max) + min;
        } while (!CollectionManager.getInstance().isIdFree(generatedId));
        return generatedId;
    }
}
