const express = require('express');
const router = express.Router();
const pool = require('../db');

// GET /v1/products - Fetching via Database View with optional search/filter
router.get('/', async (req, res) => {
  const { category_id, supplier_id, search } = req.query;
  let query = 'SELECT * FROM v_product_catalog WHERE 1=1';
  const params = [];

  if (category_id) {
    query += ' AND category_id = ?';
    params.push(category_id);
  }
  if (supplier_id) {
    query += ' AND supplier_id = ?';
    params.push(supplier_id);
  }
  if (search) {
    query += ' AND (product_name LIKE ? OR product_description LIKE ?)';
    params.push(`%${search}%`, `%${search}%`);
  }

  try {
    const [products] = await pool.query(query, params);
    res.json(products);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// GET /v1/products/:id - Single product lookup from view
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT * FROM v_product_catalog WHERE product_id = ?',
      [req.params.id]
    );
    if (rows.length === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Product not found' });
    }
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

module.exports = router;
