import java.io.*;
import java.util.Map;

public class Huffman {
    private Encoder encoder = new Encoder();
    private Decoder decoder = new Decoder();

    public void encode(String input, String output) throws IOException {
        encoder.encode(input, output);
    }

    public void decode(String input, String output) throws IOException, ClassNotFoundException {
        decoder.decode(input, output);
    }
}

class Encoder {
    public void encode(String inputFile, String outputFile) throws IOException {
        byte[] data = readFile(inputFile);
        Map<Byte, Integer> freqMap = HuffmanTree.buildFrequencyMap(data);
        Node root = HuffmanTree.buildHuffmanTree(freqMap);
        Map<Byte, String> huffmanCodes = HuffmanTree.buildCodeTable(root);

        // Кодирование данных
        StringBuilder encodedText = new StringBuilder();
        for (byte b : data) {
            encodedText.append(huffmanCodes.get(b));
        }

        // Сохранение закодированных данных в файл
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            oos.writeObject(freqMap); 
            oos.writeObject(encodedText.toString()); 
        }

        System.out.println("Файл успешно закодирован и сохранен в " + outputFile);
    }

    // Чтение файла и возврат его содержимого как массива байтов
    private static byte[] readFile(String filename) throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename))) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return data;
        }
    }
}

class Decoder {
    @SuppressWarnings("unchecked")
    public void decode(String inputFile, String outputFile) throws IOException, ClassNotFoundException {
        Map<Byte, Integer> freqMap;
        String encodedText;

        // Чтение закодированных данных из файла
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(inputFile))) {
            freqMap = (Map<Byte, Integer>) ois.readObject();
            encodedText = (String) ois.readObject();
        }

        Node root = HuffmanTree.buildHuffmanTree(freqMap);
        byte[] decodedData = decodeText(root, encodedText);

        // Сохранение декодированных данных в файл
        writeFile(outputFile, decodedData);

        System.out.println("Файл успешно декодирован и сохранен в " + outputFile);
    }

    // Декодирование закодированного текста в массив байтов
    private static byte[] decodeText(Node root, String encodedText) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Node current = root;

        // Особый случай: только один символ в дереве
        if (root.isLeaf()) {
            // Если корень — лист, записываем символ столько раз, сколько битов в закодированном тексте
            for (int i = 0; i < encodedText.length(); i++) {
                outputStream.write(root.character);
            }
            return outputStream.toByteArray();
        }

        for (char bit : encodedText.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.isLeaf()) {
                outputStream.write(current.character);
                current = root;
            }
        }
        return outputStream.toByteArray();
    }

    // Запись декодированных байтов в файл
    private static void writeFile(String filename, byte[] data) throws IOException {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filename))) {
            outputStream.write(data);
        }
    }
}