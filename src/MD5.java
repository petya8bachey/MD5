/**
 * Реализация алгоритма MD5 для хеширования строк.
 * Алгоритм разбивает входное сообщение на блоки, применяет побитовые операции и возвращает 128-битный хеш.
 * @version 1.1
 * @author petya8bachey
 */
public class MD5 {

    /** Переменные для хранения промежуточных значений A, B, C и D во время вычисления хеша. */
    private int A, B, C, D;

    /** Таблица констант для каждого раунда обработки данных в алгоритме MD5. */
    private static final int[] K = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
            0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
            0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
            0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
            0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
            0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
            0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };

    /** Таблица смещений для каждого шага алгоритма MD5, используется для сдвига битов в разных раундах. */
    private static final int[] S = {
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    };

    /**
     * Выполняет циклический сдвиг битов влево.
     * @param a Число для сдвига.
     * @param s Количество позиций, на которое нужно сдвинуть биты.
     * @return Результат циклического сдвига.
     */
    private int shift(int a, int s) {
        return (a << s) | (a >>> (32 - s));
    }

    /**
     * Основной цикл обработки одного блока данных (512 бит).
     * Осуществляет побитовые операции на входном блоке и обновляет значения переменных A, B, C и D.
     * @param M Входной блок данных, разбитый на 16 слов по 32 бита.
     */
    private void mainLoop(int[] M) {
        int a = A, b = B, c = C, d = D; // Копируем значения начальных переменных A, B, C и D
        for (int i = 0; i < 64; i++) {
            int F, g;
            // Определяем функцию F и индекс g в зависимости от текущего шага
            if (i < 16) {
                F = (b & c) | ((~b) & d);
                g = i;
            } else if (i < 32) {
                F = (d & b) | ((~d) & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                F = b ^ c ^ d;
                g = (3 * i + 5) % 16;
            } else {
                F = c ^ (b | (~d));
                g = (7 * i) % 16;
            }
            // Основные вычисления и циклический сдвиг
            int temp = d;
            d = c;
            c = b;
            b += shift(a + F + K[i] + M[g], S[i]);
            a = temp;
        }
        // Добавляем результаты в исходные переменные A, B, C и D
        A += a;
        B += b;
        C += c;
        D += d;
    }

    /**
     * Дополняет сообщение до длины кратной 512 битам, как того требует алгоритм MD5.
     * @param str Входная строка.
     * @return Массив слов, содержащий исходное сообщение с добавленными битами.
     */
    private int[] addPadding(String str) {
        int numBlocks = ((str.length() + 8) / 64) + 1; // Количество блоков для хранения сообщения
        int[] paddedMessage = new int[numBlocks * 16]; // Массив для хранения блоков данных
        int i;
        // Записываем исходное сообщение в массив, побайтно
        for (i = 0; i < str.length(); i++) {
            paddedMessage[i >> 2] |= str.charAt(i) << ((i % 4) * 8);
        }
        // Добавляем 1 бит (0x80) после сообщения
        paddedMessage[i >> 2] |= 0x80 << ((i % 4) * 8);
        // В конце добавляем длину сообщения в битах
        paddedMessage[paddedMessage.length - 2] = str.length() * 8;
        return paddedMessage;
    }

    /**
     * Вычисляет MD5 хеш от строки.
     * Алгоритм применяет побитовые операции и возвращает хеш в виде шестнадцатеричной строки.
     * @param source Входная строка для хеширования.
     * @return Хеш в виде шестнадцатеричной строки.
     */
    public String getMD5(String source) {
        // Инициализация переменных A, B, C и D стандартными значениями
        A = 0x67452301;
        B = 0xefcdab89;
        C = 0x98badcfe;
        D = 0x10325476;
        // Добавление padding к строке
        int[] paddedMessage = addPadding(source);
        // Разбиваем сообщение на блоки по 512 бит (16 слов по 32 бита) и обрабатываем их
        for (int i = 0; i < paddedMessage.length / 16; i++) {
            int[] block = new int[16];
            System.arraycopy(paddedMessage, i * 16, block, 0, 16);
            mainLoop(block); // Основная обработка каждого блока
        }
        // Возвращаем итоговый хеш в виде шестнадцатеричной строки
        return toHexString(A) + toHexString(B) + toHexString(C) + toHexString(D);
    }

    /**
     * Преобразует 32-битное число в шестнадцатеричную строку.
     * @param value 32-битное число.
     * @return Шестнадцатеричная строка.
     */
    private String toHexString(int value) {
        StringBuilder hexString = new StringBuilder(8);
        // Преобразуем каждый байт числа в шестнадцатеричный формат
        for (int i = 0; i < 4; i++) {
            hexString.append(String.format("%02x", (value >>> (i * 8)) & 0xff));
        }
        return hexString.toString();
    }
}
