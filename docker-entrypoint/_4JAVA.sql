-- initialize pljava settings

-- set libjvm path and create extension
SET pljava.libjvm_location TO '/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so';
CREATE EXTENSION pljava;

-- set java permission
GRANT USAGE ON LANGUAGE java TO "postgres";
ALTER DATABASE rapsql SET pljava.libjvm_location FROM CURRENT;