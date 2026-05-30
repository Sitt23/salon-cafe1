<!DOCTYPE html>
<html lang="lo">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Salon Cafe POS System</title>
    <style>
        * {
            box-sizing: border-box;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
        }

        body {
            background-color: #f8f9fa;
            color: #343a40;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        /* ---------------------------------
           LOGIN STYLES
        --------------------------------- */
        .login-container {
            background-color: #ffffff;
            width: 400px;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            text-align: center;
        }

        .login-container h1 {
            font-size: 32px;
            font-weight: bold;
            color: #343a40;
            margin-bottom: 5px;
        }

        .login-container p {
            font-size: 12px;
            color: #adb5bd;
            margin-bottom: 30px;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
            position: relative;
        }

        .form-group label {
            position: absolute;
            top: -8px;
            left: 12px;
            background: #fff;
            padding: 0 5px;
            font-size: 11px;
            color: #6c757d;
        }

        .form-control {
            width: 100%;
            padding: 12px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 14px;
            outline: none;
            background-color: #fff;
        }

        .btn-login {
            width: 100%;
            padding: 12px;
            background-color: #495057;
            color: #ffffff;
            border: none;
            border-radius: 4px;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s;
            margin-top: 15px;
        }

        .btn-login:hover {
            background-color: #343a40;
        }

        /* ---------------------------------
           MAIN APP STYLES (Hidden at start)
        --------------------------------- */
        .main-app {
            display: none;
            width: 100vw;
            height: 100vh;
            flex-direction: column;
            background-color: #f5f5f5;
        }

        header {
            background-color: #ffffff;
            padding: 15px 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-bottom: 1px solid #dee2e6;
        }

        header h2 {
            font-size: 18px;
            font-weight: bold;
        }

        .btn-admin {
            padding: 8px 15px;
            background-color: #6c757d;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            font-weight: bold;
        }

        .app-body {
            display: flex;
            flex: 1;
            overflow: hidden;
            padding: 15px;
            gap: 15px;
        }

        /* Menu Grid Section */
        .menu-section {
            flex: 1;
            background: #ffffff;
            border-radius: 6px;
            padding: 20px;
            overflow-y: auto;
        }

        .tabs {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            border-bottom: 2px solid #dee2e6;
            padding-bottom: 10px;
        }

        .tab-btn {
            padding: 10px 20px;
            background: none;
            border: none;
            font-size: 14px;
            font-weight: bold;
            cursor: pointer;
            color: #6c757d;
        }

        .tab-btn.active {
            color: #495057;
            border-bottom: 3px solid #495057;
        }

        .grid-container {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
        }

        .menu-item {
            background-color: #f1f3f5;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            padding: 20px;
            text-align: center;
            cursor: pointer;
            transition: transform 0.1s;
        }

        .menu-item:hover {
            transform: scale(1.02);
            background-color: #e9ecef;
        }

        .menu-item .icon {
            font-size: 24px;
            margin-bottom: 5px;
        }

        .menu-item .name {
            font-weight: bold;
            display: block;
            color: #495057;
        }

        .menu-item .price {
            font-size: 13px;
            color: #6c757d;
        }

        /* Cart Section */
        .cart-section {
            width: 380px;
            background: #ffffff;
            border-radius: 6px;
            padding: 15px;
            display: flex;
            flex-direction: column;
        }

        .cart-section h3 {
            font-size: 14px;
            margin-bottom: 10px;
        }

        .cart-table-wrapper {
            flex: 1;
            overflow-y: auto;
            border: 1px solid #dee2e6;
            margin-bottom: 15px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 13px;
        }

        th, td {
            padding: 8px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        th {
            background-color: #f8f9fa;
        }

        .summary-zone {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .total-price {
            text-align: right;
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .btn-action {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 4px;
            font-weight: bold;
            cursor: pointer;
            font-size: 13px;
        }

        .btn-clear { background-color: #e0e0e0; color: #333; }
        .btn-cash { background-color: #28a745; color: white; }
        .btn-qr { background-color: #17a2b4; color: white; }

        /* Modal Dialog */
        .modal {
            display: none;
            position: fixed;
            top: 0; left: 0; width: 100%; height: 100%;
            background: rgba(0,0,0,0.4);
            justify-content: center; align-items: center;
            z-index: 100;
        }

        .modal-content {
            background: white;
            padding: 25px;
            border-radius: 6px;
            width: 320px;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        /* ---------------------------------
           DASHBOARD MODAL STYLES
        --------------------------------- */
        .db-modal-content {
            background: white;
            padding: 25px;
            border-radius: 6px;
            width: 900px;
            max-height: 80vh;
            display: flex;
            gap: 20px;
            overflow: hidden;
            position: relative;
        }

        .close-db {
            position: absolute;
            top: 15px; right: 20px;
            font-size: 20px; cursor: pointer;
        }

        .db-left {
            width: 280px;
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .report-card {
            padding: 15px;
            border-radius: 4px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            height: 90px;
        }

        .card-title { font-size: 13px; color: #6c757d; }
        .card-val { font-size: 20px; font-weight: bold; text-align: right; }

        .db-right {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }
    </style>
</head>
<body>

    <div class="login-container" id="loginFrame">
        <h1>SALON CAFE</h1>
        <p>Minimalist POS System v1.0</p>
        
        <div class="form-group">
            <label>Role</label>
            <select class="form-control" id="cbRole">
                <option>พนักงานทั่วไป (Staff)</option>
                <option>ผู้จัดการร้าน (Admin)</option>
            </select>
        </div>

        <div class="form-group">
            <label>Username</label>
            <input type="text" class="form-control" id="txtUsername" value="001">
        </div>

        <div class="form-group">
            <label>Password</label>
            <input type="password" class="form-control" id="txtPassword" value="123456">
        </div>

        <button class="btn-login" onclick="handleLogin()">เข้าสู่ระบบ</button>
    </div>

    <div class="main-app" id="mainOrderFrame">
        <header>
            <h2>SALON CAFE | พนักงานขาย</h2>
            <button class="btn-admin" id="btnAdminDash" style="display:none;" onclick="openAdminDashboard()">แดชบอร์ดผู้จัดการ</button>
        </header>

        <div class="app-body">
            <div class="menu-section">
                <div class="tabs">
                    <button class="tab-btn active" onclick="switchTab('drinks')">หมวดเครื่องดื่ม (Drinks)</button>
                    <button class="tab-btn" onclick="switchTab('desserts')">หมวดของหวาน (Desserts)</button>
                </div>

                <div class="grid-container" id="drinksGrid">
                    <div class="menu-item" onclick="openDrinkCustom('เอสเพรสโซ่', 50)">
                        <div class="icon">☕</div><span class="name">เอสเพรสโซ่</span><span class="price">50 บาท</span>
                    </div>
                    <div class="menu-item" onclick="openDrinkCustom('ชาเขียวมัทฉะ', 55)">
                        <div class="icon">☕</div><span class="name">ชาเขียวมัทฉะ</span><span class="price">55 บาท</span>
                    </div>
                    <div class="menu-item" onclick="openDrinkCustom('ชานมไต้หวัน', 45)">
                        <div class="icon">☕</div><span class="name">ชานมไต้หวัน</span><span class="price">45 บาท</span>
                    </div>
                    <div class="menu-item" onclick="openDrinkCustom('โกโก้ลาเต้', 50)">
                        <div class="icon">☕</div><span class="name">โกโก้ลาเต้</span><span class="price">50 บาท</span>
                    </div>
                    <div class="menu-item" onclick="openDrinkCustom('นมสดคาราเมล', 55)">
                        <div class="icon">☕</div><span class="name">นมสดคาราเมล</span><span class="price">55 บาท</span>
                    </div>
                    <div class="menu-item" onclick="openDrinkCustom('สตรอว์เบอร์รี่โซดา', 40)">
                        <div class="icon">☕</div><span class="name">สตรอว์เบอร์รี่โซดา</span><span class="price">40 บาท</span>
                    </div>
                </div>

                <div class="grid-container" id="dessertsGrid" style="display:none;">
                    <div class="menu-item" onclick="addDessertToCart('ฮันนี่โทสต์', 99)">
                        <div class="icon">🍰</div><span class="name">ฮันนี่โทสต์</span><span class="price">99 บาท</span>
                    </div>
                    <div class="menu-item" onclick="addDessertToCart('ครอฟเฟิลนมสด', 45)">
                        <div class="icon">🍰</div><span class="name">ครอฟเฟิลนมสด</span><span class="price">45 บาท</span>
                    </div>
                    <div class="menu-item" onclick="addDessertToCart('เค้กช็อกโกแลต', 65)">
                        <div class="icon">🍰</div><span class="name">เค้กช็อกโกแลต</span><span class="price">65 บาท</span>
                    </div>
                    <div class="menu-item" onclick="addDessertToCart('บิงซูชาไทย', 120)">
                        <div class="icon">🍰</div><span class="name">บิงซูชาไทย</span><span class="price">120 บาท</span>
                    </div>
                </div>
            </div>

            <div class="cart-section">
                <h3>รายการออเดอร์ในบิล</h3>
                <div class="cart-table-wrapper">
                    <table id="cartTable">
                        <thead>
                            <tr>
                                <th>รายการอาหาร/น้ำ</th>
                                <th style="width: 80px; text-align: right;">ราคา</th>
                            </tr>
                        </thead>
                        <tbody>
                            </tbody>
                    </table>
                </div>

                <div class="summary-zone">
                    <div class="total-price" id="lblTotal">ยอดรวมทั้งสิ้น: 0.00 บาท</div>
                    <button class="btn-action btn-clear" onclick="clearAllOrders()">ล้างออเดอร์ทั้งหมด</button>
                    <button class="btn-action btn-cash" onclick="processCheckout('เงินสด')">💵 จ่ายด้วยเงินสด (Cash)</button>
                    <button class="btn-action btn-qr" onclick="processCheckout('QR Code')">📱 สแกนจ่าย QR Code</button>
                </div>
            </div>
        </div>
    </div>

    <div class="modal" id="drinkModal">
        <div class="modal-content">
            <h4 id="modalDrinkName" style="font-weight: bold;">ปรับแต่งเมนูเครื่องดื่ม</h4>
            <label style="font-size:12px;">เลือกประเภทเครื่องดื่ม:</label>
            <select class="form-control" id="cbType">
                <option value="5">เย็น (Ice) (+5 บ.)</option>
                <option value="10">ปั่น (Frappe) (+10 บ.)</option>
                <option value="0">ร้อน (Hot) (+0 บ.)</option>
            </select>

            <label style="font-size:12px;">เลือกระดับความหวาน:</label>
            <select class="form-control" id="cbSweet">
                <option>หวานปกติ (100%)</option>
                <option>หวานน้อย (50%)</option>
                <option>ไม่หวานเลย (0%)</option>
                <option>หวานมาก (120%)</option>
            </select>

            <button class="btn-login" style="margin-top:10px;" onclick="confirmDrinkCustom()">เพิ่มลงรายการบิล</button>
            <button class="btn-action btn-clear" onclick="closeDrinkModal()">ยกเลิก</button>
        </div>
    </div>

    <div class="modal" id="adminModal">
        <div class="db-modal-content">
            <span class="close-db" onclick="closeAdminDashboard()">&times;</span>
            
            <div class="db-left">
                <div class="report-card" style="background-color: #f1f3f5;">
                    <span class="card-title">ยอดรวมรายวันวันนี้</span>
                    <span class="card-val">2,840.00 บาท</span>
                </div>
                <div class="report-card" style="background-color: #e9ecef;">
                    <span class="card-title">ยอดรวมรายสัปดาห์นี้</span>
                    <span class="card-val">18,450.00 บาท</span>
                </div>
                <div class="report-card" style="background-color: #dee2e6;">
                    <span class="card-title">ยอดรวมรายเดือนนี้</span>
                    <span class="card-val">74,200.00 บาท</span>
                </div>
            </div>

            <div class="db-right">
                <h4 style="margin-bottom:10px; font-weight:bold;">ประวัติล็อกการบันทึกข้อมูลการขายย้อนหลัง</h4>
                <div class="cart-table-wrapper">
                    <table id="historyTable">
                        <thead>
                            <tr>
                                <th>เลขที่บิล</th>
                                <th>เวลา</th>
                                <th>รายการเมนูทั้งหมดในบิล</th>
                                <th>ราคารวม</th>
                                <th>ช่องทาง</th>
                                <th>สถานะบิล</th>
                            </tr>
                        </thead>
                        <tbody>
                            </tbody>
                    </table>
                </div>
                <button class="btn-action" style="background-color:#dc3545; color:white; width:auto; align-self:flex-end;" onclick="voidOrder()">ยกเลิกออเดอร์ที่เลือก (Void Order)</button>
            </div>
        </div>
    </div>

    <script>
        let totalPrice = 0;
        let currentOrderItems = [];
        let tempDrinkName = "";
        let tempBasePrice = 0;
        
        // จำลองข้อมูลระบบประวัติการขายเก็บไว้บนเบราว์เซอร์ (LocalStorage) ทดแทนตาราง SQLite ของเก่า
        let salesHistory = JSON.parse(localStorage.getItem('salesHistory')) || [
            { id: "INV-9856", time: "12:10:15", details: "ชาเขียวมัทฉะ (ปั่น, หวานปกติ), ครอฟเฟิลนมสด", total: 65, method: "เงินสด", status: "สำเร็จ" },
            { id: "INV-1243", time: "13:42:04", details: "เอสเพรสโซ่ (เย็น, หวานน้อย)", total: 55, method: "QR Code", status: "สำเร็จ" }
        ];

        // ---------------------------------
        // ฟังก์ชันระบบล็อกอิน (แก้ปัญหาของคุณที่หน้านี้เลย!)
        // ---------------------------------
        function handleLogin() {
            let u = document.getElementById('txtUsername').value;
            let p = document.getElementById('txtPassword').value;
            let role = document.getElementById('cbRole').value;

            // ตรวจสอบข้อมูลรหัสผ่านตามเงื่อนไขที่คุณตั้งไว้ในเครื่องพนักงานของคุณ
            if (u === "001" && p === "123456") {
                document.getElementById('loginFrame').style.display = "none";
                document.getElementById('mainOrderFrame').style.display = "flex";
                
                // ตรวจสอบสิทธิ์ถ้าเป็นแอดมิน ให้แสดงปุ่มเข้าหน้าแดชบอร์ด
                if (role.includes("Admin")) {
                    document.getElementById('btnAdminDash').style.display = "block";
                }
            } else {
                // แจ้งเตือนข้อความป๊อปอัพเป็นภาษาลาวตามของเดิมของคุณเป๊ะๆ
                alert("❌ ລະຫັດພະນັກງານ ຫຼື ລະຫັດຜ່ານບໍ່ຖືກຕ້ອງ!");
            }
        }

        // ---------------------------------
        // ฟังก์ชันจัดการเมนูและตะกร้า
        // ---------------------------------
        function switchTab(type) {
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            if(type === 'drinks') {
                document.getElementById('drinksGrid').style.display = "grid";
                document.getElementById('dessertsGrid').style.display = "none";
                event.target.classList.add('active');
            } else {
                document.getElementById('drinksGrid').style.display = "none";
                document.getElementById('dessertsGrid').style.display = "grid";
                event.target.classList.add('active');
            }
        }

        function openDrinkCustom(name, price) {
            tempDrinkName = name;
            tempBasePrice = price;
            document.getElementById('modalDrinkName').innerText = "ปรับแต่ง: " + name;
            document.getElementById('drinkModal').style.display = "flex";
        }

        function closeDrinkModal() {
            document.getElementById('drinkModal').style.display = "none";
        }

        function confirmDrinkCustom() {
            let typeSelect = document.getElementById('cbType');
            let typeName = typeSelect.options[typeSelect.selectedIndex].text.split(" ")[0];
            let typePrice = parseFloat(typeSelect.value);
            
            let sweetSelect = document.getElementById('cbSweet');
            let sweetName = sweetSelect.value.split(" ")[0];

            let finalPrice = tempBasePrice + typePrice;
            let fullItemName = `${tempDrinkName} (${typeName}, ${sweetName})`;

            addTableRow(fullItemName, finalPrice);
            closeDrinkModal();
        }

        function addDessertToCart(name, price) {
            addTableRow(name, price);
        }

        function addTableRow(name, price) {
            let tbody = document.querySelector("#cartTable tbody");
            let row = tbody.insertRow();
            row.insertCell(0).innerText = name;
            let cellPrice = row.insertCell(1);
            cellPrice.innerText = price.toFixed(2);
            cellPrice.style.textAlign = "right";

            currentOrderItems.push({ name: name, price: price });
            totalPrice += price;
            document.getElementById("lblTotal").innerText = `ยอดรวมทั้งสิ้น: ${totalPrice.toFixed(2)} บาท`;
        }

        function clearAllOrders() {
            document.querySelector("#cartTable tbody").innerHTML = "";
            currentOrderItems = [];
            totalPrice = 0;
            document.getElementById("lblTotal").innerText = "ยอดรวมทั้งสิ้น: 0.00 บาท";
        }

        // ---------------------------------
        // ฟังก์ชันจำลองพิมพ์บิล 2 ใบ
        // ---------------------------------
        function processCheckout(method) {
            if (currentOrderItems.length === 0) {
                alert("กรุณาเลือกรายการอาหารหรือน้ำลงตะกร้าก่อนค่ะ");
                return;
            }

            let billId = "INV-" + Math.floor(Math.random() * 90000 + 10000);
            let timeStamp = new Date().toLocaleTimeString('th-TH');

            let detailsLog = currentOrderItems.map(i => i.name).join(", ");
            
            // บันทึกลงหน่วยความจำชั่วคราวเบราว์เซอร์แทน SQLite
            salesHistory.push({ id: billId, time: timeStamp, details: detailsLog, total: totalPrice, method: method, status: "สำเร็จ" });
            localStorage.setItem('salesHistory', JSON.stringify(salesHistory));

            // สร้างข้อความใบเสร็จของลูกค้า
            let recCustomer = `===============================\n          SALON CAFE           \n      (RECEIPT FOR CUSTOMER)   \n===============================\nเลขที่บิล: ${billId}\nเวลาขาย: ${timeStamp}\nชำระโดย: ${method}\n-------------------------------\n`;
            currentOrderItems.forEach(i => {
                recCustomer += `${i.name} [ราคา: ${i.price}]\n`;
            });
            recCustomer += `-------------------------------\nยอดรวมสุทธิ: ${totalPrice.toFixed(2)} บาท\n===============================\n      ขอบคุณที่แวะมาดื่มน้ำนะคะ     `;

            // สร้างข้อความใบส่งบาร์ชงน้ำ
            let recKitchen = `===============================\n       SALON CAFE ORDER        \n       (คิวสำหรับบาร์ชงน้ำ)       \n===============================\nเลขที่บิล: ${billId}  | เวลา: ${timeStamp}\n-------------------------------\n`;
            currentOrderItems.forEach(i => {
                recKitchen += `[  ] ${i.name}\n`;
            });
            recKitchen += `===============================`;

            // แสดง Pop-up เลียนแบบตัวแอปดั้งเดิม
            alert(recCustomer);
            alert(recKitchen);

            clearAllOrders();
        }

        // ---------------------------------
        // ฟังก์ชันระบบแอดมินแดชบอร์ด
        // ---------------------------------
        function openAdminDashboard() {
            let tbody = document.querySelector("#historyTable tbody");
            tbody.innerHTML = "";
            
            salesHistory.forEach((item, index) => {
                let row = tbody.insertRow();
                row.setAttribute('onclick', `selectHistoryRow(this, ${index})`);
                row.style.cursor = "pointer";
                row.insertCell(0).innerText = item.id;
                row.insertCell(1).innerText = item.time;
                row.insertCell(2).innerText = item.details;
                row.insertCell(3).innerText = item.total.toFixed(2);
                row.insertCell(4).innerText = item.method;
                let statusCell = row.insertCell(5);
                statusCell.innerText = item.status;
                if(item.status === "ยกเลิกแล้ว") statusCell.style.color = "red";
            });

            document.getElementById('adminModal').style.display = "flex";
        }

        function closeAdminDashboard() {
            document.getElementById('adminModal').style.display = "none";
        }

        let selectedOrderIndex = -1;
        function selectHistoryRow(row, index) {
            document.querySelectorAll("#historyTable tr").forEach(r => r.style.backgroundColor = "");
            row.style.backgroundColor = "#e9ecef";
            selectedOrderIndex = index;
        }

        function voidOrder() {
            if(selectedOrderIndex === -1) {
                alert("กรุณาเลือกรายการบิลในตารางประวัติที่ต้องการลบก่อนค่ะ");
                return;
            }

            let order = salesHistory[selectedOrderIndex];
            if(order.status === "ยกเลิกแล้ว") {
                alert("บิลหมายเลขนี้ถูกยกเลิกออกจากระบบไปก่อนหน้านี้แล้วค่ะ");
                return;
            }

            if(confirm(`คุณแน่ใจใช่ไหมที่จะกดยกเลิกบิลหมายเลข ${order.id} ?\nยอดเงินจะถูกตัดออกจากระบบทันที`)) {
                salesHistory[selectedOrderIndex].status = "ยกเลิกแล้ว";
                localStorage.setItem('salesHistory', JSON.stringify(salesHistory));
                openAdminDashboard(); // Refresh Table
                alert(`ลบยอดออเดอร์ ${order.id} สำเร็จ!`);
                selectedOrderIndex = -1;
            }
        }
    </script>
</body>
</html>
