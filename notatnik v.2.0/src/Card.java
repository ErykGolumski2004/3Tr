import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Card extends JFrame {
    private JList<String> list; // Lista plików
    private JTextArea textAreaRead; // Obszar do odczytu tekstu
    private List<String> fileList; // Lista nazw plików
    private File selectedDirectory; // Wybrany katalog

    public Card() {
        setTitle("Edytor plików");
        setPreferredSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        textAreaRead = new JTextArea(); // Tworzenie obszaru do odczytu tekstu
        JScrollPane scrollPane = new JScrollPane(textAreaRead); // Dodawanie obszaru przewijania dla obszaru tekstu
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        fileList = new ArrayList<>(); // Inicjalizacja listy nazw plików
        list = new JList<>(fileList.toArray(new String[0])); // Tworzenie listy plików
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedFile = list.getSelectedValue(); // Pobieranie wybranego pliku z listy
                    if (selectedFile != null) {
                        try {
                            String content = new String(Files.readAllBytes(Paths.get(selectedDirectory + "/" + selectedFile))); // Odczyt zawartości pliku
                            textAreaRead.setText(content); // Ustawianie odczytanego tekstu w obszarze
                            textAreaRead.setEditable(true); // Ustawianie obszaru do edycji
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(list); // Dodawanie obszaru przewijania dla listy plików
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        listScrollPane.setPreferredSize(new Dimension(100, 100));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createMenuBar(), BorderLayout.NORTH); // Tworzenie paska menu i dodawanie go na górze panelu
        panel.add(listScrollPane, BorderLayout.WEST); // Dodawanie obszaru listy po lewej stronie panelu
        panel.add(scrollPane, BorderLayout.CENTER); // Dodawanie obszaru przewijania tekstu na środku panelu

        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        selectedDirectory = new File("src/txt"); // Ustawianie domyślnego katalogu
        getUpdatedFileList(); // Aktualizacja listy plików w wybranym katalogu
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Plik"); // Menu "Plik"
        JMenuItem openItem = new JMenuItem("Otwórz");
        JMenuItem saveItem = new JMenuItem("Zapisz");
        JMenuItem newItem = new JMenuItem("Nowy");
        JMenuItem deleteItem = new JMenuItem("Usuń");
        JMenuItem renameItem = new JMenuItem("Zmień nazwę");

        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otworzKatalog(); // Obsługa zdarzenia kliknięcia na "Otwórz"
            }
        });

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapiszPlik(); // Obsługa zdarzenia kliknięcia na "Zapisz"
            }
        });

        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nowyPlik(); // Obsługa zdarzenia kliknięcia na "Nowy"
            }
        });

        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunPlik(); // Obsługa zdarzenia kliknięcia na "Usuń"
            }
        });

        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zmienNazwePliku(); // Obsługa zdarzenia kliknięcia na "Zmień nazwę"
            }
        });

        fileMenu.add(openItem); // Dodawanie elementów do menu "Plik"
        fileMenu.add(saveItem);
        fileMenu.add(newItem);
        fileMenu.add(deleteItem);
        fileMenu.add(renameItem);

        JMenu editMenu = new JMenu("Edycja"); // Menu "Edycja"
        JMenuItem copyItem = new JMenuItem("Kopiuj");
        JMenuItem cutItem = new JMenuItem("Wytnij");
        JMenuItem pasteItem = new JMenuItem("Wklej");

        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaRead.copy(); // Obsługa zdarzenia kliknięcia na "Kopiuj"
            }
        });

        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaRead.cut(); // Obsługa zdarzenia kliknięcia na "Wytnij"
            }
        });

        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textAreaRead.paste(); // Obsługa zdarzenia kliknięcia na "Wklej"
            }
        });

        editMenu.add(copyItem); // Dodawanie elementów do menu "Edycja"
        editMenu.add(cutItem);
        editMenu.add(pasteItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        return menuBar;
    }

    private void otworzKatalog() { // Funkcjonalność zmienienia katalogu
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/txt"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDirectory = fileChooser.getSelectedFile();
            getUpdatedFileList(); // Wywołanie aktualizacji listy plików
        }
    }

    private void getUpdatedFileList() { // Metoda aktualizująca liste plików
        fileList.clear(); // Wyczyszczenie listy plików
        if (selectedDirectory != null) {
            File[] files = selectedDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        fileList.add(file.getName()); // Dodawanie nazwy pliku do listy
                    }
                }
            }
        }
        list.setListData(fileList.toArray(new String[0])); // Aktualizacja danych w liście
    }

    private void zapiszPlik() { // Funkcjonalność zapisywania pliku
        String selectedFile = list.getSelectedValue();
        if (selectedFile != null) {
            try {
                Files.write(Paths.get(selectedDirectory + "/" + selectedFile), textAreaRead.getText().getBytes());

                JOptionPane.showMessageDialog(this, "Plik zapisany pomyślnie.", "Zapisano", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException exception) {
                exception.printStackTrace();
                JOptionPane.showMessageDialog(this, "Błąd podczas zapisywania pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void nowyPlik() { // Funkcjonalność tworzenia nowego pliku
        String fileName = JOptionPane.showInputDialog(this, "Podaj nazwę nowego pliku:", "Nowy plik", JOptionPane.PLAIN_MESSAGE);
        if (fileName != null && !fileName.isEmpty()) {
            String filePath = selectedDirectory + "/" + fileName;
            if (filePath.toLowerCase().endsWith(".txt")) {
                try {
                    File file = new File(filePath);
                    if (file.createNewFile()) {
                        fileList.add(fileName);
                        list.setListData(fileList.toArray(new String[0]));
                        list.setSelectedValue(fileName, true);
                        textAreaRead.setText("");
                        textAreaRead.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Plik o podanej nazwie już istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Błąd podczas tworzenia pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Nazwa pliku musi kończyć się rozszerzeniem .txt", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void usunPlik() { // Funkcjonalność usuwania pliku
        String selectedFile = list.getSelectedValue();
        if (selectedFile != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć plik?", "Potwierdzenie", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String filePath = selectedDirectory + "/" + selectedFile;
                File file = new File(filePath);
                if (file.delete()) {
                    fileList.remove(selectedFile);
                    list.setListData(fileList.toArray(new String[0]));
                    textAreaRead.setText("");
                    textAreaRead.setEditable(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Błąd podczas usuwania pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void zmienNazwePliku() { // Funkcjonalność zmiany nazwy pliku
        String selectedFile = list.getSelectedValue();
        if (selectedFile != null) {
            String newFileName = JOptionPane.showInputDialog(this, "Podaj nową nazwę pliku:", "Zmień nazwę pliku", JOptionPane.PLAIN_MESSAGE);
            if (newFileName != null && !newFileName.isEmpty()) {
                String oldFilePath = selectedDirectory + "/" + selectedFile;
                String newFilePath = selectedDirectory + "/" + newFileName;
                if (newFilePath.toLowerCase().endsWith(".txt")) {
                    File oldFile = new File(oldFilePath);
                    File newFile = new File(newFilePath);
                    if (oldFile.renameTo(newFile)) {
                        fileList.remove(selectedFile);
                        fileList.add(newFileName);
                        list.setListData(fileList.toArray(new String[0]));
                        list.setSelectedValue(newFileName, true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Błąd podczas zmiany nazwy pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Nazwa pliku musi kończyć się rozszerzeniem .txt", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}