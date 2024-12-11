import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.swing.text.rtf.RTFEditorKit;

public class AdvancedTextEditor extends JFrame {
    private JTextPane textPane;
    private JFileChooser fileChooser;
    private File currentFile;

    public AdvancedTextEditor() {
        setTitle("Расширенный текстовый редактор");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        textPane = new JTextPane();
        textPane.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(textPane); //добавление полосы прокрутки для текстового поля

        JToolBar toolBar = createToolBar();

        fileChooser = new JFileChooser();

        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JToolBar createToolBar() { //метод для создания панели инструментов с кнопками и списками
        JToolBar toolBar = new JToolBar();

        // Шрифты
        JComboBox<String> fontBox = new JComboBox<>(new String[]{"Arial", "Times New Roman", "Courier New", "Verdana"});
        fontBox.addActionListener(e -> changeFont((String) fontBox.getSelectedItem()));
        toolBar.add(new JLabel(" Шрифт: "));
        toolBar.add(fontBox);

        // Размер шрифта
        JComboBox<Integer> sizeBox = new JComboBox<>();
        for (int i = 10; i <= 120; i += 2) sizeBox.addItem(i);
        sizeBox.setSelectedItem(16);
        sizeBox.addActionListener(e -> changeFontSize((Integer) sizeBox.getSelectedItem()));
        toolBar.add(new JLabel(" Размер: "));
        toolBar.add(sizeBox);

        // Цвет текста
        JButton colorButton = new JButton("Цвет");
        colorButton.addActionListener(e -> chooseTextColor());
        toolBar.add(colorButton);

        // Стиль текста
        JButton boldButton = new JButton("B");
        boldButton.setFont(new Font("Arial", Font.BOLD, 14));
        boldButton.addActionListener(e -> toggleStyle(Font.BOLD));
        toolBar.add(boldButton);

        JButton italicButton = new JButton("I");
        italicButton.setFont(new Font("Arial", Font.ITALIC, 14));
        italicButton.addActionListener(e -> toggleStyle(Font.ITALIC));
        toolBar.add(italicButton);

        JButton underlineButton = new JButton("U");
        underlineButton.setFont(new Font("Arial", Font.PLAIN, 14));
        underlineButton.addActionListener(e -> toggleUnderline());
        toolBar.add(underlineButton);

        // Выравнивание текста
        JComboBox<String> alignmentBox = new JComboBox<>(new String[]{"По левому краю", "По центру", "По правому краю", "По ширине"});
        alignmentBox.addActionListener(e -> changeAlignment(alignmentBox.getSelectedItem().toString()));
        toolBar.add(new JLabel(" Выравнивание: "));
        toolBar.add(alignmentBox);

        // Символы
        JButton specialSymbolsButton = new JButton("Символы");
        specialSymbolsButton.addActionListener(e -> insertSpecialSymbols());
        toolBar.add(specialSymbolsButton);

        // Создание таблиц
        JButton tableButton = new JButton("Таблица");
        tableButton.addActionListener(e -> insertTable());
        toolBar.add(tableButton);

        // Кнопки для открытия и сохранения
        JButton openButton = new JButton("Открыть");
        openButton.addActionListener(e -> openFile());
        toolBar.add(openButton);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> saveFile());
        toolBar.add(saveButton);

        return toolBar;
    }

    private void changeFont(String fontName) { //метод для изменения шрифта выделенного текста
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, fontName);
        applyAttributes(attributes);
    }

    private void changeFontSize(int size) {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontSize(attributes, size);
        applyAttributes(attributes);
    }

    private void chooseTextColor() {
        Color color = JColorChooser.showDialog(this, "Выберите цвет текста", Color.BLACK);
        if (color != null) {
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, color);
            applyAttributes(attributes);
        }
    }

    private void toggleStyle(int style) { //метод для включения/выключения стиля текста
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        if (style == Font.BOLD) StyleConstants.setBold(attributes, !StyleConstants.isBold(textPane.getCharacterAttributes()));
        if (style == Font.ITALIC) StyleConstants.setItalic(attributes, !StyleConstants.isItalic(textPane.getCharacterAttributes()));
        applyAttributes(attributes);
    }

    private void toggleUnderline() {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setUnderline(attributes, !StyleConstants.isUnderline(textPane.getCharacterAttributes()));
        applyAttributes(attributes);
    }

    private void changeAlignment(String alignment) { //метод для изменения выравнивания текста
        StyledDocument doc = textPane.getStyledDocument();
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        if (start != end) {
            Element paragraph = doc.getParagraphElement(start);
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            switch (alignment) {
                case "По левому краю" -> StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
                case "По центру" -> StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
                case "По правому краю" -> StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
                case "По ширине" -> StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_JUSTIFIED);
            }
            doc.setParagraphAttributes(paragraph.getStartOffset(), paragraph.getEndOffset() - paragraph.getStartOffset(), attributes, false);
        }
    }


    private void insertSpecialSymbols() {
        String[] symbols = {"α", "β", "γ", "∑", "√", "∞", "∂", "∫", "≈", "≠", "±", "ø"};
        String symbol = (String) JOptionPane.showInputDialog(this, "Выберите символ:", "Символы", JOptionPane.PLAIN_MESSAGE, null, symbols, symbols[0]);
        if (symbol != null) {
            textPane.replaceSelection(symbol);
        }
    }

    private void insertTable() {
        String rows = JOptionPane.showInputDialog("Введите количество строк:");
        String cols = JOptionPane.showInputDialog("Введите количество столбцов:");
        if (rows != null && cols != null) {
            try {
                int r = Integer.parseInt(rows);
                int c = Integer.parseInt(cols);
                StringBuilder table = new StringBuilder();
                for (int i = 0; i < r; i++) {
                    for (int j = 0; j < c; j++) {
                        table.append("|   ");
                    }
                    table.append("|\n");
                }
                textPane.replaceSelection(table.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyAttributes(SimpleAttributeSet attributes) {
        textPane.setCharacterAttributes(attributes, false);
    }

    private void openFile() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file)) {
                RTFEditorKit rtfEditorKit = new RTFEditorKit();
                textPane.setEditorKit(rtfEditorKit);
                rtfEditorKit.read(fis, textPane.getDocument(), 0);
                currentFile = file;
            } catch (IOException | BadLocationException e) {
                JOptionPane.showMessageDialog(this, "Ошибка при открытии файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileOutputStream fos = new FileOutputStream(file)) {
                RTFEditorKit rtfEditorKit = new RTFEditorKit();
                rtfEditorKit.write(fos, textPane.getDocument(), 0, textPane.getDocument().getLength());
                currentFile = file;
            } catch (IOException | BadLocationException e) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении файла: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdvancedTextEditor editor = new AdvancedTextEditor();
            editor.setVisible(true);
        });
    }
}
