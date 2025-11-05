CREATE TABLE Criterio
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    atributoFiltro INT                   NULL,
    valor          VARCHAR(255)          NULL,
    CONSTRAINT pk_criterio PRIMARY KEY (id)
);