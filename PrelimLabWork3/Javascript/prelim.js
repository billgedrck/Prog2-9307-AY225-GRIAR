const attendanceEl = document.getElementById("attendance");
const lab1El = document.getElementById("lab1");
const lab2El = document.getElementById("lab2");
const lab3El = document.getElementById("lab3");
const results = document.getElementById("results");

function calculate() {
  let attendance = parseFloat(attendanceEl.value);
  let lab1 = parseFloat(lab1El.value);
  let lab2 = parseFloat(lab2El.value);
  let lab3 = parseFloat(lab3El.value);

  if ([attendance, lab1, lab2, lab3].some(isNaN)) {
    alert("Please enter all fields.");
    return;
  }
  if ([attendance, lab1, lab2, lab3].some(n => n < 0 || n > 100)) {
    alert("All grades must be between 0 and 100.");
    return;
  }

  let labAvg = (lab1 + lab2 + lab3) / 3;
  let classStanding = (attendance * 0.40) + (labAvg * 0.60);

  let passExam = (75 - (classStanding * 0.30)) / 0.70;
  let excellentExam = (100 - (classStanding * 0.30)) / 0.70;

  passExam = Math.min(passExam, 100);
  excellentExam = Math.min(excellentExam, 100);

  let color;
  if (passExam > 100) color = "red";
  else if (passExam > 80) color = "yellow";
  else color = "green";

  let remark;
  if (passExam > 100) remark = "Passing is no longer possible.";
  else if (passExam <= 0) remark = "You already passed!";
  else remark = `You need at least ${passExam.toFixed(2)} in the Prelim Exam to pass.`;

  results.innerHTML = `
    <strong>Lab Work Average:</strong> ${labAvg.toFixed(2)}<br>
    <strong>Class Standing:</strong> ${classStanding.toFixed(2)}<br><br>

    <span class="${color}">
      <strong>Prelim Exam to PASS:</strong> ${passExam.toFixed(2)}
    </span><br>

    <strong>Prelim Exam for EXCELLENT:</strong> ${excellentExam.toFixed(2)}<br><br>

    <strong>Remark:</strong> ${remark}
  `;
}

function toggleDark() {
  document.body.classList.toggle("dark");
}
