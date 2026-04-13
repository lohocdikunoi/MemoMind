require('dotenv').config();
const mongoose = require('mongoose');
const connectDB = require('./config/db');
const User = require('./models/User');
const Deck = require('./models/Deck');
const Card = require('./models/Card');

const seedData = async () => {
  await connectDB();

  // Xóa dữ liệu cũ
  await Card.deleteMany({});
  await Deck.deleteMany({});
  await User.deleteMany({});

  console.log('Đã xóa dữ liệu cũ');

  // Tạo user mẫu (password: 123456)
  const user = await User.create({
    email: 'demo@memomind.com',
    password: '123456',
    name: 'Người dùng Demo',
  });

  console.log(`Tạo user: ${user.email} / 123456`);

  // ========== Deck 1: Tiếng Anh TOEIC ==========
  const deck1 = await Deck.create({
    userId: user._id,
    name: 'Tiếng Anh TOEIC',
    description: 'Từ vựng TOEIC thường gặp',
    cardCount: 10,
  });

  const toeicCards = [
    { front: 'accomplish', back: 'hoàn thành, đạt được' },
    { front: 'negotiate', back: 'đàm phán, thương lượng' },
    { front: 'deadline', back: 'hạn chót, thời hạn' },
    { front: 'budget', back: 'ngân sách' },
    { front: 'revenue', back: 'doanh thu' },
    { front: 'implement', back: 'thực hiện, triển khai' },
    { front: 'collaborate', back: 'hợp tác, cộng tác' },
    { front: 'perspective', back: 'quan điểm, góc nhìn' },
    { front: 'evaluation', back: 'đánh giá, thẩm định' },
    { front: 'strategy', back: 'chiến lược' },
  ];

  for (const card of toeicCards) {
    await Card.create({
      deckId: deck1._id,
      userId: user._id,
      front: card.front,
      back: card.back,
    });
  }

  console.log(`Tạo deck: ${deck1.name} (${toeicCards.length} thẻ)`);

  // ========== Deck 2: Tiếng Nhật N5 ==========
  const deck2 = await Deck.create({
    userId: user._id,
    name: 'Tiếng Nhật N5',
    description: 'Từ vựng cơ bản JLPT N5',
    cardCount: 10,
  });

  const japaneseCards = [
    { front: 'わたし (watashi)', back: 'tôi' },
    { front: 'がくせい (gakusei)', back: 'học sinh, sinh viên' },
    { front: 'せんせい (sensei)', back: 'giáo viên, thầy/cô' },
    { front: 'ともだち (tomodachi)', back: 'bạn bè' },
    { front: 'たべる (taberu)', back: 'ăn' },
    { front: 'のむ (nomu)', back: 'uống' },
    { front: 'いく (iku)', back: 'đi' },
    { front: 'くる (kuru)', back: 'đến' },
    { front: 'おはよう (ohayou)', back: 'chào buổi sáng' },
    { front: 'ありがとう (arigatou)', back: 'cảm ơn' },
  ];

  for (const card of japaneseCards) {
    await Card.create({
      deckId: deck2._id,
      userId: user._id,
      front: card.front,
      back: card.back,
    });
  }

  console.log(`Tạo deck: ${deck2.name} (${japaneseCards.length} thẻ)`);

  // ========== Deck 3: Lập trình - Thuật ngữ ==========
  const deck3 = await Deck.create({
    userId: user._id,
    name: 'Thuật ngữ Lập trình',
    description: 'Các thuật ngữ IT phổ biến',
    cardCount: 10,
  });

  const programmingCards = [
    { front: 'API', back: 'Application Programming Interface - Giao diện lập trình ứng dụng' },
    { front: 'REST', back: 'Representational State Transfer - Kiến trúc truyền tải dữ liệu qua HTTP' },
    { front: 'ORM', back: 'Object-Relational Mapping - Ánh xạ đối tượng sang cơ sở dữ liệu' },
    { front: 'JWT', back: 'JSON Web Token - Mã xác thực dạng JSON' },
    { front: 'CRUD', back: 'Create, Read, Update, Delete - Các thao tác cơ bản với dữ liệu' },
    { front: 'MVC', back: 'Model-View-Controller - Mô hình kiến trúc phần mềm' },
    { front: 'CI/CD', back: 'Continuous Integration/Deployment - Tích hợp và triển khai liên tục' },
    { front: 'SDK', back: 'Software Development Kit - Bộ công cụ phát triển phần mềm' },
    { front: 'Middleware', back: 'Phần mềm trung gian xử lý request giữa client và server' },
    { front: 'Repository Pattern', back: 'Mẫu thiết kế tách biệt logic truy cập dữ liệu khỏi business logic' },
  ];

  for (const card of programmingCards) {
    await Card.create({
      deckId: deck3._id,
      userId: user._id,
      front: card.front,
      back: card.back,
    });
  }

  console.log(`Tạo deck: ${deck3.name} (${programmingCards.length} thẻ)`);

  // ========== Deck 4: Tiếng Anh giao tiếp ==========
  const deck4 = await Deck.create({
    userId: user._id,
    name: 'Tiếng Anh Giao tiếp',
    description: 'Cụm từ giao tiếp hàng ngày',
    cardCount: 10,
  });

  const conversationCards = [
    { front: 'How are you doing?', back: 'Bạn khỏe không? (thân mật)' },
    { front: 'Nice to meet you', back: 'Rất vui được gặp bạn' },
    { front: 'Could you help me?', back: 'Bạn có thể giúp tôi không?' },
    { front: 'I appreciate it', back: 'Tôi rất trân trọng điều đó' },
    { front: 'Let me know', back: 'Hãy cho tôi biết nhé' },
    { front: 'Take your time', back: 'Cứ từ từ, không vội' },
    { front: 'It makes sense', back: 'Điều đó hợp lý' },
    { front: 'I\'m looking forward to it', back: 'Tôi rất mong chờ điều đó' },
    { front: 'No worries', back: 'Không sao đâu, đừng lo' },
    { front: 'Keep in touch', back: 'Giữ liên lạc nhé' },
  ];

  for (const card of conversationCards) {
    await Card.create({
      deckId: deck4._id,
      userId: user._id,
      front: card.front,
      back: card.back,
    });
  }

  console.log(`Tạo deck: ${deck4.name} (${conversationCards.length} thẻ)`);

  console.log('\n========== SEED HOÀN TẤT ==========');
  console.log(`Tổng: 1 user, 4 decks, 40 cards`);
  console.log(`Đăng nhập: demo@memomind.com / 123456`);
  console.log('====================================\n');

  await mongoose.disconnect();
  process.exit(0);
};

seedData().catch((err) => {
  console.error('Seed thất bại:', err);
  process.exit(1);
});
