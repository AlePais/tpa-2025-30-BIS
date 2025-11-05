package ar.edu.utn.frba.dds.config.dialect;

import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class MariaDbFullTextDialect extends MariaDB103Dialect {

  public MariaDbFullTextDialect() {
    super();
    registerFunction("match_against",
        new SQLFunctionTemplate(StandardBasicTypes.DOUBLE,
            "MATCH(?1, ?2) AGAINST (?3 IN BOOLEAN MODE)"));
  }
}
