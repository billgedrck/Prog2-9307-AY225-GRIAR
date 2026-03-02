import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LowPerformingProductsApp extends JFrame {

    private final JTextArea output = new JTextArea();
    private final JButton analyzeBtn = new JButton("Analyze (Auto-search CSV)");

    // If you know the exact filename, set it here (optional).
    private static final String PREFERRED_CSV_NAME = "vgchartz-2024.csv";

    public LowPerformingProductsApp() {
        setTitle("Detect Low Performing Products");
        setSize(950, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 13));

        analyzeBtn.addActionListener(e -> analyzeAuto());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(analyzeBtn);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::analyzeAuto);
    }

    private void analyzeAuto() {
        output.setText("Searching for CSV automatically...\n");

        File found = findCsvAnywhere();
        if (found != null) {
            output.append("Found CSV: " + found.getAbsolutePath() + "\n\n");
            processCsvFile(found);
            return;
        }

        // If CSV not found, try ZIP search (Downloads often has zip)
        File zipFound = findZipAnywhere();
        if (zipFound != null) {
            output.append("Found ZIP: " + zipFound.getAbsolutePath() + "\n");
            output.append("Trying to read CSV inside ZIP...\n\n");
            processCsvFromZip(zipFound);
            return;
        }

        output.setText(
                "CSV / ZIP NOT FOUND.\n\n" +
                "I searched these folders:\n" +
                "- Current folder (working directory)\n" +
                "- Documents\n" +
                "- Desktop\n" +
                "- Downloads\n\n" +
                "Fix options:\n" +
                "1) Put your CSV in the same folder as your project and run again, OR\n" +
                "2) Put the ZIP/CSV in Downloads/Documents/Desktop.\n"
        );
    }

    private File findCsvAnywhere() {
        // 1) current working directory
        File cwd = new File(System.getProperty("user.dir"));
        File direct = new File(cwd, PREFERRED_CSV_NAME);
        if (direct.exists() && direct.isFile()) return direct;

        // 2) search common folders
        File home = new File(System.getProperty("user.home"));
        File docs = new File(home, "Documents");
        File desk = new File(home, "Desktop");
        File down = new File(home, "Downloads");

        File found = deepFindFile(docs, f -> f.getName().equalsIgnoreCase(PREFERRED_CSV_NAME));
        if (found != null) return found;

        found = deepFindFile(desk, f -> f.getName().equalsIgnoreCase(PREFERRED_CSV_NAME));
        if (found != null) return found;

        found = deepFindFile(down, f -> f.getName().equalsIgnoreCase(PREFERRED_CSV_NAME));
        if (found != null) return found;

        // 3) fallback: find any CSV with "vgchartz" in filename
        found = deepFindFile(docs, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".csv"));
        if (found != null) return found;

        found = deepFindFile(desk, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".csv"));
        if (found != null) return found;

        found = deepFindFile(down, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".csv"));
        return found;
    }

    private File findZipAnywhere() {
        File home = new File(System.getProperty("user.home"));
        File docs = new File(home, "Documents");
        File desk = new File(home, "Desktop");
        File down = new File(home, "Downloads");

        File found = deepFindFile(down, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".zip"));
        if (found != null) return found;

        found = deepFindFile(docs, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".zip"));
        if (found != null) return found;

        return deepFindFile(desk, f -> f.getName().toLowerCase().contains("vgchartz") && f.getName().toLowerCase().endsWith(".zip"));
    }

    // limit recursion to avoid scanning your whole drive forever
    private File deepFindFile(File root, java.util.function.Predicate<File> match) {
        if (root == null || !root.exists() || !root.isDirectory()) return null;

        // limit scan depth
        return deepFindFile(root, match, 0, 6);
    }

    private File deepFindFile(File root, java.util.function.Predicate<File> match, int depth, int maxDepth) {
        if (depth > maxDepth) return null;

        File[] files = root.listFiles();
        if (files == null) return null;

        for (File f : files) {
            if (f.isFile() && match.test(f)) return f;
        }

        for (File f : files) {
            if (f.isDirectory()) {
                File res = deepFindFile(f, match, depth + 1, maxDepth);
                if (res != null) return res;
            }
        }
        return null;
    }

    private void processCsvFromZip(File zipFile) {
        try (ZipFile zf = new ZipFile(zipFile)) {

            ZipEntry csvEntry = null;

            // prefer exact name
            ZipEntry preferred = zf.getEntry(PREFERRED_CSV_NAME);
            if (preferred != null) {
                csvEntry = preferred;
            } else {
                // otherwise find any csv that contains vgchartz
                Enumeration<? extends ZipEntry> en = zf.entries();
                while (en.hasMoreElements()) {
                    ZipEntry e = en.nextElement();
                    String name = e.getName().toLowerCase();
                    if (!e.isDirectory() && name.endsWith(".csv") && name.contains("vgchartz")) {
                        csvEntry = e;
                        break;
                    }
                }
            }

            if (csvEntry == null) {
                output.setText("ZIP found, but no vgchartz CSV inside.");
                return;
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(zf.getInputStream(csvEntry), StandardCharsets.UTF_8))) {
                analyzeFromReader(br, "ZIP: " + zipFile.getAbsolutePath() + " -> " + csvEntry.getName());
            }

        } catch (Exception ex) {
            output.setText("Error reading ZIP:\n" + ex.getMessage());
        }
    }

    private void processCsvFile(File file) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            analyzeFromReader(br, file.getAbsolutePath());
        } catch (Exception ex) {
            output.setText("Error reading CSV:\n" + ex.getMessage());
        }
    }

    private void analyzeFromReader(BufferedReader br, String sourceLabel) throws IOException {
        Map<String, Double> totalSalesByProduct = new HashMap<>();

        String headerLine = br.readLine();
        if (headerLine == null) throw new RuntimeException("Empty CSV.");

        java.util.List<String> headers = parseCsvLine(headerLine);

        int titleIdx = -1;
        int salesIdx = -1;

        for (int i = 0; i < headers.size(); i++) {
            String h = headers.get(i).trim().toLowerCase();
            if (h.equals("title") || h.equals("name") || h.equals("product")) titleIdx = i;
            if (h.equals("total_sales") || h.equals("totalsales") || h.equals("sales")) salesIdx = i;
        }

        // fallback for vgchartz format
        if (titleIdx == -1) titleIdx = 1;
        if (salesIdx == -1) salesIdx = 7;

        int rows = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            java.util.List<String> row = parseCsvLine(line);
            rows++;

            if (titleIdx >= row.size()) continue;
            String title = row.get(titleIdx).trim();
            if (title.isEmpty()) continue;

            double sales = 0.0;
            if (salesIdx < row.size()) {
                String s = row.get(salesIdx).trim().replace("\"", "");
                if (!s.isEmpty()) sales = Double.parseDouble(s);
            }

            totalSalesByProduct.merge(title, sales, Double::sum);
        }

        double sum = 0.0;
        for (double v : totalSalesByProduct.values()) sum += v;
        double avg = sum / totalSalesByProduct.size();

        java.util.List<Map.Entry<String, Double>> flagged = new ArrayList<>();
        for (Map.Entry<String, Double> e : totalSalesByProduct.entrySet()) {
            if (e.getValue() < avg) flagged.add(e);
        }
        flagged.sort(Comparator.comparingDouble(Map.Entry::getValue));

        StringBuilder sb = new StringBuilder();
        sb.append("SOURCE:\n").append(sourceLabel).append("\n\n");
        sb.append("Rows read: ").append(rows).append("\n");
        sb.append("Unique products: ").append(totalSalesByProduct.size()).append("\n");
        sb.append(String.format(Locale.US, "Average sales per product: %.4f%n", avg));
        sb.append("Flagged (below average): ").append(flagged.size()).append("\n\n");

        sb.append(String.format("%-60s %12s%n", "PRODUCT", "TOTAL_SALES"));
        sb.append("------------------------------------------------------------------------\n");

        for (Map.Entry<String, Double> e : flagged) {
            sb.append(String.format(Locale.US, "%-60.60s %12.4f%n", e.getKey(), e.getValue()));
        }

        output.setText(sb.toString());
    }

    private static java.util.List<String> parseCsvLine(String line) {
        java.util.List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LowPerformingProductsApp().setVisible(true));
    }
}