require('dotenv').config();
const mongoose = require('mongoose');
const fs = require('fs');
const path = require('path');
const connectDB = require('./config/db');
const User = require('./models/User');
const Deck = require('./models/Deck');
const Card = require('./models/Card');

const importData = async () => {
  // Lấy file path từ argument hoặc tìm file mới nhất trong data/
  let filePath = process.argv[2];

  if (!filePath) {
    const dataDir = path.join(__dirname, '..', 'data');
    if (!fs.existsSync(dataDir)) {
      console.error('Không tìm thấy thư mục data/. Hãy chỉ định file: node src/import.js <file.json>');
      process.exit(1);
    }

    const files = fs.readdirSync(dataDir)
      .filter((f) => f.startsWith('memomind-export-') && f.endsWith('.json'))
      .sort()
      .reverse();

    if (files.length === 0) {
      console.error('Không tìm thấy file export nào trong data/');
      process.exit(1);
    }

    filePath = path.join(dataDir, files[0]);
    console.log(`Tự động chọn file mới nhất: ${files[0]}`);
  }

  if (!fs.existsSync(filePath)) {
    console.error(`File không tồn tại: ${filePath}`);
    process.exit(1);
  }

  const raw = fs.readFileSync(filePath, 'utf-8');
  const data = JSON.parse(raw);

  console.log(`\nĐọc file: ${filePath}`);
  console.log(`Exported at: ${data.exportedAt}`);
  console.log(`Stats: ${data.stats.users} users, ${data.stats.decks} decks, ${data.stats.cards} cards`);

  await connectDB();

  // Xóa dữ liệu cũ
  await Card.deleteMany({});
  await Deck.deleteMany({});
  await User.deleteMany({});

  console.log('Đã xóa dữ liệu cũ');

  // Import users (insertMany bỏ qua pre-save hook nên password giữ nguyên hash)
  if (data.collections.users.length > 0) {
    await User.insertMany(data.collections.users);
    console.log(`Import ${data.collections.users.length} users`);
  }

  // Import decks
  if (data.collections.decks.length > 0) {
    await Deck.insertMany(data.collections.decks);
    console.log(`Import ${data.collections.decks.length} decks`);
  }

  // Import cards
  if (data.collections.cards.length > 0) {
    await Card.insertMany(data.collections.cards);
    console.log(`Import ${data.collections.cards.length} cards`);
  }

  console.log('\n========== IMPORT HOÀN TẤT ==========');
  console.log(`Users: ${data.collections.users.length}`);
  console.log(`Decks: ${data.collections.decks.length}`);
  console.log(`Cards: ${data.collections.cards.length}`);
  console.log('======================================\n');

  await mongoose.disconnect();
  process.exit(0);
};

importData().catch((err) => {
  console.error('Import thất bại:', err);
  process.exit(1);
});
