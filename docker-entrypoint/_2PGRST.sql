-- init postgrest
CREATE SCHEMA IF NOT EXISTS api;

-- create event trigger function (automatic schema cache reloading)
CREATE OR REPLACE FUNCTION api.pgrst_watch() 
RETURNS event_trigger
LANGUAGE plpgsql
AS $$
BEGIN
NOTIFY pgrst, 'reload schema';
END;
$$;

-- create event trigger procedure for every ddl_command_end event
CREATE EVENT TRIGGER pgrst_watch ON ddl_command_end
EXECUTE PROCEDURE api.pgrst_watch();