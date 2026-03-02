// Detect Low Performing Products - Terminal Version
// Usage: node detect.js vgchartz-2024.csv

const fs = require("fs");

// --- CSV parser (handles quoted commas) ---
function parseCsvLine(line) {
  const out = [];
  let cur = "";
  let inQuotes = false;

  for (let i = 0; i < line.length; i++) {
    const c = line[i];

    if (c === '"') {
      if (inQuotes && line[i + 1] === '"') {
        cur += '"';
        i++;
      } else {
        inQuotes = !inQuotes;
      }
    } else if (c === "," && !inQuotes) {
      out.push(cur);
      cur = "";
    } else {
      cur += c;
    }
  }
  out.push(cur);
  return out;
}

// --- main ---
const file = process.argv[2];
if (!file) {
  console.log("Usage: node detect.js <csv-file>");
  process.exit(1);
}

if (!fs.existsSync(file)) {
  console.log("File not found:", file);
  process.exit(1);
}

const text = fs.readFileSync(file, "utf8");
const lines = text.split(/\r?\n/).filter(l => l.trim().length);

const headers = parseCsvLine(lines[0]);

// try to find columns automatically
const titleIdx = headers.findIndex(h =>
  /title|name|product/i.test(h)
);
const salesIdx = headers.findIndex(h =>
  /total.*sales|sales/i.test(h)
);

// fallback for vgchartz dataset
const tIdx = titleIdx !== -1 ? titleIdx : 1;
const sIdx = salesIdx !== -1 ? salesIdx : 7;

const totals = new Map();

for (let i = 1; i < lines.length; i++) {
  const row = parseCsvLine(lines[i]);

  if (tIdx >= row.length) continue;
  const title = row[tIdx].trim();
  if (!title) continue;

  let sales = 0;
  if (sIdx < row.length) {
    const n = Number(row[sIdx].replace(/"/g, "").trim());
    if (!isNaN(n)) sales = n;
  }

  totals.set(title, (totals.get(title) || 0) + sales);
}

// compute average
const values = [...totals.values()];
const avg = values.reduce((a, b) => a + b, 0) / values.length;

// filter flagged
const flagged = [...totals.entries()]
  .filter(([_, v]) => v < avg)
  .sort((a, b) => a[1] - b[1]);

// --- OUTPUT ---
console.log("\nSOURCE:", file);
console.log("Rows read:", lines.length - 1);
console.log("Unique products:", totals.size);
console.log("Average sales per product:", avg.toFixed(4));
console.log("Flagged (below average):", flagged.length);

console.log("\nPRODUCT".padEnd(60), "TOTAL_SALES");
console.log("-".repeat(75));

// print first 200 only (avoid terminal spam)
const limit = Math.min(flagged.length, 200);
for (let i = 0; i < limit; i++) {
  const [p, v] = flagged[i];
  console.log(
    String(p).slice(0, 60).padEnd(60),
    v.toFixed(4).padStart(10)
  );
}

if (flagged.length > limit) {
  console.log(`\n... ${flagged.length - limit} more not shown`);
}