-- Manual DDL executed after Hibernate creates the base schema.
-- Ensures full-text search support over the main textual columns of Hechos.

DROP INDEX IF EXISTS idx_hechos_fulltext ON Hechos;
CREATE FULLTEXT INDEX idx_hechos_fulltext
  ON Hechos (titulo, descripcion, categoria);

DROP INDEX IF EXISTS idx_lugares_provincia_fulltext ON Lugares;
CREATE FULLTEXT INDEX idx_lugares_provincia_fulltext
  ON Lugares (provincia);
