const express = require('express');
const router = express.Router();
const pool = require('../db');

// GET /customers - List customers
router.get('/', async (req, res) => {
  try {
    const [customers] = await pool.query('SELECT * FROM Customers');
    res.json(customers);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// POST /customers - Register new customer
router.post('/', async (req, res) => {
  const { first_name, last_name, email, phone } = req.body;
  if (!first_name || !last_name || !email) {
    return res.status(400).json({ code: 'INVALID_PARAMETER', message: 'First name, last name, and email are required' });
  }

  try {
    const [result] = await pool.query(
      'INSERT INTO Customers (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)',
      [first_name, last_name, email, phone || null]
    );
    res.status(201).json({ id: result.insertId, first_name, last_name, email, phone });
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// GET /customers/:id - Fetch single customer
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM Customers WHERE id = ?', [req.params.id]);
    if (rows.length === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Customer not found' });
    }
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// GET /customers/:customerId/addresses - Get saved addresses for a customer
router.get('/:customerId/addresses', async (req, res) => {
  try {
    const [addresses] = await pool.query(
      'SELECT * FROM CustomerAddresses WHERE customer_id = ?',
      [req.params.customerId]
    );
    res.json(addresses);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// POST /customers/:customerId/addresses - Add delivery address
router.post('/:customerId/addresses', async (req, res) => {
  const { street_address, postal_code, city, country } = req.body;
  if (!street_address || !city) {
    return res.status(400).json({ code: 'INVALID_PARAMETER', message: 'Street address and city are required' });
  }

  try {
    const [result] = await pool.query(
      'INSERT INTO CustomerAddresses (customer_id, street_address, postal_code, city, country) VALUES (?, ?, ?, ?, ?)',
      [req.params.customerId, street_address, postal_code || null, city, country || null]
    );
    res.status(201).json({ id: result.insertId, customer_id: req.params.customerId, street_address, city });
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

module.exports = router;
