-- Add person.identifier.spin metadata field for SPIN-code (RSCI / eLIBRARY.RU)
INSERT INTO metadatafieldregistry (metadata_schema_id, element, qualifier, scope_note)
  SELECT
    (SELECT metadata_schema_id FROM metadataschemaregistry WHERE short_id = 'person'),
    'identifier',
    'spin',
    'SPIN-code of the researcher in RSCI (Russian Science Citation Index) / eLIBRARY.RU'
  WHERE NOT EXISTS (
    SELECT metadata_field_id FROM metadatafieldregistry
    WHERE element = 'identifier'
      AND qualifier = 'spin'
      AND metadata_schema_id = (SELECT metadata_schema_id FROM metadataschemaregistry WHERE short_id = 'person')
  );
