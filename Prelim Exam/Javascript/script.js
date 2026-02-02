// ---------- PARSE CSV ----------
function parseCSV(data) {
    const lines = data.trim().split("\n");
    return lines.slice(1).map(line => { // skip header
        const [studentID, first_name, last_name, lab1, lab2, lab3, prelim, attendance] = line.split(",");
        return {
            studentID: studentID.trim(),
            first_name: first_name.trim(),
            last_name: last_name.trim(),
            lab1: lab1.trim(),
            lab2: lab2.trim(),
            lab3: lab3.trim(),
            prelim: prelim.trim(),
            attendance: attendance.trim()
        };
    });
}

// ---------- DISPLAY TABLE ----------
function displayTable(records) {
    const tableBody = document.getElementById("tableBody");
    tableBody.innerHTML = ""; // clear existing rows

    records.forEach(student => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${student.studentID}</td>
            <td>${student.first_name}</td>
            <td>${student.last_name}</td>
            <td>${student.lab1}</td>
            <td>${student.lab2}</td>
            <td>${student.lab3}</td>
            <td>${student.prelim}</td>
            <td>${student.attendance}</td>
            <td><button class="delete" onclick="deleteRow(this)">Delete</button></td>
        `;
        tableBody.appendChild(row);
    });
}

// ---------- DELETE FUNCTION ----------
function deleteRow(button) {
    button.parentElement.parentElement.remove();
}

// ---------- ADD STUDENT FUNCTION ----------
function addRecord() {
    const studentID = document.getElementById("studentID").value.trim();
    const first_name = document.getElementById("first_name").value.trim();
    const last_name = document.getElementById("last_name").value.trim();
    const lab1 = document.getElementById("lab1").value.trim();
    const lab2 = document.getElementById("lab2").value.trim();
    const lab3 = document.getElementById("lab3").value.trim();
    const prelim = document.getElementById("prelim").value.trim();
    const attendance = document.getElementById("attendance").value.trim();

    if (!studentID || !first_name || !last_name || !lab1 || !lab2 || !lab3 || !prelim || !attendance) {
        alert("Please fill in all fields!");
        return;
    }

    const tableBody = document.getElementById("tableBody");
    const row = document.createElement("tr");
    row.innerHTML = `
        <td>${studentID}</td>
        <td>${first_name}</td>
        <td>${last_name}</td>
        <td>${lab1}</td>
        <td>${lab2}</td>
        <td>${lab3}</td>
        <td>${prelim}</td>
        <td>${attendance}</td>
        <td><button class="delete" onclick="deleteRow(this)">Delete</button></td>
    `;
    tableBody.appendChild(row);

    // Clear inputs
    document.getElementById("studentID").value = "";
    document.getElementById("first_name").value = "";
    document.getElementById("last_name").value = "";
    document.getElementById("lab1").value = "";
    document.getElementById("lab2").value = "";
    document.getElementById("lab3").value = "";
    document.getElementById("prelim").value = "";
    document.getElementById("attendance").value = "";
}

// ---------- FETCH CSV ----------
fetch("students.csv")
    .then(response => {
        if (!response.ok) throw new Error("CSV file not found!");
        return response.text();
    })
    .then(data => {
        const students = parseCSV(data);
        displayTable(students);
    })
    .catch(err => console.error(err));
