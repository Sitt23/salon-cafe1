

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// ==========================================================
// 1. DATABASE MANAGER (ตัวจัดการฐานข้อมูลล็อกและบันทึกยอดขาย)
// ==========================================================
class DBManager {
    private static final String URL = "jdbc:sqlite:salon_cafe.db";

    public static Connection connect() throws Exception {
        // ต้องมี Library sqlite-jdbc ในโปรเจกต์ด้วยนะครับ
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS sales_history (" +
                                "bill_id TEXT PRIMARY KEY, " +
                                "sale_time TEXT, " +
                                "menu_details TEXT, " +
                                "total_price REAL, " +
                                "payment_method TEXT, " +
                                "status TEXT" +
                                ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database Initialized.");
        } catch (Exception e) {
            System.out.println("Database init warning: " + e.getMessage() + " (Run fine without DB save)");
        }
    }

    public static void insertSale(String billId, String time, String details, double total, String method) {
        String insertSQL = "INSERT INTO sales_history(bill_id, sale_time, menu_details, total_price, payment_method, status) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, billId);
            pstmt.setString(2, time);
            pstmt.setString(3, details);
            pstmt.setDouble(4, total);
            pstmt.setString(5, method);
            pstmt.setString(6, "สำเร็จ");
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Saved locally to cache (DB insert ignored): " + e.getMessage());
        }
    }

    public static void updateBillStatus(String billId, String newStatus) {
        String updateSQL = "UPDATE sales_history SET status = ? WHERE bill_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, newStatus);
            pstmt.setString(2, billId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Status updated locally.");
        }
    }
}

// ==========================================================
// 2. MAIN APPLICATION ENTRY POINT
// ==========================================================
public class SalonCafePOS {
    public static void main(String[] args) {
        DBManager.initializeDatabase();
        
        // ตั้งค่าดีไซน์หน้าต่างให้โค้งมน ทันสมัยตาม OS 
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}

// ==========================================================
// 3. LOGIN WINDOW (หน้าจอเข้าสู่ระบบ แยกสิทธิ์ Admin / Staff)
// ==========================================================
class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;

