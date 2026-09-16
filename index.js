const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

// Register API Routes
app.use('/v1/products', require('./routes/products'));
app.use('/v1/categories', require('./routes/categories'));
app.use('/v1/customers', require('./routes/customers'));
app.use('/v1/orders', require('./routes/orders'));

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Server running on http://localhost:${PORT}`);
});

