const express = require('express');
const router = express.Router();
const pool = require('../db');

// GET /categories - List all categories
router.get('/', async (req, res) => {
  try {
    const [categories] = await pool.query('SELECT * FROM ProductCategories');
    res.json(categories);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// GET /categories/:id - Get single category details
router.get('/:id', async (req, res) => {
  try {
    const [rows] = await pool.query('SELECT * FROM ProductCategories WHERE id = ?', [req.params.id]);
    if (rows.length === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Category not found' });
    }
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// POST /categories - Add new category
router.post('/', async (req, res) => {
  const { name, description } = req.body;
  if (!name) {
    return res.status(400).json({ code: 'INVALID_PARAMETER', message: 'Category name is required' });
  }

  try {
    const [result] = await pool.query(
      'INSERT INTO ProductCategories (name, description) VALUES (?, ?)',
      [name, description || null]
    );
    res.status(201).json({ id: result.insertId, name, description });
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// PUT /categories/:id - Update category
router.put('/:id', async (req, res) => {
  const { name, description } = req.body;
  try {
    const [result] = await pool.query(
      'UPDATE ProductCategories SET name = ?, description = ? WHERE id = ?',
      [name, description || null, req.params.id]
    );
    if (result.affectedRows === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Category not found' });
    }
    res.json({ id: req.params.id, name, description });
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

// DELETE /categories/:id - Remove category
router.delete('/:id', async (req, res) => {
  try {
    const [result] = await pool.query('DELETE FROM ProductCategories WHERE id = ?', [req.params.id]);
    if (result.affectedRows === 0) {
      return res.status(404).json({ code: 'NOT_FOUND', message: 'Category not found' });
    }
    res.status(204).send();
  } catch (err) {
    res.status(500).json({ code: 'DATABASE_ERROR', message: err.message });
  }
});

module.exports = router;
