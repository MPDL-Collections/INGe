package de.mpg.mpdl.inge.db.spring;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;

public class JsonPostgreSQL9Dialect extends PostgreSQL9Dialect {

  public JsonPostgreSQL9Dialect() {
    this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
  }

}
