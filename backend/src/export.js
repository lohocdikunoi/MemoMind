require('dotenv').config();
const mongoose = require('mongoose');
const fs = require('fs');
const path = require('path');
const connectDB = require('./config/db');
const User = require('./models/User');
const Deck = require('./models/Deck');
const Card = require('./models/Card');

const exportData = async () => {
  await connectDB();

  const users = await User.find({}).lean();
  const decks = await Deck.find({}).lean();
  const cards = await Card.find({}).lean();

  const data = {
    exportedAt: new Date().toISOString(),
    database: 'memomind',
    collections: {
      users,
      decks,
      cards,
    },
    stats: {
      users: users.length,
      decks: decks.length,
      cards: cards.length,
    },
  };

  const exportDir = path.join(__dirname, '..', 'data');
  if (!fs.existsSync(exportDir)) {
    fs.mkdirSync(exportDir, { recursive: true });
  }

  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const filePath = path.join(exportDir, `memomind-export-${timestamp}.json`);

  fs.writeFileSync(filePath, JSON.stringify(data, null, 2), 'utf-8');

  console.log('\n========== EXPORT HOÀN TẤT ==========');
  console.log(`File: ${filePath}`);
  console.log(`Users: ${users.length}`);
  console.log(`Decks: ${decks.length}`);
  console.log(`Cards: ${cards.length}`);
  console.log('======================================\n');

  await mongoose.disconnect();
  process.exit(0);
};

exportData().catch((err) => {
  console.error('Export thất bại:', err);
  process.exit(1);
});
