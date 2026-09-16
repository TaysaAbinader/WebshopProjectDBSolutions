const express = require('express');
const router = express.Router();
const pool = require('../db');

// GET /v1/orders - List all orders using v_order_details view
router.get('/', async (req, res) => {
  const { customer_id, status } = req.query;
  let query = 'SELECT * FROM v_order_details WHERE 1=1';
  const params = [];

  if (customer_id) {
    query += ' AND customer_id = ?';
    params.push(customer_id);
  }
  if (status) {
    query += ' AND order_status = ?';
    params.push(status);
  }

  try {
    const [orders] = await pool.query(query, params);
    res.json(orders);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// GET /v1/orders/:id - Fetch single order details from view
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query(
      'SELECT * FROM v_order_details WHERE order_id = ?',
      [req.params.id]
    );
    if (rows.length === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Order not found' });
    }
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

module.exports = router;
