-- Agrego SQL a mano para hacer fulltext la busqueda en campos de texto de hecho

ALTER TABLE Hechos
  DROP INDEX IF EXISTS idx_hechos_fulltext,
  ADD FULLTEXT INDEX idx_hechos_fulltext (titulo, descripcion, categoria);

ALTER TABLE Lugares
  DROP INDEX IF EXISTS idx_lugares_provincia_fulltext,
  ADD FULLTEXT INDEX idx_lugares_provincia_fulltext (provincia);