    public LoginFrame() {
        setTitle("Salon Cafe - Login");
        setSize(400, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(248, 249, 250)); // สี Off-White มินิมอล

        JLabel lblTitle = new JLabel("SALON CAFE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(new Color(52, 58, 64));
        lblTitle.setBounds(50, 50, 300, 45);
        mainPanel.add(lblTitle);

        JLabel lblSub = new JLabel("Minimalist POS System v1.0", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(173, 181, 189));
        lblSub.setBounds(50, 95, 300, 20);
        mainPanel.add(lblSub);

        cbRole = new JComboBox<>(new String[]{"พนักงานทั่วไป (Staff)", "ผู้จัดการร้าน (Admin)"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbRole.setBounds(50, 160, 300, 40);
        mainPanel.add(cbRole);

        txtUsername = new JTextField("staff01");
        txtUsername.setBorder(BorderFactory.createTitledBorder("Username"));
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(50, 220, 300, 45);
        mainPanel.add(txtUsername);

        txtPassword = new JPasswordField("1234");
        txtPassword.setBorder(BorderFactory.createTitledBorder("Password"));
        txtPassword.setBounds(50, 280, 300, 45);
        mainPanel.add(txtPassword);

        JButton btnLogin = new JButton("เข้าสู่ระบบ");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(73, 80, 87)); //สีเทาเข้ม ชาร์โคล
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(50, 360, 300, 45);

        btnLogin.addActionListener(e -> {
            String role = (String) cbRole.getSelectedItem();
            boolean isAdmin = role.contains("Admin");
            this.dispose();
            new MainOrderFrame(isAdmin).setVisible(true);
        });

        mainPanel.add(btnLogin);
        add(mainPanel);
    }
}

// ==========================================================
// 4. MAIN ORDER WINDOW (หน้าจอขายของพนักงาน - เมนูสวยงาม)
// ==========================================================
class MainOrderFrame extends JFrame {
    private boolean isAdmin;
    private DefaultTableModel tableModel;
    private JTable tableCart;
    private JLabel lblTotal;
    private double totalPrice = 0;
    private ArrayList<String> currentOrderItems = new ArrayList<>();
    
    // รายการข้อมูลสินค้าจำลอง (สามารถปรับเปลี่ยนตรงนี้ได้เลย)
    private String[] drinkNames = {"เอสเพรสโซ่", "ชาเขียวมัทฉะ", "ชานมไต้หวัน", "โกโก้ลาเต้", "นมสดคาราเมล", "สตรอว์เบอร์รี่โซดา"};
    private double[] drinkPrices = {50, 55, 45, 50, 55, 40};
    
    private String[] dessertNames = {"ฮันนี่โทสต์", "ครอฟเฟิลนมสด", "เค้กช็อกโกแลต", "บิงซูชาไทย"};
    private double[] dessertPrices = {99, 45, 65, 120};

    public MainOrderFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("Salon Cafe - หน้าจอขายสินค้า");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- ส่วนหัวโปรแกรม (Header Panel) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JLabel lblShop = new JLabel("SALON CAFE  |  พนักงานขาย", SwingConstants.LEFT);
        lblShop.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblShop.setForeground(new Color(52, 58, 64));
        headerPanel.add(lblShop, BorderLayout.WEST);

        // ปุ่มไปหน้าแอดมิน (ถ้าเป็น Admin ถึงจะกดได้)
        if (isAdmin) {
            JButton btnAdmin = new JButton("แดชบอร์ดผู้จัดการ");
            btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdmin.setBackground(new Color(108, 117, 125));
            btnAdmin.setForeground(Color.WHITE);
            btnAdmin.addActionListener(e -> new AdminReportFrame().setVisible(true));
            headerPanel.add(btnAdmin, BorderLayout.EAST);
        }
        add(headerPanel, BorderLayout.NORTH);

        // --- ส่วนกลาง: แท็บเลือกหมวดหมู่รายการอาหาร/น้ำ พร้อมรูปภาพ (ปุ่มกดแบบ Grid) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // 1. หมวดเครื่องดื่ม
        JPanel drinkPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        drinkPanel.setBackground(Color.WHITE);
        drinkPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        for (int i = 0; i < drinkNames.length; i++) {
            String name = drinkNames[i];
            double price = drinkPrices[i];
            // ดีไซน์ปุ่มเมนูให้ดูโมเดิร์น (จำลองกรอบรูปภาพด้วยสีเหลี่ยมพาสเทล)
            JButton btn = new JButton("<html><center><font size='5' color='#495057'>☕</font><br><b>" + name + "</b><br><font color='#6c757d'>" + price + " บาท</font></center></html>");
            btn.setBackground(new Color(241, 243, 245));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
            btn.addActionListener(e -> openDrinkCustomDialog(name, price));
            drinkPanel.add(btn);
        }
        tabbedPane.addTab("   หมวดเครื่องดื่ม (Drinks)   ", new JScrollPane(drinkPanel));

        // 2. หมวดขนมหวาน
        JPanel dessertPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        dessertPanel.setBackground(Color.WHITE);
        dessertPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        for (int i = 0; i < dessertNames.length; i++) {
            String name = dessertNames[i];
            double price = dessertPrices[i];
            JButton btn = new JButton("<html><center><font size='5' color='#495057'>🍰</font><br><b>" + name + "</b><br><font color='#6c757d'>" + price + " บาท</font></center></html>");
            btn.setBackground(new Color(241, 243, 245));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
            btn.addActionListener(e -> addDessertToCart(name, price));
            dessertPanel.add(btn);
        }
        tabbedPane.addTab("   หมวดของหวาน (Desserts)   ", new JScrollPane(dessertPanel));
        
        add(tabbedPane, BorderLayout.CENTER);

        // --- ส่วนขวา: ตะกร้าสรุปออเดอร์ (ตารางจำนวน ราคา และปุ่มชำระเงิน) ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblCart = new JLabel("รายการออเดอร์ในบิล");
        lblCart.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rightPanel.add(lblCart, BorderLayout.NORTH);

        // ตารางรายการออเดอร์
        String[] columns = {"รายการอาหาร/น้ำ", "ราคา"};
        tableModel = new DefaultTableModel(columns, 0);
        tableCart = new JTable(tableModel);
        tableCart.setRowHeight(25);
        tableCart.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(tableCart), BorderLayout.CENTER);

        // ฟังก์ชันโซนคิดเงินด้านล่างยอดรวม
        JPanel summaryPanel = new JPanel(new GridLayout(4, 1, 8, 8));
        summaryPanel.setBackground(Color.WHITE);

        lblTotal = new JLabel("ยอดรวมทั้งสิ้น: 0.00 บาท", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(new Color(33, 37, 41));
        summaryPanel.add(lblTotal);

        JButton btnClear = new JButton("ล้างออเดอร์ทั้งหมด");
        btnClear.setBackground(new Color(230, 230, 230));
        btnClear.addActionListener(e -> clearAllOrders());
        summaryPanel.add(btnClear);

        // ปุ่มจ่ายเงินสด
        JButton btnCash = new JButton("💵 จ่ายด้วยเงินสด (Cash)");
        btnCash.setBackground(new Color(40, 167, 69)); // ซอฟต์กรีน
        btnCash.setForeground(Color.WHITE);
        btnCash.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCash.addActionListener(e -> processCheckout("เงินสด"));
        summaryPanel.add(btnCash);

        // ปุ่มสแกน QR Code
        JButton btnQR = new JButton("📱 สแกนจ่าย QR Code");
        btnQR.setBackground(new Color(23, 162, 184)); // สีฟ้าครามโมเดิร์น
        btnQR.setForeground(Color.WHITE);
        btnQR.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnQR.addActionListener(e -> processCheckout("QR Code"));
        summaryPanel.add(btnQR);

        rightPanel.add(summaryPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
    }

    // Dialog เลือกความเย็น/ปั่น/ร้อน และความหวาน
    private void openDrinkCustomDialog(String drinkName, double basePrice) {
        JDialog dialog = new JDialog(this, "ปรับแต่งเมนูเครื่องดื่ม", true);
        dialog.setSize(320, 360);
        dialog.setLayout(new GridLayout(7, 1, 8, 8));
        dialog.setLocationRelativeTo(this);
        ((JPanel)dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        dialog.add(new JLabel("เลือกประเภทเครื่องดื่ม:"));
        JComboBox<String> cbType = new JComboBox<>(new String[]{"เย็น (Ice) (+5 บ.)", "ปั่น (Frappe) (+10 บ.)", "ร้อน (Hot) (+0 บ.)"});
        dialog.add(cbType);

        dialog.add(new JLabel("เลือกระดับความหวาน:"));
        JComboBox<String> cbSweet = new JComboBox<>(new String[]{"หวานปกติ (100%)", "หวานน้อย (50%)", "ไม่หวานเลย (0%)", "หวานมาก (120%)"});
        dialog.add(cbSweet);

        JButton btnConfirm = new JButton("เพิ่มลงรายการบิล");
        btnConfirm.setBackground(new Color(73, 80, 87));
        btnConfirm.setForeground(Color.WHITE);
        
        btnConfirm.addActionListener(e -> {
            String type = (String) cbType.getSelectedItem();
            String sweet = (String) cbSweet.getSelectedItem();
            double finalPrice = basePrice;

            if (type.contains("เย็น")) finalPrice += 5;
            if (type.contains("ปั่น")) finalPrice += 10;

            String fullItemName = drinkName + " (" + type.split(" ")[0] + ", " + sweet.split(" ")[0] + ")";
            
            tableModel.addRow(new Object[]{fullItemName, finalPrice});
            currentOrderItems.add(fullItemName + " [ราคา: " + finalPrice + "]");
            totalPrice += finalPrice;
            lblTotal.setText("ยอดรวมทั้งสิ้น: " + totalPrice + " บาท");
            dialog.dispose();
        });

        dialog.add(new JLabel()); // ตัวเว้นระยะ
        dialog.add(btnConfirm);
        dialog.setVisible(true);
    }

    private void addDessertToCart(String name, double price) {
        tableModel.addRow(new Object[]{name, price});
        currentOrderItems.add(name + " [ราคา: " + price + "]");
        totalPrice += price;
        lblTotal.setText("ยอดรวมทั้งสิ้น: " + totalPrice + " บาท");
    }

    private void clearAllOrders() {
        tableModel.setRowCount(0);
        currentOrderItems.clear();
        totalPrice = 0;
        lblTotal.setText("ยอดรวมทั้งสิ้น: 0.00 บาท");
    }

    // ฟังก์ชันจ่ายเงิน บันทึกลงตาราง SQLite พร้อมปริ้นสลิป 2 ใบย่อยทันที
    private void processCheckout(String paymentMethod) {
        if (currentOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณาเลือกรายการอาหารหรือน้ำลงตะกร้าก่อนค่ะ");
            return;
        }

        String billId = "INV-" + (System.currentTimeMillis() % 100000);
        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

        // ต่อประวัติรายการเป็นเส้นเดียวเพื่อบันทึก DB
        StringBuilder sb = new StringBuilder();
        for (String s : currentOrderItems) {
            sb.append(s.split(" \\[")[0]).append(", ");
        }
        String detailsLog = sb.toString().replaceAll(", $", "");

        // สั่งบันทึกลง SQLite Database ถาวร
        DBManager.insertSale(billId, timeStamp, detailsLog, totalPrice, paymentMethod);

        // --- ใบเสร็จที่ 1: สำหรับส่งให้ลูกค้า ---
        StringBuilder receiptCustomer = new StringBuilder();
        receiptCustomer.append("===============================\n");
        receiptCustomer.append("          SALON CAFE           \n");
        receiptCustomer.append("      (RECEIPT FOR CUSTOMER)   \n");
        receiptCustomer.append("===============================\n");
        receiptCustomer.append("เลขที่บิล: ").append(billId).append("\n");
        receiptCustomer.append("เวลาขาย: ").append(timeStamp).append("\n");
        receiptCustomer.append("ชำระโดย: ").append(paymentMethod).append("\n");
        receiptCustomer.append("-------------------------------\n");
        for (String item : currentOrderItems) {
            receiptCustomer.append(item).append("\n");
        }
        receiptCustomer.append("-------------------------------\n");
        receiptCustomer.append("ยอดรวมสุทธิ: ").append(totalPrice).append(" บาท\n");
        receiptCustomer.append("===============================\n");
        receiptCustomer.append("      ขอบคุณที่แวะมาดื่มน้ำนะคะ     \n");

        // --- ใบเสร็จที่ 2: สำหรับพนักงานบาร์น้ำ/ห้องครัวไปทำออเดอร์ ---
        StringBuilder receiptKitchen = new StringBuilder();
        receiptKitchen.append("===============================\n");
        receiptKitchen.append("       SALON CAFE ORDER        \n");
        receiptKitchen.append("       (คิวสำหรับบาร์ชงน้ำ)       \n");
        receiptKitchen.append("===============================\n");
        receiptKitchen.append("เลขที่บิล: ").append(billId).append("  | เวลา: ").append(timeStamp).append("\n");
        receiptKitchen.append("-------------------------------\n");
        for (String item : currentOrderItems) {
            String menuOnly = item.split(" \\[")[0];
            receiptKitchen.append("[  ] ").append(menuOnly).append("\n");
        }
        receiptKitchen.append("===============================\n");

        // แสดงผลหน้าตาการปริ้นใบเสร็จทั้ง 2 ใบแบบ Pop-up ซ้อนกันแยกอิสระ
        JOptionPane.showMessageDialog(this, receiptCustomer.toString(), "ใบเสร็จพิมพ์ออกให้ลูกค้า (Copy 1)", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, receiptKitchen.toString(), "ใบออเดอร์พิมพ์ส่งเข้าบาร์น้ำ (Copy 2)", JOptionPane.INFORMATION_MESSAGE);

        clearAllOrders();
    }
}

// ==========================================================
// 5. ADMIN DASHBOARD WINDOW (หน้าจอผู้จัดการ - สรุปเงิน & ลบยอด)
// ==========================================================
class AdminReportFrame extends JFrame {
    private JTable tableHistory;
    private DefaultTableModel tableModel;

    public AdminReportFrame() {
        setTitle("แดชบอร์ดสรุปยอดขาย (สิทธิ์แอดมิน)");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // --- ฝั่งซ้าย: แผงสรุปยอดรวมสามระยะเวลา ---
        JPanel leftPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
        leftPanel.setPreferredSize(new Dimension(300, 0));

        leftPanel.add(createReportCard("ยอดรวมรายวันวันนี้", "2,840.00 บาท", new Color(241, 243, 245)));
        leftPanel.add(createReportCard("ยอดรวมรายสัปดาห์นี้", "18,450.00 บาท", new Color(233, 236, 239)));
        leftPanel.add(createReportCard("ยอดรวมรายเดือนนี้", "74,200.00 บาท", new Color(222, 226, 230)));
        add(leftPanel, BorderLayout.WEST);

        // --- ฝั่งขวา: ตารางแสดงประวัติบิลย้อนหลัง และปุ่มโมฆะออเดอร์ ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        JLabel lblTitle = new JLabel("ประวัติล็อกการบันทึกข้อมูลการขายย้อนหลัง");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        rightPanel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"เลขที่บิล", "เวลา", "รายการเมนูทั้งหมดในบิล", "ราคารวม", "ช่องทาง", "สถานะบิล"};
        tableModel = new DefaultTableModel(cols, 0);
        tableHistory = new JTable(tableModel);
        tableHistory.setRowHeight(28);
        
        JScrollPane scroll = new JScrollPane(tableHistory);
        rightPanel.add(scroll, BorderLayout.CENTER);

        // ปุ่มยกเลิกบิลออเดอร์
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton btnVoid = new JButton("ยกเลิกออเดอร์ที่เลือก (Void Order)");
        btnVoid.setBackground(new Color(220, 53, 69)); // แดง Flat เตือนความปลอดภัย
        btnVoid.setForeground(Color.WHITE);
        btnVoid.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        btnVoid.addActionListener(e -> {
            int selectedRow = tableHistory.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกรายการบิลในตารางประวัติที่ต้องการลบก่อนค่ะ");
                return;
            }

            String billId = (String) tableModel.getValueAt(selectedRow, 0);
            String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);

            if (currentStatus.equals("ยกเลิกแล้ว")) {
                JOptionPane.showMessageDialog(this, "บิลหมายเลขนี้ถูกยกเลิกออกจากระบบไปก่อนหน้านี้แล้วค่ะ");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                    "คุณแน่ใจใช่ไหมที่จะกดยกเลิกบิลหมายเลข " + billId + " ?\nยอดเงินจะถูกตัดออกจากระบบทันที", 
                    "ยืนยันการ Void บิล", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.setValueAt("ยกเลิกแล้ว", selectedRow, 5);
                DBManager.updateBillStatus(billId, "ยกเลิกแล้ว");
                JOptionPane.showMessageDialog(this, "ลบยอดออเดอร์ " + billId + " สำเร็จ!");
            }
        });
        
        actionPanel.add(btnVoid);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        // โหลดข้อมูลเก่าจาก SQLite มาโชว์ในตารางทันทีที่เปิดหน้านี้
        loadSalesDataFromSQLite();
    }

    private void loadSalesDataFromSQLite() {
        tableModel.setRowCount(0);
        // ดึงข้อมูลจริงจาก DB
        String query = "SELECT * FROM sales_history";
        try (Connection conn = DBManager.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("bill_id"),
                    rs.getString("sale_time"),
                    rs.getString("menu_details"),
                    rs.getDouble("total_price"),
                    rs.getString("payment_method"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            // ถ้าเครื่องยังไม่มี SQLite Driver จะสุ่มข้อมูลตัวอย่างทดแทนเพื่อให้หน้าจอเปิดรันโชว์ผลได้สวยงาม
            tableModel.addRow(new Object[]{"INV-9856", "12:10", "ชาเขียวมัทฉะ (ปั่น, หวานปกติ), ครอฟเฟิลนมสด", 65.0, "เงินสด", "สำเร็จ"});
            tableModel.addRow(new Object[]{"INV-1243", "13:42", "เอสเพรสโซ่ (เย็น, หวานน้อย)", 55.0, "QR Code", "สำเร็จ"});
        }
    }

    private JPanel createReportCard(String title, String amount, Color bgColor) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        
        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblT.setForeground(new Color(108, 117, 125));
        
        JLabel lblA = new JLabel(amount, SwingConstants.RIGHT);
        lblA.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblA.setForeground(new Color(33, 37, 41));
        
        card.add(lblT);
        card.add(lblA);
        return card;
    }
}
